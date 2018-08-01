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


void *handler(void *arg) {
/*    int i = *((int *) arg); // 先转为具体的类型指针，然后再取内容
    LOGE("---------- thread handler -------------%d", i);*/


    LOGE("---------- thread handler -------------");


//    JNIEnv *env = (JNIEnv *) arg;
//    jclass jclass1 = (*env)->FindClass(env, "com/uurobot/serialportcompiler/jniTest/PersonTest");
//    jfieldID jfieldID1 = (*env)->GetFieldID(env, jclass1, "age", "I");
//    (*env)->GetIntField(env,);

/*    struct Data *data = (struct Data *) arg;
    LOGE("data age ========= %d", data->age);*/

    JNIEnv *env;
    jint result = (*gVm)->AttachCurrentThread(gVm, &env, NULL);
    LOGE("result === %d", result);

    // 子线程不能调用 findClass ？？？？？？？？？？？？？？？？？
//    jclass jclassC = (*env)->FindClass(env, "com/lang/String");
//    jclass jclassC = (*env)->FindClass(env, "Lcom/uurobot/serialportcompiler/jniTest/PersonTest");
/*    if (jclassC == NULL) {
        LOGE("------------ jclass c is null ---------");
        (*gVm)->DetachCurrentThread(gVm);//必须调用
        return 0;
    }*/

    jclass jclass1 = (*env)->GetObjectClass(env, obj);
    jfieldID jfieldID1 = (*env)->GetFieldID(env, jclass1, "age", "I");
    jint jint1 = (*env)->GetIntField(env, obj, jfieldID1);
    LOGE(" age============== %d", jint1);


    (*gVm)->DetachCurrentThread(gVm);//必须调用
}

struct Data {
    int age;
};

JNIEXPORT void JNICALL start(JNIEnv *env, jobject jobject) {
    pthread_t id;
    int data = 10;
    struct Data dataStruct;
    dataStruct.age = 10;

//   pthread_create(&id, NULL, handler, &data);
//    pthread_create(&id, NULL, handler, env); //这样传递g用不了

//    pthread_create(&id, NULL, handler, &dataStruct);

    obj = (*env)->NewGlobalRef(env, jobject);
    pthread_create(&id, NULL, handler, NULL);
    LOGE("thread id = %d", id);
}

JNIEXPORT void JNICALL stop(JNIEnv *env, jobject jobject) {
    (*env)->DeleteGlobalRef(env, obj);
}

static JNINativeMethod method_table[] = {
        {"start", "()V", (void *) start}, //大小写不能写错
        {"stop",  "()V", (void *) stop}, //大小写不能写错
};

JNIEXPORT jint JNI_OnLoad(JavaVM *vm, void *reserved) {
    //OnLoad方法是没有JNIEnv参数的，需要通过vm获取。
    gVm = vm;
    JNIEnv *env = NULL;

    if ((*vm)->AttachCurrentThread(vm, &env, NULL) == JNI_OK) {
        jclass clazz = (*env)->FindClass(env,
                                         "com/uurobot/serialportcompiler/jniTest/PersonTest"); //这里获取的是class，而不是实例对象，通过类的全名
        if (clazz == NULL) {
            LOGE("-----------------class is null--------------");
            return JNI_FALSE;
        }
        //注册方法，成功返回正确的JNIVERSION。
        if ((*env)->RegisterNatives(env, clazz, method_table,
                                    sizeof(method_table) / sizeof(method_table[0])) == JNI_OK) {
            return JNI_VERSION_1_4;
        } else {
            LOGE("------------register error -----------------");
        }

    }
    return JNI_FALSE;
}

