package soundengine;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.LinkedList;
import javax.swing.JTextField;
import org.lwjgl.BufferUtils;
import org.lwjgl.LWJGLException;
import static org.lwjgl.openal.AL.create;
import static org.lwjgl.openal.AL.destroy;
import static org.lwjgl.openal.AL10.*;
import org.lwjgl.util.WaveData;

public class OpenALFacade {

    // used when openAL requires it's float buffer in a specific format
    FloatBuffer fbuf = BufferUtils.createFloatBuffer(3).put(
            new float[] {0.0f, 0.0f, 0.0f});

    public OpenALFacade() {
    }

    /**
     * ************************************************************************
     * START OF IO METHODS
     * ************************************************************************
     */
    // Load audio sample into a buffer, return this buffer
    public static IntBuffer loadSample(String fileName) {
        // loading and storing the audio
        IntBuffer buf = BufferUtils.createIntBuffer(1);
        alGenBuffers(buf);
        WaveData wave = WaveData.create("music/" + fileName);
        alBufferData(buf.get(0), wave.format, wave.data,
                wave.samplerate);
        wave.dispose();
        return buf;
    }
    // Copy buffer into an audio source and change basic properties
    public static int storeSource(IntBuffer buf) {
        // store the source details
        IntBuffer src = BufferUtils.createIntBuffer(1);
        // initialize the source
        alGenSources(src);
        // copy buffer into the source
        alSourcei(src.get(0), AL_BUFFER, buf.get(0));
        // AL_LOOPING sets the source to loop the audio on play
        alSourcei(src.get(0), AL_LOOPING, AL_TRUE);
        // AL_GAIN is volume(amplitude) 0.0f is min, 1.0f is max
        alSourcef(src.get(0), AL_GAIN, 0.5f);

        // return the sources descriptor
        return src.get(0);
    }
    /**
     * ************************************************************************
     * END OF IO METHODS
     * ************************************************************************
     */

    /**
     * ************************************************************************
     * START OF LISTENER METHODS
     * ************************************************************************
     */
    public void newListener() {
        //store listener details (location)
        alListener3f(AL_POSITION, 0f, 0f, 0f);
        alListener3f(AL_VELOCITY, 0f, 0f, 0f);
        alListenerf(AL_GAIN, 0.5f);
    }
    public void setListenerPosition(float x, float y, float z) {
        alListener3f(AL_POSITION, x, y, z);
    }
    public void setListenerVelocity(float x, float y, float z) {
        alListener3f(AL_VELOCITY, x, y, z);
    }
    public void setListenerGain(float x, float y, float z) {
        alListener3f(AL_GAIN, x, y, z);
    }
    // Set volume of every sample
    public void setMasterVolume(float value) {
        float newVolume = value / 100.0f;
        alListenerf(AL_GAIN, newVolume);
    }

    public void moveListener(final JTextField posTB, final float[] vel,
            int duration) {
        new Thread(new Runnable() {
            //implement functionality for anstract run
            public void run() {
                //get distance of furthest axis
                int distance = 100;
                //get index of fastest velocity
                int imax = getIndexOfMax(vel);
                //dealing with meters per second. 1 kph = 0.2778 mps
                float mps = 0.2778f;
                //how long it takes to travel to inaudible distance at speed vel[imax]
                int time = Math.round((distance / vel[imax]) / mps);

                for (int i = 0; i < time; i++) {
                    float[] pos = new float[3];
                    for (int j = 0; j < 3; j++) {
                        pos[j] += vel[j];
                    }
                    setListenerPosition(pos[0], pos[1], pos[2]);
                    posTB.setText(pos[0] + " " + pos[1] + " " + pos[2]);

                    if (!anyGreater(pos, distance)) {
                        try {
                            Thread.sleep(10);
                        } catch (InterruptedException ex) {

                        }
                    } else {
                        break;
                    }
                }
            }
        }).start();
    }
    /**
     * ************************************************************************
     * END OF LISTENER METHODS
     * ************************************************************************
     */

