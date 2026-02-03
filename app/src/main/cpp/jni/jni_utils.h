#ifndef FLYMOTION_JNI_UTILS_H
#define FLYMOTION_JNI_UTILS_H

#include <jni.h>
#include <opencv2/core.hpp>

void bitmapToMat(JNIEnv *env, jobject bitmap, cv::Mat &dst, jboolean needUnPremultiplyAlpha);
void matToBitmap(JNIEnv *env, cv::Mat &src, jobject bitmap, jboolean needPremultiplyAlpha);

#endif //FLYMOTION_JNI_UTILS_H
