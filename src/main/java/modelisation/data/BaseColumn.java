package modelisation.data;

/**
 * Data class for holding information about a column.
 */
/*internal*/ abstract class BaseColumn implements Column {
    /**
     * @see #getIndex()
     */
    private final int index;

    /**
     * @see #getHeader()
     */
    private final String header;

    /**
     * @see #setContinuity(Continuity)
     */
    private Continuity continuity;

    /**
     * @param index      {@link #getIndex()}
     * @param header     {@link #getHeader()}
     * @param continuity {@link #setContinuity(Continuity)}
     */
    /*internal*/ BaseColumn(int index, String header, Continuity continuity) {
        this.index = index;
        this.header = header;
        this.continuity = continuity;
    }

    /**
     * Zero-based index of the column in the training data set.
     */
    @Override
    public int getIndex() {
        return index;
    }

    /**
     * The column's associated header/name.
     */
    @Override
    public String getHeader() {
        return header;
    }

    /**
     * @return true if this column is treated as discrete/categorical, false if it is treated as continuous-valued
     */
    @Override
    public final boolean isDiscrete() {
        return continuity == Continuity.DISCRETE;
    }

    @Override
    public void setContinuity(Continuity continuity) {
        if (!canSetContinuity()) {
            throw new IllegalStateException("this column type does not support changing its continuity");
        }
        this.continuity = continuity;
    }

    @Override
    public final double[] asDouble() {
        if (isDiscrete()) {
            throw new IllegalStateException("asDouble can only be called on columns which are interpreted as continous variables");
        }
        return getAsDouble();
    }

    /**
     * Implementation for {@link #asDouble()}.
     */
    protected abstract double[] getAsDouble();

    @Override
    public final int[] asClasses() {
        if (!isDiscrete()) {
            throw new IllegalStateException("asClasses can only be called on columns which are interpreted as discrete variables");
        }
        return getAsClasses();
    }

    /**
     * Implementation for {@link #asClasses()}.
     */
    protected abstract int[] getAsClasses();

    @Override
    public final int classCount() {
        if (!isDiscrete()) {
            throw new IllegalStateException("classCount can only be called on columns which are interpreted as discrete variables");
        }
        return getClassCount();
    }

    /**
     * Implementation for {@link #classCount()}
     */
    protected abstract int getClassCount();

    @Override
    public Column partial(int[] keepIndexes) {
        return new ColumnView(this, keepIndexes);
    }
}
