package soundengine;

public class Source {

    public int index;
    public float volume;
    public float[] velocity;
    public float[] position;
    public String name;
    Buffer buf;

    //create source with no buffer
    public Source(String name, int index) {
        this.name = name;
        this.index = index;
        volume = 0.5f;
        velocity = new float[3];
        position = new float[3];
    }

    //create a source with a buffer
    public Source(Buffer buf, int index) {
        this.name = buf.name;
        this.buf = buf;
        this.index = index;
        volume = 0.5f;
        velocity = new float[3];
        position = new float[3];
    }

    //copy a buffer into this source
    public void setBuffer(Buffer buf) {
        this.buf.buffer = buf.buffer;
        this.buf.name = buf.name;
    }

}
