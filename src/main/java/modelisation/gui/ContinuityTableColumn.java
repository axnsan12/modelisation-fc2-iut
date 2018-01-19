package modelisation.gui;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.cell.ComboBoxTableCell;
import javafx.util.StringConverter;
import modelisation.data.Column;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;


public class ContinuityTableColumn extends TableColumn<Column, Column.Continuity> {
    private BooleanProperty isRegressionTree = new SimpleBooleanProperty();

    public ContinuityTableColumn(String text, ObjectProperty<Column> targetColumn) {
        super(text);

        this.setCellValueFactory(item -> {
            Column column = item.getValue();
            Column.Continuity val = column.isDiscrete() ? Column.Continuity.DISCRETE : Column.Continuity.CONTINUOUS;
            ObjectProperty<Column.Continuity> prop = new SimpleObjectProperty<>(val);
            prop.addListener((observable, oldValue, newValue) -> {
                if (newValue != null) {
                    column.setContinuity(newValue);
                    if (column.equals(targetColumn.getValue())) {
                        isRegressionTree.setValue(newValue == Column.Continuity.CONTINUOUS);
                    }
                }
            });

            isRegressionTree.addListener((observable, wasRegression, isRegression) -> {
                if (column.equals(targetColumn.getValue())) {
                    prop.setValue(isRegression ? Column.Continuity.CONTINUOUS : Column.Continuity.DISCRETE);
                }
            });

            return prop;
        });
        this.setCellFactory(tc -> new ContinuityTableCell(Column.Continuity.DISCRETE, Column.Continuity.CONTINUOUS));
        targetColumn.addListener((observable, oldValue, newValue) -> {
            isRegressionTree.setValue(newValue != null && !newValue.isDiscrete());
        });

        this.setMinWidth(100);
    }

    public BooleanProperty isRegressionTreeProperty() {
        return isRegressionTree;
    }

    private static class ContinuityStringConverter extends StringConverter<Column.Continuity> {
        static final Map<Column.Continuity, String> labels = new HashMap<>();
        static final Map<String, Column.Continuity> labelsReverse;

        static {
            labels.put(Column.Continuity.DISCRETE, "discrete");
            labels.put(Column.Continuity.CONTINUOUS, "continue");
            labelsReverse = labels.entrySet().stream().collect(Collectors.toMap(Map.Entry::getValue, Map.Entry::getKey));
        }

        @Override
        public String toString(Column.Continuity continuity) {
            return labels.get(continuity);
        }

        @Override
        public Column.Continuity fromString(String string) {
            return labelsReverse.get(string);
        }
    }

    private static class ContinuityTableCell extends ComboBoxTableCell<Column, Column.Continuity> {
        public ContinuityTableCell(Column.Continuity... items) {
            super(new ContinuityStringConverter(), items);
            setEditable(true);
        }

        @SuppressWarnings("unchecked")
        @Override
        public void updateItem(Column.Continuity item, boolean empty) {
            super.updateItem(item, empty);
            TableRow<Column> row = getTableRow();
            setDisable(row == null || row.getItem() == null || !row.getItem().canSetContinuity());
            setStyle(isDisable() ? "-fx-text-fill: gray" : "");
        }
    }
}
