package modelisation;

import java.util.Arrays;

/**
 * Static utility class for statistical functions.
 */
public class Stat {
    /**
     * Calculate the median value of an array.
     * <p>
     * The median of a set of numbers is defined as the value X for which
     * there are as many elements &lt; X as there are &gt; X.
     *
     * @return median value
     */
    public static double median(double[] array) {
        if (array.length == 0) {
            throw new IllegalArgumentException("empty array");
        }
        array = Arrays.copyOf(array, array.length);
        Arrays.sort(array);

        int midLow = (int) Math.round(Math.floor((array.length - 1.0) / 2.0));
        int midHigh = (int) Math.round(Math.ceil((array.length - 1.0) / 2.0));
        if (midLow == midHigh) {
            return array[midLow];
        } else {
            return (array[midLow] + array[midHigh]) / 2.0;
        }
    }

    /**
     * Round the given value to 1 decimal place
     *
     * @param val value
     * @return value rounded to 1 decimal place
     */
    public static double pretty(double val) {
        return val;
    }

    /**
     * Calculate the root of the mean squared error (RMSE) of an array in comparison with a predicted value.
     *
     * @param array values to predict
     * @param value predicted value
     * @return the root mean squared error
     */
    public static double rmse(double[] array, double value) {
        return Math.sqrt(Arrays.stream(array)
                .map(val -> (val - value) * (val - value))
                .average()
                .orElseThrow(() -> new IllegalArgumentException("empty array")));
    }
}
