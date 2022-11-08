package com.team1091.tanks.nn;

public class Test {
    static double[][] input = {
            {0, 0},
            {1, 0},
            {0, 1},
            {1, 1}
    };
    static double[][] answers = {
            {0}, {1}, {1}, {0}
    };

    public static void main(String[] args) {
        NeuralNetwork nn = new NeuralNetwork(2, 10, 1);

        nn.fit(input, answers, 50000);

        double[][] input = {{0, 0}, {0, 1}, {1, 0}, {1, 1}};
        for (double d[] : input) {
            var output = nn.predict(d);
            System.out.println(output.toString());
            output.stream().forEach((it) -> System.out.println(it > 0.5));
        }
    }
}
