package modelisation.data;

/*internal*/ class StringColumn extends PossiblyDiscreteColumn {

    /*internal*/ StringColumn(int index, String header, String[] data) {
        super(index, header, Continuity.DISCRETE);
        classifyData(data);
    }

    @Override
    public boolean canSetContinuity() {
        return false;
    }

    @Override
    protected double[] getAsDouble() {
        throw new UnsupportedOperationException();
    }

    @Override
    protected int[] getAsClasses() {
        return classedData;
    }

    @Override
    public Number getValueAsNumber(int index) {
        throw new UnsupportedOperationException();
    }
}
