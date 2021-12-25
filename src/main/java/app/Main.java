package app;

import engine.Engine;
import engine.Logic;

public class Main {
    public static void main(String[] args) {
        try {
            boolean vSync = true;
            Logic logic = new App();
            Engine engine = new Engine("Lab7", 1920, 720, vSync, logic);
            engine.run();
        } catch (Exception exception) {
            exception.printStackTrace();
            System.exit(-1);
        }
    }
}
