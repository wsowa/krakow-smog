package pl.wsowa.krakowsmog.dataanalyser;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Range;
import pl.wsowa.krakowsmog.domain.Measurement;

import java.time.LocalDate;

public interface DataSource {
    ImmutableSet<Measurement> getMeasurementsForDateAndHoursRange(Range<LocalDate> dateRange, Range<Integer> hoursRange);
}
