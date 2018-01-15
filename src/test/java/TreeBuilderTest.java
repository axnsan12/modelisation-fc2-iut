import modelisation.TrainingData;
import modelisation.builder.DecisionTreeBuilder;
import modelisation.builder.strategies.RandomSplittingStrategy;
import modelisation.io.CsvDataReader;
import modelisation.tree.DecisionTree;

import java.io.IOException;
import java.util.Random;

public class TreeBuilderTest {
    public static void main(String[] args) {
        try {
            TrainingData titanic = new CsvDataReader("datasets/train.csv").read();

            DecisionTreeBuilder.Configuration cfg = DecisionTreeBuilder.DEFAULT_CONFIG
                    .withDiscreteMaxPercentage(0)
                    .withContinuousMinLines(10)
                    .withSplittingStrategy(new RandomSplittingStrategy(new Random(42)));
            int idColumnIndex = 2;
            int targetColumnIndex = 0;

            DecisionTreeBuilder treeBuilder = new DecisionTreeBuilder(titanic, idColumnIndex, targetColumnIndex, cfg);
            DecisionTree tree = treeBuilder.buildTree();
            System.out.println(tree);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
