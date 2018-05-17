package pl.wsowa.krakowsmog.domain;

import java.io.Serializable;

public class Sensor implements Serializable {
    private final SensorId sensorId;
    private final String name;
    private final String vendor;
    private final Double latitude;
    private final Double longitude;
    private final String locality;
    private final String street;
    private final String streetNumber;

    public Sensor(SensorId sensorId, String name, String vendor, Double latitude, Double longitude, String locality, String street, String streetNumber) {
        this.sensorId = sensorId;
        this.name = name;
        this.vendor = vendor;
        this.latitude = latitude;
        this.longitude = longitude;
        this.locality = locality;
        this.street = street;
        this.streetNumber = streetNumber;
    }

    public SensorId getSensorId() {
        return sensorId;
    }

    public String getName() {
        return name;
    }

    public String getVendor() {
        return vendor;
    }

    public Double getLatitude() {
        return latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public String getLocality() {
        return locality;
    }

    public String getStreet() {
        return street;
    }

    public String getStreetNumber() {
        return streetNumber;
    }
}
