package modelisation.io;

import guru.nidi.graphviz.attribute.Color;
import guru.nidi.graphviz.attribute.Shape;
import guru.nidi.graphviz.attribute.Style;
import guru.nidi.graphviz.engine.Format;
import guru.nidi.graphviz.engine.Graphviz;
import guru.nidi.graphviz.model.Graph;
import guru.nidi.graphviz.model.Label;
import guru.nidi.graphviz.model.Node;
import modelisation.data.Column;
import modelisation.data.TrainingData;
import modelisation.tree.DecisionTree;

import java.io.File;
import java.io.IOException;

import static guru.nidi.graphviz.model.Factory.*;

public class GraphvizTreeWriter {
    private Node getNode(DecisionTree tree, int targetColumnIndex, int totalPopulation) {
        TrainingData data = tree.getPopulation();
        int totalPercentage = (int) (100.0 * data.size() / totalPopulation);

        String label = "<table border=\"0\" cellspacing=\"0\" cellpadding=\"1\">";
        String columnName = tree.getColumnName();
        if (targetColumnIndex == tree.getColumnIndex()) {
            columnName = "<b>" + columnName + "</b>";
        }
        label += "<tr><td>" + columnName + "</td></tr>";
        label += String.format("<tr><td><b>pop:</b>   %1$d (%2$d%%)</td></tr>", data.size(), totalPercentage);

        Column targetColumn = data.getColumn(targetColumnIndex);
        int[] classSizes = new int[targetColumn.classCount()];
        for (int item : targetColumn.asClasses()) {
            classSizes[item] += 1;
        }

        label += "<tr><td><table color=\"black\" cellspacing=\"0\" cellpadding=\"2\">";
        for (int classId = 0; classId < classSizes.length; ++classId) {
            if (classSizes[classId] == 0) {
                continue;
            }

            String classLabel = targetColumn.classLabel(classId);
            int classPercentage = (int) (100.0 * classSizes[classId] / data.size());
            label += (String.format("<tr><td>%1$s</td><td align=\"left\">%2$4d (%3$d%%)</td></tr>", classLabel, classSizes[classId], classPercentage));
        }
        label += "</table></td></tr>";
        label += "</table>";

        Node result = node(String.valueOf(System.identityHashCode(tree))).with(Shape.RECTANGLE, Label.html(label));

        if (targetColumnIndex == tree.getColumnIndex()) {
            result = result.with(Color.DARKGREEN, Color.LIGHTGREY.fill(), Style.lineWidth(2));
        }

        for (DecisionTree child : tree.getChildren()) {
            Node childNode = getNode(child, targetColumnIndex, totalPopulation);
            result = result.link(to(childNode).with(Label.of(child.getBranchLabel())));
        }

        return result;
    }

    public void write(DecisionTree tree, int targetColumnIndex) throws IOException {
        Graph g = graph("DecisionTree").directed().with(getNode(tree, targetColumnIndex, tree.getPopulation().size()));

        Graphviz.fromGraph(g).render(Format.XDOT).toFile(new File("graphviz/ex2.dot"));
        Graphviz.fromGraph(g).render(Format.PNG).toFile(new File("graphviz/ex2.png"));
    }
}
