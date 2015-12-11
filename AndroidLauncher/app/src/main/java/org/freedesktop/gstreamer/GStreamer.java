package org.freedesktop.gstreamer;

import android.content.Context;

public class GStreamer {
    private static native void nativeInit(Context context) throws Exception;

    static {
        System.loadLibrary("gstreamer_android");
    }

    public static void init(Context context) throws Exception {
        nativeInit(context);
    }
}