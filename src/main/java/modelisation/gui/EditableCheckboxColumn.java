package modelisation.gui;

import javafx.scene.control.TableColumn;
import javafx.scene.control.cell.CheckBoxTableCell;

public class EditableCheckboxColumn<T> extends TableColumn<T, Boolean> {
    public EditableCheckboxColumn(String text) {
        super(text);

        setCellFactory(tc -> {
            CheckBoxTableCell<T, Boolean> cell = new CheckBoxTableCell<>();
            cell.setEditable(true);
            return cell;
        });
    }
}
