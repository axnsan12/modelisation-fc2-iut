package modelisation.builder.strategies;

import modelisation.Indicateurs;

import java.util.Collection;
import java.util.function.ToDoubleFunction;

public class ChiSquared extends BaseClassificationSplittingStrategy {
    @Override
    public <S> S chooseBestSplit(Collection<? extends S> splits, ToDoubleFunction<S> score) {
        return SplittingStrategy.max(splits, score);
    }

    @Override
    protected double evaluateSplit(int[] targetColumn, int[] splitColumn) {
        return Indicateurs.chi2(targetColumn, splitColumn);
    }

    @Override
    public String getName() {
        return "chi^2";
    }
}
