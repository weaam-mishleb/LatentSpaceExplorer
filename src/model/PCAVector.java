package model;
import java.util.Arrays;
public class PCAVector implements Vector {
    private final double[] values;
    public PCAVector(double[] values) {
        if (values == null || values.length != 3) {
            throw new IllegalArgumentException("vector must have exactly 3 dimensions");
        }
        this.values = Arrays.copyOf(values, values.length);
    }
    @Override
    public double[] getValues() {
        return Arrays.copyOf(this.values, this.values.length);
    }
    @Override
    public int getDimension() {
        return 3;
    }
    @Override
    public double getValueAt(int index) {
        return values[index];
    }
}