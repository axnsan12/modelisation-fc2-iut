package modelisation.builder.strategies;

import modelisation.Indicateurs;

import java.util.Collection;
import java.util.function.ToDoubleFunction;

public class ClassificationError extends BaseClassificationSplittingStrategy {
    @Override
    public <S> S chooseBestSplit(Collection<? extends S> splits, ToDoubleFunction<S> score) {
        return SplittingStrategy.min(splits, score);
    }

    @Override
    protected double evaluateSplit(int[] targetColumn, int[] splitColumn) {
        return Indicateurs.erreurClass(targetColumn, splitColumn);
    }

    @Override
    public String getName() {
        return "Erreur de classement";
    }
}
