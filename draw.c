#if defined(LINUX)
#include <GL/gl.h>
#include <GL/glu.h>
#else
#include <OpenGLES/ES1/gl.h>
#endif

#include <string.h>
#include <stdlib.h>
#include <limits.h>

#include "globals.h"
#include "draw.h"
#include "spice.h"
#include "native.h"

typedef struct _vertex3d_t {
    GLfloat x;
    GLfloat y;
    GLfloat z;
} vertex3d_t;

typedef struct _triangle3d_t {
    vertex3d_t v1;
    vertex3d_t v2;
    vertex3d_t v3;
} triangle3d_t;

static void create_main_texture(char *bitmap, int width, int height)
{
    glBindTexture(GL_TEXTURE_2D, 0);
    if (glGetError() != GL_NO_ERROR) {
        GLUE_DEBUG("glBindTexture\n");
    }
    glDeleteTextures(1, &global_state.main_texture[0]);
    if (glGetError() != GL_NO_ERROR) {
        GLUE_DEBUG("glDeleteTextures");
    }
    glGenTextures(1, &global_state.main_texture[0]);
    if (glGetError() != GL_NO_ERROR) {
        GLUE_DEBUG("glGenTextures");
    }
    glBindTexture(GL_TEXTURE_2D, global_state.main_texture[0]);
    if (glGetError() != GL_NO_ERROR) {
        GLUE_DEBUG("glBindTextures2");
    }
    glTexParameteri(GL_TEXTURE_2D,GL_TEXTURE_MIN_FILTER,GL_LINEAR);
    if (glGetError() != GL_NO_ERROR) {
        GLUE_DEBUG("parameter1");
    }
    glTexParameteri(GL_TEXTURE_2D,GL_TEXTURE_MAG_FILTER,GL_LINEAR);
    if (glGetError() != GL_NO_ERROR) {
        GLUE_DEBUG("parameter2");
    }
    glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE);
    if (glGetError() != GL_NO_ERROR) {
        GLUE_DEBUG("parameter3");
    }
    glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE);
    if (glGetError() != GL_NO_ERROR) {
        GLUE_DEBUG("parameter4");
    }
    glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, width, height,
                 0, GL_RGBA, GL_UNSIGNED_BYTE, bitmap);
    int glerror = glGetError();
    if (glerror == GL_NO_ERROR) {
        global_state.main_texture_created = 1;
        GLUE_DEBUG("Success creating texture: %dx%d\n", width, height);
    } else {
        GLUE_DEBUG("Error creating texture: %d\n", glerror);
    }
}

static void update_main_texture(char *bitmap, int width, int height)
{
    if (glGetError() != GL_NO_ERROR) {
        GLUE_DEBUG("Update: PreBindTexture\n");
        global_state.main_texture_created = 0;
    }
    glBindTexture(GL_TEXTURE_2D, global_state.main_texture[0]);
    if (glGetError() != GL_NO_ERROR) {
        GLUE_DEBUG("Update: BindTexture\n");
        global_state.main_texture_created = 0;
    }
    glTexParameteri(GL_TEXTURE_2D,GL_TEXTURE_MIN_FILTER,GL_LINEAR);
    if (glGetError() != GL_NO_ERROR) {
        GLUE_DEBUG("parameter1");
    }
    glTexParameteri(GL_TEXTURE_2D,GL_TEXTURE_MAG_FILTER,GL_LINEAR);
    if (glGetError() != GL_NO_ERROR) {
        GLUE_DEBUG("parameter2");
    }
    glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE);
    if (glGetError() != GL_NO_ERROR) {
        GLUE_DEBUG("parameter3");
    }
    glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE);
    if (glGetError() != GL_NO_ERROR) {
        GLUE_DEBUG("parameter4");
    }
    glTexSubImage2D(GL_TEXTURE_2D, 0, 0, 0,
                    width, height,
                    GL_RGBA, GL_UNSIGNED_BYTE, bitmap);
    int glerror = glGetError();
    if (glerror != GL_NO_ERROR) {
        GLUE_DEBUG("Error updating texture: %d\n", glerror);
        //global_state.main_texture_created = 0;
        glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, width, height,
                     0, GL_RGBA, GL_UNSIGNED_BYTE, bitmap);
        int glerror = glGetError();
        if (glerror == GL_NO_ERROR) {
            global_state.main_texture_created = 1;
            GLUE_DEBUG("Update: Success creating texture: %dx%d\n", width, height);
        } else {
            GLUE_DEBUG("Update: Error creating texture: %d\n", glerror);
        }
    }
}

