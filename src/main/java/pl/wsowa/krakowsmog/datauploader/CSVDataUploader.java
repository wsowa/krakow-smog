package pl.wsowa.krakowsmog.datauploader;

import pl.wsowa.krakowsmog.csv.CSVDataIterator;
import pl.wsowa.krakowsmog.csv.CSVDataSource;
import pl.wsowa.krakowsmog.datastore.MeasurementDataObject;
import pl.wsowa.krakowsmog.datastore.SensorDataObject;
import pl.wsowa.krakowsmog.domain.Measurement;
import pl.wsowa.krakowsmog.domain.Sensor;

import java.util.Set;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import static com.googlecode.objectify.ObjectifyService.ofy;

public class CSVDataUploader {

    private final CSVDataIterator dataIterator;
    private final CSVDataSource dataSource;

    public CSVDataUploader(CSVDataIterator dataIterator, CSVDataSource dataSource) {
        this.dataIterator = dataIterator;
        this.dataSource = dataSource;
    }

    public void uploadMeasurements() {
        store(StreamSupport.stream(Spliterators.spliteratorUnknownSize(dataIterator, Spliterator.ORDERED), false));
    }

    private void store(Stream<Set<Measurement>> measurements) {
        measurements
                .map(this::toDataObjects)
                .forEach(o -> ofy().save().entities(o));
    }

    private Set<MeasurementDataObject> toDataObjects(Set<Measurement> measurements) {
        return measurements.stream()
                .map(MeasurementDataObject::fromMeasurement)
                .collect(Collectors.toSet());
    }

    public void uploadSensors() {
        store(dataSource.getSensors());
    }

    private void store(Set<Sensor> sensors) {
        ofy().save().entities(sensors.stream().map(SensorDataObject::fromSensor).collect(Collectors.toSet()));
    }
}
