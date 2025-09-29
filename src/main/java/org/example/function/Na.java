package org.example.function;

public class Na implements  Function {
    public double getdX(double[] X, double[] a) {
        return( a[1] * (X[1] * X[2]));
    }
    public double getdY(double[] X, double[] a) {
        return X[0] - (a[2] ) *X[1];
    }
    public double getdZ(double[] X, double[] a) {
        return  a[3] - X[0]*X[0];
    }
}
