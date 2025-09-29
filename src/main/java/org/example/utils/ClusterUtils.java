package org.example.utils;

import java.util.*;

public class ClusterUtils {

    public static double[] clusterPeaks(double[] peaks) {
        return clusterPeaks(peaks, 0.1, 1);
    }

    public static double[] clusterPeaks(double[] peaks, double eps, int minSamples) {
        if (peaks == null || peaks.length == 0) {
            return new double[0];
        }

        // Сортируем пики для упрощения обработки
        double[] sortedPeaks = peaks.clone();
        Arrays.sort(sortedPeaks);

        int[] labels = new int[sortedPeaks.length];
        Arrays.fill(labels, -1); // -1 означает не посещенную точку
        int clusterId = 0;

        for (int i = 0; i < sortedPeaks.length; i++) {
            if (labels[i] != -1) {
                continue; // Уже посещена
            }

            // Находим соседей в пределах eps
            List<Integer> neighbors = findNeighbors(sortedPeaks, i, eps);

            if (neighbors.size() < minSamples) {
                labels[i] = -2; // Помечаем как шум
            } else {
                // Создаем новый кластер
                expandCluster(sortedPeaks, labels, i, neighbors, clusterId, eps, minSamples);
                clusterId++;
            }
        }

        return calculateClusterCenters(sortedPeaks, labels, clusterId);
    }

    private static List<Integer> findNeighbors(double[] points, int index, double eps) {
        List<Integer> neighbors = new ArrayList<>();
        double currentPoint = points[index];

        // Ищем слева
        for (int i = index; i >= 0; i--) {
            if (Math.abs(points[i] - currentPoint) <= eps) {
                neighbors.add(i);
            } else {
                break;
            }
        }

        // Ищем справа
        for (int i = index + 1; i < points.length; i++) {
            if (Math.abs(points[i] - currentPoint) <= eps) {
                neighbors.add(i);
            } else {
                break;
            }
        }

        return neighbors;
    }

    private static void expandCluster(double[] points, int[] labels, int index,
                                      List<Integer> neighbors, int clusterId,
                                      double eps, int minSamples) {
        labels[index] = clusterId;

        Queue<Integer> queue = new LinkedList<>(neighbors);

        while (!queue.isEmpty()) {
            int currentIndex = queue.poll();

            if (labels[currentIndex] == -2) {
                labels[currentIndex] = clusterId; // Шум становится частью кластера
            }

            if (labels[currentIndex] != -1) {
                continue; // Уже обработана
            }

            labels[currentIndex] = clusterId;

            List<Integer> currentNeighbors = findNeighbors(points, currentIndex, eps);
            if (currentNeighbors.size() >= minSamples) {
                queue.addAll(currentNeighbors);
            }
        }
    }

    private static double[] calculateClusterCenters(double[] points, int[] labels, int clusterCount) {
        if (clusterCount == 0) {
            return new double[0];
        }

        List<Double>[] clusters = new ArrayList[clusterCount];
        for (int i = 0; i < clusterCount; i++) {
            clusters[i] = new ArrayList<>();
        }

        for (int i = 0; i < points.length; i++) {
            if (labels[i] >= 0 && labels[i] < clusterCount) {
                clusters[labels[i]].add(points[i]);
            }
        }

        List<Double> centers = new ArrayList<>();
        for (List<Double> cluster : clusters) {
            if (!cluster.isEmpty()) {
                centers.add(calculateMean(cluster));
            }
        }

        double[] result = new double[centers.size()];
        for (int i = 0; i < centers.size(); i++) {
            result[i] = centers.get(i);
        }

        return result;
    }

    private static double calculateMean(List<Double> values) {
        double sum = 0.0;
        for (double value : values) {
            sum += value;
        }
        return sum / values.size();
    }


    // Упрощенная кластеризация для больших данных
    public static double[] simpleClusterPeaks(double[] peaks, double eps) {
        if (peaks == null || peaks.length == 0) return new double[0];

        Arrays.sort(peaks);
        List<Double> centers = new ArrayList<>();
        List<Double> currentCluster = new ArrayList<>();

        currentCluster.add(peaks[0]);

        for (int i = 1; i < peaks.length; i++) {
            if (peaks[i] - peaks[i - 1] <= eps) {
                currentCluster.add(peaks[i]);
            } else {
                // Завершаем текущий кластер
                if (!currentCluster.isEmpty()) {
                    centers.add(calculateMean(currentCluster));
                    currentCluster.clear();
                }
                currentCluster.add(peaks[i]);
            }
        }

        // Добавляем последний кластер
        if (!currentCluster.isEmpty()) {
            centers.add(calculateMean(currentCluster));
        }

        return centers.stream().mapToDouble(Double::doubleValue).toArray();
    }

}
