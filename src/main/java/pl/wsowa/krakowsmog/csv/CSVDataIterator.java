package pl.wsowa.krakowsmog.csv;

import com.google.common.base.Strings;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import pl.wsowa.krakowsmog.domain.Measurement;
import pl.wsowa.krakowsmog.domain.PM10;
import pl.wsowa.krakowsmog.domain.PM25;
import pl.wsowa.krakowsmog.domain.SensorId;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Stream;

import static pl.wsowa.krakowsmog.datastore.MeasurementDataObject.ZONE_ID;

public class CSVDataIterator implements Iterator<Set<Measurement>> {

    private final Iterator<CSVRecord> pm25iter;
    private final Iterator<CSVRecord> pm10iter;

    public CSVDataIterator(String pm25fileName, String pm10fileName) throws IOException {
        pm25iter = CSVParser.parse(new File(pm25fileName), Charset.defaultCharset(), CSVFormat.RFC4180.withHeader()).iterator();
        pm10iter = CSVParser.parse(new File(pm10fileName), Charset.defaultCharset(), CSVFormat.RFC4180.withHeader()).iterator();
    }

    private Optional<Float> getFloatValue(Map<String, String> pm25record, String sensorId) {
        return Stream.of(pm25record.getOrDefault(sensorId, "undefined"))
                .filter(x -> !Strings.isNullOrEmpty(x) && !x.equals("undefined"))
                .map(Float::valueOf)
                .findAny();
    }

    @Override
    public boolean hasNext() {
        return pm25iter.hasNext();
    }

    @Override
    public Set<Measurement> next() {
        Map<String, String> pm25record = pm25iter.next().toMap();
        Map<String, String> pm10record = pm10iter.next().toMap();

        Instant timestamp = Instant.parse(pm25record.get("Time"));
        LocalDateTime localDateTime = LocalDateTime.ofInstant(timestamp, ZONE_ID);
        LocalDate date = localDateTime.toLocalDate();
        int hour = localDateTime.toLocalTime().getHour();

        HashSet<String> sensors = new HashSet<>(pm10record.keySet());
        sensors.remove("Time");

        Set<Measurement> data = new HashSet<>();

        for (String sensorId : sensors) {
            Optional<PM25> pm25 = getFloatValue(pm25record, sensorId).map(PM25::new);
            Optional<PM10> pm10 = getFloatValue(pm10record, sensorId).map(PM10::new);
            if (!pm25.isPresent() && !pm10.isPresent()) continue;

            data.add(new Measurement(new SensorId(sensorId), timestamp, date, hour, pm25, pm10));
        }
        return data;
    }
}