    /**
     * ************************************************************************
     * START OF SOURCE METHODS
     * ************************************************************************
     */
    // Play a single sample
    public void playSound(Source s) {
        alSourcePlay(s.index);
    }
    // Play every sample loaded into a source
    public void playSounds(LinkedList<Source> srcs) {
        for (Source s : srcs)
            alSourcePlay(s.index);
    }
    // Stop a single sample
    public void stopSound(Source s) {
        alSourceStop(s.index);
    }
    // Stop every sample loaded into a source
    public void stopSounds(LinkedList<Source> srcs) {
        for (Source s : srcs)
            alSourceStop(s.index);
    }
    // Pause a single sample
    public void pauseSound(Source s) {
        alSourcePause(s.index);
    }
    // Pause every sample loaded into a source
    public void pauseSounds(LinkedList<Source> srcs) {
        for (Source s : srcs)
            alSourcePause(s.index);
    }
    // Set volume of a single sample
    public void setVolume(Source s, float value) {
        float newVolume = value / 100.0f;
        alSourcef(s.index, AL_GAIN, newVolume);
        s.volume = newVolume;
    }

    // Move source away from origin at source velocity speed. If source has no
    // speed, set a default speed.
    //
    // This method also updates the text fields displaying the sources position
    // and velocity as they get adjusted.
    public void fadeOutSound(final Source s, final JTextField posTB,
            final JTextField velTB) {
        // create a new thread to gradually move source away from listener
        new Thread(new Runnable() {
            // implement functionality for anstract run
            public void run() {
                int distance = 40;// meters
                // place current sources velocity into temp buf
                float[] vel = new float[] {s.velocity[0], s.velocity[1], s.velocity[2]};
                System.out.println(vel[0] + ":" + vel[1] + ":" + vel[2]);
                // if the source has no velocity, set a default speed of 0.1f in z
                boolean allZero = ifAllZero(vel);
                if (allZero) {
                    vel = new float[] {0.0f, 0.0f, 0.1f};
                }

                velTB.setText(vel[0] + " " + vel[1] + " " + vel[2]);

                // When any position x,y, or z reach 'distance', the thread will stop.
                // So by grabbing the fastest velocity in any direction, we can use:
                //                  time = distance/velocity
                // to find how long it should take to naturally fade out at a given speed.
                // get index of fastest velocity
                int imax = getIndexOfMax(vel);
                // dealing with meters per second. 1 k/h = 0.2778 m/s
                float mps = 0.2778f;
                // how long it takes to travel to inaudible distance at speed vel[imax]
                int time = Math.round(distance / (vel[imax] * mps));
                System.out.println(time);
                for (int i = 0; i < time; i++) {
                    for (int j = 0; j < 3; j++) {
                        s.position[j] += vel[j];
                    }
                    setPosition(s, s.position);
                    posTB.setText(
                            s.position[0] + " " + s.position[1] + " " + s.position[2]);

                    if (!anyGreater(s.position, distance)) {
                        try {
                            Thread.sleep(10);
                        } catch (InterruptedException ex) {

                        }
                    } else {
                        break;
                    }
                }

                s.position[2] = distance;
                setPosition(s, s.position);
                posTB.setText(
                        s.position[0] + " " + s.position[1] + " " + s.position[2]);

                velTB.setText(
                        s.velocity[0] + " " + s.velocity[1] + " " + s.velocity[2]);
            }
        }).start();
    }
    public int getIndexOfMax(float[] f) {
        int imax = 0;
        float max = f[0];
        for (int i = 1; i < f.length; i++) {
            if (f[i] > max) {
                max = f[i];
                imax = i;
            }
        }
        return imax;
    }

