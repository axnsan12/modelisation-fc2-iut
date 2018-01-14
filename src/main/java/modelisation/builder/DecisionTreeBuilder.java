package modelisation.builder;

import modelisation.Indicateurs;
import modelisation.tree.DecisionTree;
import org.eclipse.jdt.annotation.Nullable;

import java.util.*;
import java.util.stream.Collectors;

public class DecisionTreeBuilder implements Cloneable {
    protected int idColumnIndex, targetColumnIndex;
    protected Configuration config;

    public static class Configuration {
        private int continuousMinLines;
        private int discreteMaxPercentage;
        private int minNodeSize;
        private int minSplitSize;
        private int homogenityThreshold;

        public Configuration clone() {
            try {
                return (Configuration) super.clone();
            } catch (CloneNotSupportedException e) {
                throw new RuntimeException(e);
            }
        }

        /**
         * The minimum number of lines required in order to treat values as continuous. If there are less than this
         * number of lines in the data set, values will always be treated as discrete.
         */
        public int getContinuousMinLines() {
            return continuousMinLines;
        }

        /**
         * @see #getContinuousMinLines()
         */
        public Configuration withContinuousMinLines(int continuousMinLines) {
            Configuration result = this.clone();
            result.continuousMinLines = requireNonNegative(continuousMinLines);
            return result;
        }

        /**
         * The maximum percentage of distinct values (out of the total number of lines), after which the column
         * is considered to be a continuous value. This only applies if the total number of lines is greater than
         * or requal to {@link #getContinuousMinLines()}.
         * <p>
         * Valid range: [0, 100]
         */
        public int getDiscreteMaxPercentage() {
            return discreteMaxPercentage;
        }

        /**
         * @see #getDiscreteMaxPercentage()
         */
        public Configuration withDiscreteMaxPercentage(int discreteMaxPercentage) {
            Configuration result = this.clone();
            result.discreteMaxPercentage = requirePercentage(discreteMaxPercentage);
            return result;
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
         * group to be considered homogenous. A dataset that satisfies this homogenity threshold will not be split
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
    }

    public static final Configuration DEFAULT_CONFIG = new Configuration()
            .withContinuousMinLines(10)
            .withDiscreteMaxPercentage(20)
            .withMinNodeSize(3)
            .withMinSplitSize(7)
            .withHomogenityThreshold(75);

    public DecisionTreeBuilder(int idColumnIndex, int targetColumnIndex, Configuration config) {
        this.idColumnIndex = idColumnIndex;
        this.targetColumnIndex = targetColumnIndex;
        this.config = config;
    }

    public DecisionTreeBuilder(int idColumnIndex, int targetColumnIndex) {
        this(idColumnIndex, targetColumnIndex, DEFAULT_CONFIG);
    }

    /**
     * Stores the {@link #score} of splitting column #{@link #columnIndex} using {@link #split}, as calculated by
     * {@link #evaluateSplit(int[], int[])}.
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

    /**
     * Given a data set, find the best (most discriminating) way to split it.
     *
     * @return the best split, or null if no meaningful split can be made
     */
    @Nullable
    private SplitScore chooseBestSplit(int[][] data) {
        ArrayList<SplitScore> splits = new ArrayList<>(data.length);
        // we go through all unused columns and calculate the split score for each
        for (int columnIndex = 0; columnIndex < data.length; ++columnIndex) {
            if (columnIndex != idColumnIndex && columnIndex != targetColumnIndex) {
                final Split split;
                int[] column = data[columnIndex];
                if (Indicateurs.shouldDiscretize(data[columnIndex], config.getContinuousMinLines(), config.getDiscreteMaxPercentage())) {
                    double splitValue = Indicateurs.getSplitValue(data[columnIndex]);
                    column = Indicateurs.faireEcarts(column, splitValue, config.getContinuousMinLines(), config.getDiscreteMaxPercentage());
                    split = new ThresholdSplit(splitValue);
                } else {
                    split = new DiscreteSplit();
                }
                double score = evaluateSplit(data[targetColumnIndex], column);
                splits.add(new SplitScore(columnIndex, split, score));
            }
        }

        if (splits.isEmpty()) {
            System.out.println("No more splits to make!");
            return null;
        }

        return chooseBestSplit(splits);
    }

