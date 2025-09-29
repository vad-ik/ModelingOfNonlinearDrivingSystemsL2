package org.example.method;

import org.example.function.Function;
import org.example.function.LorenzAttractor;

public class RungeKutta4  implements Method{

        public double[][] getAns(double time, double h, double[] X, double[] a, Function func) {
            int steps = (int)(time / h) + 1;
            double[][] X1 = new double[steps][3];

            // Копируем начальные условия
            System.arraycopy(X, 0, X1[0], 0, 3);

            // Временные массивы для вычислений
            double[] k1 = new double[3];
            double[] k2 = new double[3];
            double[] k3 = new double[3];
            double[] k4 = new double[3];
            double[] X_temp = new double[3];

            for (int i = 0; i < steps - 1; i++) {
                // k1
                k1[0] = func.getdX(X1[i], a);
                k1[1] = func.getdY(X1[i], a);
                k1[2] = func.getdZ(X1[i], a);

                // k2
                X_temp[0] = X1[i][0] + 0.5 * h * k1[0];
                X_temp[1] = X1[i][1] + 0.5 * h * k1[1];
                X_temp[2] = X1[i][2] + 0.5 * h * k1[2];

                k2[0] = func.getdX(X_temp, a);
                k2[1] = func.getdY(X_temp, a);
                k2[2] = func.getdZ(X_temp, a);

                // k3
                X_temp[0] = X1[i][0] + 0.5 * h * k2[0];
                X_temp[1] = X1[i][1] + 0.5 * h * k2[1];
                X_temp[2] = X1[i][2] + 0.5 * h * k2[2];

                k3[0] = func.getdX(X_temp, a);
                k3[1] = func.getdY(X_temp, a);
                k3[2] = func.getdZ(X_temp, a);

                // k4
                X_temp[0] = X1[i][0] + h * k3[0];
                X_temp[1] = X1[i][1] + h * k3[1];
                X_temp[2] = X1[i][2] + h * k3[2];

                k4[0] = func.getdX(X_temp, a);
                k4[1] = func.getdY(X_temp, a);
                k4[2] = func.getdZ(X_temp, a);

                // Финальное вычисление
                for (int j = 0; j < 3; j++) {
                    X1[i + 1][j] = X1[i][j] + (h / 6.0) * (k1[j] + 2 * k2[j] + 2 * k3[j] + k4[j]);
                }
            }

            return X1;
        }

    @Override
    public String getName() {
        return "RungeKutta4";
    }
}
