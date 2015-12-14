#include <jni.h>
#include <stdio.h>
#include <globals.h>
#include <spice.h> 
#include <io_interface.h>
#include <native.h>
#include <android/log.h>

#define  LOG_TAG    "flexdp-jni"

#define  LOGD(...)  __android_log_print(ANDROID_LOG_DEBUG, LOG_TAG, __VA_ARGS__)
#define  LOGE(...)  __android_log_print(ANDROID_LOG_ERROR, LOG_TAG, __VA_ARGS__)

JavaVM *jvm = 0;
jclass mainActivity_class = 0;
jmethodID mainActivity_connectionChange = 0;
jclass flexJNI_class = 0;
jmethodID flexJNI_loadPNG = 0;
jmethodID flexJNI_getPNGWidth = 0;
jmethodID flexJNI_getPNGHeight = 0;
jmethodID flexJNI_showKeyboard = 0;
double mouse_scale = 1.0;


JNIEXPORT void JNICALL Java_com_flexvdi_androidlauncher_flexJNI_setConnectionData(JNIEnv *env, jobject thisObj,
    jstring jniHost, jstring jniPort, jstring jniWsport, jstring jniPassword, jint jniEnableSound)
{

    const char *host = (*env)->GetStringUTFChars(env, jniHost, NULL);
    const char *port = (*env)->GetStringUTFChars(env, jniPort, NULL);
    const char *wsport = (*env)->GetStringUTFChars(env, jniWsport, NULL);
    const char *password = (*env)->GetStringUTFChars(env, jniPassword, NULL);
    int enableSound = 0;

    if (jniEnableSound != 0) {
        enableSound = 1;
    }

    LOGE("setConnectionData: %s, %s, %s, %s", host, port, wsport, password);

    engine_spice_set_connection_data(host, port, wsport, password, enableSound);
}

JNIEXPORT void JNICALL Java_com_flexvdi_androidlauncher_flexJNI_connect(JNIEnv *env, jobject thisObj)
{
    LOGE("connect");

    engine_spice_connect();
}

JNIEXPORT void JNICALL Java_com_flexvdi_androidlauncher_flexJNI_disconnect(JNIEnv *env, jobject thisObj)
{
    LOGE("disconnect");

    engine_spice_disconnect();
}

JNIEXPORT jint JNICALL Java_com_flexvdi_androidlauncher_flexJNI_isConnected(JNIEnv *env, jobject thisObj)
{
    //LOGE("isConnected");

    return engine_spice_is_connected();
}

JNIEXPORT void JNICALL Java_com_flexvdi_androidlauncher_flexJNI_requestResolution(JNIEnv *env, jobject thisObj,
    jint width, jint height)
{
    LOGE("requestResolution");

    engine_spice_request_resolution(width, height);
}

JNIEXPORT void JNICALL Java_com_flexvdi_androidlauncher_flexJNI_resolutionChanged(JNIEnv *env, jobject thisObj)
{
    LOGE("resolutionChanged");

    engine_spice_resolution_changed();
}

JNIEXPORT void JNICALL Java_com_flexvdi_androidlauncher_flexJNI_initBuffer(JNIEnv *env, jobject thisObj,
    jint jniWidth, jint jniHeight)
{
    LOGE("initBuffer");

    global_state.width = jniWidth;
    global_state.height = jniHeight;

    engine_init_buffer(jniWidth, jniHeight);
}

JNIEXPORT void JNICALL Java_com_flexvdi_androidlauncher_flexJNI_initScreen(JNIEnv *env, jobject thisObj,
    jdouble mouseScale)
{
    LOGE("initScreen");

    mouse_scale = mouseScale;
    global_state.zoom = 0.0;
    global_state.zoom_offset_x = 0;
    global_state.zoom_offset_y = 0;
    global_state.keyboard_offset = 0;
    global_state.keyboard_opacity = 0.2;
    global_state.content_scale = 1;
    engine_init_screen();
}

JNIEXPORT jint JNICALL Java_com_flexvdi_androidlauncher_flexJNI_getGuestWidth(JNIEnv *env, jobject thisObj)
{
    return global_state.guest_width;
}

JNIEXPORT jint JNICALL Java_com_flexvdi_androidlauncher_flexJNI_getGuestHeight(JNIEnv *env, jobject thisObj)
{
    return global_state.guest_height;
}

JNIEXPORT jint JNICALL Java_com_flexvdi_androidlauncher_flexJNI_draw(JNIEnv *env, jobject thisObj,
    jint jniWidth, jint jniHeight)
{
    //LOGE("draw");

    return engine_draw(jniWidth, jniHeight);
}

