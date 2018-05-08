package pl.wsowa.krakowsmog.domain;

public class PM10 extends PolutionIndcator {
    public PM10(float value) {
        super(value);
    }

    @Override
    protected double getLimit() {
        return 50.0;
    }
}
