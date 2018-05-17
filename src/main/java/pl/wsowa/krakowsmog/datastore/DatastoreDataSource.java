package pl.wsowa.krakowsmog.datastore;

import com.google.appengine.api.memcache.MemcacheService;
import com.google.appengine.api.memcache.stdimpl.GCacheFactory;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Range;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.wsowa.krakowsmog.dataanalyser.DataSource;
import pl.wsowa.krakowsmog.domain.Measurement;
import pl.wsowa.krakowsmog.domain.Sensor;

import javax.cache.Cache;
import javax.cache.CacheException;
import javax.cache.CacheFactory;
import javax.cache.CacheManager;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.googlecode.objectify.ObjectifyService.ofy;
import static pl.wsowa.krakowsmog.datastore.MeasurementDataObject.ZONE_ID;

public class DatastoreDataSource implements DataSource {

    public static final String SENSORS_KEY = "sensors";
    final private Logger logger = LoggerFactory.getLogger(DatastoreDataSource.class);
    private Cache cache = null;

    public DatastoreDataSource() {
        try {
            CacheFactory cacheFactory = CacheManager.getInstance().getCacheFactory();
            Map<Object, Object> properties = new HashMap<>();
            properties.put(GCacheFactory.EXPIRATION_DELTA, TimeUnit.DAYS.toSeconds(7));
            properties.put(MemcacheService.SetPolicy.ADD_ONLY_IF_NOT_PRESENT, true);
            cache = cacheFactory.createCache(properties);
        } catch (CacheException e) {
            logger.warn("Cache creation failed", e);
        }
    }


    @Override
    public Set<Sensor> getSensors() {
        if (cache != null && cache.containsKey(SENSORS_KEY)) {
            return (Set<Sensor>) cache.get(SENSORS_KEY);
        } else {
            Set<Sensor> sensors = ofy().load().type(SensorDataObject.class).list()
                    .stream().map(SensorDataObject::toSensor).collect(Collectors.toSet());
            if (cache != null && sensors.size() > 0)
                cache.put(SENSORS_KEY, sensors);
            return sensors;
        }
    }

    @Override
    public ImmutableSet<Measurement> getMeasurementsForDateAndHoursRange(Range<LocalDate> dateRange, Range<Integer> hoursRange) {
        LocalDate lowerBound = dateRange.hasLowerBound() ? dateRange.lowerEndpoint() : getFirstStoredDate();
        LocalDate upperBound = dateRange.hasUpperBound() ? dateRange.upperEndpoint() : getLastStoredDate();
        if (lowerBound == null || upperBound == null) return ImmutableSet.of();

       return ImmutableSet.copyOf(Stream
               .iterate(lowerBound, d -> d.plusDays(1))
               .limit(ChronoUnit.DAYS.between(lowerBound, upperBound) + 1)
               .parallel().unordered()
               .flatMap(date -> getMeasurementsForSingleDate(date, hoursRange))
               .iterator());
    }

    private Stream<Measurement> getMeasurementsForSingleDate(LocalDate date, Range<Integer> hoursRange) {
        logger.info("Loading measurements for {}/{}", date, hoursRange);
        CacheKey cacheKey = new CacheKey(date, hoursRange);
        Collection<MeasurementDataObject> measurements;
        if (cache != null && cache.containsKey(cacheKey)) {
            measurements = (Collection<MeasurementDataObject>) cache.get(cacheKey);
            logger.info("Retuning from cache for {}/{}", date, hoursRange);
        } else {
            measurements = ofy().load().type(MeasurementDataObject.class)
                    .filter("date", toDate(date))
                    .filter("hour >=", hoursRange.lowerEndpoint())
                    .filter("hour <=", hoursRange.upperEndpoint())
                    .order("hour")
                    .list();
            if (cache != null && measurements.size() > 0 )
                cache.put(cacheKey, measurements);
            logger.info("Returning from datastore for {}/{}", date, hoursRange);
        }
        return measurements.stream().map(MeasurementDataObject::toMeasurement);

    }

    private LocalDate getFirstStoredDate() {
        return getMinOrMaxStoreDate(false);
    }

    private LocalDate getLastStoredDate() {
        return getMinOrMaxStoreDate(true);
    }

    private LocalDate getMinOrMaxStoreDate(boolean max) {
        LocalDate date = Optional.ofNullable(ofy().load().type(MeasurementDataObject.class).order(max ? "-date" : "date").limit(1).first().now())
                .map(MeasurementDataObject::toMeasurement)
                .map(Measurement::getDate)
                .orElse(null);
        logger.info("Found {} date in datastore: {}", max ? "max" : "min", date);
        return date;
    }

    private Date toDate(LocalDate localDate) {
        return Date.from(localDate.atStartOfDay(ZONE_ID).toInstant());
    }
}