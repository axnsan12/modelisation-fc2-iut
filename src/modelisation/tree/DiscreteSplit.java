package modelisation.tree;

import java.util.Arrays;

public class DiscreteSplit extends Split {
    private String[] branchLabels;

    private String valueToString(int value) {
        // TODO: map item values to strings from input file?
        return Integer.toString(value);
    }

    @Override
    protected String[] getBranchLabels(int[] column) {
        branchLabels = Arrays.stream(column)
                .distinct()
                .mapToObj(this::valueToString)
                .sorted()  // sort branches alphabetically by label
                .toArray(String[]::new);
        return branchLabels;
    }

    @Override
    protected int getBranchIndex(int value) {
        int index = Arrays.binarySearch(branchLabels, valueToString(value));
        if (index < 0) {
            throw new IllegalArgumentException("invalid value " + value + " (not in column)");
        }
        return index;
    }
}
