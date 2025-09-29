package org.example.analysis;

import org.example.function.Function;
import org.example.method.Method;
import org.example.painter.HeatmapPythonCaller;
import org.example.utils.ParamType;

import java.util.List;

import static org.example.utils.ClusterUtils.clusterPeaks;


public class BifurcationAnalyzer2D {

    HeatmapPythonCaller heatmapPythonCaller = new HeatmapPythonCaller();
    BifurcationAnalyzer bifurcationAnalyzer = new BifurcationAnalyzer();

    public void analyze(Method method, double[] X, double[] a, double h,
                        double time, double start, double finish, double step,
                        ParamType paramType, String name, double skip, Function func) {

        int debag = 0;
        int maxA2 =1000;

        double[] koefNum = generateRangeArray(start, finish, step);
//        double[] koefNum = new double[500];
//        for (int i = 0; i < 500; i++) {
//            koefNum[i]=0.0001*Math.pow(1.0139,i);
//        }
        double[] a2Num = generateRangeArray(0, maxA2, maxA2 / 500.0);
        double[][][] data = new double[a2Num.length][koefNum.length][3];
        for (int j = 0; j < a2Num.length; j++) {

            double i = a2Num[j];
            System.out.println(debag);
            debag++;

            double[] aCopy = a.clone();
            aCopy[2] = i;

            List<double[][]> peaks = bifurcationAnalyzer.collectBifurcationData(
                    method, X, aCopy, h, time, start, finish, step, paramType, skip, func
            );

            // result = количество кластеров в каждом ряду
            double[][] result = new double[peaks.size()][];
            for (int rowIdx = 0; rowIdx < peaks.size(); rowIdx++) {
                double[][] row = peaks.get(rowIdx);
                result[rowIdx] = new double[row.length];

                for (int colIdx = 0; colIdx < row.length; colIdx++) {
                    double[] peakArray = row[colIdx];
                    double[] clusters = clusterPeaks(peakArray);
                    result[rowIdx][colIdx] = clusters.length;
                }
            }

            data[j] = (result);
        }

        heatmapPythonCaller.drawHeatmap(data, method.getName(),a2Num, koefNum);
        System.out.println("Сохранили данные для метода: " + method.getName());
    }

    public double[] generateRangeArray(double start, double finish, double step) {
        // Рассчитываем размер массива
        int size = (int) Math.ceil((finish - start) / step);
        double[] result = new double[size];

        for (int i = 0; i < size; i++) {
            result[i] = start + i * step;
        }
        return result;
    }
}