static void apply_zoom(GLfloat *tex_coords)
{
    tex_coords[0] += global_state.zoom;
    tex_coords[1] -= global_state.zoom;
    tex_coords[2] -= global_state.zoom;
    tex_coords[3] -= global_state.zoom;
    tex_coords[4] += global_state.zoom;
    tex_coords[5] += global_state.zoom;
    tex_coords[6] -= global_state.zoom;
    tex_coords[7] += global_state.zoom;
}

static void apply_offset(GLfloat *tex_coords)
{
    int i;
    double offset;

    for (i = 0; i < 8; i++) {
        if (i % 2) {
            offset = global_state.zoom_offset_y;
        } else {
            offset = global_state.zoom_offset_x;
        }

        tex_coords[i] += offset;
        if (tex_coords[i] > 1.0) {
            tex_coords[i] = 1.0;
        } else if (tex_coords[i] < 0.0) {
            tex_coords[i] = 0.0;
        }
    }
}

static void render_main_texture()
{
    GLfloat square[] = {-1.0,  1.0, 0.0,
                         1.0,  1.0, 0.0,
                        -1.0, -1.0, 0.0,
                         1.0, -1.0, 0.0};
    GLfloat texCoords[] = {0.0, 1.0,
                           1.0, 1.0,
                           0.0, 0.0,
                           1.0, 0.0};

    apply_zoom(&texCoords[0]);
    apply_offset(&texCoords[0]);
    //print_coords(&texCoords[0]);

    glEnableClientState(GL_VERTEX_ARRAY);
    glEnableClientState(GL_NORMAL_ARRAY);
    glEnableClientState(GL_TEXTURE_COORD_ARRAY);

    glClearColor(0.0, 0.0, 0.0, 1.0);
    //glColor4f(1.0, 1.0, 0.0, 1.0);
    glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

    glLoadIdentity();

    //glTranslatef(0.0, 0.0, 1.0);
    //glRotatef(80.0, 0.0, 0.0, 0.0);

    //glBindTexture(GL_TEXTURE_2D, global_state.keyboard_texture[0]);
    glBindTexture(GL_TEXTURE_2D, global_state.main_texture[0]);

    glColor4f(1.0, 1.0, 1.0, global_state.main_opacity);
    glVertexPointer(3, GL_FLOAT, 0, &square[0]);
    glTexCoordPointer(2, GL_FLOAT, 0, texCoords);
    glDrawArrays(GL_TRIANGLE_STRIP, 0, 4);

    glDisableClientState(GL_VERTEX_ARRAY);
    glDisableClientState(GL_NORMAL_ARRAY);
    glDisableClientState(GL_TEXTURE_COORD_ARRAY);
}

static void render_keyboard_texture()
{
    float keyb_start = 95 - (((48 * 100) / global_state.width) * 2.0) *
        global_state.content_scale;
    float keyb_start_y = (((48 * 100) / global_state.height) / 80.0) *
        global_state.content_scale;
    
    GLfloat square[] = { keyb_start / 100.0,  keyb_start_y, 0.0,
                         0.95, keyb_start_y, 0.0,
                         keyb_start / 100.0, -keyb_start_y, 0.0,
                         0.95, -keyb_start_y, 0.0};

    GLfloat texCoords[] = {0.0, 1.0,
                           1.0, 1.0,
                           0.0, 0.0,
                           1.0, 0.0};

    glEnableClientState(GL_VERTEX_ARRAY);
    glEnableClientState(GL_NORMAL_ARRAY);
    glEnableClientState(GL_TEXTURE_COORD_ARRAY);

    //glColor4f(0.0, 0.0, 0.0, 0.0);
    //glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

    glLoadIdentity();

    //glTranslatef(0.0, 0.0, 1.0);
    //glRotatef(80.0, 0.0, 0.0, 0.0);

    glBindTexture(GL_TEXTURE_2D, global_state.keyboard_texture[0]);
    //glBindTexture(GL_TEXTURE_2D, global_state.main_texture[0]);

    glColor4f(1.0, 1.0, 1.0, global_state.keyboard_opacity);
    glVertexPointer(3, GL_FLOAT, 0, &square[0]);
    glTexCoordPointer(2, GL_FLOAT, 0, texCoords);
    glDrawArrays(GL_TRIANGLE_STRIP, 0, 4);

    glDisableClientState(GL_VERTEX_ARRAY);
    glDisableClientState(GL_NORMAL_ARRAY);
    glDisableClientState(GL_TEXTURE_COORD_ARRAY);
}

