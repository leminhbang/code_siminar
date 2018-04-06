/* DO NOT EDIT THIS FILE - it is machine generated */
#include <jni.h>
#include <stdio.h>
#include <opencv2/opencv.hpp>
#include <opencv2/core/core.hpp>
#include <opencv2/highgui/highgui.hpp>
#include <iostream>

using namespace std;
using namespace cv;
/* Header for class com_example_leminhbang_camearamini_NativeClass */

#ifndef _Included_com_example_leminhbang_camearamini_NativeClass
#define _Included_com_example_leminhbang_camearamini_NativeClass
#ifdef __cplusplus
extern "C" {
#endif
/*
 * Class:     com_example_leminhbang_camearamini_NativeClass
 * Method:    getMessage
 * Signature: ()Ljava/lang/String;
 */
 int RGBToGray(Mat src, Mat& det);
 int RGBToNegative(Mat src, Mat& det);

JNIEXPORT jstring JNICALL Java_com_example_leminhbang_camearamini_NativeClass_getMessage
  (JNIEnv *, jclass);

JNIEXPORT jint JNICALL Java_com_example_leminhbang_camearamini_NativeClass_convertToGray
  (JNIEnv *, jclass, jlong, jlong);

JNIEXPORT jint JNICALL Java_com_example_leminhbang_camearamini_NativeClass_convertToNegative
  (JNIEnv *, jclass, jlong, jlong);

#ifdef __cplusplus
}
#endif
#endif
