package pl.wsowa.krakowsmog.domain;

public class PM25 extends PolutionIndcator {
    public PM25(float value) {
        super(value);
    }

    @Override
    protected double getLimit() {
        return 25.0;
    }
}
