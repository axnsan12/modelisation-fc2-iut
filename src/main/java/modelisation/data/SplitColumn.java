package modelisation.data;

import java.util.Arrays;

public class SplitColumn extends BaseColumn {
    private int[] data;

    /*internal*/ SplitColumn(int index, String header, double[] data, double splitValue) {
        super(index, header, Continuity.DISCRETE);
        this.data = Arrays.stream(data).mapToInt(val -> val < splitValue ? 0 : 1).toArray();
    }

    /**
     * Create a split column from the given continuous column. The resulting column will have two classes, one
     * for values strictly lower than splitThreshold and the second for the rest.
     *
     * @param column     source column; {@link #isDiscrete()} must return false
     * @param splitValue threshold value to split the column around
     * @return discretized column
     * @see modelisation.builder.ThresholdSplit
     */
    public static SplitColumn fromColumn(Column column, double splitValue) {
        return new SplitColumn(column.getIndex(), column.getHeader() + " ~" + splitValue, column.asDouble(), splitValue);
    }

    @Override
    public int getIndex() {
        throw new UnsupportedOperationException();
    }

    @Override
    protected double[] getAsDouble() {
        throw new UnsupportedOperationException();
    }

    @Override
    protected int[] getAsClasses() {
        return data;
    }

    @Override
    protected int getClassCount() {
        return 2;
    }

    @Override
    protected String getClassLabel(int classId) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean canSetContinuity() {
        return false;
    }

    @Override
    public String getValueAsString(int index) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Number getValueAsNumber(int index) {
        throw new UnsupportedOperationException();
    }
}
