package modelisation.builder.strategies;

import modelisation.Indicateurs;
import modelisation.data.Column;

import java.util.Collection;
import java.util.function.ToDoubleFunction;

public class EntropySplittingStrategy implements SplittingStrategy {
    @Override
    public <S> S chooseBestSplit(Collection<? extends S> splits, ToDoubleFunction<S> score) {
        return SplittingStrategy.min(splits, score);
    }

    @Override
    public double evaluateSplit(Column targetColumn, Column splitColumn) {
        return Indicateurs.entropie(targetColumn.asClasses(), splitColumn.asClasses());
    }

    @Override
    public String getName() {
        return "entropy";
    }
}
