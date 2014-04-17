package soundengine;

public class Source {

    public int index;
    public float volume;
    public String name;
    Buffer buf;

    public Source(String name, int index) {
        this.name = name;
        this.index = index;
        volume = 0.5f;
    }

    public Source(Buffer buf, int index) {
        this.name = buf.name;
        this.buf = buf;
        this.index = index;
        volume = 0.5f;
    }

    public void setBuffer(Buffer buf) {
        this.buf.buffer = buf.buffer;
        this.buf.name = buf.name;
    }

}
