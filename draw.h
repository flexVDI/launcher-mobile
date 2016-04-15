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
