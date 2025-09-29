package org.example.method;

import org.example.function.Function;
import org.example.function.LorenzAttractor;

public class Eiler implements Method {

    public double[][] getAns(double time, double h, double[] X, double[] a, Function func) {

        int steps = (int) ((time / h) + 1);
        double[][] X1 =new double[steps][3];
        for (int i = 0; i < 3; i++) {
            X1[0][i] = X[i];
        }

        for (int i = 0; i <steps-1 ; i++) {
            double dx = func.getdX(X1[i], a);
            double dy = func.getdY(X1[i], a);
            double dz = func.getdZ(X1[i], a);

            X1[i + 1][0] = X1[i][0] + h * dx;
            X1[i + 1][1] = X1[i][1] + h * dy;
            X1[i + 1][2] = X1[i][2] + h * dz;
        }



        return X1;
    }
    public String getName() {

        return "Eiler";
    }
}
