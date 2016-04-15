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

void engine_init_buffer(int width, int height);

void engine_free_buffer(void);

void engine_init_screen(void);

int engine_draw(int width, int height);

int engine_draw_disconnected(int width, int height);

void engine_set_keyboard_opacity(float opacity);

void engine_set_keyboard_offset(float offset);

void engine_set_main_opacity(float opacity);

void engine_set_main_offset(float offset);

void engine_set_save_location(const char *path);

void engine_save_main_texture(void);

void engine_load_main_texture(int max_width, int maxh_height);
