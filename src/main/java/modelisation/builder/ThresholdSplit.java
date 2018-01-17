package modelisation.builder;

import modelisation.data.Column;

/**
 * Splits a given continous-valued column into two partitions by comparing the values against the given threshold.
 * The first partition will contain elements that are strictly lower than threshold, while the second partition will
 * contain elements that are greater than or equal to threshold.
 */
public class ThresholdSplit extends Split {
    private final double threshold;

    public ThresholdSplit(double threshold) {
        this.threshold = threshold;
    }

    @Override
    protected String[] getBranchLabels(Column column) {
        // < threshold goes into leaf 0, >= threshold goes into leaf 1
        return new String[]{"<" + threshold, ">=" + threshold};
    }

    @Override
    protected int getBranchIndex(Column column, int index) {
        double[] data = column.asDouble();
        return data[index] < threshold ? 0 : 1;
    }
}
