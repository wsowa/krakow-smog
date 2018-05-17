package pl.wsowa.krakowsmog.dataanalyser;

import com.google.common.collect.Range;
import pl.wsowa.krakowsmog.domain.Measurement;
import pl.wsowa.krakowsmog.domain.Sensor;

import java.time.LocalDate;
import java.util.Set;

public interface DataSource {
    Set<Measurement> getMeasurementsForDateAndHoursRange(Range<LocalDate> dateRange, Range<Integer> hoursRange);

    Set<Sensor> getSensors();
}
