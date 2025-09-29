package org.example.analysis;

import org.example.function.Function;
import org.example.method.Method;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

public class BifurcationTask implements Callable<double[][]> {
    private final Method method;
    private final double time;
    private final double h;
    private final double[] X;
    private final double[] a;
    private final Function func;
    private final double skip;

    public BifurcationTask(Method method, double time, double h,
                           double[] X, double[] a, Function func, double skip) {
        this.method = method;
        this.time = time;
        this.h = h;
        this.X = X;
        this.a = a;
        this.func = func;
        this.skip = skip;
    }

    @Override
    public double[][] call() throws Exception {
        // Вычисляем решение
        double[][] solution = method.getAns(time, h, X, a, func);

        // Обрезаем начальные точки
        double[][] trimmed = trimInitialPoints(solution, (int)skip);

        // Находим амплитуды пиков для каждой оси
        return findPeaksAmplitudes(trimmed);
    }

    private double[][] trimInitialPoints(double[][] solution, int skipPoints) {
        int totalPoints = solution.length;
        int remainingPoints = totalPoints - skipPoints;

        if (remainingPoints <= 0) {
            return solution; // Возвращаем все, если обрезать нечего
        }

        double[][] result = new double[remainingPoints][3];
        for (int i = 0; i < remainingPoints; i++) {
            System.arraycopy(solution[i + skipPoints], 0, result[i], 0, 3);
        }
        return result;
    }

    private double[][] findPeaksAmplitudes(double[][] data) {
        double[][] amplitudes = new double[3][];

        for (int axis = 0; axis < 3; axis++) {
            double[] axisData = extractAxisData(data, axis);
            double[] peaks = findPeaks(axisData);
            amplitudes[axis] = peaks.length > 0 ? peaks : new double[]{axisData[0]};
        }

        return amplitudes;
    }

    private double[] extractAxisData(double[][] data, int axis) {
        double[] result = new double[data.length];
        for (int i = 0; i < data.length; i++) {
            result[i] = data[i][axis];
        }
        return result;
    }

    private double[] findPeaks(double[] data) {
        // Простая реализация поиска пиков
        List<Double> peaks = new ArrayList<>();

        for (int i = 1; i < data.length - 1; i++) {
            if (data[i] > data[i-1] && data[i] > data[i+1]) {
                peaks.add(data[i]);
            }
        }

        return peaks.stream().mapToDouble(Double::doubleValue).toArray();
    }
}