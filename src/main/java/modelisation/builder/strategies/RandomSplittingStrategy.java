package modelisation.builder.strategies;

import java.util.Collection;
import java.util.Comparator;
import java.util.Random;
import java.util.function.ToDoubleFunction;

public class RandomSplittingStrategy implements SplittingStrategy {
    private final Random random;

    public RandomSplittingStrategy(Random random) {
        this.random = random;
    }


    @Override
    public <S> S chooseBestSplit(Collection<? extends S> splits, ToDoubleFunction<S> score) {
        return splits.stream().max(Comparator.comparingDouble(score))
                .orElseThrow(() -> new IllegalArgumentException("empty splits array"));
    }

    @Override
    public double evaluateSplit(int[] targetColumn, int[] splitColumn) {
        return random.nextDouble();
    }

    @Override
    public String getName() {
        return "random";
    }
}
