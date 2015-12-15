package com.flexvdi.androidlauncher;

import android.content.Context;
import android.opengl.GLSurfaceView;

public class MainGLSurfaceView extends GLSurfaceView {
    private final MainGLRenderer mRenderer;

    public MainGLSurfaceView(Context context, double mouseScale, double contentScale){
        super(context);

        setEGLContextClientVersion(1);

        mRenderer = new MainGLRenderer(context, mouseScale, contentScale);

        // Set the Renderer for drawing on the GLSurfaceView
        setRenderer(mRenderer);
    }
}
