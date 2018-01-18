package modelisation.builder;

import modelisation.data.Column;

import java.util.Arrays;
import java.util.Comparator;
import java.util.Map;
import java.util.TreeMap;

/**
 * Splits a given column into N partitions, where N is the number of distinct values in the column. Each partition will
 * have all the elements with the same value.
 */
public class DiscreteSplit extends Split {
    /**
     * Array mapping column values to their branch index.
     */
    private Map<Integer, Integer> classIdToBranchIndex = new TreeMap<>();

    /**
     * Array mapping branch index to branch labels.
     */
    private String[] branchLabels;


    @Override
    protected String[] getBranchLabels(Column column) {
        Integer[] classIds = Arrays.stream(column.asClasses())
                .distinct()
                .boxed().sorted(Comparator.comparing(column::classLabel))
                .toArray(Integer[]::new);

        branchLabels = new String[classIds.length];

        for (int branchIndex = 0; branchIndex < classIds.length; ++branchIndex) {
            classIdToBranchIndex.put(classIds[branchIndex], branchIndex);
            branchLabels[branchIndex] = column.classLabel(classIds[branchIndex]);
        }
        return branchLabels;
    }

    @Override
    protected int getBranchIndex(Column column, int index) {
        int[] values = column.asClasses();
        return classIdToBranchIndex.get(values[index]);
    }
}
