LOCAL_PATH := $(call my-dir)

include $(CLEAR_VARS)

LOCAL_MODULE := calculator
LOCAL_SRC_FILES := calculator.c

include $(BUILD_SHARED_LIBRARY)