package modelisation.builder.strategies;

import modelisation.Indicateurs;

import java.util.Collection;
import java.util.function.ToDoubleFunction;

public class EntropyReduction extends BaseClassificationSplittingStrategy {
    @Override
    public <S> S chooseBestSplit(Collection<? extends S> splits, ToDoubleFunction<S> score) {
        return SplittingStrategy.min(splits, score);
    }

    @Override
    protected double evaluateSplit(int[] targetColumn, int[] splitColumn) {
        return Indicateurs.entropie(targetColumn, splitColumn);
    }

    @Override
    public String getName() {
        return "Entropie";
    }
}
