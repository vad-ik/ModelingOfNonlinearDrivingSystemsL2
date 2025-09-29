package org.example.method;

import org.example.function.Function;
import org.example.function.LorenzAttractor;

public interface Method {
    public double[][] getAns(double time, double h, double[] X, double[] a, Function func) ;
    public String getName();
}
