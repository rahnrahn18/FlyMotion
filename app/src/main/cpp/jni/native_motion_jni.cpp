#include <jni.h>
#include "../motion/motion_estimator.h"
#include "jni_utils.h"

extern "C" {

JNIEXPORT jlong JNICALL
Java_com_fly_motion_nativebridge_NativeMotionEstimator_create(JNIEnv *env, jobject thiz) {
    return reinterpret_cast<jlong>(new flymotion::MotionEstimator());
}

JNIEXPORT void JNICALL
Java_com_fly_motion_nativebridge_NativeMotionEstimator_estimate(JNIEnv *env, jobject thiz, jlong handle, jobject prevBitmap, jobject currBitmap, jdoubleArray outTransform) {
    auto *estimator = reinterpret_cast<flymotion::MotionEstimator *>(handle);
    if (!estimator) return;

    cv::Mat prev, curr;
    bitmapToMat(env, prevBitmap, prev, false);
    bitmapToMat(env, currBitmap, curr, false);

    cv::Mat transform = estimator->estimateMotion(prev, curr);

    // Copy transform to double array (2x3 = 6 elements)
    if (transform.rows == 2 && transform.cols == 3) {
        jdouble *outData = env->GetDoubleArrayElements(outTransform, NULL);
        if (outData) {
            outData[0] = transform.at<double>(0, 0);
            outData[1] = transform.at<double>(0, 1);
            outData[2] = transform.at<double>(0, 2);
            outData[3] = transform.at<double>(1, 0);
            outData[4] = transform.at<double>(1, 1);
            outData[5] = transform.at<double>(1, 2);
            env->ReleaseDoubleArrayElements(outTransform, outData, 0);
        }
    }
}

JNIEXPORT void JNICALL
Java_com_fly_motion_nativebridge_NativeMotionEstimator_release(JNIEnv *env, jobject thiz, jlong handle) {
    auto *estimator = reinterpret_cast<flymotion::MotionEstimator *>(handle);
    if (estimator) delete estimator;
}

}
