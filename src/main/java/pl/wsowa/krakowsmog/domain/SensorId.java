package pl.wsowa.krakowsmog.domain;

import java.util.Objects;

public class SensorId {
    private final String sensorId;

    public SensorId(String sensorId) {
        this.sensorId = sensorId;
    }

    public String getSensorId() {
        return sensorId;
    }

    @Override
    public String toString() {
        return "SensorId(" + sensorId + ')';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SensorId sensorId1 = (SensorId) o;
        return Objects.equals(sensorId, sensorId1.sensorId);
    }

    @Override
    public int hashCode() {

        return Objects.hash(sensorId);
    }
}
