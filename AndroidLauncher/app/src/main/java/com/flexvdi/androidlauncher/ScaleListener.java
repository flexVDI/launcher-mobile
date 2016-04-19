/*
 * launcher-mobile: a multiplatform flexVDI/SPICE client
 *
 * Copyright (C) 2016 flexVDI (Flexible Software Solutions S.L.)
 *
 * This file is part of launcher-mobile.
 *
 * launcher-mobile is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 2 of the License, or
 * (at your option) any later version.
 *
 * launcher-mobile is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with launcher-mobile.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.flexvdi.androidlauncher;

import android.util.Log;
import android.view.ScaleGestureDetector;


public class ScaleListener extends ScaleGestureDetector.SimpleOnScaleGestureListener {
    private float zoomFactor = 0.0f;
    private GestureListener mGestureListener;

    public ScaleListener(GestureListener gestureListener) {
        super();
        mGestureListener = gestureListener;
    }

    @Override
    public boolean onScale(ScaleGestureDetector detector) {
        float diff = (detector.getScaleFactor() - 1.0f) / 3;
        zoomFactor += diff;
        if (zoomFactor > 0.30f) {
            zoomFactor = 0.30f;
        } else if (zoomFactor < 0.0f) {
            zoomFactor = 0.0f;
        }

        Log.d("androidlauncher", "onScale factor=" + zoomFactor);

        mGestureListener.setZoomFactor(zoomFactor);
        return true;
    }
}
