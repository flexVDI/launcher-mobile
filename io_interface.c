#include <time.h>
#include <unistd.h>
#include "globals.h"
#include "io_interface.h"
#include "native.h"
#include "spice.h"

static int check_keyboard_widget(io_event_t* event_in)
{
    
    float k_start_x;
    float k_start_y;
    int keyb_start_x;
    int keyb_end_x;
    int keyb_start_y;
    int keyb_end_y;
    
    if (global_state.content_scale == 2) {
        k_start_x = (95 - (((48 * 100) / global_state.width) * 4.0)) / 100.0;
        k_start_y = ((48 * 100) / global_state.height) / 40.0;
    } else {
        k_start_x = (95 - (((48 * 100) / global_state.width) * 2.0)) / 100.0;
        k_start_y = ((48 * 100) / global_state.height) / 80.0;
    }
    
    keyb_start_x = (global_state.width / 2) + (global_state.width / 2 * k_start_x);
    keyb_end_x = (global_state.width / 2) + (global_state.width / 2 * 0.95);
    keyb_start_y = (global_state.height / 2) - (global_state.height / 2 * k_start_y);
    keyb_end_y = (global_state.height / 2) + (global_state.height / 2 * k_start_y);

    if (event_in->position[0] > keyb_start_x &&
        event_in->position[0] < keyb_end_x &&
        event_in->position[1] > keyb_start_y &&
        event_in->position[1] < keyb_end_y) {
        return 1;
    }

    return 0;
}

void IO_PushEvent(io_event_t* event_in){
    struct timespec sleeptime;
    double mouse_fix[2];

    if (check_keyboard_widget(event_in)) {
        if (event_in->type == IO_EVENT_ENDED) {
            if (event_in->button == 4) {
                native_show_menu();
            } else {
                native_show_keyboard();
            }
        }
        return;
    }

    if (global_state.zoom != 0.0) {
        //double zoom_width = (global_state.width * (1 - global_state.zoom));
        //double zoom_height = (global_state.height * (1 - global_state.zoom));
        //double offset_x = global_state.width - zoom_width;
        //double offset_y = global_state.height - zoom_height;
        double offset_x = (global_state.width * global_state.zoom) +
            (global_state.zoom_offset_x * global_state.width);
        double offset_y = (global_state.height * global_state.zoom) -
            (global_state.zoom_offset_y * global_state.height);
        double ratio_x = (global_state.width - (global_state.width * global_state.zoom * 2)) / global_state.width;
        double ratio_y = (global_state.height - (global_state.height * global_state.zoom * 2)) /global_state.height;

//        mouse_fix[0] = (offset_x + (global_state.zoom_offset_x * zoom_width) + (event_in->position[0] * zoom_ratio_x));
//        mouse_fix[1] = (offset_y - (global_state.zoom_offset_y * zoom_height) + (event_in->position[1] * zoom_ratio_y));
        
        mouse_fix[0] = (event_in->position[0] * ratio_x) + offset_x;
        mouse_fix[1] = (event_in->position[1] * ratio_y) + offset_y;

#if 0
        printf("ratio_x=%f\n", ratio_x);
        printf("ratio_y=%f\n", ratio_y);
        printf("offset_x=%f\n", offset_x);
        printf("offset_y=%f\n", offset_y);
        printf("mouse_fix_x=%f\n", mouse_fix[0]);
        printf("mouse_fix_y=%f\n", mouse_fix[1]);
#endif

//        printf("zoom_max_ratio_x=%f\n", zoom_max_ratio_x);
//        printf("zoom_max_ratio_y=%f\n", zoom_max_ratio_y);
    } else {
        mouse_fix[0] = event_in->position[0] * global_state.mouse_fix[0];
        mouse_fix[1] = event_in->position[1] * global_state.mouse_fix[1];
    }

#if 0
    printf("global_state.width=%d\n", global_state.width);
    printf("global_state.height=%d\n", global_state.height);
    printf("global_state.guest_width=%d\n", global_state.guest_width);
    printf("global_state.guest_height=%d\n", global_state.guest_height);
    
    
    printf("event_in_x=%f\n", event_in->position[0]);
    printf("event_in_y=%f\n", event_in->position[1]);
    printf("mouse_fix_x=%f\n", mouse_fix[0]);
    printf("mouse_fix_y=%f\n", mouse_fix[1]);
#endif
    
    sleeptime.tv_sec = 0;
    //sleeptime.tv_nsec = 500 * 1000;
    sleeptime.tv_nsec = 50 * 1000 * 1000;
    switch(event_in->type) {
        case IO_EVENT_MOVED:
            engine_spice_motion_event(mouse_fix[0], mouse_fix[1]);
            break;
        case IO_EVENT_BEGAN:
            engine_spice_button_event(mouse_fix[0], mouse_fix[1], event_in->button, 1);
            nanosleep(&sleeptime, NULL);
            //sleep(1);
            break;
        case IO_EVENT_ENDED:
            engine_spice_button_event(mouse_fix[0], mouse_fix[1], event_in->button, 0);
            nanosleep(&sleeptime, NULL);
            //sleep(1);
            break;
    }
}
