package com.flexvdi.androidlauncher;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import java.nio.ByteBuffer;

public class flexJNI {
    native static void setJava();

    native static void setConnectionData(String jniHost, String jniPort, String jniWsport, String jniPassword, int enableSound);
    native static void connect();
    native static void disconnect();
    native static int isConnected();

    native static void initBuffer(int width, int height);
    native static void initScreen(double mouseScale, double contentScale);
    native static int getGuestWidth();
    native static int getGuestHeight();
    native static int draw(int width, int height);
    native static int drawDisconnected(int width, int height);
    native static void requestResolution(int width, int height);
    native static void setZoomFactor(float zoom);
    native static void setZoomOffset(float offsetX, float offsetY);

    native static void sendMouseClick(int x, int y, int button, int numClicks);
    native static void sendMouseEventBegan(int x, int y, int button);
    native static void sendMouseEventMoved(int x, int y, int button);
    native static void sendMouseEventEnded(int x, int y, int button);
    native static void sendKeyEvent(int charCode, int event);

    native static void setKeyboardOpacity(double opacity);
    native static void setKeyboardOffset(double offset);

    private static ByteBuffer keyboardBuffer;
    private static int keyboardWidth;
    private static int keyboardHeight;

    private static MainActivity mainActivity;

    public static void setContext(MainActivity mMainActivity) {
        mainActivity = mMainActivity;
    }

    public static ByteBuffer loadPNG() {
        Resources res = mainActivity.getResources();
        int id = R.drawable.keyboard;

        Bitmap keyboardBitmap = BitmapFactory.decodeResource(res, id);
        keyboardWidth = keyboardBitmap.getWidth();
        keyboardHeight = keyboardBitmap.getHeight();
        keyboardBuffer = ByteBuffer.allocateDirect(keyboardBitmap.getByteCount());
        keyboardBitmap.copyPixelsToBuffer(keyboardBuffer);
        return keyboardBuffer;
    }

    public static int getPNGWidth() {
        return keyboardWidth;
    }

    public static int getPNGHeight() {
        return keyboardHeight;
    }

    public static void showKeyboard() {
        Log.d("androidlauncher", "showKeyboard");
        mainActivity.toggleKeyboard();
    }

    static {
        System.loadLibrary("flexdp-jni");
    }
}
