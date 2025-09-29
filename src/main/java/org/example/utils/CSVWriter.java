package org.example.utils;

import java.io.*;
import java.util.List;
import java.util.Locale;

public class CSVWriter {

    public static void saveDataToCSV(List<double[]> data, String filename,
                                     ParamType paramType, int axis, String methodName) {
        try (PrintWriter writer = new PrintWriter(new FileWriter(filename))) {
            // Заголовок
            writer.println("parameter_value,amplitude,axis,method,param_type");

            // Данные
            for (double[] row : data) {
                writer.printf(Locale.US, "%.10f,%.10f,%s,%s,%s%n",
                        row[0], row[1], getAxisChar(axis), methodName, paramType.name());
            }

            System.out.println("Saved: " + filename);

        } catch (IOException e) {
            System.err.println("Error saving CSV: " + e.getMessage());
        }
    }

    private static char getAxisChar(int axis) {
        switch (axis) {
            case 0: return 'x';
            case 1: return 'y';
            case 2: return 'z';
            default: return ' ';
        }
    }
}