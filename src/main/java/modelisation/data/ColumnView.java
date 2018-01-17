package modelisation.data;

import java.util.Arrays;
import java.util.List;

/*internal*/ class ColumnView implements Column {
    private final Column realColumn;
    private final int[] indexMapping;

    // caching variables
    private double[] mappedDoubleData;
    private int[] mappedClassedData;

    /*internal*/ ColumnView(Column realColumn, int[] keepIndexes) {
        this.realColumn = realColumn;
        this.indexMapping = keepIndexes;
    }

    @Override
    public int getIndex() {
        return realColumn.getIndex();
    }

    @Override
    public String getHeader() {
        return realColumn.getHeader();
    }

    @Override
    public boolean isDiscrete() {
        return realColumn.isDiscrete();
    }

    @Override
    public boolean canSetContinuity() {
        return false;
    }

    @Override
    public void setContinuity(Continuity continuity) {
        throw new UnsupportedOperationException("this is a read-only view");
    }

    @Override
    public double[] asDouble() {
        if (mappedDoubleData == null) {
            double[] realData = realColumn.asDouble();
            this.mappedDoubleData = Arrays.stream(indexMapping).mapToDouble(idx -> realData[idx]).toArray();
        }
        return mappedDoubleData;
    }

    @Override
    public int[] asClasses() {
        if (mappedClassedData == null) {
            int[] realData = realColumn.asClasses();
            this.mappedClassedData = Arrays.stream(indexMapping).map(idx -> realData[idx]).toArray();
        }
        return mappedClassedData;
    }

    @Override
    public int size() {
        return indexMapping.length;
    }

    @Override
    public String getValueAsString(int index) {
        return realColumn.getValueAsString(indexMapping[index]);
    }

    @Override
    public Number getValueAsNumber(int index) {
        return realColumn.getValueAsNumber(index);
    }

    @Override
    public Column partial(int[] keepIndexes) {
        return realColumn.partial(Arrays.stream(keepIndexes).map(idx -> indexMapping[idx]).toArray());
    }

    @Override
    public Column partial(List<Integer> keepIndexes) {
        return realColumn.partial(keepIndexes.stream().mapToInt(idx -> indexMapping[idx]).toArray());
    }
}
