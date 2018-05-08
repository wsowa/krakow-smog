package pl.wsowa.krakowsmog.domain;

import com.google.common.base.Preconditions;

import java.time.Instant;
import java.time.LocalDate;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;

public class Measurement {
    private final SensorId sensorId;
    private final Instant timestamp;
    private final LocalDate date;
    private final int hour;
    private final Optional<PM25> pm25;
    private final Optional<PM10> pm10;

    public Measurement(SensorId sensorId, Instant timestamp, LocalDate date, int hour, Optional<PM25> pm25, Optional<PM10> pm10) {
        Preconditions.checkArgument(pm10.isPresent() || pm25.isPresent());
        this.sensorId = sensorId;
        this.timestamp = timestamp;
        this.date = date;
        this.hour = hour;
        this.pm25 = pm25;
        this.pm10 = pm10;
    }

    public Boolean withinLimit(float maxLimitExceed) {
        return Stream.concat(optionalToStream(pm25), optionalToStream(pm10))
                .allMatch(pi -> pi.withinLimit(maxLimitExceed));
    }

    private <T> Stream<T> optionalToStream(Optional<T> o) {
        return o.map(Stream::of).orElseGet(Stream::empty);
    }

    public SensorId getSensorId() {
        return sensorId;
    }

    public LocalDate getDate() {
        return date;
    }

    public int getHour() {
        return hour;
    }


    public Instant getTimestamp() {
        return timestamp;
    }

    public Optional<PM25> getPm25() {
        return pm25;
    }

    public Optional<PM10> getPm10() {
        return pm10;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Measurement that = (Measurement) o;
        return hour == that.hour &&
                Objects.equals(sensorId, that.sensorId) &&
                Objects.equals(timestamp, that.timestamp) &&
                Objects.equals(date, that.date) &&
                Objects.equals(pm10, that.pm10) &&
                Objects.equals(pm25, that.pm25);
    }

    @Override
    public int hashCode() {

        return Objects.hash(sensorId, timestamp, date, hour, pm10, pm25);
    }
}
