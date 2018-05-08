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

    public static SensorCriteria.Builder sensorsMatchingCriteria() {
        return new Builder();
    }

    static class Builder {
        private Range<LocalDate> dateRange = Range.closed(LocalDate.MIN,LocalDate.MAX);
        private Range<Integer> hoursRange = Range.closed(0,23);
        private float maxExceededDaysPercentage = (float) 1.0;
        private float maxLimitExceed = Float.MAX_VALUE;
        private int maxExceededHours = 24;

        Builder withinDateRange(LocalDate from, LocalDate to) {
            Preconditions.checkArgument(from.isBefore(to) || from.isEqual(to));
            dateRange = Range.closed(from, to);
            return this;
        }

        Builder betweenHours(int from, int to) {
            Preconditions.checkArgument(from >= 0);
            Preconditions.checkArgument(to <= 23);
            Preconditions.checkArgument(from <= to);
            hoursRange = Range.closed(from, to);
            return this;
        }

        Builder maxPercentageOfDaysExceeded(float maxExceededDaysPercentage) {
            Preconditions.checkArgument(maxExceededDaysPercentage >= 0.0);
            Preconditions.checkArgument(maxExceededDaysPercentage <= 1.0);
            this.maxExceededDaysPercentage = maxExceededDaysPercentage;
            return this;
        }

        Builder maxAllowedLimitExceed(float maxLimitExceed) {
            Preconditions.checkArgument(maxLimitExceed > 0);
            this.maxLimitExceed = maxLimitExceed;
            return this;
        }

        Builder maxNumberOfHoursExceededADay(int maxExceededHours) {
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
