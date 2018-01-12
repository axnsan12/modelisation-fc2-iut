package modelisation.builder;

import modelisation.Indicateurs;
import modelisation.tree.DecisionTree;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;

public class DecisionTreeBuilder implements Cloneable {
    protected int idColumnIndex, targetColumnIndex;
    protected Configuration config;

    public static class Configuration
    {
        private int continuousMinLines;
        private int discreteMaxPercentage;
        private int minNodeSize;

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
         * <p>The maximum percentage of distinct values (out of the total number of lines), after which the column
         * is considered to be a continuous value. This only applies if the total number of lines is greater than
         * or requal to {@link #getContinuousMinLines()}.</p>
         *
         * Valid range: [0, 100]
         */
        public int getDiscreteMaxPercentage() {
            return discreteMaxPercentage;
        }

        /**
         * <p></p>The minimum number of individuals in the training set that must be matched by a node. If a split would
         * generate a node that would drop below this minimum size, the split will not be made.</p>
         *
         * In effect, this ensures that {@link DecisionTree#getPopulationCount()} will return at least
         * {@code minNodeSize} for every node in a tree generated with this configuration.
         */
        public int getMinNodeSize() {
            return minNodeSize;
        }

        /**
         * See {@link #getContinuousMinLines()}.
         */
        public Configuration withContinuousMinLines(int continuousMinLines) {
            Configuration result = this.clone();
            result.continuousMinLines = continuousMinLines;
            return result;
        }

        /**
         * See {@link #getDiscreteMaxPercentage()}.
         */
        public Configuration withDiscreteMaxPercentage(int discreteMaxPercentage) {
            Configuration result = this.clone();
            result.discreteMaxPercentage = discreteMaxPercentage;
            return result;
        }

        /**
         * See {@link #getMinNodeSize()}.
         */
        public Configuration withMinNodeSize(int minNodeSize) {
            Configuration result = this.clone();
            result.minNodeSize = minNodeSize;
            return result;
        }
    }

    public static final Configuration DEFAULT_CONFIG = new Configuration()
            .withContinuousMinLines(10)
            .withDiscreteMaxPercentage(20)
            .withMinNodeSize(4);

    public DecisionTreeBuilder(int idColumnIndex, int targetColumnIndex, Configuration config) {
        this.idColumnIndex = idColumnIndex;
        this.targetColumnIndex = targetColumnIndex;
        this.config = config;
    }

    public DecisionTreeBuilder(int idColumnIndex, int targetColumnIndex) {
        this(idColumnIndex, targetColumnIndex, DEFAULT_CONFIG);
    }

    protected static class SplitScore
    {
        /**
         *
         */
        public final int columnIndex;

        /**
         *
         */
        public final double splitValue;

        /**
         *
         */
        public final double score;

        public SplitScore(int columnIndex, double splitValue, double score) {
            this.columnIndex = columnIndex;
            this.splitValue = splitValue;
            this.score = score;
        }
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

        ArrayList<SplitScore> splits = new ArrayList<>(data.length);
        // we go through all unused columns and calculate the split score for each
        for (int columnIndex = 0; columnIndex < data.length; ++columnIndex) {
            if (columnIndex != idColumnIndex && columnIndex != targetColumnIndex) {
                double splitValue = Double.NaN;
                if (Indicateurs.shouldDiscretize(data[columnIndex], config.getContinuousMinLines(), config.getDiscreteMaxPercentage())) {
                    splitValue = Indicateurs.getSplitValue(data[columnIndex]);
                }
                double score = evaluateSplit(data[targetColumnIndex], data[columnIndex]);
                splits.add(new SplitScore(columnIndex, splitValue, score));
            }
        }

        if (splits.isEmpty()) {
            System.out.println("No more splits to make!");
            return null;
        }

        SplitScore split = chooseBestSplit(splits);
        DecisionTree result = new DecisionTree(split.columnIndex, data[0].length);
        return result;
    }

    /**
     * Choose the best split from a collection of splits scored by {@link #evaluateSplit(int[], int[])}.
     *
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
     * @param splitColumn column containing discrete values to make a split decision on
     * @return a split score as interpreted by {@link #chooseBestSplit(Collection)}
     */
    protected double evaluateSplit(int[] targetColumn, int[] splitColumn) {
        return Indicateurs.gini(targetColumn, splitColumn);
    }

    private static void checkInputData(int[][] data, int idColumnIndex, int targetColumnIndex) throws IllegalArgumentException {
        if (data == null || data.length == 0) {
            throw new IllegalArgumentException("data set must not be empty");
        }
        int columnCount = data[0].length;
        if (idColumnIndex < 0 || idColumnIndex >= columnCount) {
            throw new IllegalArgumentException("id column out of range");
        }
        if (targetColumnIndex < 0 || targetColumnIndex >= columnCount) {
            throw new IllegalArgumentException("target column out of range");
        }
        for (int[] line : data) {
            if (line.length != columnCount) {
                throw new IllegalArgumentException("all lines in the data set must be of the same length");
            }
        }
    }
}
