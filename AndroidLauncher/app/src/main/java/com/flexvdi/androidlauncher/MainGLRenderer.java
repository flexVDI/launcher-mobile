package com.flexvdi.androidlauncher;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.util.Log;
import android.widget.Toast;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

public class MainGLRenderer implements GLSurfaceView.Renderer {
    private MainActivity mainActivity;
    private int backingWidth = 0;
    private int backingHeight = 0;
    private int engineWidth = 0;
    private int engineHeight = 0;
    private double engineScale = 1.0;
    private long lastResolutionCheck = 0;
    private boolean resolutionIsOK = false;
    private Toast statusToast;
    private long lastToastShown = 0;

    public MainGLRenderer(Context context, double scale) {
        super();
        mainActivity = (MainActivity) context;
        statusToast = new Toast(mainActivity);
        engineScale = scale;
    }

    public void onSurfaceCreated(GL10 gl, EGLConfig config) {

    }

    public void onDrawFrame(GL10 gl) {
        gl.glClear(GL10.GL_COLOR_BUFFER_BIT |
                GL10.GL_DEPTH_BUFFER_BIT);

        if (flexJNI.isConnected() != 0) {
            //Log.e("androidlauncher", "SPICE is connected");
            long now = System.currentTimeMillis();
            if (now - lastResolutionCheck > 3000) {
                int guest_width = flexJNI.getGuestWidth();
                int guest_height = flexJNI.getGuestHeight();

                if (guest_width != engineWidth || guest_height != engineHeight) {
                    Log.d("androidlauncher", "wrong resolution: " + guest_width + "x" + guest_height + " vs " + engineWidth + "x" + engineHeight);
                    resolutionIsOK = false;
                    if (guest_width == 0 || guest_height == 0) {
                        flexJNI.draw(engineWidth, engineHeight);
                    } else {
                        mainActivity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                statusToast.cancel();
                                statusToast.makeText(mainActivity, "Solicitando cambio de resolución", Toast.LENGTH_SHORT).show();
                            }
                        });
                        flexJNI.requestResolution(engineWidth, engineHeight);
                    }
                } else {
                    Log.d("androidlauncher", "good resolution");
                    if (!resolutionIsOK) {
                        mainActivity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                statusToast.cancel();
                            }
                        });
                    }
                    resolutionIsOK = true;
                }
                lastResolutionCheck = now;
            }
        } else {
            long now = System.currentTimeMillis();
            if (now - lastToastShown > 2000) {
                mainActivity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        statusToast.cancel();
                        statusToast.makeText(mainActivity, "Conectando a su escritorio", Toast.LENGTH_SHORT).show();
                    }
                });
                lastToastShown = now;
            }
        }

        //if (resolutionIsOK)
        flexJNI.draw(engineWidth, engineHeight);
    }

    public void onSurfaceChanged(GL10 gl, int width, int height) {
        if (width != backingWidth || height != backingHeight) {
            backingWidth = width;
            backingHeight = height;
            engineWidth = (int) (width * engineScale);
            engineHeight = (int) (height * engineScale);
            //engineWidth = width;
            //engineHeight = height;
            flexJNI.initBuffer(engineWidth, engineHeight);
            flexJNI.initScreen(engineScale);
        }

        gl.glViewport(0, 0, backingWidth, backingHeight);
        gl.glMatrixMode(gl.GL_PROJECTION);
        gl.glLoadIdentity();
        gl.glOrthof(0, backingWidth, backingHeight, 0, 0, 1);
    }
}
