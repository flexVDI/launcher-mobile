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

#include <stdlib.h>

void SpiceGlibGlue_InitializeLogging(int32_t verbosityLevel);

int16_t SpiceGlibGlue_MainLoop(void);

int16_t SpiceGlibGlue_Connect(char* h, char* p,
                              char* tp, char* ws,
                              char* pw, char* cf,
                              char* cs, int32_t sound);

void SpiceGlibGlueSetDisplayBuffer(uint32_t *display_buffer,
                                   int32_t width,
                                   int32_t height);

int16_t SpiceGlibGlueLockDisplayBuffer(int32_t *width, int32_t *height);

void SpiceGlibGlueUnlockDisplayBuffer();

void SpiceGlibGlue_Disconnect(void);

int16_t SpiceGlibGlue_isConnected(void);

int16_t SpiceGlibGlue_getNumberOfChannels(void);

int SpiceGlibRecalcGeometry(int x, int y, int w, int h);

int SpiceGlibGlueUpdateDisplayData(char *, int *, int *);

int16_t SpiceGlibGlueMotionEvent(int pos_x, int pos_y, int16_t button_mask);

int16_t SpiceGlibGlueButtonEvent(int pos_x, int pos_y,
                                 int16_t button, int16_t button_mask, int16_t down);

void SpiceGlibGlue_SpiceKeyEvent(int down, int keycode);
