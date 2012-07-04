package future;

import org.lwjgl.LWJGLException;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.ARBTextureRectangle;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.UnicodeFont;
import org.newdawn.slick.font.effects.ColorEffect;
import utility.*;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL11.glDeleteTextures;

/**
 * Demonstrates face culling
 *
 * @author Oskar Veerhoek
 */
public class CullingTheoryDemo {

    private static final String WINDOW_TITLE = "Culling!";
    private static final int[] WINDOW_DIMENSIONS = {1280, 720};

    private static enum RenderShape {TRIANGLES, LINES, POINTS, MODELS}
    private static enum CullState {FRONT, FRONT_AND_BACK, BACK, NONE,}
    private static CullState cullState = CullState.NONE;
    private static UnicodeFont font;
    private static RenderShape renderShape = RenderShape.TRIANGLES;

    private static void render() {
        glClear(GL_COLOR_BUFFER_BIT);
        glMatrixMode(GL_PROJECTION);
        glOrtho(0, 1280, 720, 0, 1, -1);
        glMatrixMode(GL_MODELVIEW);
        glPushAttrib(GL_ENABLE_BIT);
        glDisable(GL_CULL_FACE);
        font.drawString(15, 15, "Drawing: " + renderShape.toString());
        font.drawString(15, 35, "Culling: " + cullState.toString());
        glPopAttrib();
        glMatrixMode(GL_PROJECTION);
        glLoadIdentity();
        glMatrixMode(GL_MODELVIEW);
        switch (renderShape) {
            case TRIANGLES:
                glBegin(GL_TRIANGLES);
                glColor3d(1, 0, 0);
                glVertex2d(-0.75, -0.85);
                glColor3d(0, 1, 0);
                glVertex2d(+0.75, -0.85);
                glColor3d(0, 0, 1);
                glVertex2d(+0.00, +0.85);
                glEnd();
                break;
            case LINES:
                glColor3d(1, 1, 1);
                glBegin(GL_LINES);
                glVertex2d(-0.75, +0.75);
                glVertex2d(+0.75, -0.75);
                glEnd();
                break;
            case POINTS:
                glColor3d(1, 1, 1);
                glBegin(GL_POINTS);
                glVertex2d(-0.5, +0.5);
                glVertex2d(+0.5, +0.5);
                glVertex2d(+0.5, -0.5);
                glVertex2d(-0.5, -0.5);
                glEnd();
                break;
        }
    }

    private static void logic() {

    }

    private static void setUpFonts() {
        java.awt.Font awtFont = new java.awt.Font("Helvetica", java.awt.Font.BOLD, 18);
        font = new UnicodeFont(awtFont);
        font.getEffects().add(new ColorEffect(java.awt.Color.white));
        font.addAsciiGlyphs();
        try {
            font.loadGlyphs();
        } catch (SlickException e) {
            e.printStackTrace();
            cleanUp(true);
        }
    }

    private static void input() {
        while (Keyboard.next()) {
            if (Keyboard.getEventKeyState()) {
                switch (Keyboard.getEventKey()) {
                    case Keyboard.KEY_F:
                        cullState = CullState.FRONT;
                        glEnable(GL_CULL_FACE);
                        glCullFace(GL_FRONT);
                        break;
                    case Keyboard.KEY_B:
                        cullState = CullState.BACK;
                        glEnable(GL_CULL_FACE);
                        glCullFace(GL_BACK);
                        break;
                    case Keyboard.KEY_N:
                        cullState = CullState.NONE;
                        glDisable(GL_CULL_FACE);
                        break;
                    case Keyboard.KEY_A:
                        cullState = CullState.FRONT_AND_BACK;
                        glEnable(GL_CULL_FACE);
                        glCullFace(GL_FRONT_AND_BACK);
                        break;
                    case Keyboard.KEY_P:
                        renderShape = RenderShape.POINTS;
                        break;
                    case Keyboard.KEY_L:
                        renderShape = RenderShape.LINES;
                        break;
                    case Keyboard.KEY_T:
                        renderShape = RenderShape.TRIANGLES;
                        break;
                }
            }
        }
    }

    private static void setUpTextures() {
        int loadingTexture = ImagingTools.glLoadTextureLinear("res/loading16x9.png");
        glPushAttrib(GL_ENABLE_BIT);
        glEnable(ARBTextureRectangle.GL_TEXTURE_RECTANGLE_ARB);
        glBindTexture(ARBTextureRectangle.GL_TEXTURE_RECTANGLE_ARB, loadingTexture);
        glBegin(GL_QUADS);
        glTexCoord2d(0, 0);
        glVertex2d(-1, +1);
        glTexCoord2d(0, 720);
        glVertex2d(-1, -1);
        glTexCoord2d(1280, 720);
        glVertex2d(+1, -1);
        glTexCoord2d(1280, 0);
        glVertex2d(+1, +1);
        glEnd();
        glFlush();
        Display.update();
        glBindTexture(ARBTextureRectangle.GL_TEXTURE_RECTANGLE_ARB, 0);
        glPopAttrib();
        glDeleteTextures(loadingTexture);
    }

    private static void setUpStates() {
        glEnable(GL_CULL_FACE);
        glEnable(GL_BLEND);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
        glLineWidth(3);
        glPointSize(3);
    }

    private static void cleanUp(boolean asCrash) {
        font.destroy();
        Display.destroy();
        System.exit(asCrash ? 1 : 0);
    }

    private static void update() {
        Display.update();
    }

    private static void enterGameLoop() {
        while (!Display.isCloseRequested()) {
            render();
            logic();
            input();
            update();
        }
    }

    private static void setUpDisplay() {
        try {
            Display.setDisplayMode(new DisplayMode(WINDOW_DIMENSIONS[0], WINDOW_DIMENSIONS[1]));
            Display.setVSyncEnabled(true);
            Display.setTitle(WINDOW_TITLE);
            Display.create();
        } catch (LWJGLException e) {
            e.printStackTrace();
            cleanUp(true);
        }
    }

    public static void main(String[] args) {
        setUpDisplay();
        setUpTextures();
        setUpFonts();
        setUpStates();
        enterGameLoop();
        cleanUp(false);
    }

}