    /**
     * Given a data set and a splitting method, generate the child nodes and branches resulting from the split.
     *
     * @return mapping of branch labels to child nodes, or null if {@code split} cannot be applied
     * @see DecisionTree#getBranchLabel()
     */
    @Nullable
    private LinkedHashMap<String, DecisionTree> getChildren(int[][] data, SplitScore split) {
        LinkedHashMap<String, DecisionTree> children = new LinkedHashMap<>();
        for (Map.Entry<String, int[][]> entry : split.split.applySplit(data, split.columnIndex).entrySet()) {
            String branchLabel = entry.getKey();
            int[][] childData = entry.getValue();
            if (childData[0].length < config.getMinNodeSize()) {
                System.out.println("skipping split of " + split.columnIndex + " because branch " + branchLabel +
                        " has too few results (" + childData[0].length + " vs needed " + config.getMinNodeSize() + ")");
                return null;
            }
            children.put(branchLabel, buildTree(childData));
        }

        return children;
    }

    /**
     * Check if an array is homogenous - i.e. if there is any single value
     * which satisfies {@link Configuration#getHomogenityThreshold()}.
     *
     * @param column array to check for homogenity
     * @return true if {@code column} is homogenous
     */
    private boolean isHomogenous(int[] column) {
        return Arrays.stream(column).boxed()
                .collect(Collectors.groupingBy(v -> v, Collectors.counting()))
                .entrySet().stream()
                .anyMatch(e -> (e.getValue() * 100.0 / column.length) > config.getHomogenityThreshold());
    }

    /**
     * Generate a decision tree from some input data.
     *
     * @param data input data as a column-indexed matrix, i.e. an array of columns - data[i][j] is the `j`th row
     *             of column `i`, and `data.length` is the number of columns
     * @return the built decision tree
     */
    public DecisionTree buildTree(int[][] data) {
        checkInputData(data, idColumnIndex, targetColumnIndex);
        int[] targetColumn = data[targetColumnIndex];

        // we only try a split if the dataset is not too small and if it is not already homogenous
        if (data[0].length >= config.getMinSplitSize() && !isHomogenous(targetColumn)) {
            SplitScore bestSplit = chooseBestSplit(data);
            if (bestSplit != null) {
                DecisionTree result = new DecisionTree(bestSplit.columnIndex, data[0].length);
                LinkedHashMap<String, DecisionTree> children = getChildren(data, bestSplit);

                if (children != null) {
                    result.setChildren(children);
                    return result;
                }
            }
        }

        // if no split can be made, return a terminal node
        return new DecisionTree(targetColumnIndex, data[0].length);
    }

    /**
     * Choose the best split from a collection of splits scored by {@link #evaluateSplit(int[], int[])}.
     * <p>
     * The score returned by {@link #evaluateSplit(int[], int[])} is accesible via {@link SplitScore#score}.
     *
     * @param splits an array of possible splits to be compared
     * @return the best scoring split
     */
    protected SplitScore chooseBestSplit(Collection<SplitScore> splits) {
        return splits.stream().min(Comparator.comparingDouble(s -> s.score))
                .orElseThrow(() -> new IllegalArgumentException("empty splits array"));
    }

    /**
     * Return a score for the possible prediction accuracy gains against `targetColumn`
     * by introducing a splitter node on the given `splitColumn`.
     *
     * @param targetColumn column containing the values to classify/predict
     * @param splitColumn  column containing discrete values to make a split decision on
     * @return a split score as interpreted by {@link #chooseBestSplit(Collection)}
     */
    protected double evaluateSplit(int[] targetColumn, int[] splitColumn) {
        return Indicateurs.gini(targetColumn, splitColumn);
    }

    private static void checkInputData(int[][] data, int idColumnIndex, int targetColumnIndex) throws IllegalArgumentException {
        if (data == null || data.length == 0) {
            throw new IllegalArgumentException("data set must not be empty");
        }
        if (idColumnIndex < 0 || idColumnIndex >= data.length) {
            throw new IllegalArgumentException("id column out of range");
        }
        if (targetColumnIndex < 0 || targetColumnIndex >= data.length) {
            throw new IllegalArgumentException("target column out of range");
        }
        for (int[] column : data) {
            if (column.length != data[0].length) {
                throw new IllegalArgumentException("all columns in the data set must be of the same size");
            }
        }
    }
}
