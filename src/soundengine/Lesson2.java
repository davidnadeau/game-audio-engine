package soundengine;

import java.io.IOException;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import org.lwjgl.BufferUtils;
import org.lwjgl.LWJGLException;
import org.lwjgl.Sys;
import org.lwjgl.openal.AL;
import org.lwjgl.openal.AL10;
import org.lwjgl.util.WaveData;

/**
 * $Id$
 * <p>
 * Lesson 2: Looping and Fade-away
 * </p>
 *
 * @author Brian Matzon <brian@matzon.dk>
 * @version $Revision$
 */
public class Lesson2 {

    /**
     * Buffers hold sound data.
     */
    IntBuffer buffer = BufferUtils.createIntBuffer(1);

    /**
     * Sources are points emitting sound.
     */
    IntBuffer source = BufferUtils.createIntBuffer(1);

    /**
     * Position of the source sound.
     */
    FloatBuffer sourcePos = BufferUtils.createFloatBuffer(3).put(
            new float[] {0.0f, 0.0f, 0.0f});

    /*
     * These are 3D cartesian vector coordinates. A structure or class would be
     * a more flexible of handling these, but for the sake of simplicity we will
     * just leave it as is.
     */
    /**
     * Velocity of the source sound.
     */
    FloatBuffer sourceVel = BufferUtils.createFloatBuffer(3).put(
            new float[] {0.0f, 0.0f, 0.1f});

    /**
     * Position of the listener.
     */
    FloatBuffer listenerPos = BufferUtils.createFloatBuffer(3).put(
            new float[] {0.0f, 0.0f, 0.0f});

    /**
     * Velocity of the listener.
     */
    FloatBuffer listenerVel = BufferUtils.createFloatBuffer(3).put(
            new float[] {0.0f, 0.0f, 0.0f});

    /**
     * Orientation of the listener. (first 3 elements are "at", second 3 are
     * "up") Also note that these should be units of '1'.
     */
    FloatBuffer listenerOri = BufferUtils.createFloatBuffer(6).put(
            new float[] {0.0f, 0.0f, -1.0f, 0.0f, 1.0f, 0.0f});

    public Lesson2() {
        // CRUCIAL!
        // any buffer that has data added, must be flipped to establish its position and limits
        sourcePos.flip();
        sourceVel.flip();
        listenerPos.flip();
        listenerVel.flip();
        listenerOri.flip();
    }

    /**
     * boolean LoadALData()
     *
     * This function will load our sample data from the disk using the Alut
     * utility and send the data into OpenAL as a buffer. A source is then also
     * created to play that buffer.
     */
    int loadALData() {
        // Load wav data into a buffer.
        AL10.alGenBuffers(buffer);

        if (AL10.alGetError() != AL10.AL_NO_ERROR) {
            return AL10.AL_FALSE;
        }

        WaveData waveFile = WaveData.create("Footsteps.wav");
        AL10.alBufferData(buffer.get(0), waveFile.format, waveFile.data,
                waveFile.samplerate);
        waveFile.dispose();

        // Bind the buffer with the source.
        AL10.alGenSources(source);

        if (AL10.alGetError() != AL10.AL_NO_ERROR) {
            return AL10.AL_FALSE;
        }

        AL10.alSourcei(source.get(0), AL10.AL_BUFFER, buffer.get(0));
        AL10.alSourcef(source.get(0), AL10.AL_PITCH, 1.0f);
        AL10.alSourcef(source.get(0), AL10.AL_GAIN, 1.0f);
        AL10.alSource(source.get(0), AL10.AL_POSITION, sourcePos);
        AL10.alSource(source.get(0), AL10.AL_VELOCITY, sourceVel);
        AL10.alSourcei(source.get(0), AL10.AL_LOOPING, AL10.AL_TRUE);

        // Do another error check and return.
        if (AL10.alGetError() == AL10.AL_NO_ERROR) {
            return AL10.AL_TRUE;
        }

        return AL10.AL_FALSE;
    }

    /**
     * void setListenerValues()
     *
     * We already defined certain values for the Listener, but we need to tell
     * OpenAL to use that data. This function does just that.
     */
    void setListenerValues() {
        AL10.alListener(AL10.AL_POSITION, listenerPos);
        AL10.alListener(AL10.AL_VELOCITY, listenerVel);
        AL10.alListener(AL10.AL_ORIENTATION, listenerOri);
    }

    /**
     * void killALData()
     *
     * We have allocated memory for our buffers and sources which needs to be
     * returned to the system. This function frees that memory.
     */
    void killALData() {
        AL10.alDeleteSources(source);
        AL10.alDeleteBuffers(buffer);
    }

    /**
     * Check for keyboard hit
     */
    private boolean kbhit() {
        try {
            return (System.in.available() != 0);
        } catch (IOException ioe) {
        }
        return false;
    }

    public void execute() {
        // Initialize OpenAL and clear the error bit.
        try {
            AL.create();
        } catch (LWJGLException le) {
            le.printStackTrace();
            return;
        }
        AL10.alGetError();

        // Load the wav data.
        if (loadALData() == AL10.AL_FALSE) {
            System.out.println("Error loading data.");
            return;
        }

        setListenerValues();

        AL10.alSourcePlay(source.get(0));

        // loop
        long time = Sys.getTime();
        long elapse = 0;

        System.out.println("Press ENTER to exit");

        while (!kbhit()) {
            elapse += Sys.getTime() - time;
            time += elapse;

            if (elapse > 5000) {
                elapse = 0;

                sourcePos.put(0, sourcePos.get(0) + sourceVel.get(0));
                sourcePos.put(1, sourcePos.get(1) + sourceVel.get(1));
                sourcePos.put(2, sourcePos.get(2) + sourceVel.get(2));

                AL10.alSource(source.get(0), AL10.AL_POSITION, sourcePos);
            }
        }

        killALData();
    }

    public static void main(String[] args) {
        new Lesson2().execute();
    }

}
