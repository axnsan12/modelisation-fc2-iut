package modelisation.io;

import guru.nidi.graphviz.attribute.Shape;
import guru.nidi.graphviz.engine.Format;
import guru.nidi.graphviz.engine.Graphviz;
import guru.nidi.graphviz.model.Graph;
import guru.nidi.graphviz.model.Label;
import guru.nidi.graphviz.model.Node;
import modelisation.tree.DecisionTree;

import java.io.File;
import java.io.IOException;

import static guru.nidi.graphviz.model.Factory.*;

public class GraphvizTreeWriter {

    private Node getNode(DecisionTree tree) {
        Node result = node(String.valueOf(System.identityHashCode(tree)))
                .with(Shape.RECTANGLE, Label.of(tree.getColumnName()));

        for (DecisionTree child : tree.getChildren()) {
            Node childNode = getNode(child);
            result = result.link(to(childNode).with(Label.of(child.getBranchLabel())));
        }

        return result;
    }

    public void write(DecisionTree tree) throws IOException {
        Graph g = graph("DecisionTree").directed().with(getNode(tree));

        Graphviz.fromGraph(g).render(Format.XDOT).toFile(new File("graphviz/ex2.dot"));
        Graphviz.fromGraph(g).render(Format.PNG).toFile(new File("graphviz/ex2.png"));
    }
}
