package soundengine;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.util.LinkedList;

public class BufferData {

    public IntBuffer buf;
    public LinkedList<Byte> waveformSml;
    public LinkedList<Byte> waveformMed;
    public LinkedList<Byte> waveformLrg;

    public BufferData(IntBuffer buf, ByteBuffer waveform) {
        this.buf = buf;
        copy(waveform);
    }

    private void copy(ByteBuffer w) {
        waveformSml = new LinkedList<>();
        waveformMed = new LinkedList<>();
        waveformLrg = new LinkedList<>();
        int count = 0;

        while (w.hasRemaining()) {
            if (count++ >= 30000) {
                if (count >= 100000) {
                    this.waveformLrg.add(scale(w.get()));
                    continue;
                }
                this.waveformMed.add(scale(w.get()));
                this.waveformLrg.add(this.waveformMed.getLast());
                continue;
            }
            this.waveformSml.add(scale(w.get()));
            this.waveformMed.add(this.waveformSml.getLast());
            this.waveformLrg.add(this.waveformMed.getLast());
        }
    }
    // scales 8 bit value from [0,1] and multiplies by 100, result: [0,100]
    private byte scale(byte val) {
        return (byte) ((((float) val + 128.0f) / 255.0f) * 100.0f);
    }

}
