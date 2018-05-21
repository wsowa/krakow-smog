package pl.wsowa.krakowsmog.dataanalyser;

import com.google.common.collect.Range;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.wsowa.krakowsmog.domain.Measurement;
import pl.wsowa.krakowsmog.domain.Sensor;
import pl.wsowa.krakowsmog.domain.SensorId;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Stream;

import static java.util.function.Function.identity;
import static java.util.stream.Collectors.*;

@Path("/sensors")
public class DataAnalyser {

    final private Logger logger = LoggerFactory.getLogger(DataAnalyser.class);

    private static final double MIN_ACCEPTABLE_DATAPOINTS_AVAILABILITY = 0.8;
    private final DataSource dataSource;

    public DataAnalyser(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @POST
    @Path("/matchCriteria")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Map<SensorId, AnalysisResult> getSensorsMatchingCriteria(SensorCriteriaParam criteriaParam) {
        SensorCriteria criteria = criteriaParam.toCriteria();
        logger.info("criteria = {}", criteria);
        return dataSource.getMeasurementsForDateAndHoursRange(criteria.dateRange, criteria.hoursRange).stream()
                .collect(groupingBy(Measurement::getSensorId))
                .entrySet().stream()
                .collect(toMap(Map.Entry::getKey, e -> analyseSensorData(e.getValue(), criteria)));
    }

    @GET
    @Path("/")
    @Produces(MediaType.APPLICATION_JSON)
    public Set<Sensor> getSensors() {
        return dataSource.getSensors();
    }


    private AnalysisResult analyseSensorData(List<Measurement> measurements, SensorCriteria criteria) {
        Map<LocalDate, List<Measurement>> measurementsByDate = measurements.stream()
                .collect(groupingBy(Measurement::getDate));
        Stream<AnalysisResult> dailyDataAnalysis = measurementsByDate.values().stream()
                .map(m -> analyseDailyData(m, criteria));

        int days = getAnalysisPeriodLength(criteria.dateRange, measurementsByDate.keySet());

        Map<AnalysisResult, Long> summary = Stream.concat(dailyDataAnalysis, Stream.generate(() -> AnalysisResult.INSUFFICIENT_DATA))
                .limit(days)
                .collect(groupingBy(identity(), counting()));


        if (!getSufficientDailyData(summary.getOrDefault(AnalysisResult.INSUFFICIENT_DATA, (long) 0), days)) {
            return AnalysisResult.INSUFFICIENT_DATA;
        } else if (summary.getOrDefault(AnalysisResult.BAD, (long) 0) > days * criteria.maxExceededDaysPercentage) {
            return AnalysisResult.BAD;
        } else {
            return AnalysisResult.GOOD;
        }
    }

    private int getAnalysisPeriodLength(Range<LocalDate> dateRange, Set<LocalDate> measurementsDates) {
        LocalDate lowerDate = dateRange.hasLowerBound() ? dateRange.lowerEndpoint() : measurementsDates.stream().min(Comparator.naturalOrder()).orElse(LocalDate.MIN);
        LocalDate upperDate = dateRange.hasUpperBound() ? dateRange.upperEndpoint() : measurementsDates.stream().max(Comparator.naturalOrder()).orElse(LocalDate.MIN);
        return lowerDate.until(upperDate).getDays() + 1;
    }

    private AnalysisResult analyseDailyData(List<Measurement> measurements, SensorCriteria criteria) {
        if (!gotSuffcientHourlyData(measurements, criteria.hoursRange))
            return AnalysisResult.INSUFFICIENT_DATA;
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
