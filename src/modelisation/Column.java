package modelisation;

import java.util.ArrayList;

public class Column<T> {
    private String name;
    private ArrayList<T> values;

    public Column(String name, ArrayList<T> values) {
        this.name = name;
        this.values = values;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ArrayList<T> getValues() {
        return values;
    }

    public void setValues(ArrayList<T> values) {
        this.values = values;
    }

    @Override
    public String toString() {
        return "Column{" +
                "name='" + name + '\'' +
                ", values=" + values +
                '}';
    }
}
