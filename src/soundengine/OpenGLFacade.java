/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
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
    public void drawThing() {
        System.out.println(isActive());
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

    }

    public volatile LinkedList<Byte> waveform;
    public volatile boolean selected = false;
    public volatile boolean firstRun = true;

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

                        byte min = 0;
                        byte max = 0;
                        if (firstRun) {
                            System.out.println("First");
                            // clear the screen and depth buffer
                            glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

                            // find the min and max of the first 250 samples
                            // to scale each sample between [0,100] for graphing
                            byte[] minmax = new byte[2];
                            minmax = findGlobalMaximums(waveform);
                            min = minmax[0];
                            max = minmax[1];
                            firstRun = false;
                        }

                        float offset = 0.0f;
                        float number = waveform.size();
                        float width = canvassize / number;

                        float[] c = getColors();
                        // set the color of the quad (R,G,B,A)
                        glColor3f(c[0], c[1], c[2]);
                        for (int i = 0; i < number; i++) {
                            if (firstRun)
                                break;

                            int height = scale(waveform.get(i), min, max,
                                    (byte) 100);
                            if (i % 2500 == 0) {
                                c = getColors();
                                System.out.println(
                                        c[0] + " : " + c[1] + " : " + c[2]);
                                // set the color of the quad (R,G,B,A)
                                glColor3f(c[0], c[1], c[2]);

                            }
                            // draw quad
                            glBegin(GL_QUADS);
                            glVertex2f(offset, 0);
                            glVertex2f(offset + width, 0);
                            glVertex2f(offset + width, height);
                            glVertex2f(offset, height);
                            glEnd();
                            offset += width;
                        }
                    } else {
                        firstRun = true;
                    }
                    update();
                    try {
                        Thread.sleep(10);
                    } catch (InterruptedException ex) {
                    }
                }
                destroy();
            }
        }).start();

    }

    Random r = new Random();
    private float[] getColors() {
        int c = r.nextInt(8);
        System.out.println(c);
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

//find global min and global max values in list
    private byte[] findGlobalMaximums(LinkedList<Byte> l) {
        int c1, c2;
        byte[] m = new byte[2];
        for (int i = 0; i < l.size(); i++) {
            m[0] = l.get(i) < m[0] ? l.get(i) : m[0];
            m[1] = l.get(i) > m[1] ? l.get(i) : m[1];
        }
        return m;
    }

    private byte scale(byte val, byte min, byte max, byte range) {
        return (byte) ((float) ((float) (val - min) / (float) (max - min)) * range);
    }

}
