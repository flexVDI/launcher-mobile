LOCAL_PRELINK_MODULE := false
LOCAL_PATH := $(call my-dir)

LOCAL_MODULE := spice-mono-glue
LOCAL_SRC_FILES := deps/$(TARGET_ARCH_ABI)/libspice-mono-glue.a
include $(PREBUILT_STATIC_LIBRARY)
include $(CLEAR_VARS)

LOCAL_MODULE := glib-2.0
LOCAL_SRC_FILES := deps/$(TARGET_ARCH_ABI)/libglib-2.0.a
include $(PREBUILT_STATIC_LIBRARY)
include $(CLEAR_VARS)

LOCAL_MODULE := spice-client-glib-2.0
LOCAL_SRC_FILES := deps/$(TARGET_ARCH_ABI)/libspice-client-glib-2.0.a
include $(PREBUILT_STATIC_LIBRARY)
include $(CLEAR_VARS)

LOCAL_MODULE := gobject-2.0
LOCAL_SRC_FILES := deps/$(TARGET_ARCH_ABI)/libgobject-2.0.a
include $(PREBUILT_STATIC_LIBRARY)
include $(CLEAR_VARS)

LOCAL_MODULE := gio-2.0
LOCAL_SRC_FILES := deps/$(TARGET_ARCH_ABI)/libgio-2.0.a
include $(PREBUILT_STATIC_LIBRARY)
include $(CLEAR_VARS)

LOCAL_MODULE := gmodule-2.0
LOCAL_SRC_FILES := deps/$(TARGET_ARCH_ABI)/libgmodule-2.0.a
include $(PREBUILT_STATIC_LIBRARY)
include $(CLEAR_VARS)

LOCAL_MODULE := ffi
LOCAL_SRC_FILES := deps/$(TARGET_ARCH_ABI)/libffi.a
include $(PREBUILT_STATIC_LIBRARY)
include $(CLEAR_VARS)

LOCAL_MODULE := intl
LOCAL_SRC_FILES := deps/$(TARGET_ARCH_ABI)/libintl.a
include $(PREBUILT_STATIC_LIBRARY)
include $(CLEAR_VARS)

LOCAL_MODULE := iconv
LOCAL_SRC_FILES := deps/$(TARGET_ARCH_ABI)/libiconv.a
include $(PREBUILT_STATIC_LIBRARY)
include $(CLEAR_VARS)

LOCAL_MODULE := pixman-1
LOCAL_SRC_FILES := deps/$(TARGET_ARCH_ABI)/libpixman-1.a
include $(PREBUILT_STATIC_LIBRARY)
include $(CLEAR_VARS)

LOCAL_MODULE := nopoll
LOCAL_SRC_FILES := deps/$(TARGET_ARCH_ABI)/libnopoll.a
include $(PREBUILT_STATIC_LIBRARY)
include $(CLEAR_VARS)

LOCAL_MODULE := ssl
LOCAL_SRC_FILES := deps/$(TARGET_ARCH_ABI)/libssl.a
include $(PREBUILT_STATIC_LIBRARY)
include $(CLEAR_VARS)

LOCAL_MODULE := crypto
LOCAL_SRC_FILES := deps/$(TARGET_ARCH_ABI)/libcrypto.a
include $(PREBUILT_STATIC_LIBRARY)
include $(CLEAR_VARS)

LOCAL_MODULE := jpeg
LOCAL_SRC_FILES := deps/$(TARGET_ARCH_ABI)/libjpeg.a
include $(PREBUILT_STATIC_LIBRARY)
include $(CLEAR_VARS)

# Edit this line
GSTREAMER_ROOT_ANDROID := /Users/slopez/gstreamer/$(TARGET_ARCH_ABI)/

ifndef GSTREAMER_ROOT
ifndef GSTREAMER_ROOT_ANDROID
$(error GSTREAMER_ROOT_ANDROID is not defined!)
endif
GSTREAMER_ROOT        := $(GSTREAMER_ROOT_ANDROID)
endif
GSTREAMER_NDK_BUILD_PATH  := $(GSTREAMER_ROOT)/share/gst-android/ndk-build


include $(GSTREAMER_NDK_BUILD_PATH)/plugins.mk
GSTREAMER_PLUGINS         := $(GSTREAMER_PLUGINS_CORE) $(GSTREAMER_PLUGINS_PLAYBACK) $(GSTREAMER_PLUGINS_CODECS) $(GSTREAMER_PLUGINS_SYS) $(GSTREAMER_PLUGINS_CODECS_RESTRICTED)
G_IO_MODULES              := gnutls

include $(GSTREAMER_NDK_BUILD_PATH)/gstreamer-1.0.mk

#LOCAL_MODULE := gstapp-1.0
#LOCAL_SRC_FILES := deps/$(TARGET_ARCH_ABI)/libgstapp-1.0.a
#include $(PREBUILT_STATIC_LIBRARY)
#include $(CLEAR_VARS)

#LOCAL_MODULE := gstbase-1.0
#LOCAL_SRC_FILES := deps/$(TARGET_ARCH_ABI)/libgstbase-1.0.a
#include $(PREBUILT_STATIC_LIBRARY)
#include $(CLEAR_VARS)

#LOCAL_MODULE := gstaudio-1.0
#LOCAL_SRC_FILES := deps/$(TARGET_ARCH_ABI)/libgstaudio-1.0.a
#include $(PREBUILT_STATIC_LIBRARY)
#include $(CLEAR_VARS)

#LOCAL_MODULE := gstreamer-1.0
#LOCAL_SRC_FILES := deps/$(TARGET_ARCH_ABI)/libgstreamer-1.0.a
#include $(PREBUILT_STATIC_LIBRARY)
#include $(CLEAR_VARS)

LOCAL_MODULE := flexdp-jni
LOCAL_MODULE_FILENAME := libflexdp-jni
LOCAL_C_INCLUDES := \
$(LOCAL_PATH)/../../..

LOCAL_LDFLAGS += -L/Users/slopez/cerbero/dist/android_armv7/lib
LOCAL_LDLIBS := -llog -lz -lGLESv1_CM
LOCAL_SHARED_LIBRARIES := gstreamer_android
#LOCAL_STATIC_LIBRARIES := spice-mono-glue spice-client-glib-2.0 gio-2.0 gmodule-2.0 gobject-2.0 ffi pixman-1 jpeg nopoll ssl crypto gstapp-1.0 gstbase-1.0 gstaudio-1.0 gstreamer-1.0 glib-2.0 intl iconv
LOCAL_STATIC_LIBRARIES := spice-mono-glue spice-client-glib-2.0 gio-2.0 gmodule-2.0 gobject-2.0 ffi pixman-1 jpeg nopoll ssl crypto glib-2.0 intl iconv

LOCAL_SRC_FILES := \
../../../draw.c \
../../../io_interface.c \
../../../spice.c \
./flexdp-jni.c

include $(BUILD_SHARED_LIBRARY)

