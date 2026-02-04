#include <jni.h>
#include <string>

extern "C" JNIEXPORT jstring JNICALL
Java_com_fly_motion_activity_FlymoActivity_stringFromJNI(
        JNIEnv* env,
        jobject /* this */) {
    std::string hello = "FlyMotion Native Ready";
    return env->NewStringUTF(hello.c_str());
}
