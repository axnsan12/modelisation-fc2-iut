import modelisation.TrainingData;
import modelisation.builder.DecisionTreeBuilder;
import modelisation.io.CsvDataReader;
import modelisation.tree.DecisionTree;

import java.io.IOException;

public class TreeBuilderTest {
    public static void main(String[] args) {
        try {
            TrainingData titanic = new CsvDataReader("datasets/train.csv").read();
            DecisionTreeBuilder treeBuilder = new DecisionTreeBuilder(titanic, 2, 0);
            DecisionTree tree = treeBuilder.buildTree();
            System.out.println(tree);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
