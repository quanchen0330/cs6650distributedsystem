import com.opencsv.CSVWriter;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class CSVGenerator {


    public CSVWriter getWriter() throws IOException {
        File file = new File("/Users/Quan/Desktop/output/file.csv");
        FileWriter csvWriter = new FileWriter(file);
        CSVWriter writer = new CSVWriter(csvWriter);
        String[] header = {"start", "request type", "latency", "response code"};
        writer.writeNext(header);
        return writer;
    }
}
