package modelisation.tree;


import modelisation.data.TrainingData;
import org.eclipse.jdt.annotation.NonNull;
import org.eclipse.jdt.annotation.Nullable;

import java.util.*;

public class DecisionTree {
    private final int columnIndex;
    private final String columnName;
    private DecisionTree parent;
    private final TrainingData population;
    private String branchLabel;
    private final ArrayList<DecisionTree> children = new ArrayList<>();

    /**
     * Create a new DecisionTree node.
     *
     * @param columnIndex the index of the column this node is splitting on
     * @param columnName  the name of the column this node is splitting on
     * @param population  trainng set partition matched by this node
     * @see #getColumnIndex()
     */
    public DecisionTree(int columnIndex, String columnName, TrainingData population) {
        this.columnIndex = columnIndex;
        this.columnName = columnName;
        this.population = population;
    }


    /**
     * Attach children to this node. Trying to attach children to a node which already
     * has children will result in an {@link IllegalStateException}.
     *
     * @param children a map of branch labels to child nodes
     * @see #getBranchLabel()
     */
    public void setChildren(@NonNull Map<String, DecisionTree> children) {
        if (!this.children.isEmpty()) {
            throw new IllegalStateException("this node already has children attached");
        }
        if (children.isEmpty()) {
            throw new IllegalArgumentException("empty child list is not allowed");
        }
        for (Map.Entry<String, DecisionTree> entry : children.entrySet()) {
            DecisionTree child = entry.getValue();
            String branchLabel = entry.getKey();
            child.setParent(this, branchLabel);
            this.children.add(child);
        }
    }

    /**
     * Attach this node to its parent node.
     *
     * @param parent      the
     * @param branchLabel this node's branch label
     * @see #getBranchLabel()
     */
    private void setParent(@NonNull DecisionTree parent, @NonNull String branchLabel) {
        if (this.parent != null) {
            throw new IllegalStateException("this node is already attached to a parent");
        }
        this.parent = Objects.requireNonNull(parent);
        this.branchLabel = Objects.requireNonNull(branchLabel);
    }

    /**
     * @return the name of the column this node is splitting on.
     * @see #getColumnIndex()
     */
    public String getColumnName() {
        return columnName;
    }

    /**
     * Each node in the decision tree splits the data set lines into two or more partitions based on some splitting
     * criteria applied to a certain column.
     * <p>
     * The same column can be encountered multiple times when traversing the tree from root to leaf, but this should
     * only happen if the column has continuous values.
     *
     * @return the index of the column this node is splitting on.
     */
    public int getColumnIndex() {
        return columnIndex;
    }

    /**
     * The partiotion label that was used to arrive at this node from its parent.
     * <p>
     * Example 1: if a node splits on column A with continous values between 10 and 100, it might have
     * two children with branch labels <code>"&lt;40"</code> and <code>"&gt;=40"</code>.
     * <p>
     * Example 2: a node that splits on column B with three possible values, `a`, `b`, `c`, it might have
     * three children, whose branch labels will be <code>"a", "b", "c"</code> respectively
     *
     * @return this node's branch label, or null if it is the root node
     */
    @Nullable
    public String getBranchLabel() {
        return branchLabel;
    }

    /**
     * @return this node's parent node, or null if it is the root node
     */
    @Nullable
    public DecisionTree getParent() {
        return parent;
    }

    /**
     * @return a list of 0 or more child nodes
     */
    public List<DecisionTree> getChildren() {
        return Collections.unmodifiableList(children);
    }

    /**
     * Get the training set partition that was matched into this node.
     */
    public TrainingData getPopulation() {
        return population;
    }
}
