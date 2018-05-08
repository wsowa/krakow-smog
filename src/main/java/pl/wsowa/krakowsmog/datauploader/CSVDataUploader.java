package pl.wsowa.krakowsmog.datauploader;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Range;
import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;
import pl.wsowa.krakowsmog.dataanalyser.DataSource;
import pl.wsowa.krakowsmog.domain.Measurement;
import pl.wsowa.krakowsmog.domain.PM10;
import pl.wsowa.krakowsmog.domain.PolutionIndcator;
import pl.wsowa.krakowsmog.domain.SensorId;

import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.io.IOException;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.googlecode.objectify.ObjectifyService.ofy;

@Path("/csv")
public class CSVDataUploader {

    private final DataSource dataSource;

    public CSVDataUploader(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @POST
    @Path("/upload")
    @Produces(MediaType.TEXT_PLAIN)
    public String upload() {
        store(dataSource.getMeasurementsForDateAndHoursRange(Range.all(), Range.all()));
        return "OK";
    }


    @POST
    @Path("/uploadTest")
    @Produces(MediaType.TEXT_PLAIN)
    public String uploadTest() {
        store(ImmutableSet.of(new Measurement(new SensorId("x"), Instant.now(), LocalDate.now(), LocalTime.now().getHour(), Optional.empty(), Optional.of(new PM10(10)))));
        return "OK";
    }


    @POST
    @Path("/dummy")
    @Produces(MediaType.TEXT_PLAIN)
    public String dummy() {
        return "OK";
    }

    private void store(ImmutableSet<Measurement> measurements) {
        List<MeasurementDataObject> dataObjects = measurements.stream()
                .map(MeasurementDataObject::fromMeasurement)
                .collect(Collectors.toList());
        ofy().save().entities(dataObjects).now();
    }
}
