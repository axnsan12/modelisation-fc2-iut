package modelisation.data;

import java.util.List;

public interface Column {
    /**
     * Zero-based index of the column in the training data set.
     */
    int getIndex();

    /**
     * The column's associated header/name.
     */
    String getHeader();

    /**
     * @return true if this column is treated as discrete/categorical, false if it is treated as continuous-valued
     */
    boolean isDiscrete();

    /**
     * Check if the continuity of this column is ambiguous or not.
     * <p>
     * For example, a column with real-numbered values can not be treated as discrete, while a column with
     * non-numerical string values (e.g. male/female) can never be treated as a continuous, numerical variable.
     * <p>
     * Meanwhile, a column with just integer values could be interpreted both as a continuous
     * (e.g. age in integral years) and as a discrete (e.g. a 0/1 boolean column) variable.
     *
     * @return true if the column can be interpreted both as discrete and as continuous
     */
    boolean canSetContinuity();

    /**
     * Set the continuity nature of this column, as returned by {@link #isDiscrete()}.
     * <p>
     * This method MUST NOT be called if {@link #canSetContinuity()} returns false.
     *
     * @param continuity the new countinuity treatment to be applied
     */
    void setContinuity(Column.Continuity continuity);

    /**
     * Get the column values as real numbers. Can only be used if {@link #isDiscrete()} returns false.
     *
     * @return values as real numbers
     */
    double[] asDouble();

    /**
     * Get the class/partition of each value in the column. The return value is an array of integers in the range
     * {@code [0,N)}, where N is {@link #classCount()}, the total number of classes the column values can be in.
     * Can only be used if {@link #isDiscrete()} returns true.
     *
     * @return row classes
     * @see #classCount()
     */
    int[] asClasses();

    /**
     * Get the number of distnict classes this column's values are divided into. It is guaranteed that the array
     * returned by {@link #asClasses()} contains **at most** this many distinct values, and that every value in said
     * array is strictly smaller than this value.
     * <p>
     * Can only be used if {@link #isDiscrete()} returns true.
     *
     * @return row classes
     * @see #asClasses()
     */
    int classCount();

    /**
     * Return the label for the given class of a discrete column. Can only be used if {@link #isDiscrete()} returns true.
     *
     * @param classId the class ID in [0...N)
     * @return class name/label
     * @see #asClasses()
     * @see #classCount()
     */
    String classLabel(int classId);

    /**
     * @return count of valuet in the column
     */
    default int size() {
        if (isDiscrete()) {
            return asClasses().length;
        } else {
            return asDouble().length;
        }
    }

    /**
     * Return the string representation of the item at the given index.
     *
     * @param index row index
     * @return value as string
     */
    String getValueAsString(int index);

    /**
     * Return the value at the given index as a number. Can only be called if {@link #isDiscrete()} is false.
     *
     * @param index row index
     * @return value as number
     */
    Number getValueAsNumber(int index);

    /**
     * Return a new column containing only the rows specified by {@code keepIndexes}.
     *
     * @param keepIndexes array of row indexes to keep
     * @return the new column
     */
    Column partial(int[] keepIndexes);

    /**
     * Return a new column containing only the rows specified by {@code keepIndexes}.
     *
     * @param keepIndexes array of row indexes to keep
     * @return the new column
     */
    default Column partial(List<Integer> keepIndexes) {
        return partial(keepIndexes.stream().mapToInt(Integer::intValue).toArray());
    }

    /**
     * Enum that describes a column's nature - continuous-valued vs discrete-valued.
     */
    enum Continuity {
        DISCRETE, CONTINUOUS
    }
}
