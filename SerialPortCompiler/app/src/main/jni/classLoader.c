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

typedef jclass pVoid;
static JavaVM *gVm;
static jmethodID methodId;
static jobject obj; // 实例对象
static jclass jcls;// class

// 全局引用不再受到JVM统一管理,需要手动删除
// 局部引用是JVM负责的引用类型，其被JVM分配管理，并占用JVM的资源

JNIEXPORT void JNICALL start(JNIEnv *env, jobject obj) {
    jclass jclass1 = (*env)->FindClass(env,
                                       "com/uurobot/serialportcompiler/jniTest/PersonTest");
    jmethodID initId = (*env)->GetMethodID(env, jclass1, "<init>", "(I)V"); // 有参构造
//    jmethodID initId = (*env)->GetMethodID(env, jclass1, "<init>", "()V"); // 无参数构造
    //创建对象1：
    jobject jobject1 = (*env)->NewObject(env, jclass1, initId);

    jmethodID testId = (*env)->GetMethodID(env, jclass1, "cloneTest",
                                           "(Lcom/uurobot/serialportcompiler/jniTest/PersonTest;)V");

    (*env)->CallVoidMethod(env, obj, testId, jobject1);
    (*env)->CallVoidMethod(env, obj, testId, obj);

    //创建对象2 ， 不会调用构造方法
    jobject jobject2 = (*env)->AllocObject(env, jclass1);
    if (jobject2 == NULL) {
        LOGE("-------------create obj is null ---------");
    }
    (*env)->CallVoidMethod(env, obj, testId, jobject2);

    jboolean boolean = (*env)->IsInstanceOf(env, jobject2, jclass1); // 1=true, 0 =false
    LOGE(" isInstanceof ======= %d", boolean);

    jboolean same = (*env)->IsSameObject(env, jobject1, jobject2);
    LOGE("isSameObj======== %d", same);

    jfieldID jfieldID1 = (*env)->GetFieldID(env, jclass1, "age", "I");
    jint jint1 = (*env)->GetIntField(env, jobject1, jfieldID1);
    LOGE("PersonTest age = %d", jint1);

    jfieldID jfieldID2 = (*env)->GetFieldID(env, jclass1, "name", "Ljava/lang/String;"); //“必须要加；”

    jstring jstring1 = (*env)->GetObjectField(env, jobject1, jfieldID2);
    printMyString(env, jstring1);
    printMyString2(env, jstring1);


    jmethodID jmethodID1 = (*env)->GetMethodID(env, jclass1, "stringGet", "(Ljava/lang/String;)V");
    char *jHello = "helloHoney";
    jstring helloo = (*env)->NewStringUTF(env, jHello); //如果是传递到java，不需要释放
    (*env)->CallVoidMethod(env, jobject1, jmethodID1, helloo);
//    (*env)->ReleaseStringUTFChars(env, helloo, jHello);
}

JNIEXPORT void JNICALL printMyString(JNIEnv *env, jstring jstring1) {
    jsize len = (*env)->GetStringLength(env, jstring1);
    jchar buff[len];
    (*env)->GetStringRegion(env, jstring1, 0, len, buff); // 字符串的截取
    int i = 0;
    for (i = 0; i < len; i++) {
        LOGE("PersonTest age = %c", buff[i]);
    }
}

JNIEXPORT void JNICALL printMyString2(JNIEnv *env, jstring jstring1) {
    jboolean isCopy;
    jsize len = (*env)->GetStringLength(env, jstring1);
    const jchar *data = (*env)->GetStringChars(env, jstring1, &isCopy);
    LOGE("isCopy==========%d", isCopy);
/*    while (*data != NULL && *data != ' ') { // 这里会返回很多脏数据
        char temp = *data;
        LOGE("char is == %c", temp);
        data++;
    }*/
    while (len--) {
        char temp = *data;
        LOGE("char is == %c", temp);
        data++;
    }
    const char *helloData = "hello world";
    LOGE("data is ==== %s", helloData);
    while (*helloData != NULL) {
        char temp = *helloData;
        LOGE("printf char is == %c", temp);
        helloData++;
    }
}


static JNINativeMethod method_table[] = {
        {"start", "()V", (void *) start}, //大小写不能写错

};

JNIEXPORT jint JNI_OnLoad(JavaVM *vm, void *reserved) {
    //OnLoad方法是没有JNIEnv参数的，需要通过vm获取。
    gVm = vm;
    JNIEnv *env = NULL;

    if ((*vm)->AttachCurrentThread(vm, &env, NULL) == JNI_OK) {
        pVoid clazz = (*env)->FindClass(env,
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

