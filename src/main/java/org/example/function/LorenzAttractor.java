package org.example.function;

public class LorenzAttractor implements Function {
    public double getdX(double[] X, double[] a) {
        return( a[1] * (X[1] - X[0]));
    }
    public double getdY(double[] X, double[] a) {
        return X[0] * (a[2] - X[2]) - X[1];
    }
    public double getdZ(double[] X, double[] a) {
        return X[0] * X[1] - a[3] * X[2];
    }
}
