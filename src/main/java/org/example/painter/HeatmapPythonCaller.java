package org.example.painter;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.util.Locale;

public class HeatmapPythonCaller {

    public void drawHeatmap(double[][][] data, String name, double[] a2Values, double[] hValues) {
        try {
            // Создаем временный файл с данными
            Path dataFile = Files.createTempFile("heatmap_data", ".csv");
            writeHeatmapDataToCSV(data, a2Values, hValues, dataFile.toString());
            System.out.println("Данные тепловой карты сохранены в: " + dataFile.toString());

            // Создаем Python скрипт
            Path pythonScript = createHeatmapPythonScript(dataFile.toString(), name, a2Values, hValues);

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

    private void writeHeatmapDataToCSV(double[][][] data, double[] a2Values, double[] hValues, String filename)
            throws IOException {
        try (PrintWriter writer = new PrintWriter(new FileWriter(filename))) {
            // Заголовок CSV - используем простые названия без специальных символов
            writer.println("a2,h,x,y,z");

            // Создаем форматтер с явными настройками
            DecimalFormat df = new DecimalFormat("0.##################");
            DecimalFormatSymbols symbols = new DecimalFormatSymbols(Locale.US);
            symbols.setDecimalSeparator('.');
            df.setDecimalFormatSymbols(symbols);

            for (int i = 0; i < a2Values.length; i++) {
                for (int j = 0; j < hValues.length; j++) {
                    // Форматируем каждое значение отдельно
                    String a2Str = df.format(a2Values[i]);
                    String hStr = df.format(hValues[j]);
                    String xStr = df.format(data[i][j][0]);
                    String yStr = df.format(data[i][j][1]);
                    String zStr = df.format(data[i][j][2]);

                    // Записываем строку с явным разделителем
                    writer.println(a2Str + "," + hStr + "," + xStr + "," + yStr + "," + zStr);
                }
            }
        }
    }
    private Path createHeatmapPythonScript(String dataFile, String name, double[] a2Values, double[] hValues)
            throws IOException {
        Path scriptFile = Files.createTempFile("heatmap_script", ".py");

        String pythonCode =
                "import pandas as pd\n" +
                        "import numpy as np\n" +
                        "import matplotlib.pyplot as plt\n" +
                        "import seaborn as sns\n" +
                        "import os\n\n" +
                        "def main():\n" +
                        "    try:\n" +
                        "        # Читаем данные из CSV\n" +
                        "        print('Чтение данных тепловой карты...')\n" +
                        "        df = pd.read_csv('" + dataFile.replace("\\", "\\\\") + "')\n" +
                        "        \n" +
                        "        # Получаем уникальные значения параметров\n" +
                        "        a2_values = sorted(df['a2'].unique())\n" +
                        "        h_values = sorted(df['h'].unique())\n" +
                        "        \n" +
                        "        print(f'Размерность: {len(a2_values)} x {len(h_values)}')\n" +
                        "        \n" +
                        "        # Создаем выходную директорию\n" +
                        "        file_path = \"C:\\\\Users\\\\Dark Cat\\\\PycharmProjects\\\\ModNelDC\\\\out\\\\2d\\\\\" + '" + name + "' + \"\\\\\"\n" +
                        "        os.makedirs(file_path, exist_ok=True)\n" +
                        "        \n" +
                        "        # Создаем тепловые карты для каждой оси\n" +
                        "        for axis in ['x', 'y', 'z']:\n" +
                        "            # Создаем матрицу для тепловой карты\n" +
                        "            heatmap_data = np.zeros((len(a2_values), len(h_values)))\n" +
                        "            \n" +
                        "            for i, a2_val in enumerate(a2_values):\n" +
                        "                for j, h_val in enumerate(h_values):\n" +
                        "                    mask = (df['a2'] == a2_val) & (df['h'] == h_val)\n" +
                        "                    if mask.any():\n" +
                        "                        heatmap_data[i][j] = df.loc[mask, axis].iloc[0]\n" +
                        "            \n" +
                        "            # Создаем график\n" +
                        "            fig, ax = plt.subplots(figsize=(14, 10))\n" +
                        "            \n" +
                        "            # Настройка тепловой карты БЕЗ белых полосок\n" +
                        "            sns.heatmap(heatmap_data,\n" +
                        "                        cmap='plasma',\n" +
                        "                        ax=ax,\n" +
                        "                        cbar_kws={'label': 'Значение', 'shrink': 0.8},\n" +
                        "                        linewidths=0,  # Убираем белые полоски\n" +
                        "                        square=True)   # Квадратные ячейки\n" +
                        "            \n" +
                        "            # НАСТРОЙКА ПОДПИСЕЙ ОСЕЙ - показываем только каждую N-ую\n" +
                        "            step_x = max(1, len(h_values) // 15)  # Около 15 подписей по X\n" +
                        "            step_y = max(1, len(a2_values) // 15)  # Около 15 подписей по Y\n" +
                        "            \n" +
                        "            xticks_positions = list(range(0, len(h_values), step_x))\n" +
                        "            yticks_positions = list(range(0, len(a2_values), step_y))\n" +
                        "            \n" +
                        "            ax.set_xticks(xticks_positions)\n" +
                        "            ax.set_yticks(yticks_positions)\n" +
                        "            \n" +
                        "            ax.invert_yaxis()\n"+
                        "            # Подписи для выбранных позиций\n" +
                        "            ax.set_xticklabels([f'{h_values[i]:.4f}' for i in xticks_positions], \n" +
                        "                              rotation=45, ha='right', fontsize=9)\n" +
                        "            ax.set_yticklabels([f'{a2_values[i]:.2f}' for i in yticks_positions], \n" +
                        "                              fontsize=9)\n" +
                        "            \n" +
                        "            # Настраиваем подписи\n" +
                        "            ax.set_title(f'Тепловая карта: {axis}-ось ("+name+")', fontsize=16, pad=20)\n" +
                        "            ax.set_ylabel('Параметр a2', fontsize=12)\n" +
                        "            ax.set_xlabel('Параметр h', fontsize=12)\n" +
                        "            \n" +
                        "            # Сохраняем\n" +
                        "            filename = f'{file_path}heatmap_(h до 0.14; a2 да 1000){axis}.png'\n" +
                        "            plt.savefig(filename, dpi=300, bbox_inches='tight', \n" +
                        "                        facecolor='white', edgecolor='none')\n" +
                        "            plt.close()\n" +
                        "            print(f'Сохранено: {filename}')\n" +
                        "        \n" +
                        "        print('Все тепловые карты построены успешно!')\n" +
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