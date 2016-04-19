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
