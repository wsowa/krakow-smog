package pl.wsowa.krakowsmog.dataanalyser;

import org.junit.Test;
import pl.wsowa.krakowsmog.domain.SensorId;

import java.io.IOException;
import java.time.LocalDate;
import java.util.Map;

import static org.junit.Assert.*;

public class DataAnalyserTest {

    @Test
    public void anayzeFromCsv() throws IOException {
//        DataAnalyser dataAnalyser = new DataAnalyser(new CSVDataSource("WEB-INF/resources/pm25.csv", "/pm10.csv"));
//
//        LocalDate startDate = LocalDate.of(2018, 3, 9);
//        Map<SensorId, AnalysisResult> sensorsMatchingCriteria = dataAnalyser.getSensorsMatchingCriteria(SensorCriteria.sensorsMatchingCriteria()
//                .withinDateRange(startDate, startDate.plusWeeks(2))
//                .betweenHours(8, 20)
//                .maxPercentageOfDaysExceeded((float) 0.2)
//                .maxAllowedLimitExceed((float) 1.6)
//                .maxNumberOfHoursExceededADay(2)
//                .build());
//        System.out.println(sensorsMatchingCriteria);
    }
}