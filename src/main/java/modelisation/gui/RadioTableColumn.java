package modelisation.gui;


import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;

public class RadioTableColumn<T> extends EditableCheckboxColumn<T> {
    public RadioTableColumn(String text, ObjectProperty<T> selectedObject) {
        super(text);

        setCellValueFactory(item -> {
            BooleanProperty prop = new SimpleBooleanProperty(item.getValue().equals(selectedObject.getValue()));
            prop.addListener((observable, oldValue, newValue) -> {
                if (newValue) {
                    selectedObject.setValue(item.getValue());
                }
            });

            selectedObject.addListener((observable, oldValue, newValue) -> {
                prop.setValue(item.getValue().equals(newValue));
            });
            return prop;
        });
    }
}