static void create_keyboard_texture(GLuint *texture)
{
    unsigned char *bitmap;
    int width;
    int height;

    native_load_png(&bitmap, &width, &height);
    
    glGenTextures(1, texture);
    glBindTexture(GL_TEXTURE_2D, *texture);
    glTexParameteri(GL_TEXTURE_2D,GL_TEXTURE_MIN_FILTER,GL_LINEAR);
    glTexParameteri(GL_TEXTURE_2D,GL_TEXTURE_MAG_FILTER,GL_LINEAR);
    glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE);
    glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE);
    glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, width, height,
                 0, GL_RGBA, GL_UNSIGNED_BYTE, bitmap);
}

void engine_init_buffer(int width, int height)
{
    if (global_state.spice_display_buffer != NULL) {
        free(global_state.spice_display_buffer);
    }
    
    global_state.spice_display_buffer = malloc(width * height * 4);
}


void engine_init_screen()
{
    glEnable(GL_TEXTURE_2D);
    glEnable(GL_BLEND);
    glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
    //glBlendFunc(GL_ONE, GL_SRC_COLOR);
    
    create_keyboard_texture(&global_state.keyboard_texture[0]);
    global_state.main_opacity = 1.0;
    global_state.keyboard_opacity = 0.2;
    global_state.main_texture_created = 0;

    //glEnable(GL_TEXTURE_2D);
}

void engine_set_keyboard_opacity(float opacity)
{
    global_state.keyboard_opacity = opacity;
}

void engine_set_main_opacity(float opacity)
{
    global_state.main_opacity = opacity;
}

int engine_draw(int max_width, int max_height)
{
    int flags = 0;
    int width = max_width;
    int height = max_height;
    
    if (global_state.conn_state == AUTOCONNECT) {
        global_state.conn_state = DISCONNECTED;
        global_state.display_state = DISCONNECTED;
        native_connection_change(DISCONNECTED);
    }
    
    if (global_state.display_state != CONNECTED) {
        return engine_draw_disconnected(max_width, max_height);
    }
    
    flags = engine_spice_update_display(global_state.spice_display_buffer, &width, &height);
    
    if (flags < 0) {
        return flags;
    }
    
    if (width == 0 || height == 0)
        return 0;
    
    if (flags) {
        if (flags & DISPLAY_CHANGE_RESOLUTION || !global_state.main_texture_created) {
            create_main_texture(global_state.spice_display_buffer, width, height);
        } else {
            update_main_texture(global_state.spice_display_buffer, width, height);
            //create_main_texture(global_state.spice_display_buffer, width, height);
        }
        render_main_texture();
        
        render_keyboard_texture();
    }

    return 0;
}

int engine_draw_disconnected(int max_width, int max_height)
{
    float keyb_opacity;
    float main_opacity;
    
    keyb_opacity = global_state.keyboard_opacity;
    main_opacity = global_state.main_opacity;
    
    global_state.keyboard_opacity = 0.2;
    global_state.main_opacity = 0.5;
    render_main_texture();
    render_keyboard_texture();
    global_state.keyboard_opacity = keyb_opacity;
    global_state.main_opacity = main_opacity;
    
    return 0;
}
