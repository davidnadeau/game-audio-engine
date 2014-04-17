package soundengine;

import java.nio.IntBuffer;

public class Buffer {

    public IntBuffer buffer;
    public String name;

    public Buffer(String fileName) {
        name = fileName;
        buffer = OpenALFacade.loadSample(fileName + ".wav");
    }

}
