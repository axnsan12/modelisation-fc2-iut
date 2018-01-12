package modelisation.builder;

import modelisation.Indicateurs;
import modelisation.tree.DecisionTree;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;

public class DecisionTreeBuilder {
    protected int idColumnIndex, targetColumnIndex;

    public DecisionTreeBuilder(int idColumnIndex, int targetColumnIndex) {
        this.idColumnIndex = idColumnIndex;
        this.targetColumnIndex = targetColumnIndex;
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

        ArrayList<SplitScore> scores = new ArrayList<>(data.length);
        // we go through all unused columns and calculate the split score for each
        for (int columnIndex = 0; columnIndex < data.length; ++columnIndex) {
            if (columnIndex != idColumnIndex && columnIndex != targetColumnIndex) {
                final double splitValue;
                if (Indicateurs.shouldDiscretize(data[columnIndex])) {
                    splitValue = Indicateurs.getSplitValue(data[columnIndex]);
                }
                else {
                    splitValue = Double.NaN;
                }
                double score = evaluateSplit(data[targetColumnIndex], data[columnIndex]);
                scores.add(new SplitScore(columnIndex, splitValue, score));
            }
        }

        SplitScore split = chooseBestScore(scores);

        return null;
    }

    /**
     * Choose the best split from a collection of splits scored by {@link #evaluateSplit(int[], int[])}.
     *
     * The score returned by {@link #evaluateSplit(int[], int[])} is accesible via {@link SplitScore#score}.
     *
     * @param splits
     * @return
     */
    protected SplitScore chooseBestScore(Collection<SplitScore> splits) {
        return splits.stream().min(Comparator.comparingDouble(s -> s.score))
                .orElseThrow(() -> new IllegalArgumentException("empty splits array"));
    }

    /**
     * Return a score for the possible prediction accuracy gains against `targetColumn`
     * by introducing a splitter node on the given `splitColumn`.
     *
     * @param targetColumn column containing the values to classify/predict
     * @param splitColumn column containing discrete values to make a split decision on
     * @return a split score as interpreted by {@link #chooseBestScore(Collection)}
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
