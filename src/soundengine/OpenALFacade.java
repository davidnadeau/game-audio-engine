/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package soundengine;

import java.nio.IntBuffer;
import org.lwjgl.BufferUtils;
import org.lwjgl.LWJGLException;
import static org.lwjgl.openal.AL.create;
import static org.lwjgl.openal.AL10.AL_BUFFER;
import static org.lwjgl.openal.AL10.AL_LOOPING;
import static org.lwjgl.openal.AL10.AL_ORIENTATION;
import static org.lwjgl.openal.AL10.AL_POSITION;
import static org.lwjgl.openal.AL10.AL_TRUE;
import static org.lwjgl.openal.AL10.AL_VELOCITY;
import static org.lwjgl.openal.AL10.alBufferData;
import static org.lwjgl.openal.AL10.alDeleteBuffers;
import static org.lwjgl.openal.AL10.alDeleteSources;
import static org.lwjgl.openal.AL10.alGenBuffers;
import static org.lwjgl.openal.AL10.alGenSources;
import static org.lwjgl.openal.AL10.alGetError;
import static org.lwjgl.openal.AL10.alListener3f;
import static org.lwjgl.openal.AL10.alSourcePause;
import static org.lwjgl.openal.AL10.alSourcePlay;
import static org.lwjgl.openal.AL10.alSourceStop;
import static org.lwjgl.openal.AL10.alSourcei;
import org.lwjgl.util.WaveData;

/**
 *
 * @author soote
 */
public class OpenALFacade {

    public OpenALFacade() {
        //initialize OpenAL
        try {
            create();
        } catch (LWJGLException le) {
        }
        alGetError();
    }

    public int loadSample(String fileName) {
        //loading and storing the audio
        IntBuffer buf = BufferUtils.createIntBuffer(1);
        alGenBuffers(buf);
        WaveData wave = WaveData.create("music/" + fileName);
        System.out.println(wave.data);
        alBufferData(buf.get(0), wave.format, wave.data,
                wave.samplerate);
        wave.dispose();
        return buf.get(0);
    }

    public int storeSource(int buf) {
        //store the source details
        IntBuffer src = BufferUtils.createIntBuffer(1);
        alGenSources(src);
        alSourcei(src.get(0), AL_BUFFER, buf);
        alSourcei(src.get(0), AL_LOOPING, AL_TRUE);
        return src.get(0);
    }

    public void storeListener() {
        //store listener details (location)
        alListener3f(AL_POSITION, 0f, 0f, 0f);
        alListener3f(AL_VELOCITY, 0f, 0f, 0f);
        alListener3f(AL_ORIENTATION, 0f, 0f, 0f);
    }

    public void playSound(int src) {
        alSourcePlay(src);
    }

    public void stopSound(int src) {
        alSourceStop(src);
    }
    public void pauseSound(int src) {
        alSourcePause(src);
    }
    public void cleanUp(int src, int buf) {
        alDeleteSources(src);
        alDeleteBuffers(buf);
    }

}
