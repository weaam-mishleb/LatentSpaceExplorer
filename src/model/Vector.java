package model;
public interface Vector {
    /**
      returns the raw double array representation of the vector.
     */
    double[] getValues();
    /**
      returns the dimensionality of this vector.
     */
    int getDimension();
    /**
      returns a specific value at a given index.
     */
    double getValueAt(int index);
}