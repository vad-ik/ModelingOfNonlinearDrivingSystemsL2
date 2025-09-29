package org.example.painter;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class BifurcationPythonCaller {

    public void drawBifurcation(List<double[][]> data, double start, double finish,
                                double step, String paramType, String methodName) {
        try {
            // Создаем временный файл с данными
            Path dataFile = Files.createTempFile("bifurcation_data", ".csv");
            writeBifurcationDataToCSV(data, start, finish, step, dataFile.toString());
            System.out.println("Данные бифуркации сохранены в: " + dataFile.toString());

            // Создаем Python скрипт
            Path pythonScript = createBifurcationPythonScript(dataFile.toString(), start, finish, step, paramType, methodName);

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

    private void writeBifurcationDataToCSV(List<double[][]> data, double start, double finish,
                                           double step, String filename) throws IOException {
        try (PrintWriter writer = new PrintWriter(new FileWriter(filename))) {
            // Заголовок CSV
            writer.println("param_value,amplitude,axis");

            char[] axes = {'x', 'y', 'z'};
            double paramValue = start;

            for (double[][] amplitudes : data) {
                for (int axis = 0; axis < 3; axis++) {
                    for (double amplitude : amplitudes[axis]) {
                        writer.printf(Locale.US, "%.10f,%.10f,%c%n",
                                paramValue, amplitude, axes[axis]);
                    }
                }
                paramValue += step;
            }
        }
    }

    private Path createBifurcationPythonScript(String dataFile, double start, double finish,
                                               double step, String paramType, String methodName)
            throws IOException {
        Path scriptFile = Files.createTempFile("bifurcation_script", ".py");

        String pythonCode =
                "import pandas as pd\n" +
                        "import numpy as np\n" +
                        "import matplotlib.pyplot as plt\n" +
                        "import os\n\n" +
                        "def main():\n" +
                        "    try:\n" +
                        "        # Читаем данные из CSV\n" +
                        "        print('Чтение данных бифуркации из файла...')\n" +
                        "        df = pd.read_csv('" + dataFile.replace("\\", "\\\\") + "')\n" +
                        "        print(f'Прочитано {len(df)} точек')\n" +
                        "        \n" +
                        "        # Создаем значения параметра для осей X\n" +
                        "        start = " + start + "\n" +
                        "        finish = " + finish + "\n" +
                        "        step = " + step + "\n" +
                        "        x_values = np.arange(start, finish, step)\n" +
                        "        \n" +
                        "        # Строим отдельные графики для каждой оси\n" +
                        "        axes = ['x', 'y', 'z']\n" +
                        "        colors = ['red', 'blue', 'green']\n" +
                        "        \n" +
                        "        for j, axis in enumerate(axes):\n" +
                        "            # Фильтруем данные по оси\n" +
                        "            axis_data = df[df['axis'] == axis]\n" +
                        "            \n" +
                        "            if len(axis_data) == 0:\n" +
                        "                print(f'Нет данных для оси {axis}')\n" +
                        "                continue\n" +
                        "            \n" +
                        "            # Подготовка данных для графика\n" +
                        "            x_plot = []\n" +
                        "            y_plot = []\n" +
                        "            \n" +
                        "            # Группируем по значению параметра\n" +
                        "            for i, param_val in enumerate(x_values):\n" +
                        "                if i >= len(axis_data.groupby('param_value')):\n" +
                        "                    break\n" +
                        "                # Берем все амплитуды для данного значения параметра\n" +
                        "                amplitudes = axis_data[axis_data['param_value'] == param_val]['amplitude'].values\n" +
                        "                for amplitude in amplitudes:\n" +
                        "                    x_plot.append(param_val)\n" +
                        "                    y_plot.append(amplitude)\n" +
                        "            \n" +
                        "            # Построение графика\n" +
                        "            plt.figure(figsize=(12, 8))\n" +
                        "            plt.scatter(x_plot, y_plot, color='black', s=0.1, alpha=0.7)\n" +
                        "            plt.xlabel('Значения переменной ' + '" + paramType + "')\n" +
                        "            plt.ylabel('амплитуда по оси ' + axis)\n" +
                        "            plt.title('Бифуркационная диаграмма ' + '" + methodName + "' + ' - переменная состояния ' + axis)\n" +
                        "            plt.grid(True, alpha=0.3)\n" +
//                        "            plt.show()\n" +
                        "            filename = f\"C:\\\\Users\\\\Dark Cat\\\\PycharmProjects\\\\ModNelDC\\\\out\\\\" + methodName + "\\\\diagram_{axis}_" + System.currentTimeMillis() + ".png\"\n" +
                        "            plt.savefig(filename, dpi=300, bbox_inches='tight')\n" +
                        "            plt.close()  # Важно закрыть фигуру!\n" +
                        "            \n" +
                        "            print(f'Построен график для оси {axis}')\n" +
                        "        \n" +
//                        "        # Комбинированный график всех осей\n" +
//                        "        plt.figure(figsize=(14, 10))\n" +
//                        "        \n" +
//                        "        for j, axis in enumerate(axes):\n" +
//                        "            axis_data = df[df['axis'] == axis]\n" +
//                        "            if len(axis_data) == 0:\n" +
//                        "                continue\n" +
//                        "            \n" +
//                        "            x_plot = []\n" +
//                        "            y_plot = []\n" +
//                        "            \n" +
//                        "            for i, param_val in enumerate(x_values):\n" +
//                        "                if i >= len(axis_data.groupby('param_value')):\n" +
//                        "                    break\n" +
//                        "                amplitudes = axis_data[axis_data['param_value'] == param_val]['amplitude'].values\n" +
//                        "                for amplitude in amplitudes:\n" +
//                        "                    x_plot.append(param_val)\n" +
//                        "                    y_plot.append(amplitude)\n" +
//                        "            \n" +
//                        "            plt.scatter(x_plot, y_plot, color=colors[j], s=0.1, alpha=0.5, label='Ось ' + axis)\n" +
//                        "        \n" +
//                        "        plt.xlabel('Значения переменной " + paramType + "')\n" +
//                        "        plt.ylabel('Амплитуда')\n" +
//                        "        plt.title('Бифуркационная диаграмма " + methodName + " (все оси)')\n" +
//                        "        plt.legend()\n" +
//                        "        plt.grid(True, alpha=0.3)\n" +
//                        "        plt.show()\n" +
                        "        \n" +
                        "        print('Все графики построены успешно')\n" +
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