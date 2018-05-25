LOCAL_PATH := $(call my-dir)
include $(CLEAR_VARS)

#opencv
OPENCVROOT:= D:\USB\DOAN\DOAN\opencv-3.4.0-android-sdk\OpenCV-android-sdk
OPENCV_CAMERA_MODULES:=on
OPENCV_INSTALL_MODULES:=on
OPENCV_LIB_TYPE:=SHARED
include ${OPENCVROOT}/sdk/native/jni/OpenCV.mk


LOCAL_SRC_FILES := com_example_leminhbang_camearamini_NativeClass.cpp
LOCAL_LDLIBS += -llog
LOCAL_MODULE := Mylib
include $(BUILD_SHARED_LIBRARY)