package pl.wsowa.krakowsmog.datauploader;

import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;
import pl.wsowa.krakowsmog.domain.Measurement;
import pl.wsowa.krakowsmog.domain.PolutionIndcator;

import java.time.Instant;
import java.time.LocalDate;

@Entity(name = "Measurement")
public class MeasurementDataObject {
    @Id
    String id;
    String sensorId;
    Instant timestamp;
    LocalDate date;
    Integer hour;
    Float pm25;
    Float pm10;

    public static MeasurementDataObject fromMeasurement(Measurement measurement) {
        MeasurementDataObject dataObject = new MeasurementDataObject();
        dataObject.sensorId = measurement.getSensorId().getSensorId();
        dataObject.timestamp = measurement.getTimestamp();
        dataObject.date = measurement.getDate();
        dataObject.hour = measurement.getHour();
        dataObject.pm25 = measurement.getPm25().map(PolutionIndcator::getValue).orElse(null);
        dataObject.pm10 = measurement.getPm10().map(PolutionIndcator::getValue).orElse(null);
        dataObject.id = dataObject.sensorId + "/" + dataObject.timestamp.toString();

        return dataObject;
    }

}
