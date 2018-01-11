package modelisation.builder;

import modelisation.Indicateurs;
import modelisation.tree.DecisionTree;

public class DecisionTreeBuilder {
    protected int idColumnIndex, targetColumnIndex;

    public DecisionTreeBuilder(int idColumnIndex, int targetColumnIndex) {
        this.idColumnIndex = idColumnIndex;
        this.targetColumnIndex = targetColumnIndex;
    }

    /**
     * Generate a decision tree from some input data.
     *
     * @param data input data as a column matrix, i.e. an array of columns - data[i][j] is the `j`th row of column `i`
     * @return the built decision tree
     */
    public DecisionTree buildTree(int[][] data) {
        checkInputData(data, idColumnIndex, targetColumnIndex);

        for (int columnIndex = 0; columnIndex < data.length; ++columnIndex) {
            if (columnIndex != idColumnIndex && columnIndex != targetColumnIndex) {
                
            }
        }
        return null;
    }


    protected double evaluateSplit(int[][] data, int columnIndex) {
        return Indicateurs.gini(data[idColumnIndex], data[columnIndex]);
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
