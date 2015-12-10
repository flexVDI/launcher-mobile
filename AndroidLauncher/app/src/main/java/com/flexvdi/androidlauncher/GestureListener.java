package com.flexvdi.androidlauncher;

import android.content.Context;
import android.os.Vibrator;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;

public class GestureListener extends GestureDetector.SimpleOnGestureListener {
    private boolean grabbing = false;
    private static final int SCROLL_EMPTY = 0;
    private static final int SCROLL_DOWN = 1;
    private static final int SCROLL_UP = 2;
    private int scrollDirection = SCROLL_EMPTY;
    private int scrollAcumDistance = 0;
    private float zoomFactor = 0.0f;
    private float zoomOffsetX = 0.0f;
    private float zoomOffsetY = 0.0f;
    private static final int GRAB_EMPTY = 0;
    private static final int GRAB_STATIC = 1;
    private static final int GRAB_MOVED = 2;
    private int prevX;
    private int prevY;
    private boolean grabMoved = false;
    private Context mContext;

    public GestureListener(Context context) {
        mContext = context;
    }

    @Override
    public boolean onSingleTapConfirmed(MotionEvent e) {
        Log.d("androidlauncher", "onSingleTapConfirmed");
        flexJNI.sendMouseClick((int) e.getX(), (int) e.getY(), 1, 1);
        return false;
    }
    public boolean onDoubleTap(MotionEvent e) {
        Log.d("androidlauncher", "onDoubleTap");
        flexJNI.sendMouseClick((int) e.getX(), (int) e.getY(), 1, 2);
        return true;
    }
    public void onLongPress(MotionEvent e) {
        Log.d("androidlauncher", "onLongPress");
        Vibrator v = (Vibrator) mContext.getSystemService(Context.VIBRATOR_SERVICE);
        v.vibrate(100);

        grabbing = true;
        prevX = (int) e.getX();
        prevY = (int) e.getY();
        //flexJNI.sendMouseEventEnded((int) e.getX(), (int) e.getY(), 1);
        flexJNI.sendMouseEventMoved(prevX, prevY, 1);
        flexJNI.sendMouseEventBegan(prevX, prevY, 1);
    }
    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
        Log.d("androidlauncher", "onScroll");
        if (zoomFactor != 0.0) {
            if (distanceX > 8.0) {
                zoomOffsetX += 0.02;
            } else if (distanceX < -8.0){
                zoomOffsetX -= 0.02;
            }

            if (distanceY > 8.0) {
                zoomOffsetY -= 0.02;
            } else if (distanceY < -8.0) {
                zoomOffsetY += 0.02;
            }

            adjustZoomOffset();
        } else if (!grabbing) {
            int direction = SCROLL_DOWN;
            int button = 5;
            if (distanceY < 0.0) {
                direction = SCROLL_UP;
                button = 4;
            }

            if (direction != scrollDirection) {
                scrollDirection = direction;
            } else {
                scrollAcumDistance += distanceY;
            }

            int velocity = Math.abs(scrollAcumDistance);
            int numClicks = 0;

            if (velocity < 20) {
                return true;
            } else if (velocity < 50) {
                numClicks = 1;
            } else {
                numClicks = 2;
            }

            scrollAcumDistance = 0;
            flexJNI.sendMouseClick((int) e1.getX(), (int) e1.getY(), button, numClicks);
        }
        return false;
    }
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
        Log.d("androidlauncher", "onFling");
        return true;
    }

    public void onTouchUp(MotionEvent e) {
        if (grabbing) {
            Log.d("androidlauncher", "de-grabbing");
            flexJNI.sendMouseEventEnded((int) e.getX(), (int) e.getY(), 1);
            if (!grabMoved) {
                flexJNI.sendMouseClick((int) e.getX(), (int) e.getY(), 3, 1);
            }
            grabbing = false;
        }

        grabMoved = false;
        scrollDirection = SCROLL_EMPTY;
        scrollAcumDistance = 0;
    }

    public void onMovement(MotionEvent e) {
        if (grabbing) {
            Log.d("androidlauncher", "movement while grabbing");

            int movX = (int) e.getX();
            int movY = (int) e.getY();
            int diffX = Math.abs(prevX - movX);
            int diffY = Math.abs(prevY - movY);

            if (diffX > 10 || diffY > 10) {
                prevX = diffX;
                prevY = diffY;
                grabMoved = true;
                flexJNI.sendMouseEventMoved((int) e.getX(), (int) e.getY(), 1);
            }
        }
    }

    private void adjustZoomOffset() {
        if (Math.abs(zoomOffsetX) > zoomFactor) {
            if (zoomOffsetX > 0) {
                zoomOffsetX = zoomFactor;
            } else {
                zoomOffsetX = -zoomFactor;
            }
        }

        if (Math.abs(zoomOffsetY) > zoomFactor) {
            if (zoomOffsetY > 0) {
                zoomOffsetY = zoomFactor;
            } else {
                zoomOffsetY = -zoomFactor;
            }
        }

        flexJNI.setZoomOffset(zoomOffsetX, zoomOffsetY);
    }

    public void setZoomFactor(float zoom) {
        zoomFactor = zoom;
        adjustZoomOffset();
        flexJNI.setZoomFactor(zoomFactor);
    }
}
