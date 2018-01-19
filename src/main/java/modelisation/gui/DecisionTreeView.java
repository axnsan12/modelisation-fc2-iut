package modelisation.gui;

import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import modelisation.tree.DecisionTree;

import java.util.stream.Collectors;

public class DecisionTreeView extends TreeView<String> {
    public DecisionTreeView() {
        super();

    }

    private TreeItem<String> getTreeItem(DecisionTree tree) {
        TreeItem<String> treeItem = new TreeItem<>("Arbre");
        treeItem.setExpanded(true);
        if (tree.getBranchLabel() != null) {
            treeItem.setValue(tree.getBranchLabel());
        }

        treeItem.getChildren().add(new TreeItem<>("pop: " + tree.getPopulation().size()));

        if (!tree.getChildren().isEmpty()) {
            TreeItem<String> splitNode = new TreeItem<>(tree.getColumnName());
            splitNode.setExpanded(true);
            splitNode.getChildren().addAll(tree.getChildren().stream()
                    .map(this::getTreeItem)
                    .collect(Collectors.toList())
            );
            treeItem.getChildren().add(splitNode);
        }

        return treeItem;
    }

    public void setTree(DecisionTree tree) {
        setRoot(getTreeItem(tree));
    }
}
