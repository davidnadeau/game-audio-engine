package soundengine;

import java.nio.IntBuffer;
import org.lwjgl.BufferUtils;
import org.lwjgl.LWJGLException;
import org.lwjgl.util.WaveData;
import static org.lwjgl.openal.AL.*;
import static org.lwjgl.openal.AL10.*;

/**
 *
 * @author soote
 */
public class OpenALFacade {

    public OpenALFacade() {
    }

    public static IntBuffer loadSample(String fileName) {
        //loading and storing the audio
        IntBuffer buf = BufferUtils.createIntBuffer(1);
        alGenBuffers(buf);
        WaveData wave = WaveData.create("music/" + fileName);
        System.out.println(wave.data);
        alBufferData(buf.get(0), wave.format, wave.data,
                wave.samplerate);
        wave.dispose();
        return buf;
    }
    public static int storeSource(IntBuffer buf) {
        //store the source details
        IntBuffer src = BufferUtils.createIntBuffer(1);
        alGenSources(src);
        alSourcei(src.get(0), AL_BUFFER, buf.get(0));
        alSourcei(src.get(0), AL_LOOPING, AL_TRUE);
        return src.get(0);
    }
    public void storeListener() {
        //store listener details (location)
        alListener3f(AL_POSITION, 0f, 0f, 0f);
        alListener3f(AL_VELOCITY, 0f, 0f, 0f);
        alListener3f(AL_ORIENTATION, 0f, 0f, 0f);
    }

    /**
     * Play a single sample
     */
    public void playSound(int src) {
        alSourcePlay(src);
    }
    /**
     * Play every sample loaded into a source
     */
    public void playSounds() {
        for (Samples s : Samples.values()) {
            alSourcePlay(s.source);
        }
    }
    /**
     * Stop a single sample
     */
    public void stopSound(int src) {
        alSourceStop(src);
    }
    /**
     * Stop every sample loaded into a source
     */
    public void stopSounds() {
        for (Samples s : Samples.values()) {
            alSourceStop(s.source);
        }
    }
    /**
     * Pause a single sample
     */
    public void pauseSound(int src) {
        alSourcePause(src);
    }
    /**
     * Pause every sample loaded into a source
     */
    public void pauseSounds() {
        for (Samples s : Samples.values()) {
            alSourcePause(s.source);
        }
    }

    /**
     * Set volume of a single sample
     */
    public void setVolume(int src, int value) {
        alSourcef(src, AL_GAIN, (float) (((value) / 100.0) * 1.0f));
    }
    /**
     * Set volume of every sample
     */
    public void setMasterVolume(int value) {
        for (Samples s : Samples.values()) {
            alSourcef(s.source, AL_GAIN, (float) (((value) / 100.0) * 1.0f));
        }
    }

    public void cleanUp() {
        for (Samples s : Samples.values()) {
            alDeleteSources(s.source);
            alDeleteBuffers(s.buffer.get(0));
        }

    }
    public static void init() {
        //initialize OpenAL
        try {
            create();
        } catch (LWJGLException le) {
        }
        alGetError();
    }
    public static void destructor() {
        destroy();
    }

}
