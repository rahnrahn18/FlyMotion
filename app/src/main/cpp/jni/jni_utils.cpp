#include "jni_utils.h"
#include <android/bitmap.h>
#include <opencv2/imgproc.hpp>

void bitmapToMat(JNIEnv *env, jobject bitmap, cv::Mat &dst, jboolean needUnPremultiplyAlpha) {
    AndroidBitmapInfo info;
    void *pixels = 0;

    if (AndroidBitmap_getInfo(env, bitmap, &info) < 0) return;
    if (AndroidBitmap_lockPixels(env, bitmap, &pixels) < 0) return;

    if (info.format == ANDROID_BITMAP_FORMAT_RGBA_8888) {
        cv::Mat tmp(info.height, info.width, CV_8UC4, pixels);
        if (needUnPremultiplyAlpha) {
            cvtColor(tmp, dst, cv::COLOR_mRGBA2RGBA);
        } else {
            tmp.copyTo(dst);
        }
    } else if (info.format == ANDROID_BITMAP_FORMAT_RGB_565) {
        cv::Mat tmp(info.height, info.width, CV_8UC2, pixels);
        cvtColor(tmp, dst, cv::COLOR_BGR5652RGBA);
    }

    AndroidBitmap_unlockPixels(env, bitmap);
}

void matToBitmap(JNIEnv *env, cv::Mat &src, jobject bitmap, jboolean needPremultiplyAlpha) {
    AndroidBitmapInfo info;
    void *pixels = 0;

    if (AndroidBitmap_getInfo(env, bitmap, &info) < 0) return;
    if (AndroidBitmap_lockPixels(env, bitmap, &pixels) < 0) return;

    if (info.format == ANDROID_BITMAP_FORMAT_RGBA_8888) {
        cv::Mat tmp(info.height, info.width, CV_8UC4, pixels);
        if (src.type() == CV_8UC4) {
            if (needPremultiplyAlpha) {
                cvtColor(src, tmp, cv::COLOR_RGBA2mRGBA);
            } else {
                src.copyTo(tmp);
            }
        } else {
            cvtColor(src, tmp, cv::COLOR_BGR2RGBA);
        }
    } else if (info.format == ANDROID_BITMAP_FORMAT_RGB_565) {
        cv::Mat tmp(info.height, info.width, CV_8UC2, pixels);
        if (src.type() == CV_8UC4) {
            cvtColor(src, tmp, cv::COLOR_RGBA2BGR565);
        } else {
            cvtColor(src, tmp, cv::COLOR_BGR2BGR565);
        }
    }

    AndroidBitmap_unlockPixels(env, bitmap);
}
