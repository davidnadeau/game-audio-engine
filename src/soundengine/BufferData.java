package soundengine;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.util.LinkedList;

public class BufferData {

    public IntBuffer buf;
    public LinkedList<Byte> waveform;

    public BufferData(IntBuffer buf, ByteBuffer waveform) {
        this.buf = buf;
        copy(waveform);
    }

    private void copy(ByteBuffer w) {
        waveform = new LinkedList<>();
        int count = 0;

        while (w.hasRemaining()) {
            if (count++ >= 30000)
                break;
            this.waveform.add(w.get());
        }
    }

}