JNIEXPORT jint JNICALL Java_com_flexvdi_androidlauncher_flexJNI_drawDisconnected(JNIEnv *env, jobject thisObj,
    jint jniWidth, jint jniHeight)
{
    //LOGE("drawDisconnected");

    return engine_draw_disconnected(jniWidth, jniHeight);
}

JNIEXPORT jint JNICALL Java_com_flexvdi_androidlauncher_flexJNI_setZoomFactor(JNIEnv *env, jobject thisObj,
    jfloat zoom)
{
    LOGE("setZoom: %f\n", zoom);

    global_state.zoom = zoom;
}

JNIEXPORT jint JNICALL Java_com_flexvdi_androidlauncher_flexJNI_setZoomOffset(JNIEnv *env, jobject thisObj,
    jfloat zoomOffsetX, jfloat zoomOffsetY)
{
    LOGE("setZoom: %f, %f\n", zoomOffsetX, zoomOffsetY);

    global_state.zoom_offset_x = zoomOffsetX;
    global_state.zoom_offset_y = zoomOffsetY;
}

JNIEXPORT void JNICALL Java_com_flexvdi_androidlauncher_flexJNI_sendMouseClick(JNIEnv *env, jobject thisObj,
    jint x, jint y, jint button, jint numClicks)
{
    LOGE("sendMouseClick: %dx%d, %d, %d, %f\n", x, y, button, numClicks, mouse_scale);

    int i;
    io_event_t io_event;

    for (i = 0; i < numClicks; ++i) {
        io_event.type = IO_EVENT_MOVED;
        io_event.position[0] = x * mouse_scale;
        io_event.position[1] = y * mouse_scale;
        io_event.button = button;
        IO_PushEvent(&io_event);

        io_event.type = IO_EVENT_BEGAN;
        io_event.position[0] = x * mouse_scale;
        io_event.position[1] = y * mouse_scale;
        io_event.button = button;
        IO_PushEvent(&io_event);

        io_event.type = IO_EVENT_ENDED;
        io_event.position[0] = x * mouse_scale;
        io_event.position[1] = y * mouse_scale;
        io_event.button = button;
        IO_PushEvent(&io_event);
    }
}

JNIEXPORT void JNICALL Java_com_flexvdi_androidlauncher_flexJNI_sendMouseEventBegan(JNIEnv *env, jobject thisObj,
    jint x, jint y, jint button)
{
    LOGE("sendMouseEventBegan: %dx%d, %d\n", x, y, button);

    io_event_t io_event;

    io_event.type = IO_EVENT_BEGAN;
    io_event.position[0] = x * mouse_scale;
    io_event.position[1] = y * mouse_scale;
    io_event.button = button;
    IO_PushEvent(&io_event);
}

JNIEXPORT void JNICALL Java_com_flexvdi_androidlauncher_flexJNI_sendMouseEventMoved(JNIEnv *env, jobject thisObj,
    jint x, jint y, jint button)
{
    LOGE("sendMouseEventMoved: %dx%d, %d\n", x, y, button);

    int i;
    io_event_t io_event;

    io_event.type = IO_EVENT_MOVED;
    io_event.position[0] = x * mouse_scale;
    io_event.position[1] = y * mouse_scale;
    io_event.button = button;
    IO_PushEvent(&io_event);
}

JNIEXPORT void JNICALL Java_com_flexvdi_androidlauncher_flexJNI_sendMouseEventEnded(JNIEnv *env, jobject thisObj,
    jint x, jint y, jint button)
{
    LOGE("sendMouseEventEnded: %dx%d, %d\n", x, y, button);

    int i;
    io_event_t io_event;

    io_event.type = IO_EVENT_ENDED;
    io_event.position[0] = x * mouse_scale;
    io_event.position[1] = y * mouse_scale;
    io_event.button = button;
    IO_PushEvent(&io_event);
}

JNIEXPORT void JNICALL Java_com_flexvdi_androidlauncher_flexJNI_sendKeyEvent(JNIEnv *env, jobject thisObj,
    jint charCode, jint event)
{
    LOGE("sendKeyPress: %d %d\n", charCode, event);

    engine_spice_keyboard_event(charCode, event);
}

JNIEXPORT void JNICALL Java_com_flexvdi_androidlauncher_flexJNI_setKeyboardOpacity(JNIEnv *env, jobject thisObj,
    jdouble opacity)
{
    global_state.keyboard_opacity = opacity;
}

JNIEXPORT void JNICALL Java_com_flexvdi_androidlauncher_flexJNI_setKeyboardOffset(JNIEnv *env, jobject thisObj,
    jdouble offset)
{
    global_state.keyboard_offset = offset;
}

