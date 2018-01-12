package modelisation.tree;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class DecisionTree {
    public static class Node {
        private Node parent;
        private ArrayList<Node> children;

        public Node getParent() {
            return parent;
        }

        public List<Node> getChildren() {
            return Collections.unmodifiableList(children);
        }
    }
}
