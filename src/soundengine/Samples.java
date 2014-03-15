package soundengine;

import java.nio.IntBuffer;

/**
 *
 * @author soote
 */
public enum Samples {

    WAR("Battle.wav"),
    FOOTSTEPS("Footsteps.wav"),
    GUN2("Gun2.wav");

    public final IntBuffer buffer;

    //only ~16 sources should be used. If >16 samples are to be used, we'll have
    //manage which buffers get to be in which source (TODO).
    public int source;

    Samples(String fileName) {
        buffer = OpenALFacade.loadSample(fileName);
        source = OpenALFacade.storeSource(buffer);
    }

}
