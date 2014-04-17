package soundengine;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import javax.swing.JSlider;
import org.lwjgl.BufferUtils;
import org.lwjgl.LWJGLException;
import static org.lwjgl.openal.AL.create;
import static org.lwjgl.openal.AL.destroy;
import static org.lwjgl.openal.AL10.*;
import org.lwjgl.util.WaveData;

public class OpenALFacade {

    final float MAXVOLUME = 100.0f;

    public OpenALFacade() {
    }

    /**
     * Load audio sample into a buffer, return this buffer
     */
    public static IntBuffer loadSample(String fileName) {
        //loading and storing the audio
        IntBuffer buf = BufferUtils.createIntBuffer(1);
        alGenBuffers(buf);
        WaveData wave = WaveData.create("music/" + fileName);
        alBufferData(buf.get(0), wave.format, wave.data,
                wave.samplerate);
        wave.dispose();
        return buf;
    }
    /**
     * Copy buffer into an audio source and change basic properties
     */
    public static int storeSource(IntBuffer buf) {
        //store the source details
        IntBuffer src = BufferUtils.createIntBuffer(1);
        //initialize the source
        alGenSources(src);
        //copy buffer into the source
        alSourcei(src.get(0), AL_BUFFER, buf.get(0));
        //AL_LOOPING sets the source to loop the audio on play
        alSourcei(src.get(0), AL_LOOPING, AL_TRUE);
        //AL_GAIN is volume(amplitude) 0.0f is min, 1.0f is max
        alSourcef(src.get(0), AL_GAIN, 0.5f);

        //return the sources descriptor
        return src.get(0);
    }
    public void newListener() {
        //store listener details (location)
        alListener3f(AL_POSITION, 0f, 0f, 0f);
        alListener3f(AL_VELOCITY, 0f, 0f, 0f);
        alListener3f(AL_ORIENTATION, 0f, 0f, 0f);
        alListener3f(AL_GAIN, 0f, 0f, 0f);
    }
    public void setListenerPosition(float x, float y, float z) {
        alListener3f(AL_POSITION, x, y, z);
    }
    public void setListenerVelocity(float x, float y, float z) {
        alListener3f(AL_VELOCITY, x, y, z);
    }
    public void setListenerOrientation(float x, float y, float z) {
        alListener3f(AL_ORIENTATION, x, y, z);
    }
    public void setListenerGain(float x, float y, float z) {
        alListener3f(AL_GAIN, x, y, z);
    }
    /**
     * Play a single sample
     */
    public void playSound(Source s) {
        alSourcePlay(s.index);
    }

    /**
     * Play every sample loaded into a source
     */
    public void playSounds() {
//        for (Samples s : Samples.values()) {
//            alSourcePlay(s.source);
//        }
    }
    /**
     * Stop a single sample
     */
    public void stopSound(Source s) {
        alSourceStop(s.index);
    }
    /**
     * Stop every sample loaded into a source
     */
    public void stopSounds() {
        //TODO: only stop playing samples
//        for (Samples s : Samples.values()) {
//            alSourceStop(s.source);
//        }
    }
    /**
     * Pause a single sample
     */
    public void pauseSound(Source s) {
        alSourcePause(s.index);
    }
    /**
     * Pause every sample loaded into a source
     */
    public void pauseSounds() {
        //TODO: only pause playing samples
//        for (Sample s : Sample.values()) {
//            alSourcePause(s.source);
//        }
    }

    /**
     * Set volume of a single sample
     */
    public void setVolume(Source s, float value) {
        float newVolume = value / MAXVOLUME;
        alSourcef(s.index, AL_GAIN, newVolume);
        s.volume = newVolume;
    }
    /**
     * Set volume of every sample
     */
    public void setMasterVolume(float value) {
        float newVolume = value / MAXVOLUME;
        //set the volume of every sample
        //TODO: only adjust volume of playing samples
//        for (Sample s : Sample.values()) {
//            alSourcef(s.source, AL_GAIN, newVolume);
//            s.volume = newVolume;
//        }
    }
    /**
     * Reduces a samples sound to 0. This task runs in a thread so as not to
     * hang the game.
     */
    public void fadeOutSound(final Source s, final JSlider js) {
        Thread fadeout = new Thread(new Runnable() {
            public void run() {
                while (true) {
                    setVolume(s, (s.volume * MAXVOLUME) - 1.0f);
                    js.setValue((int) (s.volume * 100));
                    if (s.volume > 0.0f) {
                        try {
                            Thread.sleep(10);
                        } catch (InterruptedException ex) {

                        }
                    } else {
                        break;
                    }
                }
            }
        });
        fadeout.start();
    }
    /**
     * Reduces every samples sound to 0. This task runs in a thread so as not to
     * hang the game.
     */
    public void fadeOutSounds(JSlider slider) {
        //real code
//        for (Samples s : Samples.values()) {
//            fadeOutSound(s);
//        }
        //just for demo
//        fadeOutSound(Samples.values()[0], slider);

    }
    /**
     * Increases a samples sound to 100. This task runs in a thread so as not to
     * hang the game.
     */
    public void fadeInSound(final Source s, final JSlider js) {
        Thread fadein = new Thread(new Runnable() {
            public void run() {
                while (true) {
                    setVolume(s, (s.volume * MAXVOLUME) + 1.0f);
                    js.setValue((int) (s.volume * 100));
                    if (s.volume < 1.0f) {
                        try {
                            Thread.sleep(10);
                        } catch (InterruptedException ex) {

                        }
                    } else {
                        break;
                    }
                }
            }
        });
        fadein.start();
    }
    /**
     * Increases every samples sound to 100. This task runs in a thread so as
     * not to hang the game.
     */
    public void fadeInSounds(JSlider slider) {
        //real code
//        for (Samples s : Samples.values()) {
//            fadeInSound(s);
//        }
        //just for demo
//        fadeInSound(Samples.values()[0], slider);

    }

    public void rotateListener(final Source s) {
        Thread fadein = new Thread(new Runnable() {
            public void run() {
                while (true) {
                    if (s.volume < 1.0f) {
                        try {
                            Thread.sleep(10);
                        } catch (InterruptedException ex) {

                        }
                    } else {
                        break;
                    }
                }
            }
        });
        fadein.start();
    }
    public void setLoopingMode(Source s, boolean val) {
        alSourcei(s.index, AL_LOOPING, val ? AL_TRUE : AL_FALSE);
    }
    public void setVelocity(Source s, float x, float y, float z) {
        FloatBuffer fbuf = BufferUtils.createFloatBuffer(3).put(
                new float[] {x, y, z});
        alSource(s.index, AL_VELOCITY, fbuf);
    }
    public void setPosition(Source s, float x, float y, float z) {
        FloatBuffer fbuf = BufferUtils.createFloatBuffer(3).put(
                new float[] {x, y, z});
        alSource(s.index, AL_POSITION, fbuf);
    }
    public void setPitch(Source s, float pitch) {
        alSourcef(s.index, AL_PITCH, pitch);

    }
    /**
     * Release sources and buffers
     */
    public void cleanUp() {
//        for (Samples s : Samples.values()) {
//            alDeleteSources(s.source);
//            alDeleteBuffers(s.buffer.get(0));
//        }

    }
    /**
     * Create on OpenAL instance
     */
    public static void init() {
        //initialize OpenAL
        try {
            create();
        } catch (LWJGLException le) {
        }
        alGetError();
    }
    /**
     * Destroy on OpenAL instance
     */
    public static void destructor() {
        destroy();
    }

}
