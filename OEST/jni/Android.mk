LOCAL_PATH := $(call my-dir)
include $(CLEAR_VARS)
LOCAL_MODULE    := oest
LOCAL_SRC_FILES := oest.c vector.cpp complex.cpp fft.cpp ocean.cpp
include $(BUILD_SHARED_LIBRARY)