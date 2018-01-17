package modelisation.builder.strategies;

import modelisation.builder.DecisionTreeBuilder;
import modelisation.data.Column;
import org.eclipse.jdt.annotation.NonNull;

import java.util.Collection;
import java.util.Comparator;
import java.util.function.ToDoubleFunction;

/**
 * Responsible for implementing algorithms to choose the best way to split a given dataset
 * in order to predict a target column. This class is used together with {@link DecisionTreeBuilder}
 * to train decision (i.e. classification/regression) trees on some given dataset.
 */
public interface SplittingStrategy {
    /**
     * Shortcut for choosing the highest element from a collection according to some score.
     * Same signature as {@link #chooseBestSplit(Collection, ToDoubleFunction)}.
     */
    static <S> S max(Collection<? extends S> splits, ToDoubleFunction<S> scorer) {
        return splits.stream().max(Comparator.comparingDouble(scorer))
                .orElseThrow(() -> new IllegalArgumentException("empty splits array"));
    }

    /**
     * Shortcut for choosing the lowest element from a collection according to some score.
     * Same signature as {@link #chooseBestSplit(Collection, ToDoubleFunction)}.
     */
    static <S> S min(Collection<? extends S> splits, ToDoubleFunction<S> scorer) {
        return splits.stream().min(Comparator.comparingDouble(scorer))
                .orElseThrow(() -> new IllegalArgumentException("empty splits array"));
    }

    /**
     * Choose the best split from a collection of splits scored by {@link #evaluateSplit(Column, Column)}.
     * <p>
     * The score returned by {@link #evaluateSplit(Column, Column)} is accesible via {@code score(S)}.
     *
     * @param splits a collection of possible splits to be compared; must not be empty
     * @param score  functor to extract the score from a split
     * @param <S>    type of split
     * @return the best scoring split
     */
    @NonNull <S> S chooseBestSplit(Collection<? extends S> splits, ToDoubleFunction<S> score);

    /**
     * Return a score for the possible prediction accuracy gains against `targetColumn`
     * by introducing a splitter node on the given `splitColumn`.
     *
     * @param targetColumn column containing the values to classify/predict
     * @param splitColumn  column containing discrete values to make a split decision on
     * @return a split score as interpreted by {@link #chooseBestSplit(Collection, ToDoubleFunction)}
     */
    double evaluateSplit(Column targetColumn, Column splitColumn);

    /**
     * Human-readable name for this strategy.
     *
     * @return human readable name
     */
    String getName();
}
