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


//(byte a, short b, int c, long d, double e, float f, boolean g, Object object, Myclass myclass, int[] array)
JNIEXPORT void JNICALL native_byte(JNIEnv *env, jclass clazz,
                                   jbyte a) { // 这个class才是实例对象
    LOGE("byte ==%d", a);
}

JNIEXPORT void JNICALL native_arrays(JNIEnv *env, jclass clazz,
                                     jintArray a) { // 这个class才是实例对象
    jsize len = (*env)->GetArrayLength(env, a);
    jint buf[len];
    memset(buf, 0, len);
    LOGE("len =  %d", len);
    // 中间两个参数，start，end
    (*env)->GetIntArrayRegion(env, a, 0, len, buf);
    int i = 0;
    for (i = 0; i < len; i++) {
        LOGE("array int = %d", buf[i]);
    }
}

JNIEXPORT void JNICALL native_boolean(JNIEnv *env, jclass clazz,
                                      jboolean a) { // 这个class才是实例对象
    LOGE("boolean ==%d", a);
}

JNIEXPORT void JNICALL native_double(JNIEnv *env, jclass clazz,
                                     jdouble a) { // 这个class才是实例对象
    LOGE("double ==%ld", a);
}

JNIEXPORT void JNICALL native_long(JNIEnv *env, jclass clazz,
                                   jlong a) { // 这个class才是实例对象
    LOGE("long ==%ld", a);
}

JNIEXPORT void JNICALL native_float(JNIEnv *env, jclass clazz,
                                    jfloat a) { // 这个class才是实例对象
    LOGE("float ==%f", a); //默认是6个小数
}

JNIEXPORT void JNICALL native_short(JNIEnv *env, jclass clazz,
                                    jshort a) { // 这个class才是实例对象
    LOGE("short ==%d", a);
}

JNIEXPORT void JNICALL native_int(JNIEnv *env, jclass clazz,
                                  jint a) { // 这个class才是实例对象
    LOGE("int  ==%d", a);
}

JNIEXPORT void JNICALL native_char(JNIEnv *env, jclass clazz,
                                   jchar a) { // 这个class才是实例对象
    LOGE("char  ==%c", a);
}

JNIEXPORT void JNICALL native_obj(JNIEnv *env, jclass clazz,
                                  jobject obj) { // 这个class才是实例对象
    LOGE("obj myclasss==================== ");
    jclass jclass1 = (*env)->GetObjectClass(env, obj); //根据实例对象获取class；

    // 方法签名说明： 如果是引用类型，后面必须加 ":"
    jmethodID setNameId = (*env)->GetMethodID(env, jclass1, "setName", "(Ljava/lang/String;)V");

    jstring name = (*env)->NewStringUTF(env, "Xiaowei");
    (*env)->CallVoidMethod(env, obj, setNameId, name);

    jmethodID printnameId = (*env)->GetMethodID(env, jclass1, "printName", "()V");
//    jmethodID printnameId = (*env)->GetMethodID(env, jclass1, "printName", "(V)V");如果没有参数，什么都不需要写，
    (*env)->CallVoidMethod(env, obj, printnameId);

    jmethodID testId = (*env)->GetMethodID(env, jclass1, "test", "()V"); // private方法也是可以调用
    (*env)->CallVoidMethod(env, obj, testId);

    jmethodID hashcodeId = (*env)->GetMethodID(env, jclass1, "hashCode", "()I"); // 父类里面的方法也是可以直接调用
    jint code = (*env)->CallIntMethod(env, obj, hashcodeId);
    LOGE("myclass hashCode = %d", code);

    //局部引用
    (*env)->DeleteLocalRef(env,jclass1);
}

static JNINativeMethod method_table[] = {
        {"native_byte",    "(B)V",                                                (void *) native_byte}, //大小写不能写错
        {"native_int",     "(I)V",                                                (void *) native_int},
        {"native_short",   "(S)V",                                                (void *) native_short},
        {"native_float",   "(F)V",                                                (void *) native_float},
        {"native_long",    "(J)V",                                                (void *) native_long},
        {"native_double",  "(D)V",                                                (void *) native_double},
        {"native_boolean", "(Z)V",                                                (void *) native_boolean},
        {"native_char",    "(C)V",                                                (void *) native_char},
        {"native_arrays",  "([I)V",                                               (void *) native_arrays},
        {"native_obj",     "(Lcom/uurobot/serialportcompiler/jniTest/Myclass;)V", (void *) native_obj},
};

JNIEXPORT jint JNI_OnLoad(JavaVM *vm, void *reserved) {
    //OnLoad方法是没有JNIEnv参数的，需要通过vm获取。
    gVm = vm;
    JNIEnv *env = NULL;

    if ((*vm)->AttachCurrentThread(vm, &env, NULL) == JNI_OK) {
        jclass clazz = (*env)->FindClass(env,
                                         "com/uurobot/serialportcompiler/DataType"); //这里获取的是class，而不是实例对象，通过类的全名
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

