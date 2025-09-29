package org.example;

import org.example.analysis.BifurcationAnalyzer;
import org.example.analysis.BifurcationAnalyzer2D;
import org.example.function.Function;
import org.example.function.LorenzAttractor;
import org.example.function.Na;
import org.example.method.*;
import org.example.painter.PythonCaller;
import org.example.utils.ParamType;

import java.io.IOException;

public class Main {
    static PythonCaller pythonCaller = new PythonCaller();


    static void solve(Method method, double time, double h, double[] X, double[] a, Function attractor) {
        double[][] X1 = method.getAns(time, h, X, a, attractor);
        System.out.println(method.getName()+" finish");
        pythonCaller.draw(X1, h, method.getName(), false);
    }
    public static void main(String[] args) throws IOException {
        BifurcationAnalyzer analyzer = new BifurcationAnalyzer();
        BifurcationAnalyzer2D analyzer2D=new BifurcationAnalyzer2D();
        LorenzAttractor attractor = new LorenzAttractor();

        double[] a = {1, 10.0,28.0, 8.0 / 3};
        double[] X = {0.1, 0.1, 0.1};
        double time = 100;
        double h = 0.0001;


//        double[] a = {1, 10.0,202.0, 8.0 / 3};

//        solve(new CD(),time,h,X,a,attractor);
//        solve(new RungeKutta4(),time,h,X,a,attractor);

        double maxA3=0.14;
        Method[] methods={new Eiler(),new RungeKutta2(),new CD(),new RungeKutta4()};
//        for (Method method : methods) {
            analyzer.analyze(new Eiler(), X, a, h, time,
                    0.0001, 1, 0.0001,
                    ParamType.H, "h", 60/h, attractor);
//            analyzer.analyze(method, X, a, h, time,
//                    0, 25, 25.0 / 1000,
//                    ParamType.A1, "a1", 60/h, attractor);
//            analyzer.analyze(method, X, a, h, time,
//                    0, 350, 350.0/ 1000,
//                    ParamType.A2, "a2", 60/h, attractor);
//            analyzer.analyze(method, X, a, h, time,
//                    0, 4.5, 4.5 / 1000,
//                    ParamType.A3, "a3", 60/h, attractor);
//        }
//
//        analyzer2D.analyze(new Eiler(), X, a, h, time,
//                0.0001, maxA3, maxA3 / 500,
//                ParamType.H, "h", 60/h, attractor);
//        analyzer2D.analyze(new RungeKutta2(), X, a, h, time,
//                0.0001, maxA3, maxA3 / 500,
//                ParamType.H, "h", 60/h, attractor);
//        analyzer2D.analyze(new CD(), X, a, h, time,
//                0.0001, maxA3, maxA3 / 500,
//                ParamType.H, "h", 60/h, attractor);
//        analyzer2D.analyze(new RungeKutta4(), X, a, h, time,
//                0.0001, maxA3, maxA3 / 500,
//                ParamType.H, "h", 60/h, attractor);

    }
}