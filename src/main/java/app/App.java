package app;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.glViewport;

import app.marchingcubes.Extractor;
import engine.*;
import engine.graphics.Camera;
import engine.graphics.Mesh;
import engine.graphics.Texture;
import org.joml.Intersectionf;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.lwjgl.system.CallbackI;

import java.io.File;
import java.net.URL;

public class App implements Logic {

    private static final float CAMERA_POS_STEP = 0.1f;
    private static final float MOUSE_SENSITIVITY = 0.8f;

    private int dir;
    float rot = 0;


    private final Renderer renderer;

    private Mesh mesh;

    Camera camera;
    Vector3f cameraLocation = new Vector3f();

    private Item[] items;
    public App() {
        renderer = new Renderer();
    }

    @Override
    public void init(Window window) throws Exception {
        renderer.init(window);

        URL fileName = getClass().getClassLoader().getResource("rawData/fuel.raw");
        File rawData = new File(fileName.toURI());
        File outputFile = File.createTempFile("fuel", ".obj");

        Extractor.extractHandlerChar(rawData, outputFile, new int[]{64, 64, 64}, new float[]{1, 1, 1}, (char)50, 12);

        Mesh mesh = OBJLoader.loadMesh(OBJLoader.getListString(outputFile));
        Texture texture = new Texture("textures/texture.png");

        mesh.setColour(new Vector3f(0f, 0f, 0f));
        Item item = new Item(mesh);
        item.setScale(0.5f);
        item.setPosition(0, 0, -2);
        item.setRotation(90, 0f, 0);
        items = new Item[]{item};



        camera = new Camera(new Vector3f(0f, 0f, 1.5f), new Vector3f(0f, 0f, 0f));
    }

    @Override
    public void input(Window window, MouseInput mouseInput) {
        cameraLocation.set(0, 0, 0);
        if (window.isKeyPressed(GLFW_KEY_W)) {
            cameraLocation.z = -0.5f;
        } else if (window.isKeyPressed(GLFW_KEY_S)) {
            cameraLocation.z = 0.5f;
        }
        if (window.isKeyPressed(GLFW_KEY_A)) {
            cameraLocation.x = -0.5f;
        } else if (window.isKeyPressed(GLFW_KEY_D)) {
            cameraLocation.x = 0.5f;
        }
        if (window.isKeyPressed(GLFW_KEY_Z)) {
            cameraLocation.y = -0.5f;
        } else if (window.isKeyPressed(GLFW_KEY_X)) {
            cameraLocation.y = 0.5f;
        }

        if (mouseInput.isRightButtonPressed()) {
           // Intersectionf.intersectRayAab(camera.getPosition(), dir,)
        }

        if (mouseInput.isLeftButtonPressed()) {
            glfwSetInputMode(window.getWindowHandle(), GLFW_CURSOR, GLFW_CURSOR_DISABLED);
        }
    }

    @Override
    public void update(float interval, MouseInput mouseInput) {
        rot += 0.5;

        if (rot == 360) rot = 0;
    /*    items[0].setRotation(rot, rot, rot);
        items[1].setRotation(-rot, -rot, rot);
        items[2].setRotation(-rot, rot, -rot);*/
        camera.movePosition(cameraLocation.x * CAMERA_POS_STEP,
                cameraLocation.y * CAMERA_POS_STEP,
                cameraLocation.z * CAMERA_POS_STEP);


            Vector2f rotVec = mouseInput.getMouseDeltaVector();
            camera.moveRotation(rotVec.x * MOUSE_SENSITIVITY, rotVec.y * MOUSE_SENSITIVITY, 0);

    }

    @Override
    public void render(Window window) throws Exception {
        if (window.isResized()) {
            glViewport(0, 0, window.getWidth(), window.getHeight());
            window.setResized(false);
        }

        window.setClearColor(1f, 1f, 1f, 0f);
        renderer.render(window, camera, items);
    }

    @Override
    public void cleanup(){
        renderer.cleanup();
    }
}
