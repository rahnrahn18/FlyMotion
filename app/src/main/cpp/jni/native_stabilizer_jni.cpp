#include <jni.h>
#include "../core/stabilization_pipeline.h"
#include "jni_utils.h"

extern "C" {

JNIEXPORT jlong JNICALL
Java_com_fly_motion_nativebridge_NativeStabilizer_create(JNIEnv *env, jobject thiz) {
    auto *pipeline = new flymotion::StabilizationPipeline();
    pipeline->init();
    return reinterpret_cast<jlong>(pipeline);
}

JNIEXPORT void JNICALL
Java_com_fly_motion_nativebridge_NativeStabilizer_pushFrame(JNIEnv *env, jobject thiz, jlong handle, jobject bitmap) {
    auto *pipeline = reinterpret_cast<flymotion::StabilizationPipeline *>(handle);
    if (!pipeline) return;

    cv::Mat frame;
    bitmapToMat(env, bitmap, frame, false);
    pipeline->pushFrameForAnalysis(frame);
}

JNIEXPORT void JNICALL
Java_com_fly_motion_nativebridge_NativeStabilizer_finalizeAnalysis(JNIEnv *env, jobject thiz, jlong handle, jint radius) {
    auto *pipeline = reinterpret_cast<flymotion::StabilizationPipeline *>(handle);
    if (!pipeline) return;

    pipeline->finalizeAnalysis(radius);
}

JNIEXPORT void JNICALL
Java_com_fly_motion_nativebridge_NativeStabilizer_stabilizeFrame(JNIEnv *env, jobject thiz, jlong handle, jobject srcBitmap, jobject dstBitmap, jint index) {
    auto *pipeline = reinterpret_cast<flymotion::StabilizationPipeline *>(handle);
    if (!pipeline) return;

    cv::Mat src;
    bitmapToMat(env, srcBitmap, src, false);

    cv::Mat dst = pipeline->getStabilizedFrame(src, index);

    matToBitmap(env, dst, dstBitmap, false);
}

JNIEXPORT void JNICALL
Java_com_fly_motion_nativebridge_NativeStabilizer_release(JNIEnv *env, jobject thiz, jlong handle) {
    auto *pipeline = reinterpret_cast<flymotion::StabilizationPipeline *>(handle);
    if (pipeline) {
        delete pipeline;
    }
}

JNIEXPORT jint JNICALL
Java_com_fly_motion_nativebridge_NativeStabilizer_getFrameCount(JNIEnv *env, jobject thiz, jlong handle) {
    auto *pipeline = reinterpret_cast<flymotion::StabilizationPipeline *>(handle);
    if (!pipeline) return 0;
    return pipeline->getFrameCount();
}

}
