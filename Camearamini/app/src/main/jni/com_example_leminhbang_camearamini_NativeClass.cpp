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

JNIEXPORT jint JNICALL Java_com_example_leminhbang_camearamini_NativeClass_segmentByColorKMeans
  (JNIEnv *, jclass, jlong IM, jint k) {
      Mat& mIM = *(Mat *) IM;
      int nColors = (int) k;
      int conv;
      jint reval;
      conv = getMat(mIM,k);
      reval = (jint) conv;
      return reval;
  }
  int getMat(Mat& img, int k) {
    //normalize(img, img, 0.0, 255.0, NORM_MINMAX);
    img = img / (k*1.0);
    img = img * 255.0;

    return 1;
  }
  int segment(Mat image) {
      cv::Mat reshaped_image = image.reshape(1, image.cols * image.rows);
      cv::Mat reshaped_image32f;
      reshaped_image.convertTo(reshaped_image32f, CV_32FC1, 1.0 / 255.0);

      cv::Mat labels;
      int cluster_number = 5;
      cv::TermCriteria criteria(cv::TermCriteria::COUNT, 100, 1);
      cv::Mat centers;
      cv::kmeans(reshaped_image32f, cluster_number, labels, criteria, 1, cv::KMEANS_PP_CENTERS, centers);
  }

  int SegmentByKMeans(Mat src, Mat& det, int nColors) {
      int nRows, nCols;
      cvtColor(src, det, CV_RGB2Lab);
      Mat lab[3];
      split(det, lab);
      Mat mA = lab[1];
      Mat mB = lab[2];
      nRows = src.rows;
      nCols = src.cols;
      mA.reshape(1, nRows * nCols);
      mB.reshape(1, nRows * nCols);
      Mat mAB;
      mAB.push_back(mA);
      mAB.push_back(mB);
      Mat centers, labels;
      kmeans(mAB, nColors, labels,
              TermCriteria(TermCriteria::EPS+TermCriteria::COUNT, 10, 1.0),
              3, KMEANS_PP_CENTERS, centers);
      return 1;
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
