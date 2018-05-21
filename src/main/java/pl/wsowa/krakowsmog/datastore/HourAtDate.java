package pl.wsowa.krakowsmog.datastore;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.Objects;

class HourAtDate implements Serializable {
    LocalDate date;
    int hour;

    public HourAtDate(LocalDate date, int hour) {
        this.date = date;
        this.hour = hour;
    }

    @Override
    public String toString() {
        return date + "/" + hour;
    }

    @Override
    public boolean equals(Object o) {

        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        HourAtDate that = (HourAtDate) o;
        return hour == that.hour &&
                Objects.equals(date, that.date);
    }

    @Override
    public int hashCode() {

        return Objects.hash(date, hour);
    }
}
