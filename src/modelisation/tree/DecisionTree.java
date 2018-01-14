package modelisation.tree;

import com.sun.istack.internal.NotNull;
import com.sun.istack.internal.Nullable;

import java.util.*;

public class DecisionTree {
    private final int columnIndex;
    private DecisionTree parent;
    private int populationCount;
    private String branchLabel;
    private final ArrayList<DecisionTree> children = new ArrayList<>();

    /**
     * Create a new DecisionTree node. <br/>
     *
     * @param columnIndex     the index of the column this node is splitting on
     * @param populationCount number of trainng set rows matched by this node
     * @see #getColumnIndex()
     * @see #getPopulationCount()
     */
    public DecisionTree(int columnIndex, int populationCount) {
        this.columnIndex = columnIndex;
        this.populationCount = populationCount;
    }

    /**
     * Attach children to this node. Trying to attach children to a node which already
     * has children will result in an {@link IllegalStateException}.
     *
     * @param children a map of branch labels to child nodes
     * @see #getBranchLabel()
     */
    public void setChildren(@NotNull HashMap<String, DecisionTree> children) {
        if (!this.children.isEmpty()) {
            throw new IllegalStateException("this node already has children attached");
        }
        if (children.isEmpty()) {
            throw new IllegalArgumentException("empty child list is not allowed");
        }
        for (HashMap.Entry<String, DecisionTree> entry : children.entrySet()) {
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
    private void setParent(@NotNull DecisionTree parent, @NotNull String branchLabel) {
        if (this.parent != null) {
            throw new IllegalStateException("this node is already attached to a parent");
        }
        this.parent = Objects.requireNonNull(parent);
        this.branchLabel = Objects.requireNonNull(branchLabel);
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
     * two children with branch labels <code>"&lt;40"</code> and <code>"&gt;=40"</code>. <br/>
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
     * The number of lines from the training set that were matched by the path from the root
     * of the tree up to this node.
     *
     * @return number of trainng set rows matched by this node
     */
    public int getPopulationCount() {
        return populationCount;
    }
}
