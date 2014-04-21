package soundengine;

import java.nio.IntBuffer;
import java.util.LinkedList;

public class Buffer {

    public IntBuffer buffer;
    public String name;
    public LinkedList<Byte> waveformSml;
    public LinkedList<Byte> waveformMed;
    public LinkedList<Byte> waveformLrg;

    public Buffer(String fileName) {
        name = fileName;
        BufferData bd = OpenALFacade.loadSample(fileName);
        buffer = bd.buf;
        this.waveformSml = new LinkedList(bd.waveformSml);
        this.waveformMed = new LinkedList(bd.waveformMed);
        this.waveformLrg = new LinkedList(bd.waveformLrg);
    }

}
