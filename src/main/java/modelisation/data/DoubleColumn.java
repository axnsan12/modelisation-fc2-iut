package modelisation.data;

/*internal*/ class DoubleColumn extends BaseColumn {
    private final double[] data;

    /*internal*/ DoubleColumn(int index, String header, double[] data) {
        super(index, header, Continuity.CONTINUOUS);
        this.data = data;
    }

    @Override
    public boolean canSetContinuity() {
        return false;
    }

    @Override
    protected double[] getAsDouble() {
        return data;
    }

    @Override
    protected int[] getAsClasses() {
        throw new UnsupportedOperationException();
    }

    @Override
    protected int getClassCount() {
        throw new UnsupportedOperationException();
    }

    @Override
    protected String getClassLabel(int classId) {
        throw new UnsupportedOperationException();
    }

    @Override
    public String getValueAsString(int index) {
        return String.valueOf(data[index]);
    }

    @Override
    public Number getValueAsNumber(int index) {
        return data[index];
    }
}
