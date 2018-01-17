package modelisation.builder;

import modelisation.Indicateurs;
import modelisation.builder.strategies.Chi2SplittingStrategy;
import modelisation.builder.strategies.SplittingStrategy;
import modelisation.data.Column;
import modelisation.data.TrainingData;
import modelisation.tree.DecisionTree;
import org.eclipse.jdt.annotation.NonNull;
import org.eclipse.jdt.annotation.Nullable;

import java.util.*;
import java.util.stream.Collectors;

public class DecisionTreeBuilder {
    public static final Configuration DEFAULT_CONFIG = new Configuration()
            .withMinNodeSize(3)
            .withMinSplitSize(7)
            .withHomogenityThreshold(75)
            .withMaxDepth(3)
            .withSplittingStrategy(new Chi2SplittingStrategy());

    protected TrainingData trainingData;
    protected int idColumnIndex, targetColumnIndex;
    protected Set<Integer> dataColumnIndexes;
    protected Configuration config;

    /**
     * @param trainingData      data to be used for training/building the tree
     * @param idColumnIndex     index of the column which uniquely identifies rows in the dataset
     * @param targetColumnIndex index of the column whose values must be predicted by the decision tree
     * @param dataColumnIndexes optional; an array of indexes of columns to use for splitting when building the
     *                          decision tree. Columns which are not included in this array will never appear as
     *                          split nodes in the resulting tree. If {@code null}, all columns are conisdered included.
     *                          MUST NOT include the ID and target columns and MUST have at least one element if given.
     * @param config            configuration parameters for the tree builder; see {@link Configuration}
     */
    public DecisionTreeBuilder(TrainingData trainingData, int idColumnIndex, int targetColumnIndex, @Nullable Collection<Integer> dataColumnIndexes, Configuration config) {
        this.trainingData = trainingData;
        this.idColumnIndex = idColumnIndex;
        this.targetColumnIndex = targetColumnIndex;
        this.config = config;
        if (dataColumnIndexes != null) {
            this.dataColumnIndexes = new TreeSet<>(dataColumnIndexes);
        } else {
            this.dataColumnIndexes = trainingData.getColumns().stream()
                    .filter(c -> c.getIndex() != idColumnIndex && c.getIndex() != targetColumnIndex)
                    .map(Column::getIndex)
                    .collect(Collectors.toSet());
        }

        checkInput(trainingData.getColumns(), idColumnIndex, targetColumnIndex, this.dataColumnIndexes);
        if (!trainingData.getColumn(targetColumnIndex).isDiscrete()) {
            throw new UnsupportedOperationException("DecisionTreeBuilder does not handle regression trees (for now)!");
        }
    }

    private static void checkInput(List<Column> data, int idColumnIndex, int targetColumnIndex, @Nullable Set<Integer> dataColumnIndexes) throws IllegalArgumentException {
        if (data == null || data.size() == 0) {
            throw new IllegalArgumentException("data set must not be empty");
        }
        if (idColumnIndex < 0 || idColumnIndex >= data.size()) {
            throw new IllegalArgumentException("id column out of range");
        }
        if (targetColumnIndex < 0 || targetColumnIndex >= data.size()) {
            throw new IllegalArgumentException("target column out of range");
        }
        for (Column column : data) {
            if (column.size() != data.get(0).size()) {
                throw new IllegalArgumentException("all columns in the data set must be of the same size");
            }
        }
        if (dataColumnIndexes != null) {
            if (dataColumnIndexes.size() == 0) {
                throw new IllegalArgumentException("dataColumnIndexes is empty");
            }

            for (int col : dataColumnIndexes) {
                if (col < 0 || col >= data.size()) {
                    throw new IllegalArgumentException("data column index out of range");
                }
            }
        }
    }

