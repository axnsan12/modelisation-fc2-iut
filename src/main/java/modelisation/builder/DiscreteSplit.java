package modelisation.builder;

import modelisation.data.Column;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * Splits a given column into N partitions, where N is the number of distinct values in the column. Each partition will
 * have all the elements with the same value.
 */
public class DiscreteSplit extends Split {
    /**
     * Array mapping column values to their branch index.
     */
    private Map<Integer, Integer> valueToBranchIndex = new TreeMap<>();

    /**
     * Array mapping branch index to branch labels.
     */
    private String[] branchLabels;


    @Override
    protected String[] getBranchLabels(Column column) {
        int[] values = column.asClasses();
        // create a map of class IDs (column values) to labels; iterating over row index is necessary because
        // column.getValueAsString needs the row index, not the value itself
        Map<Integer, String> valueLabels = IntStream.range(0, values.length).collect(
                HashMap::new,
                (map, idx) -> map.computeIfAbsent(values[idx], val -> column.getValueAsString(idx)),
                HashMap::putAll
        );

        // sort the (value, label) pairs alphabetically by value
        List<Map.Entry<Integer, String>> sortedValueLabels = valueLabels.entrySet().stream()
                .sorted(Comparator.comparing(Map.Entry::getValue))
                .collect(Collectors.toList());

        // tie up the column value to branch index & branch index to branch label mappings
        branchLabels = new String[sortedValueLabels.size()];
        for (int branchIndex = 0; branchIndex < sortedValueLabels.size(); ++branchIndex) {
            Map.Entry<Integer, String> entry = sortedValueLabels.get(branchIndex);
            branchLabels[branchIndex] = entry.getValue();
            valueToBranchIndex.put(entry.getKey(), branchIndex);
        }
        return branchLabels;
    }

    @Override
    protected int getBranchIndex(Column column, int index) {
        int[] values = column.asClasses();
        return valueToBranchIndex.get(values[index]);
    }
}
