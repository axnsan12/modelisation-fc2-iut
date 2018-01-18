package modelisation.builder.strategies;

import modelisation.builder.DecisionTreeBuilder;
import modelisation.data.Column;

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
    <S> S chooseBestSplit(Collection<? extends S> splits, ToDoubleFunction<S> score);

    /**
     * Return a score for the possible prediction accuracy gains against `targetColumn`
     * by introducing a splitter node on the given `splitColumn`.
     *
     * @param targetColumn column containing the values to classify/predict
     * @param splitColumn  column containing discrete values to make a split decision on;
     *                     {@link Column#isDiscrete() splitColumn.isDiscrete()} must return true
     * @return a split score as interpreted by {@link #chooseBestSplit(Collection, ToDoubleFunction)}
     * @see #evaluateSplit(Column, Column, double)
     */
    double evaluateSplit(Column targetColumn, Column splitColumn);

    /**
     * Return a score for the possible prediction accuracy gains against `targetColumn`
     * by introducing a splitter node on the given `splitColumn` at the threshold value {@code splitValue}.
     *
     * @param targetColumn column containing the values to classify/predict
     * @param splitColumn  column containing discrete values to make a split decision on;
     *                     {@link Column#isDiscrete() splitColumn.isDiscrete()} must return false
     * @return a split score as interpreted by {@link #chooseBestSplit(Collection, ToDoubleFunction)}
     * @see #evaluateSplit(Column, Column)
     */
    double evaluateSplit(Column targetColumn, Column splitColumn, double splitValue);

    /**
     * For a continous-valued column that needs to be reduced to a discrete-valued column,
     * choose an appropriate value to split on.
     * <p>
     * A column is reduced by choosing a value to split on, and classing the column in two categories -
     * <em>below</em> the split value and <em>equal or above</em> the split value.
     *
     * @param targetColumn the column to be predicted
     * @param splitColumn  continuous column to be split;
     *                     {@link Column#isDiscrete() splitColumn.isDiscrete()} must return false
     * @return desired split value
     */
    double chooseSplitValue(Column targetColumn, Column splitColumn);

    /**
     * Human-readable name for this strategy.
     *
     * @return human readable name
     */
    String getName();

    /**
     * Return true if this splitting strategy works against the given column.
     *
     * @param targetColumn column to be predicted
     * @return true if {@code targetColumn} is supported
     */
    boolean supportsTarget(Column targetColumn);
}