    /**
     * Given a data set, find the best (most discriminating) way to split it.
     * Optionally take a set of column indexes to ignore.
     *
     * @return the best split, or null if no meaningful split can be made
     */
    private Optional<SplitScore> chooseBestSplit(TrainingData data, @NonNull Set<Integer> ignoring) {
        ArrayList<SplitScore> splits = new ArrayList<>(data.size());
        // we go through all unused columns and calculate the split score for each
        for (int columnIndex = 0; columnIndex < data.size(); ++columnIndex) {
            if (dataColumnIndexes.contains(columnIndex) && !ignoring.contains(columnIndex)) {
                final Split split;
                Column column = data.getColumn(columnIndex);
                int[] columnData;
                if (!column.isDiscrete()) {
                    // a column of continuous values may need to be transformed into classes before splitting metrics
                    // can be applied to it; the algorithm used is a simple binary split on a chosen threshold value
                    double splitValue = chooseSplitValue(column);
                    split = new ThresholdSplit(splitValue);
                    columnData = Arrays.stream(column.asDouble()).mapToInt(val -> val < splitValue ? 0 : 1).toArray();
                } else {
                    split = new DiscreteSplit();
                    columnData = column.asClasses();
                }

                double score = config.getSplittingStrategy().evaluateSplit(data.getColumn(targetColumnIndex).asClasses(), columnData);
                splits.add(new SplitScore(columnIndex, split, score));
            }
        }

        if (splits.isEmpty()) {
            System.out.println("No more splits to make!");
            return Optional.empty();
        }

        return Optional.of(config.getSplittingStrategy().chooseBestSplit(splits, sc -> sc.score));
    }

    /**
     * Given a data set and a splitting method, generate the child nodes and branches resulting from the split.
     *
     * @return mapping of branch labels to child nodes, or null if {@code split} cannot be applied
     * @see DecisionTree#getBranchLabel()
     */
    private Optional<LinkedHashMap<String, DecisionTree>> getChildren(TrainingData data, SplitScore split, int depth) {
        LinkedHashMap<String, DecisionTree> children = new LinkedHashMap<>();
        Optional<? extends Map<String, TrainingData>> partitions = split.split.applySplit(data, split.columnIndex);

        if (partitions.isPresent()) {
            for (Map.Entry<String, TrainingData> entry : partitions.get().entrySet()) {
                String branchLabel = entry.getKey();
                TrainingData childData = entry.getValue();
                if (childData.size() < config.getMinNodeSize()) {
                    System.out.println("skipping split of " + split.columnIndex + " because branch " + branchLabel +
                            " has too few results (" + childData.size() + " out of " + config.getMinNodeSize() + ")");
                    break;
                }
                children.put(branchLabel, buildTree(childData, depth + 1));
            }
        }

        return children.size() >= 2 ? Optional.of(children) : Optional.empty();
    }

    /**
     * Check if an array is homogeneous - i.e. if there is any single value
     * which satisfies {@link Configuration#getHomogenityThreshold()}.
     *
     * @param column array to check for homogenity
     * @return true if {@code column} is homogeneous
     */
    private boolean isHomogeneous(int[] column) {
        return Arrays.stream(column).boxed()
                .collect(Collectors.groupingBy(v -> v, Collectors.counting()))
                .entrySet().stream()
                .anyMatch(e -> (e.getValue() * 100.0 / column.length) > config.getHomogenityThreshold());
    }

    /**
     * Build the decision tree based on the provided {@link TrainingData}.
     *
     * @return the built decision tree
     */
    public DecisionTree buildTree() {
        return buildTree(trainingData, 0);
    }

    /**
     * Generate a decision tree from some input data.
     *
     * @param data  input data as a column-indexed matrix, i.e. an array of columns - data[i][j] is the `j`th row
     *              of column `i`, and `data.length` is the number of columns
     * @param depth the current depth in the tree generation process; the root starts at depth 0
     * @return the built decision tree
     */
    protected DecisionTree buildTree(TrainingData data, int depth) {
        Column targetColumn = data.getColumn(targetColumnIndex);
        TreeSet<Integer> triedColumns = new TreeSet<>();

        // we only try a split if the dataset is not too small and if it is not already homogeneous
        if (data.size() >= config.getMinSplitSize() && !isHomogeneous(targetColumn.asClasses()) && depth <= config.getMaxDepth()) {
            Optional<SplitScore> chosenSplit;
            while ((chosenSplit = chooseBestSplit(data, triedColumns)).isPresent()) {
                SplitScore split = chosenSplit.get();
                String columnName = trainingData.getColumn(split.columnIndex).getHeader();
                DecisionTree result = new DecisionTree(split.columnIndex, columnName, data);
                Optional<? extends Map<String, DecisionTree>> children = getChildren(data, split, depth);

                if (children.isPresent()) {
                    result.setChildren(children.get());
                    return result;
                }

                // if getChildren returned null (split could not be applied), keep trying other columns
                triedColumns.add(split.columnIndex);
            }
        }

        // if no split can be made, return a terminal node
        return new DecisionTree(targetColumnIndex, targetColumn.getHeader(), data);
    }