    // Move source towards origin at source velocity speed. If source has no
    // speed, set a default speed.
    //
    // This method also updates the text fields displaying the sources position
    // and velocity as they get adjusted.
    public void fadeInSound(final Source s, final JTextField posTB,
            final JTextField velTB) {
        new Thread(new Runnable() {
            // implement functionality for anstract run
            public void run() {
                // get distance of furthest axis
                int idistance = getIndexOfMax(s.position);
                int distance = (int) s.position[idistance];// meters

                // place current sources velocity into temp buf
                float[] vel = new float[] {s.velocity[0], s.velocity[1], s.velocity[2]};
                // if the source has no velocity, set a default speed of 0.1f in z
                boolean allZero = ifAllZero(vel);
                if (allZero) {
                    vel = new float[] {0.0f, 0.0f, 0.1f};
                }
                velTB.setText(vel[0] + " " + vel[1] + " " + vel[2]);

                // When any position x,y, or z reach 'distance', the thread will stop.
                // So by grabbing the fastest velocity in any direction, we can use:
                //                  t = d/v
                // to find how long it should take to naturally fade out at a given speed.
                // get index of fastest velocity
                int imax = getIndexOfMax(vel);
                // dealing with meters per second. 1 kph = 0.2778 mps
                float mps = 0.2778f;
                // how long it takes to travel to inaudible distance at speed vel[imax]
                int time = Math.round((distance / vel[imax]) / mps);

                for (int i = 0; i < time; i++) {
                    for (int j = 0; j < 3; j++) {
                        s.position[j] = (s.position[j] - vel[j] < 0) ? 0.0f
                                : s.position[j] - vel[j];
                    }
                    setPosition(s, s.position);
                    posTB.setText(
                            s.position[0] + " " + s.position[1] + " " + s.position[2]);

                    if (!anyGreater(s.position, distance)) {
                        try {
                            Thread.sleep(10);
                        } catch (InterruptedException ex) {

                        }
                    } else {
                        break;
                    }
                }
                velTB.setText(
                        s.velocity[0] + " " + s.velocity[1] + " " + s.velocity[2]);
            }
        }).start();
    }
    private boolean ifAllZero(float[] buf) {
        return buf[0] == 0 && buf[1] == 0 && buf[2] == 0;
    }
    private boolean anyGreater(float[] buf, int val) {
        return buf[0] > val || buf[1] > val || buf[2] > val;
    }

    public void moveSource(Source s, final JTextField posTB, final float[] vel,
            int duration) {
        new Thread(new Runnable() {
            // implement functionality for anstract run
            public void run() {
                // get distance of furthest axis
                int distance = 100;
                // get index of fastest velocity
                int imax = getIndexOfMax(vel);
                // dealing with meters per second. 1 kph = 0.2778 mps
                float mps = 0.2778f;
                // how long it takes to travel to inaudible distance at speed vel[imax]
                int time = Math.round((distance / vel[imax]) / mps);

                for (int i = 0; i < time; i++) {
                    float[] pos = new float[3];
                    for (int j = 0; j < 3; j++) {
                        pos[j] += vel[j];
                    }
                    setListenerPosition(pos[0], pos[1], pos[2]);
                    posTB.setText(pos[0] + " " + pos[1] + " " + pos[2]);

                    if (!anyGreater(pos, distance)) {
                        try {
                            Thread.sleep(10);
                        } catch (InterruptedException ex) {

                        }
                    } else {
                        break;
                    }
                }
            }
        }
        ).start();

    }

    public void setLoopingMode(Source s, boolean val) {
        alSourcei(s.index, AL_LOOPING, val ? AL_TRUE : AL_FALSE);
    }
    public void setVelocity(Source s, float[] vel) {
        fbuf.clear();
        fbuf.put(vel);
        // need to flip an io buffer after writting to allow reading
        fbuf.flip();
        alSource(s.index, AL_VELOCITY, fbuf);
    }
    public void setPosition(Source s, float[] pos) {
        fbuf.clear();
        fbuf.put(pos);
        fbuf.flip();
        alSource(s.index, AL_POSITION, fbuf);
    }
    public void setPitch(Source s, int pitch) {
        alSourcef(s.index, AL_PITCH, (float) (pitch / 50.0));
    }
    /**
     * ************************************************************************
     * END OF SOURCE METHODS
     * ************************************************************************
     */

    /**
     * ************************************************************************
     * START OF INIT & CLEAN UP METHODS
     * ************************************************************************
     */
    //release sources and buffers
    public void cleanUp(LinkedList<Source> srcs, LinkedList<Buffer> bufs) {
        for (Source s : srcs)
            alDeleteSources(s.index);
        for (Buffer b : bufs)
            alDeleteBuffers(b.buffer);
    }

    public void clearSource(Source s) {
        alDeleteSources(s.index);
    }

    // Create on OpenAL instance
    public static void init() {
        // initialize OpenAL
        try {
            create();
        } catch (LWJGLException le) {
        }
        alGetError();
    }

    // Destroy on OpenAL instance
    public static void destructor() {
        destroy();
    }

    /**
     * ************************************************************************
     * END OF INIT & CLEAN UP METHODS
     * ************************************************************************
     */
}
