package pl.wsowa.krakowsmog.dataanalyser;

import java.time.LocalDate;

public class SensorCriteriaParam {
    private LocalDate minDate;
    private LocalDate maxDate;
    private Integer minHour;
    private Integer maxHour;
    private Float maxExceededDaysPercentage;
    private Float maxLimitExceed;
    private Integer maxExceededHours;

    public void setMinDate(LocalDate minDate) {
        this.minDate = minDate;
    }

    public void setMaxDate(LocalDate maxDate) {
        this.maxDate = maxDate;
    }

    public void setMinHour(Integer minHour) {
        this.minHour = minHour;
    }

    public void setMaxHour(Integer maxHour) {
        this.maxHour = maxHour;
    }

    public void setMaxExceededDaysPercentage(Float maxExceededDaysPercentage) {
        this.maxExceededDaysPercentage = maxExceededDaysPercentage;
    }

    public void setMaxLimitExceed(Float maxLimitExceed) {
        this.maxLimitExceed = maxLimitExceed;
    }

    public void setMaxExceededHours(Integer maxExceededHours) {
        this.maxExceededHours = maxExceededHours;
    }

    public SensorCriteria toCriteria() {
        return SensorCriteria.sensorsMatchingCriteria()
                .withinDateRange(minDate, maxDate)
                .betweenHours(minHour, maxHour)
                .maxAllowedLimitExceed(maxLimitExceed)
                .maxNumberOfHoursExceededADay(maxExceededHours)
                .maxPercentageOfDaysExceeded(maxExceededDaysPercentage)
                .build();
    }

    @Override
    public String toString() {
        return "SensorCriteriaParam{" +
                "minDate=" + minDate +
                ", maxDate=" + maxDate +
                ", minHour=" + minHour +
                ", maxHour=" + maxHour +
                ", maxExceededDaysPercentage=" + maxExceededDaysPercentage +
                ", maxLimitExceed=" + maxLimitExceed +
                ", maxExceededHours=" + maxExceededHours +
                '}';
    }
}
