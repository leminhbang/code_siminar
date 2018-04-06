#include "com_example_leminhbang_camearamini_NativeClass.h"

JNIEXPORT jstring JNICALL Java_com_example_leminhbang_camearamini_NativeClass_getMessage
  (JNIEnv *env, jclass obj) {
    return env->NewStringUTF("This is the jni native class");
  }

JNIEXPORT jint JNICALL Java_com_example_leminhbang_camearamini_NativeClass_convertToGray
  (JNIEnv *env, jclass obj, jlong mSrc, jlong mDes) {
    Mat& mRGB = *(Mat *)mSrc;
     Mat& mGray = *(Mat *)mDes;
     int conv;
     jint reval;
     conv = RGBToGray(mRGB,mGray);
     reval = (jint) conv;
     return reval;
  }

JNIEXPORT jint JNICALL Java_com_example_leminhbang_camearamini_NativeClass_convertToNegative
  (JNIEnv *, jclass, jlong mSrc, jlong mDet) {
    Mat& mRGB = *(Mat *)mSrc;
    Mat& mNeg = *(Mat *)mDet;
    int conv;
    jint reval;
    conv = RGBToNegative(mRGB,mNeg);
    reval = (jint) conv;
    return reval;
  }

  int RGBToGray(Mat src, Mat& des) {
    cvtColor(src,des,CV_RGB2GRAY);
    if (src.rows == des.rows && src.cols == des.cols)
    return 1;
    return 0;
  }

  int RGBToNegative(Mat src, Mat& det) {
    det = 255 - src;
    if (det.rows == src.rows && det.cols == src.cols)
    return 1;
    return 0;
  }
