package pl.wsowa.krakowsmog.datastore;

import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;
import com.googlecode.objectify.annotation.Index;
import pl.wsowa.krakowsmog.domain.*;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.Optional;

@Entity(name = "Measurement")
public class MeasurementDataObject implements Serializable {

    public static final ZoneId ZONE_ID = ZoneId.of("Europe/Warsaw");

    @Id String id;
    String sensorId;
    Date timestamp;
    @Index Date date;
    @Index Integer hour;
    Float pm25;
    Float pm10;

    public static MeasurementDataObject fromMeasurement(Measurement measurement) {
        MeasurementDataObject dataObject = new MeasurementDataObject();
        dataObject.sensorId = measurement.getSensorId().getSensorId();
        dataObject.timestamp = Date.from(measurement.getTimestamp());
        dataObject.date = Date.from(measurement.getDate().atStartOfDay(ZONE_ID).toInstant());
        dataObject.hour = measurement.getHour();
        dataObject.pm25 = measurement.getPm25().map(PolutionIndcator::getValue).orElse(null);
        dataObject.pm10 = measurement.getPm10().map(PolutionIndcator::getValue).orElse(null);
        dataObject.id = dataObject.sensorId + "/" + measurement.getTimestamp().toString();

        return dataObject;
    }

    public Measurement toMeasurement() {
        return new Measurement(
                new SensorId(sensorId),
                timestamp.toInstant(),
                LocalDateTime.ofInstant(date.toInstant(), ZONE_ID).toLocalDate(),
                hour,
                Optional.ofNullable(pm25).map(PM25::new),
                Optional.ofNullable(pm10).map(PM10::new));
    }

}
