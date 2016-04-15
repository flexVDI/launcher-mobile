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

#ifndef io_interface_h
#define io_interface_h

typedef enum {IO_EVENT_BEGAN, IO_EVENT_MOVED, IO_EVENT_ENDED} event_type_t;

typedef struct _io_event_t
{
	// Used to be an array of int (Worked well on windows,macosx and iphone).
	// but Android return coordinates as float....
	float position[2];
    int button;

    event_type_t type;
} io_event_t;


//Expect event position in windows active+passive surface coordinates system.
void IO_PushEvent(io_event_t* event);

#endif
