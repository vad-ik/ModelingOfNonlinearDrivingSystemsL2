package org.example.method;

import org.example.function.Function;
import org.example.function.LorenzAttractor;


public class RungeKutta2 implements Method {
    public double[][] getAns(double time, double h, double[] X, double[] a, Function func) {
        int steps = (int) (time / h) + 1;
        double[][] X1 = new double[steps][3]; // 3 столбца (x, y, z)

        // Установка начальных условий
        System.arraycopy(X, 0, X1[0], 0, 3);
        double[] Xtemp = new double[3];

        for (int i = 0; i < steps - 1; i++) {
            double dx = func.getdX(X1[i], a);
            double dy = func.getdY(X1[i], a);
            double dz = func.getdZ(X1[i], a);

            Xtemp[0] = X1[i][0] + 0.5 * h * dx;
            Xtemp[1] = X1[i][1] + 0.5 * h * dy;
            Xtemp[2] = X1[i][2] + 0.5 * h * dz;

            dx = func.getdX(Xtemp, a);
            dy = func.getdY(Xtemp, a);
            dz = func.getdZ(Xtemp, a);

            X1[i + 1][0] = X1[i][0] + h * dx;
            X1[i + 1][1] = X1[i][1] + h * dy;
            X1[i + 1][2] = X1[i][2] + h * dz;
        }

        return X1;
    }

    public String getName() {
        return "Метод средней точки";
    }
}


