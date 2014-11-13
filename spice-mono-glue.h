#include <stdlib.h>

void SpiceGlibGlue_InitializeLogging(int32_t verbosityLevel);

int16_t SpiceGlibGlue_Init(void);

int16_t SpiceGlibGlue_Connect(char* h, char* p,
                              char* tp, char* ws,
                              char* pw, char* cf,
                              char* cs, int32_t sound);

void SpiceGlibGlue_Disconnect(void);

int16_t SpiceGlibGlue_isConnected(void);

int SpiceGlibRecalcGeometry(int x, int y, int w, int h);

int SpiceGlibGlueUpdateDisplayData(char *, int *, int *);

int16_t SpiceGlibGlueMotionEvent(int pos_x, int pos_y, int16_t button_mask);

int16_t SpiceGlibGlueButtonEvent(int pos_x, int pos_y,
                                 int16_t button, int16_t button_mask, int16_t down);

void SpiceGlibGlue_SpiceKeyEvent(int down, int keycode);
