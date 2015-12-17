package com.flexvdi.androidlauncher;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.util.Log;
import android.widget.Toast;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

public class MainGLRenderer implements GLSurfaceView.Renderer {
    private MainActivity mainActivity;
    private boolean engineConfigured = false;
    private int backingWidth = 0;
    private int backingHeight = 0;
    private int engineWidth = 0;
    private int engineHeight = 0;
    private int guestWidth = 0;
    private int guestHeight = 0;
    private double mouseScale = 1.0;
    private double contentScale = 2.0;
    private long lastResolutionCheck = 0;
    private int resolutionChangeRequests = 0;
    private boolean resolutionIsOK = false;
    private boolean resolutionRescaled = false;
    private Toast statusToast;
    private long lastToastShown = 0;

    public MainGLRenderer(Context context, double dMouseScale, double dContentScale) {
        super();
        mainActivity = (MainActivity) context;
        statusToast = new Toast(mainActivity);
        mouseScale = dMouseScale;
        contentScale = dContentScale;
    }

    private void initEngine(GL10 gl) {
        flexJNI.initBuffer(engineWidth, engineHeight);
        flexJNI.initScreen(mouseScale, contentScale);

        gl.glViewport(0, 0, backingWidth, backingHeight);
        gl.glMatrixMode(gl.GL_PROJECTION);
        gl.glLoadIdentity();
        gl.glOrthof(0, backingWidth, backingHeight, 0, 0, 1);
    }

    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        Log.d("androidlauncher", "onSurfaceCreated");
        if (engineConfigured) {
            initEngine(gl);
        }
    }

    public void onDrawFrame(GL10 gl) {
        gl.glClear(GL10.GL_COLOR_BUFFER_BIT |
                GL10.GL_DEPTH_BUFFER_BIT);

        if (flexJNI.isConnected() != 0) {
            //Log.e("androidlauncher", "SPICE is connected");
            long now = System.currentTimeMillis();
            if (now - lastResolutionCheck > 3000) {
                int newGuestWidth = flexJNI.getGuestWidth();
                int newGuestHeight = flexJNI.getGuestHeight();

                if (newGuestWidth != guestWidth || newGuestHeight != guestHeight) {
                    if (resolutionRescaled) {
                        resolutionRescaled = false;
                    } else {
                        resolutionChangeRequests = 0;
                    }
                }

                guestWidth = newGuestWidth;
                guestHeight = newGuestHeight;

                if (guestWidth != engineWidth || guestHeight != engineHeight) {
                    Log.d("androidlauncher", "wrong resolution: " + guestWidth + "x" + guestHeight + " vs " + engineWidth + "x" + engineHeight);
                    resolutionIsOK = false;
                    if (guestWidth == 0 || guestHeight == 0) {
                        flexJNI.draw(engineWidth, engineHeight);
                    } else if (resolutionChangeRequests < 3) {
                        mainActivity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                statusToast.cancel();
                                statusToast.makeText(mainActivity, "Solicitando cambio de resolución", Toast.LENGTH_SHORT).show();
                            }
                        });
                        flexJNI.requestResolution(engineWidth, engineHeight);
                        resolutionChangeRequests++;
                    } else if (guestWidth > engineWidth || guestHeight > engineHeight) {
                        if (mouseScale < 1.0) {
                            mainActivity.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    statusToast.cancel();
                                    statusToast.makeText(mainActivity, "Incrementando la resolución del dispositivo", Toast.LENGTH_SHORT).show();
                                }
                            });
                            mouseScale = 1.0;
                            contentScale = 2.0;
                            resolutionRescaled = true;
                            engineWidth = (int) (backingWidth * mouseScale);
                            engineHeight = (int) (backingHeight * mouseScale);
                            initEngine(gl);
                        } else {
                            mainActivity.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    statusToast.cancel();
                                    statusToast.makeText(mainActivity, "Resolución de escritorio inválida", Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
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
            engineWidth = (int) (width * mouseScale);
            engineHeight = (int) (height * mouseScale);
        }

        resolutionChangeRequests = 0;
        initEngine(gl);
    }
}
