package soundengine;

import java.awt.Canvas;
import java.util.LinkedList;
import java.util.Random;
import org.lwjgl.LWJGLException;
import static org.lwjgl.opengl.Display.*;
import org.lwjgl.opengl.DisplayMode;
import static org.lwjgl.opengl.GL11.*;

public class OpenGLFacade {

    private final static float canvassize = 475f;

    // these variables are modified from outside of the drawWaveForm thread.
    // because of this they must be marked volatile, and any write or read operation
    // must synchronize the OpenGLFacade instance.
    //
    // Contains the sources waveform. Depending on which button the user clicked,
    // this linkedlist could contain 30k, 100k, or more samples
    public volatile LinkedList<Byte> waveform;

    // Determines wether a source is selected. A source must be selected in order
    // for the waveform to have any samples in it
    //
    // if true  -> check if firstRun
    // if false -> do nothing
    public volatile boolean selected = false;

    // While a source is selected, it should only be drawn once. No point wasting
    // cpu cycles redrawing the same thing.
    //
    // if true  -> draw waveform
    // if false -> do nothing
    public volatile boolean firstRun = false;

    public synchronized void drawWaveForm(final Canvas c) {
        new Thread(new Runnable() {
            // implement functionality for anstract run
            public void run() {

                try {
                    setParent(c);
                    setDisplayMode(new DisplayMode(475, 103));
                    create();

                } catch (LWJGLException e) {
                    e.printStackTrace();
                }
                // init OpenGL
                glMatrixMode(GL_PROJECTION);
                glLoadIdentity();
                glOrtho(0, 475, 0, 103, 1, -1);
                glMatrixMode(GL_MODELVIEW);

                while (!isCloseRequested()) {

                    if (selected) {
                        if (firstRun) {
                            glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
                            float[] c = getColors();
                            // set the color of the quad (R,G,B,A)
                            glColor3f(c[0], c[1], c[2]);
                            firstRun = false;

                            drawRectangles();
                            update();
                        }

                    } else {
                        // if something is still displaying, clear it
                        if (firstRun == false) {
                            glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
                            firstRun = true;
                            update();
                        }
                    }

                    try {
                        Thread.sleep(10);
                    } catch (InterruptedException ex) {
                    }
                }
                destroy();
            }
        }).start();

    }

    private synchronized void drawRectangles() {
        float offset = 0.0f;
        float number = waveform.size();
        float width = canvassize / number;

        for (int i = 0; i < number; i++) {
            // When user switches to a new source, stop drawing and
            // allow new size to propagate to thread.
            // bug fix: when switching to source with < 30k samples,
            //          for loop attempted to get() indexes larger than
            //          the size() of the source buffer.
            if (firstRun)
                break;

            int height = waveform.get(i);

            // draw quad
            glBegin(GL_QUADS);
            glVertex2f(offset, 0);
            glVertex2f(offset + width, 0);
            glVertex2f(offset + width, height);
            glVertex2f(offset, height);
            glEnd();

            offset += width;

        }
    }

    // safe way to create a random number in a range with nextInt()
    private Random r = new Random();
    // get a random colour from the 8 choices. Used to randomely colour waveforms
    private float[] getColors() {
        int c = r.nextInt(8);
        float[] b = new float[3];
        switch (c) {
            case 0: // yellow
                b[0] = 181f / 255f;
                b[1] = 137f / 255f;
                b[2] = 0f / 255f;
                break;
            case 1: // orange
                b[0] = 203f / 255f;
                b[1] = 75f / 255f;
                b[2] = 22f / 255f;
                break;
            case 2: // red
                b[0] = 220f / 255f;
                b[1] = 50f / 255f;
                b[2] = 47f / 255f;
                break;
            case 3: // magenta
                b[0] = 211f / 255f;
                b[1] = 54f / 255f;
                b[2] = 130f / 255f;
            case 4: // violet
                b[0] = 108f / 255f;
                b[1] = 113f / 255f;
                b[2] = 196f / 255f;
                break;
            case 5: // blue
                b[0] = 38f / 255f;
                b[1] = 139f / 255f;
                b[2] = 210f / 255f;
                break;
            case 6: // cyan
                b[0] = 42f / 255f;
                b[1] = 161f / 255f;
                b[2] = 152f / 255f;
                break;
            case 7: // green
                b[0] = 133f / 255f;
                b[1] = 153f / 255f;
                b[2] = 0f / 255f;
                break;
        }
        return b;
    }

}
