package pl.wsowa.krakowsmog.dataanalyser;

import com.google.common.collect.Range;
import pl.wsowa.krakowsmog.domain.Measurement;
import pl.wsowa.krakowsmog.domain.SensorId;

import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import static java.util.function.Function.identity;
import static java.util.stream.Collectors.*;

public class DataAnalyser {

    private static final double MIN_ACCEPTABLE_DATAPOINTS_AVAILABILITY = 0.8;
    private final DataSource dataSource;

    public DataAnalyser(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public Map<SensorId, AnalysisResult> getSensorsMatchingCriteria(SensorCriteria criteria) {
        return dataSource.getMeasurementsForDateAndHoursRange(criteria.dateRange, criteria.hoursRange).stream()
                .collect(groupingBy(Measurement::getSensorId))
                .entrySet().stream()
                .collect(toMap(Map.Entry::getKey, e -> analyseSensorData(e.getValue(), criteria)));

    }

    private AnalysisResult analyseSensorData(List<Measurement> measurements, SensorCriteria criteria) {
        Stream<AnalysisResult> dailyDataAnalysis = measurements.stream()
                .collect(groupingBy(Measurement::getDate))
                .values().stream()
                .map(m -> analyseDailyData(m, criteria));

        Map<AnalysisResult, Long> summary = Stream.concat(dailyDataAnalysis, Stream.generate(() -> AnalysisResult.UNSUFICIENT_DATA))
                .limit(criteria.dateRange.lowerEndpoint().until(criteria.dateRange.upperEndpoint()).getDays() + 1)
                .collect(groupingBy(identity(), counting()));

        int days = criteria.dateRange.lowerEndpoint().until(criteria.dateRange.upperEndpoint()).getDays() + 1;

        if (!getSufficientDailyData(summary.getOrDefault(AnalysisResult.UNSUFICIENT_DATA, (long) 0), days)) {
            return AnalysisResult.UNSUFICIENT_DATA;
        } else if (summary.getOrDefault(AnalysisResult.BAD, (long) 0) > days * criteria.maxExceededDaysPercentage) {
            return AnalysisResult.BAD;
        } else {
            return AnalysisResult.GOOD;
        }
    }

    private AnalysisResult analyseDailyData(List<Measurement> measurements, SensorCriteria criteria) {
        if (!gotSuffcientHourlyData(measurements, criteria.hoursRange))
            return AnalysisResult.UNSUFICIENT_DATA;
        else {
            return measurements.stream()
                    .map(measurement -> measurement.withinLimit(criteria.maxLimitExceed))
                    .filter(Boolean.FALSE::equals)
                    .limit(criteria.maxExceededHours + 1)
                    .count() > criteria.maxExceededHours ?
                    AnalysisResult.BAD : AnalysisResult.GOOD;
        }
    }

    private boolean gotSuffcientHourlyData(List<Measurement> measurements, Range<Integer> hoursRange) {
        return measurements.size() > (hoursRange.upperEndpoint() - hoursRange.lowerEndpoint() + 1) * MIN_ACCEPTABLE_DATAPOINTS_AVAILABILITY;
    }

    private boolean getSufficientDailyData(Long unsifficientDataDays, int totalDays) {
        return totalDays * (1 - MIN_ACCEPTABLE_DATAPOINTS_AVAILABILITY) >= unsifficientDataDays;
    }
}
