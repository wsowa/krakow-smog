package pl.wsowa.krakowsmog.datastore;

import com.google.common.collect.Range;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.Objects;

public class CacheKey implements Serializable {
    long date;
    int minHour;
    int maxHour;
    public CacheKey(LocalDate date, Range<Integer> hoursRange) {
        this.date = date.toEpochDay();
        minHour = hoursRange.hasLowerBound() ? hoursRange.lowerEndpoint() : -1;
        maxHour = hoursRange.hasUpperBound() ? hoursRange.upperEndpoint() : -1;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CacheKey cacheKey = (CacheKey) o;
        return date == cacheKey.date &&
                minHour == cacheKey.minHour &&
                maxHour == cacheKey.maxHour;
    }

    @Override
    public int hashCode() {

        return Objects.hash(date, minHour, maxHour);
    }
}
