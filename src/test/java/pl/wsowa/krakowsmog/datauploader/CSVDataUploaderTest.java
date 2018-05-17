package pl.wsowa.krakowsmog.datauploader;

import com.googlecode.objectify.ObjectifyService;
import org.junit.Ignore;
import org.junit.Test;
import pl.wsowa.krakowsmog.csv.CSVDataIterator;
import pl.wsowa.krakowsmog.csv.CSVDataSource;
import pl.wsowa.krakowsmog.datastore.MeasurementDataObject;
import pl.wsowa.krakowsmog.datastore.SensorDataObject;

import java.io.Closeable;
import java.io.IOException;

public class CSVDataUploaderTest {

    @Test
    @Ignore
    public void uploadMeasurementsFromCsv() throws IOException {
        ObjectifyService.init();
        Closeable begin = ObjectifyService.begin();
        ObjectifyService.register(MeasurementDataObject.class);

        CSVDataIterator iterator = new CSVDataIterator("pm25.csv", "pm10.csv");
        CSVDataSource dataSource = new CSVDataSource(iterator, "sensors.csv");
        CSVDataUploader csvDataUploader = new CSVDataUploader(iterator, dataSource);

        csvDataUploader.uploadMeasurements();

        begin.close();
    }

    @Test
    @Ignore
    public void uploadSensorsFromCsv() throws IOException {
        ObjectifyService.init();
        Closeable begin = ObjectifyService.begin();
        ObjectifyService.register(SensorDataObject.class);

        CSVDataIterator iterator = new CSVDataIterator("pm25.csv", "pm10.csv");
        CSVDataSource dataSource = new CSVDataSource(iterator, "sensors.csv");
        CSVDataUploader csvDataUploader = new CSVDataUploader(iterator, dataSource);

        csvDataUploader.uploadSensors();

        begin.close();
    }
}