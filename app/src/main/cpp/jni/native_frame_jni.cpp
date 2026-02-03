#include <jni.h>
#include <opencv2/imgproc.hpp>
#include "jni_utils.h"

extern "C" {

JNIEXPORT void JNICALL
Java_com_fly_motion_nativebridge_NativeFrameProcessor_convertToGrayscale(JNIEnv *env, jobject thiz, jobject srcBitmap, jobject dstBitmap) {
    cv::Mat src, dst;
    bitmapToMat(env, srcBitmap, src, false);

    cv::cvtColor(src, dst, cv::COLOR_RGBA2GRAY);
    // Convert back to RGBA for bitmap
    cv::cvtColor(dst, dst, cv::COLOR_GRAY2RGBA);

    matToBitmap(env, dst, dstBitmap, false);
}

JNIEXPORT void JNICALL
Java_com_fly_motion_nativebridge_NativeFrameProcessor_resize(JNIEnv *env, jobject thiz, jobject srcBitmap, jobject dstBitmap, jint width, jint height) {
    cv::Mat src, dst;
    bitmapToMat(env, srcBitmap, src, false);

    cv::resize(src, dst, cv::Size(width, height));

    matToBitmap(env, dst, dstBitmap, false);
}

}
