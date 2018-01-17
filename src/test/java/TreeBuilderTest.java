import modelisation.data.TrainingData;
import modelisation.builder.DecisionTreeBuilder;
import modelisation.builder.strategies.RandomSplittingStrategy;
import modelisation.io.CsvDataReader;
import modelisation.tree.DecisionTree;

import java.io.IOException;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class TreeBuilderTest {
    public static void main(String[] args) {
        try {
            TrainingData titanic = new CsvDataReader("datasets/train.csv").read();

            DecisionTreeBuilder.Configuration cfg = DecisionTreeBuilder.DEFAULT_CONFIG
                    .withSplittingStrategy(new RandomSplittingStrategy(new Random(420)));
            int idColumnIndex = 2;
            int targetColumnIndex = 0;

            List<Integer> dataColumns = Stream.of("pclass", "sex", "age", "sibsp", "parch", "fare", "embarked")
                    .map(name -> titanic.getColumn(name).getIndex())
                    .collect(Collectors.toList());

            DecisionTreeBuilder treeBuilder = new DecisionTreeBuilder(titanic, idColumnIndex, targetColumnIndex, dataColumns, cfg);
            DecisionTree tree = treeBuilder.buildTree();
            System.out.println(tree);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
