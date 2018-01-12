package modelisation.tree;

import java.util.LinkedHashMap;

public abstract class Split {
    /**
     * Split the given dataset into at least two partitions.
     *
     * @param data input data as a column-indexed matrix, i.e. an array of columns - data[i][j] is the `j`th row
     *             of column `i`, and `data.length` is the number of columns
     * @param columnIndex index of the column which contains the values to split against
     * @return a mapping of branch labels to split dataset partition; see {@link DecisionTree#getBranchLabel()}
     */
    public LinkedHashMap<String, int[][]> applySplit(int[][] data, int columnIndex) {
        int rowCount = data[0].length;
        String[] labels = getBranchLabels(data[columnIndex]);
        int leafCount = labels.length;
        // for each leaf, store the number of rows it will have into `leafSize`
        int[] leafSize = new int[leafCount];
        // for each row, store the index of the leaf it will be in after the split into `targetLeaf`
        int[] targetLeaf = new int[rowCount];

        for (int rowIndex = 0; rowIndex < rowCount; ++rowIndex) {
            int target = getBranchIndex(data[columnIndex][rowIndex]);
            leafSize[target] += 1;
            targetLeaf[rowIndex] = target;
        }

        // we now allocate `leafCount` column-indexed matrices, in the same format as `data`
        int[][][] leaves = new int[leafCount][][];
        for (int leafIndex = 0; leafIndex < leafCount; ++leafIndex) {
            leaves[leafIndex] = new int[data.length][];
            // each leaf will have the same number of columns as the input data, but only `leafSize[leafIndex]` rows
            for (int leafColumnIndex = 0; leafColumnIndex < data.length; ++leafColumnIndex) {
                leaves[leafIndex][leafColumnIndex] = new int[leafSize[leafIndex]];
                // leafSize will be re-used below to keep track of the first free row index for each leaf
                leafSize[leafIndex] = 0;
            }
        }

        for (int rowIndex = 0; rowIndex < rowCount; ++ rowIndex) {
            // copy each row from the input data into the appropriate leaf according to `targetLeaf[rowIndex]`
            int leafIndex = targetLeaf[rowIndex];
            int leafRowIndex = leafSize[leafIndex]++;
            for (int leafColumnIndex = 0; leafColumnIndex < data.length; ++leafColumnIndex) {
                leaves[leafIndex][leafColumnIndex][leafRowIndex] = data[columnIndex][rowIndex];
            }
        }

        LinkedHashMap<String, int[][]> result = new LinkedHashMap<>();
        for (int leafIndex = 0; leafIndex < leafCount; ++leafIndex) {
            result.put(labels[leafIndex], leaves[leafIndex]);
        }
        return result;
    }

    /**
     * Get the set of labels to be applied to the partitions resulting from this split. <br/>
     * See also: description of {@link DecisionTree#getBranchLabel()}.
     *
     * @param column the values of the target column of this split
     */
    protected abstract String[] getBranchLabels(int[] column);

    /**
     * <p>Given {@code value}, return the index of the branch it should be partitioned into.</p>
     *
     * <p>This method will always be called (multiple times) after {@link #getBranchLabels(int[])} and the
     * returned index must correspond to the appropiate label returned by that function.</p>
     *
     * @param value target value from the data set column
     * @return branch index
     */
    protected abstract int getBranchIndex(int value);
}
