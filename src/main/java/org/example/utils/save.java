package org.example.utils;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

public class save {
    public static void writeDataToCSV(double[][] X, String filename) throws IOException {
        try (PrintWriter writer = new PrintWriter(new FileWriter(filename))) {
            // Записываем заголовок
            writer.println("step,x,y,z");


            // Записываем данные
            for (int i = 0; i < X.length; i++) {
                writer.print(
                        i+","+
                                String.valueOf(X[i][0]).replace(",",".")+","+
                                String.valueOf(X[i][1]).replace(",",".")+","+
                                String.valueOf(X[i][2]).replace(",",".")+"\n");
            }
        }
    }
}
