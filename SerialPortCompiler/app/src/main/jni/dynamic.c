#include <termios.h>
#include <unistd.h>
#include <sys/types.h>
#include <sys/stat.h>
#include <fcntl.h>
#include <string.h>
#include <jni.h>
#include <pthread.h>
#include <stdio.h>
#include "android/log.h"

#define TAG "SecondActivity" // 这个是自定义的LOG的标识
#define LOGD(...) __android_log_print(ANDROID_LOG_DEBUG,TAG ,__VA_ARGS__)
#define LOGI(...) __android_log_print(ANDROID_LOG_INFO,TAG ,__VA_ARGS__)
#define LOGW(...) __android_log_print(ANDROID_LOG_WARN,TAG ,__VA_ARGS__)
#define LOGE(...) __android_log_print(ANDROID_LOG_ERROR,TAG ,__VA_ARGS__)
#define LOGF(...) __android_log_print(ANDROID_LOG_FATAL,TAG ,__VA_ARGS__)

static JavaVM *gVm;
static jmethodID methodId;
static jobject obj; // 实例对象
static jclass jcls;// class


void *threadreadTtyData(void *arg) {
    LOGE("create read data thred success.....threadreadTtyData.....");

    int index = 0;
    JNIEnv *env = NULL;
    if ((*gVm)->AttachCurrentThread(gVm, &env, NULL) == 0) {
        LOGE("--------------Attach ok=-=-=-=-=-=-=-=-=-=-=-=-=-");
        jclass cls = (*env)->GetObjectClass(env, obj); //这里获取的是class， GetObjectClass=通过对象获取class
        while (index < 10) {
            (*env)->CallVoidMethod(env, obj, methodId, 2222); //只能传递 obj
            index++;
            sleep(3);
        }
//        (*env)->CallVoidMethod(env, cls, methodId, 2222);

        (*gVm)->DetachCurrentThread(gVm);

    } else {
        LOGE("---------Attach error-----------");
    }
}
/**
 * 线程间不能直接传递JNIEnv和jobject这类线程专属属性值
 * 每个进程只有一个JavaVM，而这个JavaVM可以被多线程共享，但是JNIEnv和jobject是属于线程私有的，不能共享
 * > 线程间传递参数
 * > 参数的全局引用
 * @param env
 * @param clazz
 * @return
 */
//返回一个字符串
JNIEXPORT jstring JNICALL native_hello(JNIEnv *env, jclass clazz) {
    // (*env)->CallVoidMethod(env, clazz, methodId,11);
    pthread_t id;
    int ret;
    (*env)->CallVoidMethod(env, clazz, methodId, 11133);
    obj = (*env)->NewGlobalRef(env, clazz);
//    obj = clazz;  这是错误的， 一旦这个方法返回，clazz就会被回收，导致非法调用
    ret = pthread_create(&id, NULL, threadreadTtyData, NULL);
    if (ret != 0) {
        LOGE("create receiver thread failure ");
    } else {
        LOGE("create read data thred success");
    }

    return (*env)->NewStringUTF(env, "helloJNI");
}
//求两个int的值
JNIEXPORT jint JNICALL native_add(JNIEnv *env, jclass clazz, jint a, jint b) { // 这个class才是实例对象
    return a + b;
}


static JNINativeMethod method_table[] = {
        {"native_hello", "()Ljava/lang/String;", (void *) native_hello},
        {"native_add",   "(II)I",                (void *) native_add}
};


JNIEXPORT jint JNI_OnLoad(JavaVM *vm, void *reserved) {
    //OnLoad方法是没有JNIEnv参数的，需要通过vm获取。
    gVm = vm;
    JNIEnv *env = NULL;

    if ((*vm)->AttachCurrentThread(vm, &env, NULL) == JNI_OK) {
        //获取对应声明native方法的Java类 com.uurobot.serialportcompiler
        jclass clazz = (*env)->FindClass(env,
                                         "com/uurobot/serialportcompiler/DynamicReg"); //这里获取的是class，而不是实例对象，通过类的全名
        methodId = (*env)->GetMethodID(env, clazz, "onAudio", "(I)V");

        if (methodId == 0) {
            LOGE("find method3 error");
            return 0;
        }

        LOGE("find method3........................."); //这里不能 回调方法

        if (clazz == NULL) {
            return JNI_FALSE;
        }
        //注册方法，成功返回正确的JNIVERSION。
        if ((*env)->RegisterNatives(env, clazz, method_table,
                                    sizeof(method_table) / sizeof(method_table[0])) == JNI_OK) {
            return JNI_VERSION_1_4;
        }

    }
    return JNI_FALSE;
}

