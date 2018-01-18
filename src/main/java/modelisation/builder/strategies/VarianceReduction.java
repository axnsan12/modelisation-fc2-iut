package modelisation.builder.strategies;

import modelisation.FonctionsRegression;
import modelisation.data.Column;

import java.util.Arrays;
import java.util.Collection;
import java.util.function.ToDoubleFunction;

public class VarianceReduction implements SplittingStrategy {
    @Override
    public <S> S chooseBestSplit(Collection<? extends S> splits, ToDoubleFunction<S> score) {
        return SplittingStrategy.min(splits, score);
    }

    @Override
    public double evaluateSplit(Column targetColumn, Column splitColumn) {
        double[] target = targetColumn.asDouble();
        int[] split = splitColumn.asClasses();

        return Arrays.stream(split)
                .distinct()
                .mapToDouble(clsid -> FonctionsRegression.variance(target, idx -> split[idx] == clsid))
                .sum();
    }

    @Override
    public double evaluateSplit(Column targetColumn, Column splitColumn, double splitValue) {
        return FonctionsRegression.d(splitValue, targetColumn.asDouble(), splitColumn.asDouble());
    }

    @Override
    public double chooseSplitValue(Column targetColumn, Column splitColumn) {
        double[] target = targetColumn.asDouble(), split = splitColumn.asDouble();
        int meil = FonctionsRegression.meilleureVarianceXi(target, split, 2);
        return FonctionsRegression.C(meil, split);
    }

    @Override
    public String getName() {
        return "variance reduction";
    }

    @Override
    public boolean supportsTarget(Column targetColumn) {
        return !targetColumn.isDiscrete();
    }
}
