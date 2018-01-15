package modelisation.builder.strategies;

import modelisation.Indicateurs;

import java.util.Collection;
import java.util.Comparator;
import java.util.function.ToDoubleFunction;

public class Chi2SplittingStrategy implements SplittingStrategy {
    @Override
    public <S> S chooseBestSplit(Collection<? extends S> splits, ToDoubleFunction<S> score) {
        return splits.stream().min(Comparator.comparingDouble(score))
                .orElseThrow(() -> new IllegalArgumentException("empty splits array"));
    }

    @Override
    public double evaluateSplit(int[] targetColumn, int[] splitColumn) {
        return Indicateurs.chi2(targetColumn, splitColumn);
    }

    @Override
    public String getName() {
        return "chi^2";
    }
}
