package modelisation.gui;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.collections.ObservableSet;
import javafx.collections.SetChangeListener;

public class CheckBoxTableColumn<T> extends EditableCheckboxColumn<T> {
    public CheckBoxTableColumn(String text, ObservableSet<T> selectedObjects) {
        super(text);
        setCellValueFactory(item -> {
            boolean selected = selectedObjects.contains(item.getValue());
            BooleanProperty prop = new SimpleBooleanProperty(selected);
            prop.addListener((observable, oldValue, newValue) -> {
                if (newValue) {
                    selectedObjects.add(item.getValue());
                } else {
                    selectedObjects.remove(item.getValue());
                }
            });

            selectedObjects.addListener((SetChangeListener<T>) change -> {
                if (change.wasRemoved() && change.getElementRemoved().equals(item.getValue())) {
                    prop.setValue(false);
                } else if (change.wasAdded() && change.getElementAdded().equals(item.getValue())) {
                    prop.setValue(true);
                }
            });
            return prop;
        });
    }
}
