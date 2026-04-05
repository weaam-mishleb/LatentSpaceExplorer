package model;

import java.util.Arrays;

public class RawVector implements Vector {
    private final double[] values;

    public RawVector(double[] values) {
        if (values == null) {
            throw new IllegalArgumentException(" vector values cannot be null");
        }
        this.values = Arrays.copyOf(values, values.length);
    }

    @Override
    public double[] getValues() {
        return Arrays.copyOf(this.values, this.values.length);
    }

    @Override
    public int getDimension() {
        return values.length;
    }

    @Override
    public double getValueAt(int index) {
        return values[index];
    }
}