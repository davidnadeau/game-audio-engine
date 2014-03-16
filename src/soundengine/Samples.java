package soundengine;

import java.nio.IntBuffer;

/**
 *
 * @author soote
 */
public enum Samples {

    WAR("Battle"),
    FOOTSTEPS("Footsteps"),
    GUN2("Gun2");

    public final IntBuffer buffer;

    //only ~16 sources should be used. If >16 samples are to be used, we'll have
    //manage which buffers get to be in which source (TODO).
    public int source;

    public float volume;

    Samples(String fileName) {
        buffer = OpenALFacade.loadSample(fileName + ".wav");
        source = OpenALFacade.storeSource(buffer);
        volume = 0.5f;
    }

}
