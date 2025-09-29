package org.example.analysis;

import org.example.function.Function;
import org.example.method.Method;
import org.example.painter.BifurcationPythonCaller;
import org.example.utils.ParamType;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

public class BifurcationAnalyzer {

    public void analyze(Method method, double[] X, double[] a, double h,
                        double time, double start, double finish, double step,
                        ParamType paramType, String name, double skip, Function func) {

        List<double[][]> data = collectBifurcationData(method, X, a, h, time,
                start, finish, step, paramType, skip, func);
        BifurcationPythonCaller plotter = new BifurcationPythonCaller();
        plotter.drawBifurcation(data, start, finish, step, paramType.name(), method.getName());
    }

    public List<double[][]> collectBifurcationData(Method method, double[] X, double[] a,
                                                   double h, double time, double start,
                                                   double finish, double step, ParamType paramType,
                                                   double skip, Function func) {

        List<BifurcationTask> tasks = new ArrayList<>();
        int totalTasks = (int) ((finish - start) / step);

        // Создаем задачи для параллельного выполнения
        for (double paramValue = start; paramValue < finish; paramValue += step) {
            BifurcationTask task = createTask(method, X, a.clone(), h, time,
                    paramValue, paramType, skip, func);
            tasks.add(task);
        }

        // Параллельное выполнение
        ExecutorService executor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors() - 3);
        List<double[][]> results = new ArrayList<>();

        try {
            // Используем CompletionService для прогресса
            CompletionService<double[][]> completionService = new ExecutorCompletionService<>(executor);

            // Отправляем все задачи
            List<Future<double[][]>> futures = new ArrayList<>();
            for (BifurcationTask task : tasks) {
                Future<double[][]> future = completionService.submit(task);
                futures.add(future);
            }

            // Собираем результаты с прогрессом
            System.out.println("Method: " + method.getName());
            for (int i = 0; i < tasks.size(); i++) {
                try {
                    double[][] result = completionService.take().get();
                    results.add(result);

                    // Прогресс
                    if ((i+1) % 25 == 0||i== tasks.size()-1) {
                        System.out.printf("\rProgress: %d/%d", i + 1, tasks.size());
                    }
                } catch (InterruptedException | ExecutionException e) {
                    System.err.println("Task failed: " + e.getMessage());
                    results.add(new double[3][0]); // Пустой результат при ошибке
                }
            }
            System.out.println();
        } finally {
            executor.shutdown();
        }

        return results;
    }

    private BifurcationTask createTask(Method method, double[] X, double[] a,
                                       double h, double time, double paramValue,
                                       ParamType paramType, double skip, Function func) {

        // Настраиваем параметры согласно типу
        switch (paramType) {
            case H:
                h = paramValue;
//                h=0.0001*Math.pow(1.0139,paramValue);
                skip = 60.0 / h;
                break;
            case A0:
                a[0] = paramValue;
                break;
            case A1:
                a[1] = paramValue;
                break;
            case A2:
                a[2] = paramValue;
                break;
            case A3:
                a[3] = paramValue;
                break;
            case A4:
                a[4] = paramValue;
                break;
        }

        return new BifurcationTask(method, time, h, X.clone(), a, func, skip);
    }


    private char getAxisChar(int axis) {
        switch (axis) {
            case 0:
                return 'x';
            case 1:
                return 'y';
            case 2:
                return 'z';
            default:
                return ' ';
        }
    }
}