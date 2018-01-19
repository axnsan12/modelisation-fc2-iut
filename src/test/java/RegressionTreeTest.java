import modelisation.builder.DecisionTreeBuilder;
import modelisation.builder.strategies.VarianceReduction;
import modelisation.data.TrainingData;
import modelisation.io.CsvDataReader;
import modelisation.io.GraphvizTreeWriter;
import modelisation.tree.DecisionTree;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class RegressionTreeTest {
    public static void main(String[] args) {
        try {
            TrainingData car93 = new CsvDataReader("datasets/93cars.dat.csv").read();

            DecisionTreeBuilder.Configuration cfg = DecisionTreeBuilder.DEFAULT_CONFIG
                    .withMaxDepth(3)
                    .withMinSplitSize(4)
                    .withMinNodeSize(1)
                    .withSplittingStrategy(new VarianceReduction());

            int idColumnIndex = 0;
            int targetColumnIndex = 3;

            List<Integer> dataColumns = Stream.of("horsepower", "wheelbase")
                    .map(name -> car93.getColumn(name).getIndex())
                    .collect(Collectors.toList());

            DecisionTreeBuilder treeBuilder = new DecisionTreeBuilder(car93, idColumnIndex, targetColumnIndex, dataColumns, cfg);
            DecisionTree tree = treeBuilder.buildTree();
            GraphvizTreeWriter treeWriter = new GraphvizTreeWriter(new File("graphviz/test-regression.png"));
            treeWriter.write(tree, targetColumnIndex);
            System.out.println(tree);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
