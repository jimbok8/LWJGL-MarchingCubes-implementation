package engine;

public class Engine implements Runnable {

    public static final int TARGET_FPS = 1000;

    public static final int TARGET_UPS = 60;

    private final Window window;

    private final Time time;

    private final Logic logic;

    MouseInput mouseInput = new MouseInput();

    public Engine(String windowTitle, int width, int height, boolean vSync, Logic logic) throws Exception {
        window = new Window(windowTitle, width, height, vSync);
        this.logic = logic;
        time = new Time();
    }

    @Override
    public void run() {
        try {
            init();
            loop();
        } catch (Exception exception) {
            exception.printStackTrace();
        } finally {
            cleanup();
        }
    }

    protected void init() throws Exception {
        window.init();
        time.init();
        logic.init(window);
        mouseInput.init(window);
    }

    protected void loop() throws Exception {
        float elapsedTime;
        float accumulator = 0f;
        float interval = 1f / TARGET_UPS;

        boolean running = true;
        while (running && !window.windowShouldClose()) {
            elapsedTime = time.getElapsedTime();
            accumulator += elapsedTime;

            input();

            while (accumulator >= interval) {
                update(interval);
                accumulator -= interval;
            }

            render();

            if (!window.isvSync()) {
                sync();
            }
        }
    }

    private void sync() {
        float loopSlot = 1f / TARGET_FPS;
        double endTime = time.getLastLoopTime() + loopSlot;
        while (time.getTime() < endTime) {
            try {
                Thread.sleep(1);
            } catch (InterruptedException ie) {
                ie.printStackTrace();
            }
        }
    }
    protected void cleanup() {
        logic.cleanup();
    }

    protected void input() {
        mouseInput.input(window);
        logic.input(window, mouseInput);
    }

    protected void update(float interval) {
        logic.update(interval, mouseInput);
    }

    protected void render() throws Exception {
        logic.render(window);
        window.update();
    }

}
