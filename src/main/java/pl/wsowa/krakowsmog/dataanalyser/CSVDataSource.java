package pl.wsowa.krakowsmog.dataanalyser;

import com.google.common.base.Strings;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Range;
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
import java.time.*;
import java.util.*;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toSet;

public class CSVDataSource implements DataSource {

    public static final ZoneId ZONE_ID = ZoneId.of("Europe/Warsaw");

    private final List<Measurement> data = new LinkedList<>();

    public CSVDataSource(String pm25fileName, String pm10fileName) throws IOException {
        Iterator<CSVRecord> pm25iter = CSVParser.parse(new File(pm25fileName), Charset.defaultCharset(), CSVFormat.RFC4180.withHeader()).iterator();
        Iterator<CSVRecord> pm10iter = CSVParser.parse(new File(pm10fileName), Charset.defaultCharset(), CSVFormat.RFC4180.withHeader()).iterator();

        while (pm25iter.hasNext()) {
            Map<String, String> pm25record = pm25iter.next().toMap();
            Map<String, String> pm10record = pm10iter.next().toMap();

            Instant timestamp = Instant.parse(pm25record.get("Time"));
            LocalDateTime localDateTime = LocalDateTime.ofInstant(timestamp, ZONE_ID);
            LocalDate date = localDateTime.toLocalDate();
            int hour = localDateTime.toLocalTime().getHour();

            HashSet<String> sensors = new HashSet<>(pm10record.keySet());
            sensors.remove("Time");

            for (String sensorId : sensors) {
                Optional<PM25> pm25 = getFloatValue(pm25record, sensorId).map(PM25::new);
                Optional<PM10> pm10 = getFloatValue(pm10record, sensorId).map(PM10::new);
                if (!pm25.isPresent() && !pm10.isPresent()) continue;

                data.add(new Measurement(new SensorId(sensorId), timestamp, date, hour, pm25, pm10));
            }
        }
    }

    private Optional<Float> getFloatValue(Map<String, String> pm25record, String sensorId) {
        return Stream.of(pm25record.getOrDefault(sensorId, "undefined"))
                .filter(x -> !x.equals("undefined") && !Strings.isNullOrEmpty(x))
                .map(Float::valueOf)
                .findAny();
    }

    @Override
    public ImmutableSet<Measurement> getMeasurementsForDateAndHoursRange(Range<LocalDate> dateRange, Range<Integer> hoursRange) {
        return ImmutableSet.copyOf(data.stream()
                .filter(m -> dateRange.contains(m.getDate()))
                .filter(m -> hoursRange.contains(m.getHour()))
                .collect(toSet()));
    }
}
