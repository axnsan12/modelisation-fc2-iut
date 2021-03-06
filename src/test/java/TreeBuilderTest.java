import modelisation.builder.DecisionTreeBuilder;
import modelisation.builder.strategies.EntropyReduction;
import modelisation.data.TrainingData;
import modelisation.io.CsvDataReader;
import modelisation.io.GraphvizTreeWriter;
import modelisation.tree.DecisionTree;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class TreeBuilderTest {
    public static void main(String[] args) {
        try {
            TrainingData titanic = new CsvDataReader("datasets/train.csv").read();

            DecisionTreeBuilder.Configuration cfg = DecisionTreeBuilder.DEFAULT_CONFIG
                    .withSplittingStrategy(new EntropyReduction());
            int idColumnIndex = 2;
            int targetColumnIndex = 0;

            List<Integer> dataColumns = Stream.of("pclass", "sex", "age", "sibsp", "parch", "fare", "embarked")
                    .map(name -> titanic.getColumn(name).getIndex())
                    .collect(Collectors.toList());

            DecisionTreeBuilder treeBuilder = new DecisionTreeBuilder(titanic, idColumnIndex, targetColumnIndex, dataColumns, cfg);
            DecisionTree tree = treeBuilder.buildTree();
            GraphvizTreeWriter treeWriter = new GraphvizTreeWriter(new File("graphviz/test3.png"));
            treeWriter.write(tree, targetColumnIndex);
            System.out.println(tree);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
