package soundengine;

import java.nio.IntBuffer;
import java.util.LinkedList;

public class Buffer {

    public IntBuffer buffer;
    public String name;
    public LinkedList<Byte> waveform;

    public Buffer(String fileName) {
        name = fileName;
        BufferData bd = OpenALFacade.loadSample(fileName);
        buffer = bd.buf;
        this.waveform = new LinkedList(bd.waveform);
    }

}
