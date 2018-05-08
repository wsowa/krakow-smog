package pl.wsowa.krakowsmog.domain;

import java.util.Objects;

public abstract class PolutionIndcator {
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PolutionIndcator that = (PolutionIndcator) o;
        return Float.compare(that.value, value) == 0;
    }

    @Override
    public int hashCode() {

        return Objects.hash(value);
    }

    protected final float value;

    protected PolutionIndcator(float value) {
        this.value = value;
    }

    public boolean withinLimit(float maxLimitExceed) {
        return getLimit() * maxLimitExceed >= value;
    }

    public float getValue() {
        return value;
    }

    protected abstract double getLimit();
}
