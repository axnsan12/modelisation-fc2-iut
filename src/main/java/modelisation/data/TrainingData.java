package modelisation.data;

import org.eclipse.jdt.annotation.NonNull;

import java.util.*;
import java.util.stream.Collectors;

public class TrainingData {
    private List<Column> columns;
    private Map<String, Column> columnsByHeader;

    public TrainingData(@NonNull String[] headers, @NonNull List<String[]> lines) {
        checkInput(Objects.requireNonNull(headers), Objects.requireNonNull(lines));

        columns = new ArrayList<>(headers.length);
        for (int col = 0; col < lines.get(0).length; ++col) {
            String[] columnStrings = extractColumn(lines, col);
            Column column;
            try {
                // first, try to parse the column as an integer column
                int[] data = Arrays.stream(columnStrings)
                        .mapToInt(val -> val.isEmpty() ? 0 : Integer.parseInt(val))
                        .toArray();
                column = new IntColumn(col, headers[col], data);
            } catch (NumberFormatException e1) {
                try {
                    // if it's not an integer column, it might be a real-valued column
                    // this differs from an integer column in that it can never be discrete - only continuous
                    double[] data = Arrays.stream(columnStrings)
                            .mapToDouble(val -> val.isEmpty() ? 0 : Double.parseDouble(val))
                            .toArray();
                    column = new DoubleColumn(col, headers[col], data);
                } catch (NumberFormatException e2) {
                    // if the column is not numerical, its values are presumed to be
                    // class names and it is used as a discrete column
                    column = new StringColumn(col, headers[col], columnStrings);
                }
            }

            columns.add(column);
        }

        columnsByHeader = columns.stream().collect(Collectors.toMap(Column::getHeader, c -> c));
    }

    private TrainingData(@NonNull List<Column> columns) {
        this.columns = columns;
        this.columnsByHeader = columns.stream().collect(Collectors.toMap(Column::getHeader, c -> c));
    }

    /**
     * Extract a column from a row-major matrix of strings.
     *
     * @param lines array of equal-length lines
     * @param col   index of column to extract
     */
    private String[] extractColumn(List<String[]> lines, int col) {
        return lines.stream()
                .map(line -> line[col])
                .toArray(String[]::new);
    }

    /**
     * Return the array of column headers for this dataset.
     * This is the same array passed to {@link #TrainingData(String[], List)}.
     *
     * @return
     */
    public List<String> getHeaders() {
        return columns.stream().map(Column::getHeader).collect(Collectors.toList());
    }


    private void checkInput(String[] headers, List<String[]> lines) {
        if (lines.size() == 0 || headers.length == 0) {
            throw new IllegalArgumentException("empty trainingData set");
        }

        if (headers.length < 3) {
            throw new IllegalArgumentException("dataset must have at least 3 columns - one ID column, " +
                    "one target column and one or more data columns");
        }

        for (String[] line : lines) {
            if (line.length != headers.length) {
                throw new IllegalArgumentException("length of all lines must match header");
            }
        }
    }

    /**
     * Get a list of this dataset's columns and information about them.
     *
     * @return list of columns
     */
    public List<Column> getColumns() {
        return Collections.unmodifiableList(columns);
    }

    /**
     * Get information about a single column in the dataset.
     *
     * @param header header name of the desired column
     * @return target column
     */
    public Column getColumn(String header) {
        if (columnsByHeader.containsKey(header)) {
            return columnsByHeader.get(header);
        } else {
            throw new IllegalArgumentException("no such column " + header);
        }
    }

    /**
     * Get information about a single column in the dataset.
     *
     * @param index index of the desired column
     * @return target column
     */
    public Column getColumn(int index) {
        return columns.get(index);
    }

    /**
     * @return the number of rows in this dataset
     */
    public int size() {
        return columns.get(0).size();
    }

    public TrainingData partition(List<Integer> keepIndexes) {
        return new TrainingData(columns.stream().map(col -> col.partial(keepIndexes)).collect(Collectors.toList()));
    }
}
