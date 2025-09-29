package org.example.method;

import org.example.function.Function;
import org.example.function.LorenzAttractor;

public class CD implements Method {
    public double[][] getAns(double time, double h, double[] X, double[] a, Function func) {
        int steps = (int)(time / h) + 1;
        double[][] X1 = new double[steps][3];

        // Копируем начальные условия
        System.arraycopy(X, 0, X1[0], 0, 3);

        for (int i = 0; i < steps - 1; i++) {
            double h1 = h * a[0];
            double h2 = h * (1 - a[0]);

            // Вычисляем временные значения
            double tmp0 = X1[i][0] + h1 * func.getdX(X1[i], a);
            double tmp1 = X1[i][1] + h1 * func.getdY(X1[i], a);
            double tmp2 = X1[i][2] + h1 * func.getdZ(X1[i], a);

            // Обратный порядок вычислений (сначала z, потом y, потом x)
            X1[i + 1][2] = (tmp2 + h2 * a[2]) / (1 - h2 * (tmp0 - a[3]));
            X1[i + 1][1] = (tmp1 + h2 * tmp0) / (1 - h2 * a[1]);
            X1[i + 1][0] = tmp0 + h2 * (-X1[i + 1][1] - X1[i + 1][2]);

//            X1[i + 1][2] = tmp2+h2*(a[3]-tmp0*tmp0);
//            X1[i + 1][1] = (tmp1+h2*tmp0) / (1 +h2* a[2]);
//            X1[i + 1][0] = tmp0 + h2 * (a[1]* X1[i + 1][1]* X1[i + 1][2]);

//            X1[i + 1][2] = (tmp2 + h2 * tmp1*tmp0) / (1 + h2 * (a[3]));
//            X1[i + 1][1] = (tmp1 + h2 * (a[2]-X1[i + 1][2])*tmp0) / (1 + h2 );
//            X1[i + 1][0] = (tmp0 + h2 * X1[i + 1][1]) / (1 + h2 * (a[1]));
        }

        return X1;
    }


    public String getName() {
        return "CD";
    }


}
