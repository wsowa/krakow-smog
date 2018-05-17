package pl.wsowa.krakowsmog.domain;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

import java.io.Serializable;
import java.util.Objects;

public class SensorId implements Serializable {
    private final String sensorId;

    @JsonCreator
    public SensorId(String sensorId) {
        this.sensorId = sensorId;
    }

    @JsonValue
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
