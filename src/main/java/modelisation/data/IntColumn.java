package modelisation.data;

import java.util.Arrays;

/*internal*/ class IntColumn extends PossiblyDiscreteColumn {
    /**
     * A variable with count less than this will always be considered discrete.
     */
    public static final int CONTINUITY_GUESS_MIN_SAMPLE = 4;
    /**
     * A variable with more distinct values than this will always be considered continuous.
     */
    public static final int CONTINUITY_GUESS_MAX_DISTINCT = 10;
    /**
     * A variable whose count of distinct values exceeds this percentage out of the total count will
     * always be considered continuous. Percentage range is {@code [0.0,1.0]}.
     */
    public static final double CONTINUITY_GUESS_MAX_PERCENTAGE = 0.5;
    private final int[] data;
    /**
     * Caching variable used when {@link #asDouble()} is needed. Holds the values in {@link #data} converted to double.
     */
    private double[] doubleData;

    /*internal*/ IntColumn(int index, String header, int[] data) {
        super(index, header, guessContinuity(data));
        this.data = data;
    }

    /**
     * Try to guess if a variable is discrete or continuous based on the distribution of its values.
     *
     * @param data column data
     * @return continuity guess
     * @see #CONTINUITY_GUESS_MIN_SAMPLE
     * @see #CONTINUITY_GUESS_MAX_DISTINCT
     * @see #CONTINUITY_GUESS_MAX_PERCENTAGE
     */
    private static Continuity guessContinuity(int[] data) {
        if (data.length < CONTINUITY_GUESS_MIN_SAMPLE) {
            return Continuity.DISCRETE;
        }
        long distinctValues = Arrays.stream(data).distinct().count();
        double distinctPercentage = (double) distinctValues / data.length;
        if (distinctValues > CONTINUITY_GUESS_MAX_DISTINCT || distinctPercentage > CONTINUITY_GUESS_MAX_PERCENTAGE) {
            return Continuity.CONTINUOUS;
        }
        return Continuity.DISCRETE;
    }

    @Override
    public boolean canSetContinuity() {
        return true;
    }

    @Override
    protected double[] getAsDouble() {
        if (doubleData == null) {
            doubleData = Arrays.stream(data).mapToDouble(val -> val).toArray();
        }
        return doubleData;
    }

    @Override
    protected int[] getAsClasses() {
        if (classedData == null) {
            classifyData(Arrays.stream(data).boxed().toArray(Integer[]::new));
        }
        return data;
    }

    @Override
    public String getValueAsString(int index) {
        if (isDiscrete()) {
            getAsClasses(); // populate classes and labels
            return super.getValueAsString(index); // call PossiblyDiscreteColumn::getValueAsString
        } else {
            return String.valueOf(data[index]);
        }
    }

    @Override
    public Number getValueAsNumber(int index) {
        if (isDiscrete()) {
            throw new UnsupportedOperationException();
        }
        return data[index];
    }
}
