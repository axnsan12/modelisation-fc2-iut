package modelisation.builder.strategies;

import modelisation.builder.DecisionTreeBuilder;

import java.util.Collection;
import java.util.function.ToDoubleFunction;

/**
 * Responsible for implementing algorithms to choose the best way to split a given dataset
 * in order to predict a target column. This class is used together with {@link DecisionTreeBuilder}
 * to train decision (i.e. classification/regression) trees on some given dataset.
 */
public interface SplittingStrategy {
    /**
     * Choose the best split from a collection of splits scored by {@link #evaluateSplit(int[], int[])}.
     * <p>
     * The score returned by {@link #evaluateSplit(int[], int[])} is accesible via {@code score(S)}.
     *
     * @param splits an array of possible splits to be compared
     * @param score  functor to extract the score from a split
     * @param <S>    type of split
     * @return the best scoring split
     */
    <S> S chooseBestSplit(Collection<? extends S> splits, ToDoubleFunction<S> score);

    /**
     * Return a score for the possible prediction accuracy gains against `targetColumn`
     * by introducing a splitter node on the given `splitColumn`.
     *
     * @param targetColumn column containing the values to classify/predict
     * @param splitColumn  column containing discrete values to make a split decision on
     * @return a split score as interpreted by {@link #chooseBestSplit(Collection, ToDoubleFunction)}
     */
    double evaluateSplit(int[] targetColumn, int[] splitColumn);

    /**
     * Human-readable name for this strategy.
     *
     * @return human readable name
     */
    String getName();
}
