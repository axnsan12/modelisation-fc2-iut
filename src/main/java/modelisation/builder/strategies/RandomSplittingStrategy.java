package modelisation.builder.strategies;

import modelisation.Stat;
import modelisation.data.Column;

import java.util.Collection;
import java.util.Random;
import java.util.function.ToDoubleFunction;

public class RandomSplittingStrategy implements SplittingStrategy {
    private final Random random;

    public RandomSplittingStrategy(Random random) {
        this.random = random;
    }

    @Override
    public <S> S chooseBestSplit(Collection<? extends S> splits, ToDoubleFunction<S> score) {
        return SplittingStrategy.max(splits, score);
    }

    @Override
    public double evaluateSplit(Column targetColumn, Column splitColumn) {
        return random.nextDouble();
    }

    @Override
    public double evaluateSplit(Column targetColumn, Column splitColumn, double splitValue) {
        return evaluateSplit(targetColumn, splitColumn);
    }

    @Override
    public double chooseSplitValue(Column targetColumn, Column splitColumn) {
        return Stat.median(splitColumn.asDouble());
    }

    @Override
    public String getName() {
        return "random";
    }

    @Override
    public boolean supportsTarget(Column targetColumn) {
        return true;
    }
}
