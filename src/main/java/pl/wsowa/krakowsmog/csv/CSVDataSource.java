package pl.wsowa.krakowsmog.csv;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Range;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import pl.wsowa.krakowsmog.dataanalyser.DataSource;
import pl.wsowa.krakowsmog.domain.Measurement;
import pl.wsowa.krakowsmog.domain.Sensor;
import pl.wsowa.krakowsmog.domain.SensorId;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.time.LocalDate;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toSet;

public class CSVDataSource implements DataSource {

    private final List<Measurement> data = new LinkedList<>();
    private final Set<Sensor> sensors;

    public CSVDataSource(CSVDataIterator csvDataIterator, String sensorsFileName) throws IOException {
        this.sensors = readAllSensors(sensorsFileName);
        while (csvDataIterator.hasNext()) {
            data.addAll(csvDataIterator.next());
        }
    }

    private Set<Sensor> readAllSensors(String sensorsFileName) throws IOException {
        return CSVParser.parse(new File(sensorsFileName), Charset.defaultCharset(), CSVFormat.RFC4180.withHeader()).getRecords()
                .stream()
                .map(this::csvRecordToSensor)
                .collect(Collectors.toSet());
        }

    private Sensor csvRecordToSensor(CSVRecord record) {
        Map<String, String> fields = record.toMap();
        return new Sensor(
                new SensorId(fields.get("id")),
                getAsString(fields,"name"),
                getAsString(fields,"vendor"),
                Double.valueOf(fields.get("lat")),
                Double.valueOf(fields.get("long")),
                getAsString(fields,"locality"),
                getAsString(fields,"street"),
                getAsString(fields,"streetNumber")
        );
    }

    private String getAsString(Map<String, String> fields, String name) {
        String s = fields.getOrDefault(name, null);
        return "undefined".equals(s) ? null : s;
    }

    @Override
    public ImmutableSet<Measurement> getMeasurementsForDateAndHoursRange(Range<LocalDate> dateRange, Range<Integer> hoursRange) {
        return ImmutableSet.copyOf(data.stream()
                .filter(m -> dateRange.contains(m.getDate()))
                .filter(m -> hoursRange.contains(m.getHour()))
                .collect(toSet()));
    }

    @Override
    public Set<Sensor> getSensors() {
        return sensors;
    }
}
