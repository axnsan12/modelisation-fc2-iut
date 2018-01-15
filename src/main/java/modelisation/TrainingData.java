package modelisation;

import org.eclipse.jdt.annotation.NonNull;
import org.eclipse.jdt.annotation.Nullable;

import java.util.*;
import java.util.stream.Collectors;

public class TrainingData {
    private String[] headers;
    private List<String[]> rawLines;
    private ArrayList<Column> columns;
    private Map<String, Column> columnsByHeader;

    public TrainingData(@NonNull String[] headers, @NonNull List<String[]> lines) {
        checkInput(Objects.requireNonNull(headers), Objects.requireNonNull(lines));
        this.headers = headers;
        this.rawLines = lines;

        columns = new ArrayList<>(headers.length);
        for (int col = 0; col < lines.get(0).length; ++col) {
            int[] data = new int[lines.size()];
            String[] labels = null;
            try {
                extractDoubleColumn(lines, col, data);
            } catch (NumberFormatException e) {
                labels = extractColumnLabels(lines, col, data);
            }

            columns.add(new Column(col, headers[col], labels, data));
        }

        columnsByHeader = columns.stream().collect(Collectors.toMap(c -> c.header, c -> c));
    }

    /**
     * Return the array of column headers for this dataset.
     * This is the same array passed to {@link #TrainingData(String[], List)}.
     *
     * @return
     */
    public String[] getHeaders() {
        return headers;
    }

    /**
     * Return the raw list of lines in this dataset.
     * This is the same list passed to {@link #TrainingData(String[], List)}.
     *
     * @return
     */
    public List<String[]> getRawLines() {
        return rawLines;
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
     * Extract a column from a row-major matrix of strings, and convert its values to {@code int}.
     * Empty values are treated as zero.
     * <p>
     * The results are written into {@code result}.
     *
     * @param lines  array of equal-length lines
     * @param col    index of column to extract
     * @param result [out] the extraction result
     * @throws NumberFormatException if any line contains a value at index
     *                               {@code col} that could not be parsed as an int
     */
    private void extractDoubleColumn(List<String[]> lines, int col, int[] result) throws NumberFormatException {
        for (int row = 0; row < lines.size(); ++row) {
            String value = lines.get(row)[col];
            result[row] = value.isEmpty() ? 0 : Integer.parseInt(value);
        }
    }

    /**
     * Assign an integer to each distinct string in the column, and return an
     * array which maps every value back to the original string by index.
     * <p>
     * For example, a column with values {@code [A, B, B, A, C]} would be mapped
     * to {@code result = [0, 1, 1, 0, 2]} and {@code labels = [A, B, C]}
     *
     * @param lines  array of equal-length lines
     * @param col    index of column to extract
     * @param result [out] the extraction result
     * @return labels for column values
     * @see Column#valueLabels
     */
    private String[] extractColumnLabels(List<String[]> lines, int col, int[] result) {
        HashMap<String, Integer> labelValues = new HashMap<>();
        for (int row = 0; row < lines.size(); ++row) {
            String label = lines.get(row)[col];
            Integer value = labelValues.get(label);
            if (value == null) {
                value = labelValues.size();
                labelValues.put(label, value);
            }
            result[row] = value;
        }

        String[] valueLabels = new String[labelValues.size()];
        for (HashMap.Entry<String, Integer> entry : labelValues.entrySet()) {
            valueLabels[entry.getValue()] = entry.getKey();
        }

        return valueLabels;
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
        return columnsByHeader.get(header);
    }

    /**
     * Data class for holding information about a column.
     */
    public static class Column {
        /**
         * Zero-based index of the column in the trainingData set.
         */
        public final int index;

        /**
         * The column's associated header/name.
         */
        public final String header;

        /**
         * If the values in the column are non-numeric, they get mapped to integers in [0,N)
         * (where N is the number of distinct values in the column), and this array maps values
         * in {@link #data} back to the original string in the input file.
         *
         * @see #extractColumnLabels(List, int, int[])
         */
        @Nullable public final String[] valueLabels;

        /**
         * Column trainingData.
         */
        public final int[] data;

        /**
         * @param index       {@link #index}
         * @param header      {@link #header}
         * @param valueLabels {@link #valueLabels}
         * @param data        {@link #data}
         */
        public Column(int index, String header, @Nullable String[] valueLabels, int[] data) {
            this.index = index;
            this.header = header;
            this.valueLabels = valueLabels;
            this.data = data;
        }

        /**
         * Return the item at the given index as a string, taking {@link #valueLabels} into account.
         *
         * @param index row index
         * @return value as string
         */
        public String getValueAsString(int index) {
            int value = data[index];
            return valueLabels != null ? valueLabels[value] : String.valueOf(value);
        }
    }
}
