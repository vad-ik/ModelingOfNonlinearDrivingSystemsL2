package org.example.painter;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.example.utils.save.writeDataToCSV;

public class PythonCaller {

    public void draw(double[][] X, double h, String name, boolean mode) {
        try {
            // Создаем временный файл с данными
            Path dataFile = Files.createTempFile("plot_data", ".csv");
            writeDataToCSV(X, dataFile.toString());
            System.out.println("Данные сохранены в: " + dataFile.toString());

            // Создаем Python скрипт
            Path pythonScript = createPythonScript(dataFile.toString(), h, name, mode ? "True" : "False");

            // Запускаем Python
            ProcessBuilder pb = new ProcessBuilder("python", pythonScript.toString());
            pb.redirectErrorStream(true);
            Process process = pb.start();

            // Читаем вывод
            try (BufferedReader reader = new BufferedReader(
                    new InputStreamReader(process.getInputStream()))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    System.out.println("Python: " + line);
                }
            }

            int exitCode = process.waitFor();

            // Удаляем временные файлы
            Files.deleteIfExists(dataFile);
            Files.deleteIfExists(pythonScript);

            System.out.println("Python process exited with code: " + exitCode);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private static Path createPythonScript(String dataFile, double h, String name, String mode)
            throws IOException {
        Path scriptFile = Files.createTempFile("plot_script", ".py");

        String pythonCode =
                "import pandas as pd\n" +
                        "import numpy as np\n" +
                        "import matplotlib.pyplot as plt\n" +
                        "import os\n\n" +
                        "def main():\n" +
                        "    try:\n" +
                        "        # Читаем данные из CSV\n" +
                        "        print('Чтение данных из файла...')\n" +
                        "        df = pd.read_csv('" + dataFile.replace("\\", "\\\\") + "')\n" +
                        "        print(f'Прочитано {len(df)} точек')\n" +
                        "        \n" +
                        "        # Извлекаем координаты\n" +
                        "        X = df['x'].values\n" +
                        "        Y = df['y'].values\n" +
                        "        Z = df['z'].values\n" +
                        "        \n" +
                        "        # Создаем временную ось\n" +
                        "        time = np.arange(0, len(X) * " + h + ", " + h + ")\n" +
                        "        if len(time) > len(X):\n" +
                        "            time = time[:len(X)]\n" +
                        "        \n" +
                        "        # Строим графики\n" +
                        "        print('Построение графиков...')\n" +
                        "        plt.figure(figsize=(18, 10))\n" +
                        "        \n" +
                        "        # X vs Y\n" +
                        "        plt.subplot2grid((2, 3), (0, 0))\n" +
                        "        plt.plot(X, Y, linewidth=0.5)\n" +
                        "        plt.xlabel('X')\n" +
                        "        plt.ylabel('Y')\n" +
                        "        \n" +
                        "        # X vs Z\n" +
                        "        plt.subplot2grid((2, 3), (0, 1))\n" +
                        "        plt.plot(X, Z, linewidth=0.5)\n" +
                        "        plt.xlabel('X')\n" +
                        "        plt.ylabel('Z')\n" +
                        "        \n" +
                        "        # Y vs Z\n" +
                        "        plt.subplot2grid((2, 3), (0, 2))\n" +
                        "        plt.plot(Y, Z, linewidth=0.5)\n" +
                        "        plt.xlabel('Y')\n" +
                        "        plt.ylabel('Z')\n" +
                        "        \n" +
                        "        # Временные ряды\n" +
                        "        plt.subplot2grid((2, 3), (1, 0), colspan=3, rowspan=2)\n" +
                        "        if " + mode + ":\n" +
                        "            plt.semilogy(time, X, 'b-', label='X(t)', linewidth=0.5)\n" +
                        "            plt.semilogy(time, Y, 'r-', label='Y(t)', linewidth=0.5)\n" +
                        "            plt.semilogy(time, Z, 'g-', label='Z(t)', linewidth=0.5)\n" +
                        "        else:\n" +
                        "            plt.plot(time, X, 'b-', label='X(t)', linewidth=0.5)\n" +
                        "            plt.plot(time, Y, 'r-', label='Y(t)', linewidth=0.5)\n" +
                        "            plt.plot(time, Z, 'g-', label='Z(t)', linewidth=0.5)\n" +
                        "        \n" +
                        "        plt.xlabel('Время')\n" +
                        "        plt.ylabel('Значения')\n" +
                        "        plt.title('Временные ряды')\n" +
                        "        plt.legend()\n" +
                        "        plt.grid(True, alpha=0.3)\n" +
                        "        \n" +
                        "        plt.suptitle('" + name.replace("'", "\\'") + "')\n" +
                        "        plt.tight_layout()\n" +
                        "        plt.show()\n" +
                        "        print('График построен успешно')\n" +
                        "        \n" +
                        "    except Exception as e:\n" +
                        "        print('Ошибка:', e)\n" +
                        "        import traceback\n" +
                        "        traceback.print_exc()\n" +
                        "        return 1\n" +
                        "    return 0\n\n" +
                        "if __name__ == '__main__':\n" +
                        "    exit(main())\n";

        Files.write(scriptFile, pythonCode.getBytes());
        return scriptFile;
    }
}