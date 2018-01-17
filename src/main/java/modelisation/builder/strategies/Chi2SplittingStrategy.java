package modelisation.builder.strategies;

import modelisation.Indicateurs;
import modelisation.data.Column;
import org.eclipse.jdt.annotation.NonNull;

import java.util.Collection;
import java.util.function.ToDoubleFunction;

public class Chi2SplittingStrategy implements SplittingStrategy {
    @NonNull
    @Override
    public <S> S chooseBestSplit(Collection<? extends S> splits, ToDoubleFunction<S> score) {
        return SplittingStrategy.min(splits, score);
    }

    @Override
    public double evaluateSplit(Column targetColumn, Column splitColumn) {
        return Indicateurs.chi2(targetColumn.asClasses(), splitColumn.asClasses());
    }

    @Override
    public String getName() {
        return "chi^2";
    }
}
