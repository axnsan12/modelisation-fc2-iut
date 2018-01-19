package modelisation.builder.strategies;

import modelisation.Stat;
import modelisation.data.Column;
import modelisation.data.SplitColumn;

/*internal*/ abstract class BaseClassificationSplittingStrategy implements SplittingStrategy {
    @Override
    public final double evaluateSplit(Column targetColumn, Column splitColumn) {
        return evaluateSplit(targetColumn.asClasses(), splitColumn.asClasses());
    }

    @Override
    public double evaluateSplit(Column targetColumn, Column splitColumn, double splitValue) {
        return evaluateSplit(targetColumn, SplitColumn.fromColumn(splitColumn, splitValue));
    }

    protected abstract double evaluateSplit(int[] targetColumn, int[] splitColumn);

    @Override
    public double chooseSplitValue(Column targetColumn, Column splitColumn) {
        return Stat.median(splitColumn.asDouble());
    }

    @Override
    public boolean supportsTarget(Column targetColumn) {
        return targetColumn.isDiscrete();
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        return obj != null && getClass().equals(obj.getClass());
    }
}
