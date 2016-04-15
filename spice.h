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

#ifndef SPICE_H
#define SPICE_H

#include "spice-mono-glue.h"

typedef struct _spice_conn_data_t {
    char *host;
    char *port;
    char *wsport;
    char *password;
} spice_conn_data_t;

void engine_spice_worker(void *data);

void engine_spice_set_connection_data(const char *host,
                                      const char *port,
                                      const char *wsport,
                                      const char *password,
                                      int32_t enableAudio);

int engine_spice_connect(void);

void engine_spice_disconnect(void);

int engine_spice_is_connected(void);

void engine_spice_request_resolution(int width, int height);

void engine_spice_resolution_changed(void);

int engine_spice_lock_display(char *display_buffer, int *width, int *height);

void engine_spice_unlock_display(void);

void engine_spice_motion_event(int pos_x, int pos_y);

void engine_spice_button_event(int pos_x, int pos_y, int button, int down);

void engine_spice_keyboard_event(int keycode, int16_t down);
#endif