    /**
     * For a continous-valued column that needs to be reduced to a discrete-valued column,
     * choose an appropriate value to split on.
     * <p>
     * A column is reduced by choosing a value to split on, and classing the column in two categories -
     * <em>below</em> the split value and <em>equal or above</em> the split value.
     *
     * @param column continuous column
     * @return desired split value
     */
    protected double chooseSplitValue(Column column) {
        // TODO: pending change to double
        return Indicateurs.getSplitValue(Arrays.stream(column.asDouble())
                .mapToInt(val -> (int) Math.round(val))
                .toArray()
        );
    }

    public static class Configuration implements Cloneable {
        private int minNodeSize;
        private int minSplitSize;
        private int homogenityThreshold;
        private int maxDepth;
        private SplittingStrategy splittingStrategy;

        private static int requirePercentage(int value) {
            if (value < 0 || value > 100) {
                throw new IllegalArgumentException("should be a percentage value (0..100)");
            }

            return value;
        }

        private static int requireNonNegative(int value) {
            if (value < 0) {
                throw new IllegalArgumentException("should be a non-negative value");
            }

            return value;
        }

        public Configuration clone() {
            try {
                return (Configuration) super.clone();
            } catch (CloneNotSupportedException e) {
                throw new RuntimeException(e);
            }
        }

        /**
         * The minimum number of individuals in the training set that must be matched by a node. If a split would
         * generate a node that would drop below this minimum size, the split will not be made.
         * <p>
         * In effect, this ensures that {@link DecisionTree#getPopulationCount()} will return at least
         * {@code minNodeSize} for every node in a tree generated with this configuration.
         * <p>
         * <b>NOTE:</b> minNodeSize should be less than {@link #getMinSplitSize()}.
         */
        public int getMinNodeSize() {
            return minNodeSize;
        }

        /**
         * @see #getMinNodeSize()
         */
        public Configuration withMinNodeSize(int minNodeSize) {
            Configuration result = this.clone();
            result.minNodeSize = requireNonNegative(minNodeSize);
            return result;
        }

        /**
         * Get the minimum node size for which a split will be attempted. If the node matches less rows that
         * this setting, it will not be split fruther. This differs from {@link #getMinNodeSize()} in that it imposes
         * a minimum node size <em>before</em> splitting, while the latter prevents a split if it results in
         * nodes that are too small (<em>after</em> splitting).
         * <p>
         * <b>NOTE:</b> {@link #getMinNodeSize()} should be less than minSplitSize.
         */
        public int getMinSplitSize() {
            return minSplitSize;
        }

        /**
         * @see #getMinSplitSize()
         */
        public Configuration withMinSplitSize(int minSplitSize) {
            Configuration result = this.clone();
            result.minSplitSize = requireNonNegative(minSplitSize);
            return result;
        }

        /**
         * The minimum percentage of rows required to be of the same value in the target column for a
         * group to be considered homogeneous. A dataset that satisfies this homogenity threshold will not be split
         * any further, even if it would satisfy {@link #getMinNodeSize()}.
         * <p>
         * Valid range: [0, 100]
         */
        public int getHomogenityThreshold() {
            return homogenityThreshold;
        }

        /**
         * @see #getHomogenityThreshold()
         */
        public Configuration withHomogenityThreshold(int homogenityThreshold) {
            Configuration result = this.clone();
            result.homogenityThreshold = requirePercentage(homogenityThreshold);
            return result;
        }

        /**
         * Limits the maximum height of the generated tree. A maxDepth of 0 will
         * generate a single terminal node containing all rows.
         */
        public int getMaxDepth() {
            return maxDepth;
        }

        /**
         * See {@link #getMaxDepth()}.
         */
        public Configuration withMaxDepth(int maxDepth) {
            Configuration result = this.clone();
            result.maxDepth = requireNonNegative(maxDepth);
            return result;
        }

        /**
         * Scoring function for deciding how to create split nodes when building the decision tree.
         */
        public SplittingStrategy getSplittingStrategy() {
            return splittingStrategy;
        }

        /**
         * @see #getSplittingStrategy()
         */
        public Configuration withSplittingStrategy(SplittingStrategy splittingStrategy) {
            Configuration result = this.clone();
            result.splittingStrategy = Objects.requireNonNull(splittingStrategy);
            return result;
        }
    }

    /**
     * Stores the {@link #score} of splitting column #{@link #columnIndex} using {@link #split}, as calculated by
     * {@link SplittingStrategy#evaluateSplit(int[], int[])}.
     */
    protected static class SplitScore {
        public final int columnIndex;
        public final Split split;
        public final double score;

        public SplitScore(int columnIndex, Split split, double score) {
            this.columnIndex = columnIndex;
            this.split = split;
            this.score = score;
        }
    }
}
