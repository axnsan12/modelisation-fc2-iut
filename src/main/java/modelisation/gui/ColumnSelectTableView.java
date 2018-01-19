package modelisation.gui;

import javafx.beans.property.*;
import javafx.beans.value.ChangeListener;
import javafx.collections.FXCollections;
import javafx.collections.SetChangeListener;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import modelisation.data.Column;

import java.util.List;
import java.util.stream.Collectors;

public class ColumnSelectTableView extends TableView<Column> {
    ObjectProperty<Column> targetColumn, idColumn;
    SetProperty<Column> selectedColumns;

    public ColumnSelectTableView() {
        super();
        targetColumn = new SimpleObjectProperty<>();
        idColumn = new SimpleObjectProperty<>();
        selectedColumns = new SimpleSetProperty<>(FXCollections.observableSet());
        bindPropertyListeners();
        addColumns();
    }

    public void setColumns(List<Column> columns) {
        getItems().clear();
        selectedColumns.clear();
        idColumn.setValue(columns.get(0));
        targetColumn.setValue(columns.get(1));
        getItems().setAll(columns);
    }

    private void bindPropertyListeners() {
        ChangeListener<Column> ensureIncludedListener = (observable, oldValue, newValue) -> {
            if (newValue != null) {
                selectedColumns.add(newValue);
            }
        };
        targetColumn.addListener(ensureIncludedListener);
        idColumn.addListener(ensureIncludedListener);

        targetColumn.addListener((observable, oldValue, newValue) -> {
            if (newValue != null && newValue.equals(idColumn.getValue())) {
                if (oldValue != null && !oldValue.equals(idColumn.getValue())) {
                    idColumn.setValue(oldValue);
                }
            }
        });
        idColumn.addListener((observable, oldValue, newValue) -> {
            if (newValue != null && newValue.equals(targetColumn.getValue())) {
                if (oldValue != null && !oldValue.equals(targetColumn.getValue())) {
                    targetColumn.setValue(oldValue);
                }
            }
        });

        selectedColumns.addListener((SetChangeListener<Column>) change -> {
            if (change.wasRemoved()) {
                Column removed = change.getElementRemoved();
                if (removed.equals(targetColumn.getValue())) {
                    targetColumn.setValue(null);
                }
                if (removed.equals(idColumn.getValue())) {
                    idColumn.setValue(null);
                }
            }
        });
    }

    private void addColumns() {
        TableColumn<Column, String> columnName = new TableColumn<>("Nom");
        columnName.setCellValueFactory(col -> new SimpleStringProperty(col.getValue().getHeader()));
        this.getColumns().add(columnName);

        this.setEditable(true);

        this.getColumns().add(new CheckBoxTableColumn<>("Inclus", selectedColumns));
        this.getColumns().add(new RadioTableColumn<>("Cible", targetColumn));
        this.getColumns().add(new RadioTableColumn<>("ID", idColumn));
    }


    public Column getTargetColumn() {
        return targetColumn.getValue();
    }

    public Column getIdColumn() {
        return idColumn.getValue();
    }

    public List<Integer> getDataColumnIndexes() {
        return selectedColumns.get().stream()
                .filter(col -> !col.equals(getTargetColumn()) && !col.equals(getIdColumn()))
                .map(Column::getIndex)
                .collect(Collectors.toList());
    }
}
