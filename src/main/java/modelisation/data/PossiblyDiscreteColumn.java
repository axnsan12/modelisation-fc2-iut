package modelisation.data;

import java.util.ArrayList;
import java.util.HashMap;

/*internal*/ abstract class PossiblyDiscreteColumn extends BaseColumn {
    /**
     * For each index in data (as passed to {@link #classifyData(Object[])}), this array holds the respective
     * value's class ID. {@link #classLabels} can be used to recover the original value in String form.
     */
    protected int[] classedData;

    /**
     * Array that maps values in {@link #classedData} back to the original value in String form. The size of this
     * array is equal to {@link #classCount()}.
     *
     * @see #classifyData(Object[])
     */
    protected String[] classLabels;

    /**
     * @param index      {@link #index}
     * @param header     {@link #header}
     * @param continuity {@link #setContinuity(Continuity)}
     */
    PossiblyDiscreteColumn(int index, String header, Continuity continuity) {
        super(index, header, continuity);
    }

    /**
     * Populate {@link #classedData} and {@link #classLabels} from the given data
     * set by assigning a class ID to each distinct value.
     * <p>
     * For example, a column with values {@code [A, B, B, A, C]} would be mapped
     * to {@code classedData = [0, 1, 1, 0, 2]} and {@code classLabels = [A, B, C]}
     *
     * @param data discrete data
     * @param <S>  type of data values; must properly support .equals() and .toString()
     */
    protected <S> void classifyData(S[] data) {
        classedData = new int[data.length];
        HashMap<S, Integer> valueClasses = new HashMap<>();
        ArrayList<String> classLabels = new ArrayList<>();
        for (int i = 0; i < data.length; ++i) {
            Integer vclass = valueClasses.get(data[i]);
            if (vclass == null) {
                vclass = classLabels.size();
                classLabels.add(String.valueOf(data[i]));
                valueClasses.put(data[i], vclass);
            }
            classedData[i] = vclass;
        }

        this.classLabels = classLabels.toArray(new String[classLabels.size()]);
    }

    /**
     * Return the item at the given index as a string, taking {@link #classLabels} into account.
     * Assumes that {@link #classifyData(Object[])} was called.
     *
     * @param index row index
     * @return value as string
     */
    public String getValueAsString(int index) {
        if (!isDiscrete() || classedData == null || classLabels == null) {
            throw new IllegalStateException("PossiblyDiscreteColumn::getValueAsString can only " +
                    "be called on a discrete column, after calling ::classifyData");
        }
        int value = classedData[index];
        return classLabels[value];
    }

    @Override
    protected int getClassCount() {
        if (!isDiscrete() || classedData == null || classLabels == null) {
            throw new IllegalStateException("PossiblyDiscreteColumn::getClassCount can only " +
                    "be called on a discrete column, after calling ::classifyData");
        }
        return classLabels.length;
    }
}
