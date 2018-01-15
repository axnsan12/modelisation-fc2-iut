package modelisation.io;

import com.opencsv.CSVParserBuilder;
import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import modelisation.TrainingData;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.List;
import java.util.Objects;

public class CsvDataReader {
    private final char separator;
    private Reader reader;
    private String fileName;

    /**
     * Create a {@link TrainingData} reader that reads from the given file.
     *
     * @param fileName  path to a .csv file
     * @param separator CSV field separator ({@code ','}, {@code ';'}, etc.)
     */
    public CsvDataReader(String fileName, char separator) {
        this.fileName = Objects.requireNonNull(fileName);
        this.separator = separator;
    }

    /**
     * Create a {@link TrainingData} reader that reads from the given reader.
     *
     * @param reader    csv file reader
     * @param separator CSV field separator ({@code ','}, {@code ';'}, etc.)
     */
    public CsvDataReader(Reader reader, char separator) {
        this.reader = Objects.requireNonNull(reader);
        this.separator = separator;
    }


    private CSVReader getReader() throws FileNotFoundException {
        if (reader == null) {
            reader = new FileReader(fileName);
        }
        return new CSVReaderBuilder(reader)
                .withCSVParser(new CSVParserBuilder()
                        .withSeparator(separator)
                        .build()
                ).build();
    }

    public TrainingData read() throws IOException {
        try (CSVReader csvReader = getReader()) {
            String[] headers = csvReader.readNext();
            List<String[]> lines = csvReader.readAll();
            return new TrainingData(headers, lines);
        }
    }
}
