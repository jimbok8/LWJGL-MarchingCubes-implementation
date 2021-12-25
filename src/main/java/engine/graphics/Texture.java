package engine.graphics;

import org.lwjgl.system.MemoryStack;

import java.io.File;
import java.net.URL;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.nio.file.Paths;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_2D;
import static org.lwjgl.opengl.GL30.glGenerateMipmap;
import static org.lwjgl.stb.STBImage.*;

public class Texture {

    //id текстуры
    private final int id;

    public Texture(String fileName) throws Exception {
        this(loadTexture(fileName));
    }

    public Texture(int id) {
        this.id = id;
    }

    //связать текстуру с конкретным объектом в памяти GPU
    public void bind() {
        glBindTexture(GL_TEXTURE_2D, id);
    }

    //получить id текстуры
    public int getId() {
        return id;
    }

    private static int loadTexture(String fileName) throws Exception {
        int width;
        int height;
        ByteBuffer buf;

        URL res = Texture.class.getClassLoader().getResource(fileName);
        File file = Paths.get(res.toURI()).toFile();
        String absolutePath = file.getAbsolutePath();
        // загрузка файла
        try (MemoryStack stack = MemoryStack.stackPush()) {
            IntBuffer w = stack.mallocInt(1);
            IntBuffer h = stack.mallocInt(1);
            IntBuffer channels = stack.mallocInt(1);

            buf = stbi_load(absolutePath, w, h, channels, 4); //загрузка файла как потока байтов через буфер
            if (buf == null) {
                throw new Exception("Image file [" + fileName  + "] not loaded: " + stbi_failure_reason());
            }

            /* Получить ширину и высоту  */
            width = w.get();
            height = h.get();
        }

        // создать текстуру в памяти GPU
        int textureId = glGenTextures();
        // Связать текстуру с объектом в памяти GPU
        glBindTexture(GL_TEXTURE_2D, textureId);

        // Режим распаковки RGBA байтов. Каждый компонент имеет размер в один байт
        glPixelStorei(GL_UNPACK_ALIGNMENT, 1);


        // загрузить данные текстуры
        glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, width, height, 0,
                GL_RGBA, GL_UNSIGNED_BYTE, buf);
        // Создать MipMap
        glGenerateMipmap(GL_TEXTURE_2D);

        // очистить память буфера
        stbi_image_free(buf);

        return textureId;
    }
    //удалить текстуры из памяти после завершения выполнения
    public void cleanup() {
        glDeleteTextures(id);
    }
}

