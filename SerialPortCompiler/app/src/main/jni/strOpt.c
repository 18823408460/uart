#include <termios.h>
#include <unistd.h>
#include <sys/types.h>
#include <sys/stat.h>
#include <fcntl.h>
#include <string.h>
#include <jni.h>
#include <pthread.h>
#include <stdio.h>
#include <malloc.h>
#include "android/log.h"
#include <sys/ipc.h>
#include <sys/types.h>
#include <stdio.h>
#include <stdlib.h>
#include <sys/ipc.h>
#include <sys/msg.h>
#include <sys/types.h>
#include <string.h>
#include <time.h>
#include <unistd.h>
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
void test1();

JNIEXPORT void test1() {
    char *src = "hello";
    char *src1 = "world";
    LOGE("-----------len = %d", sizeof(src)); //这不能计算内容长度
    int len = strlen(src);
    int len1 = strlen(src1);
    LOGE("-----------len = %d", len);
    char buf[14] = {0};
    // 方式一：
    strcat(buf, src);
    strcat(buf, src1); //这样会一直往后面追加内容

    // 方式二：
    //memcpy(buf, src, len);
    //memcpy(buf, src1, len1); //这会覆盖上面的内容
    int i = 0;
    for (i = 0; i < 10; ++i) {
        LOGE("--------data  = %c", buf[i]);
    }
}

JNIEXPORT void *threadSend(void *arg) {
    LOGE("--------threadSend ----------- ");
    key_t key = 100;
}

JNIEXPORT void *threadRev(void *arg) {
    LOGE("--------threadRev -----------");
}

JNIEXPORT void test2() {
    pthread_t id1;
    pthread_t id2;

    pthread_create(&id1, NULL, threadSend, NULL);
    pthread_create(&id2, NULL, threadRev, NULL);
}

jstring JNICALL native_hello(JNIEnv *env, jclass clazz) {
    // (*env)->CallVoidMethod(env, clazz, methodId,11);
    LOGE("-------------------------hello----------------");
    test2();

    return (*env)->NewStringUTF(env, "helloJNI");
}


static JNINativeMethod method_table[] = {
        {"native_hello", "()Ljava/lang/String;", (void *) native_hello},
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

