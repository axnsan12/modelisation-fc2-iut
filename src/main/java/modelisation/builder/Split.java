package modelisation.builder;

import modelisation.data.Column;
import modelisation.data.TrainingData;
import modelisation.tree.DecisionTree;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

/**
 * Implements a data partitioning strategy - given a matrix of data and a pivot column, group the rows into
 * partitions according to some criteria applied to the column.
 */
public abstract class Split {
    /**
     * Split the given dataset into at least two partitions.
     * <p>
     * This implementation uses {@link #getBranchLabels(Column)} and {@link #getBranchIndex(Column, int)}
     * to determine the partition of each element.
     *
     * @param data             input data as a column-indexed matrix, i.e. an array of columns - data[i][j] is
     *                         the `j`th row of column `i`, and `data.length` is the number of columns
     * @param splitColumnIndex index of the column which contains the values to split against
     * @return a mapping of branch labels to split dataset partition, or null if the split cannot be applied
     * @see DecisionTree#getBranchLabel()
     */
    public Optional<LinkedHashMap<String, TrainingData>> applySplit(TrainingData data, int splitColumnIndex) {
        Column splitColumn = data.getColumn(splitColumnIndex);
        int rowCount = data.size();


        String[] branchLabels = getBranchLabels(splitColumn);
        int leafCount = branchLabels.length;
        if (leafCount < 2) {
            return Optional.empty();
        }

        // for each resulting leaf partition, determine which rows it should contain
        List<ArrayList<Integer>> leafRowIndexes = Stream.generate(ArrayList<Integer>::new)
                .limit(leafCount)
                .collect(Collectors.toList());
        for (int rowIndex = 0; rowIndex < rowCount; ++rowIndex) {
            int targetBranchIndex = getBranchIndex(splitColumn, rowIndex);
            leafRowIndexes.get(targetBranchIndex).add(rowIndex);
        }

        return Optional.of(IntStream.range(0, leafCount).collect(
                LinkedHashMap<String, TrainingData>::new,
                (map, idx) -> map.put(branchLabels[idx], data.partition(leafRowIndexes.get(idx))),
                LinkedHashMap::putAll
        ));
    }

    /**
     * Get the set of labels to be applied to the partitions resulting from this split.
     *
     * @param column the values of the target column of this split
     * @see DecisionTree#getBranchLabel()
     */
    protected abstract String[] getBranchLabels(Column column);

    /**
     * Given a value in the column, return the index of the branch it should be partitioned into.
     * <p>
     * This method will always be called (multiple times) after {@link #getBranchLabels(Column)} and the
     * returned index must correspond to the appropiate label returned by that function.
     *
     * @param column the column being split
     * @param index  index of the value to be assigned to a partition
     * @return branch index
     */
    protected abstract int getBranchIndex(Column column, int index);
}
