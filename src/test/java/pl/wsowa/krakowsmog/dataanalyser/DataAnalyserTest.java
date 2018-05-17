package pl.wsowa.krakowsmog.dataanalyser;

import com.googlecode.objectify.ObjectifyService;
import org.junit.Ignore;
import org.junit.Test;
import pl.wsowa.krakowsmog.datastore.DatastoreDataSource;
import pl.wsowa.krakowsmog.datastore.MeasurementDataObject;
import pl.wsowa.krakowsmog.domain.SensorId;

import java.io.Closeable;
import java.io.IOException;
import java.time.LocalDate;
import java.util.Map;

import static java.lang.System.out;

public class DataAnalyserTest {

    @Test
    @Ignore
    public void anayzeFromDatastore() throws IOException {
        ObjectifyService.init();
        Closeable begin = ObjectifyService.begin();
        ObjectifyService.register(MeasurementDataObject.class);

        DataAnalyser dataAnalyser = new DataAnalyser(new DatastoreDataSource());

        SensorCriteriaParam criteriaParam = new SensorCriteriaParam();
        LocalDate minDate = LocalDate.of(2018, 4, 10);
        criteriaParam.setMinDate(minDate);
        criteriaParam.setMaxDate(minDate.plusDays(2));
        criteriaParam.setMinHour(8);
        criteriaParam.setMaxHour(20);
        criteriaParam.setMaxExceededDaysPercentage((float) 0.2);
        criteriaParam.setMaxExceededHours(4);
        criteriaParam.setMaxLimitExceed((float) 2.0);
        Map<SensorId, AnalysisResult> sensorsMatchingCriteria = dataAnalyser.getSensorsMatchingCriteria(criteriaParam);
        out.println(sensorsMatchingCriteria);
        begin.close();
    }

}