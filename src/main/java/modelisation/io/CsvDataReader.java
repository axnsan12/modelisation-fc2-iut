package modelisation.io;

import com.opencsv.CSVParserBuilder;
import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import modelisation.data.TrainingData;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class CsvDataReader {
    private static final char[] SEPARATORS = new char[]{',', '\t', ';', ' ', ':'};
    private static final char[] QUOTES = new char[]{'"', '\'', '\0'};
    private static final int SNIFF_SAMPLE = 100;
    private Character separator, quote;
    private FileInputStream stream;
    private String fileName;

    /**
     * Create a {@link TrainingData} reader that reads from the given file and
     * tries to guess the appropriate separator and quote characters.
     *
     * @param fileName path to a .csv file
     */
    public CsvDataReader(String fileName) {
        this.fileName = fileName;
    }

    /**
     * Create a {@link TrainingData} reader that reads from the given file and
     * tries to guess the appropriate separator and quote characters.
     *
     * @param stream input stream from csv data (must support reset to beginning)
     */
    public CsvDataReader(FileInputStream stream) {
        this.stream = stream;
    }

    /**
     * Create a {@link TrainingData} reader that reads from the given file.
     *
     * @param fileName  path to a .csv file
     * @param separator CSV field separator ({@code ','}, {@code ';'}, etc.)
     * @param quote     CSV quote character ({@code '\''}, {@code '"'}, etc.)
     */
    public CsvDataReader(String fileName, char separator, char quote) {
        this.fileName = Objects.requireNonNull(fileName);
        this.quote = quote;
        this.separator = separator;
    }

    /**
     * Create a {@link TrainingData} reader that reads from the given input stream.
     *
     * @param stream    input stream from csv data (must support reset to beginning)
     * @param separator CSV field separator ({@code ','}, {@code ';'}, etc.)
     * @param quote     CSV quote character ({@code '\''}, {@code '"'}, etc.)
     */
    public CsvDataReader(FileInputStream stream, char separator, char quote) {
        this.stream = Objects.requireNonNull(stream);
        this.quote = quote;
        this.separator = separator;
    }

    /**
     * Try to guess the separator and quote characters of a CSV file by trying multiple
     * combinations and choosing the one which correctly parses the highest number of columns.
     *
     * @throws IOException if no separator succeeded in parsing the input
     */
    private void sniffFormat() throws IOException {
        int maxColumns = 0;
        for (char quot : QUOTES) {
            for (char sep : SEPARATORS) {
                CSVReader testReader = getReader(sep, quot);
                try {
                    String[] headers = testReader.readNext();
                    ArrayList<String[]> lines = new ArrayList<>(SNIFF_SAMPLE);
                    for (int i = 0; i < SNIFF_SAMPLE; ++i) {
                        lines.add(testReader.readNext());
                    }

                    TrainingData data = new TrainingData(headers, lines);
                    if (maxColumns < data.getColumns().size()) {
                        // if multiple sep/quot combinations succeed, prefer the one that results in more columns
                        maxColumns = data.getColumns().size();
                        this.separator = sep;
                        this.quote = quot;
                    }
                } catch (Exception e) {
                    // failed to parse data using 'sep' and 'quote', try another...
                }
            }
        }

        if (this.separator == null) {
            throw new IOException("failed to guess CSV quote and separator characters; data might be malformed...");
        }

        System.out.println("[CSV] sniffed separator " + separator + " and quote " + quote);
    }

    private CSVReader getReader(char separator, char quote) throws IOException {
        final Reader reader;
        if (stream == null) {
            reader = new FileReader(fileName);
        } else {
            stream.getChannel().position(0);
            reader = new InputStreamReader(stream, StandardCharsets.UTF_8);
        }
        return new CSVReaderBuilder(reader)
                .withCSVParser(new CSVParserBuilder()
                        .withSeparator(separator)
                        .withQuoteChar(quote)
                        .withIgnoreQuotations(quote == '\0')
                        .build()
                ).build();
    }

    private CSVReader getReader() throws IOException {
        if (separator == null) {
            sniffFormat();
        }
        return getReader(separator, quote);
    }

    public TrainingData read() throws IOException {
        try (CSVReader csvReader = getReader()) {
            String[] headers = csvReader.readNext();
            List<String[]> lines = csvReader.readAll();
            return new TrainingData(headers, lines);
        }
    }
}
