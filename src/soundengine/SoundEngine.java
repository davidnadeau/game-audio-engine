package soundengine;

import java.io.FileNotFoundException;
import soundengine.ui.Demo;

public class SoundEngine {

    public SoundEngine() throws FileNotFoundException {
    }

    public static void main(String[] args) throws FileNotFoundException {
        new Demo().setVisible(true);
    }

}
