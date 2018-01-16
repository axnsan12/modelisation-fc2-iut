package modelisation.builder.strategies;

import org.eclipse.jdt.annotation.NonNull;

import java.util.Collection;
import java.util.Random;
import java.util.function.ToDoubleFunction;

public class RandomSplittingStrategy implements SplittingStrategy {
    private final Random random;

    public RandomSplittingStrategy(Random random) {
        this.random = random;
    }

    @NonNull
    @Override
    public <S> S chooseBestSplit(Collection<? extends S> splits, ToDoubleFunction<S> score) {
        return SplittingStrategy.max(splits, score);
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
