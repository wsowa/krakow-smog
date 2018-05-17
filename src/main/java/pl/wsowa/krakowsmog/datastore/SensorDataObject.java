package pl.wsowa.krakowsmog.datastore;

import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;
import pl.wsowa.krakowsmog.domain.Sensor;
import pl.wsowa.krakowsmog.domain.SensorId;

import java.io.Serializable;

@Entity(name = "Sensor")
public class SensorDataObject implements Serializable {

    @Id String sensorId;
    String name;
    String vendor;
    Double latitude;
    Double longitude;
    String locality;
    String street;
    String streetNumber;

    public static SensorDataObject fromSensor(Sensor sensor) {
        SensorDataObject dataObject = new SensorDataObject();
        dataObject.sensorId = sensor.getSensorId().getSensorId();
        dataObject.name = sensor.getName();
        dataObject.vendor = sensor.getVendor();
        dataObject.latitude = sensor.getLatitude();
        dataObject.longitude = sensor.getLongitude();
        dataObject.locality = sensor.getLocality();
        dataObject.street = sensor.getStreet();
        dataObject.streetNumber = sensor.getStreetNumber();

        return dataObject;
    }

    public Sensor toSensor() {
        return new Sensor(
                new SensorId(sensorId),
                name,
                vendor,
                latitude,
                longitude,
                locality,
                street,
                streetNumber);
    }

}
