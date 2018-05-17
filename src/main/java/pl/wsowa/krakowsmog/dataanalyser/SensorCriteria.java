package pl.wsowa.krakowsmog.dataanalyser;

import com.google.common.base.Preconditions;
import com.google.common.collect.Range;

import java.time.LocalDate;

public class SensorCriteria {

    public final Range<LocalDate> dateRange;
    public final Range<Integer> hoursRange;
    public final float maxExceededDaysPercentage;
    public final float maxLimitExceed;
    public final int maxExceededHours;

    public SensorCriteria(Range<LocalDate> dateRange, Range<Integer> hoursRange, float maxExceededDaysPercentage, float maxLimitExceed, int maxExceededHours) {

        this.dateRange = dateRange;
        this.hoursRange = hoursRange;
        this.maxLimitExceed = maxLimitExceed;
        this.maxExceededHours = maxExceededHours;
        this.maxExceededDaysPercentage = maxExceededDaysPercentage;
    }

    @Override
    public String toString() {
        return "SensorCriteria{" +
                "dateRange=" + dateRange +
                ", hoursRange=" + hoursRange +
                ", maxExceededDaysPercentage=" + maxExceededDaysPercentage +
                ", maxLimitExceed=" + maxLimitExceed +
                ", maxExceededHours=" + maxExceededHours +
                '}';
    }

    public static SensorCriteria.Builder sensorsMatchingCriteria() {
        return new Builder();
    }

    static class Builder {
        private Range<LocalDate> dateRange = Range.all();
        private Range<Integer> hoursRange = Range.closed(0,23);
        private float maxExceededDaysPercentage = (float) 1.0;
        private float maxLimitExceed = Float.MAX_VALUE;
        private int maxExceededHours = 24;

        Builder withinDateRange(LocalDate from, LocalDate to) {
            dateRange =
                    (from == null && to == null) ? Range.all() :
                    (from == null) ? Range.atMost(to) :
                    (to == null) ? Range.atLeast(from) :
                            Range.closed(from, to);
            return this;
        }

        Builder betweenHours(Integer from, Integer to) {
            from = from == null ? 0 : from;
            to = to == null ? 23 : to;
            Preconditions.checkArgument(from >= 0);
            Preconditions.checkArgument(to <= 23);
            Preconditions.checkArgument(from <= to);
            hoursRange = Range.closed(from, to);
            return this;
        }

        Builder maxPercentageOfDaysExceeded(Float maxExceededDaysPercentage) {
            maxExceededDaysPercentage = maxExceededDaysPercentage == null ? this.maxExceededDaysPercentage : maxExceededDaysPercentage;
            Preconditions.checkArgument(maxExceededDaysPercentage >= 0.0);
            Preconditions.checkArgument(maxExceededDaysPercentage <= 1.0);
            this.maxExceededDaysPercentage = maxExceededDaysPercentage;
            return this;
        }

        Builder maxAllowedLimitExceed(Float maxLimitExceed) {
            maxLimitExceed = maxLimitExceed == null ? this.maxLimitExceed : maxLimitExceed;
            Preconditions.checkArgument(maxLimitExceed > 0);
            this.maxLimitExceed = maxLimitExceed;
            return this;
        }

        Builder maxNumberOfHoursExceededADay(Integer maxExceededHours) {
            maxExceededHours = maxExceededHours == null ? this.maxExceededHours : maxExceededHours;
            Preconditions.checkArgument(maxExceededHours >= 0);
            Preconditions.checkArgument(maxExceededHours <= 24);
            this.maxExceededHours = maxExceededHours;
            return this;
        }

        SensorCriteria build() {
            return new SensorCriteria(dateRange, hoursRange, maxExceededDaysPercentage, maxLimitExceed, maxExceededHours);
        }
    }
}