jint JNI_OnLoad(JavaVM *vm, void *reserved)
{
    JNIEnv *env = 0;
    jvm = vm;

    if ((*jvm)->GetEnv(jvm, (void **) &env, JNI_VERSION_1_6) != JNI_OK) {
        LOGE("can't get JavaVM env");
        exit(1);
    }

    mainActivity_class = (jclass) (*env)->NewGlobalRef(env, (*env)->FindClass(env, "com/flexvdi/androidlauncher/MainActivity"));
    if (mainActivity_class == 0) {
        LOGE("can't find class MainActivity");
        exit(1);
    }

    mainActivity_connectionChange = (*env)->GetStaticMethodID(env, mainActivity_class, "nativeConnectionChange", "(I)V");
    if (mainActivity_connectionChange == 0) {
        LOGE("can't find method connectionChange");
        exit(1);
    }

    flexJNI_class = (jclass) (*env)->NewGlobalRef(env, (*env)->FindClass(env, "com/flexvdi/androidlauncher/flexJNI"));
    if (flexJNI_class == 0) {
        LOGE("can't find class flexJNI");
        exit(1);
    }

    flexJNI_loadPNG = (*env)->GetStaticMethodID(env, flexJNI_class, "loadPNG", "()Ljava/nio/ByteBuffer;");
    if (flexJNI_loadPNG == 0) {
        LOGE("can't find method loadPNG");
        exit(1);
    }

    flexJNI_getPNGWidth = (*env)->GetStaticMethodID(env, flexJNI_class, "getPNGWidth", "()I");
    if (flexJNI_getPNGWidth == 0) {
        LOGE("can't find method getPNGWidth");
        exit(1);
    }

    flexJNI_getPNGHeight = (*env)->GetStaticMethodID(env, flexJNI_class, "getPNGHeight", "()I");
    if (flexJNI_getPNGHeight == 0) {
        LOGE("can't find method getPNGHeight");
        exit(1);
    }

    flexJNI_showKeyboard = (*env)->GetStaticMethodID(env, flexJNI_class, "showKeyboard", "()V");
    if (flexJNI_showKeyboard == 0) {
        LOGE("can't find method showKeyboard");
        exit(1);
    }

    return JNI_VERSION_1_6;
}

void native_load_png(unsigned char **imgbuf, int *width, int *height)
{
    LOGE("native_load_png");

    JNIEnv *env = 0;
    if ((*jvm)->GetEnv(jvm, (void **) &env, JNI_VERSION_1_6) != JNI_OK) {
        LOGE("can't get JavaVM env");
        exit(1);
    }

    jobject directBuffer = (*env)->CallStaticObjectMethod(env, flexJNI_class, flexJNI_loadPNG);
    char *jniBuf = (char*)(*env)->GetDirectBufferAddress(env, directBuffer);
    int jniWidth = (int)(*env)->CallStaticIntMethod(env, flexJNI_class, flexJNI_getPNGWidth);
    int jniHeight = (int)(*env)->CallStaticIntMethod(env, flexJNI_class, flexJNI_getPNGHeight);

    int length = jniWidth * jniHeight * 4;
    int stride = jniWidth * 4;
    char *buf = (char *) malloc(jniWidth * jniHeight * 4);
    buf += (jniWidth - 1) * stride;

    int i;
    for (i = 0; i < jniHeight; i++) {
        memcpy(buf, jniBuf, stride);

        buf -= stride;
        jniBuf += stride;
    }

    *imgbuf = buf;
    *width = jniWidth;
    *height = jniHeight;

    LOGE("native_load_png: imgbuf=0x%x buf=0x%x width=%d height=%d", *imgbuf, buf, *width, *height);
}

void native_show_keyboard()
{
    LOGE("native_show_keyboard");

    JNIEnv *env = 0;
    if ((*jvm)->GetEnv(jvm, (void **) &env, JNI_VERSION_1_6) != JNI_OK) {
        LOGE("can't get JavaVM env");
        exit(1);
    }

    (*env)->CallStaticVoidMethod(env, flexJNI_class, flexJNI_showKeyboard);
}

void native_show_menu()
{
}

void native_connection_change(int state)
{
    LOGE("native_connection_change");

    JNIEnv *env = 0;
    if ((*jvm)->GetEnv(jvm, (void **) &env, JNI_VERSION_1_6) != JNI_OK) {
        LOGE("can't get JavaVM env");
        exit(1);
    }

    (*env)->CallStaticVoidMethod(env, mainActivity_class, mainActivity_connectionChange, state);
}

void native_resolution_change(int changing)
{
}


