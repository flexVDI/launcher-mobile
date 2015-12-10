#ifndef _GLOBALS_H
#define _GLOBALS_H

#if defined(LINUX)
#include <GL/gl.h>
#elif defined(ANDROID)
#include <android/log.h>
#include <GLES/gl.h>
#else
#include <OpenGLES/ES1/gl.h>
#endif
#include <stdio.h>
#include "spice.h"

typedef struct _global_state_t {
    char *save_path;
    
    int conn_state;
    int main_loop_running;
    int display_state;
    spice_conn_data_t *conn_data;
    int enable_audio;
    
    int width;
    int height;
    int guest_width;
    int guest_height;
    int content_scale;
    int change_resolution;
    int input_initialized;
    int first_frame;
    
    double mouse_fix[2];
    double zoom;
    double zoom_offset_x;
    double zoom_offset_y;

    GLuint main_texture[1];
    int main_texture_created;
    GLuint keyboard_texture[1];
    char *spice_display_buffer;
    
    int button_mask;
    int keyboard_visible;
    float keyboard_opacity;
    float keyboard_offset;
    float main_opacity;
    float main_offset;
} global_state_t;

global_state_t global_state;

#define DISPLAY_INVALIDATE 0x1
#define DISPLAY_CHANGE_RESOLUTION 0x2

#define DISCONNECTED 0x0
#define CONNECTED 0x1
#define AUTOCONNECT 0x2

#ifdef ANDROIDX
#define GLUE_DEBUG(fmt, ...) __android_log_print(ANDROID_LOG_ERROR, "flexdp-engine", fmt, ## __VA_ARGS__);
#else
#define GLUE_DEBUG(fmt, ...) printf(fmt, ## __VA_ARGS__);
#endif

#endif

