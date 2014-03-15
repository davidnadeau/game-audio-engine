package soundengine;

import soundengine.ui.Demo;

public class SoundEngine {

    public SoundEngine() {
    }

    public static void main(String[] args) {
        OpenALFacade.init();
        new Demo().setVisible(true);
    }

}
