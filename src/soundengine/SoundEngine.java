package soundengine;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.IntBuffer;
import soundengine.ui.Demo;

public class SoundEngine {

    public SoundEngine() throws FileNotFoundException {
        OpenALFacade f = new OpenALFacade();
        IntBuffer buf = f.loadSample("Battle.wav");
        IntBuffer src = f.storeSouce(buf);
        f.storeListener();
        f.playSound(src);

        while (!kbhit());

        f.cleanUp(src, buf);
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
    public static void main(String[] args) throws FileNotFoundException {
        new Demo().setVisible(true);
    }

}
