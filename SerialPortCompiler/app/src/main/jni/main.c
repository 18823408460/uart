#include <termios.h>
#include <unistd.h>
#include <sys/types.h>
#include <sys/stat.h>
#include <fcntl.h>
#include <string.h>
#include <jni.h>
#include <pthread.h>
#include <stdio.h>
#include <sys/types.h>
#include <sys/stat.h>
#include <fcntl.h>
#include <string.h>
#include <sys/ioctl.h>
#include "android/log.h"

static const char *TAG = "serial_port";
#define LOGI(fmt, args...) __android_log_print(ANDROID_LOG_INFO,  TAG, fmt, ##args)
#define LOGD(fmt, args...) __android_log_print(ANDROID_LOG_DEBUG, TAG, fmt, ##args)
#define LOGE(fmt, args...) __android_log_print(ANDROID_LOG_ERROR, TAG, fmt, ##args)
int fd;
jmethodID method3;
JNIEnv *env;
jobject cls;
static speed_t getBaudrate(jint baudrate) {
    switch (baudrate) {
        case 0:
            return B0;
        case 50:
            return B50;
        case 75:
            return B75;
        case 110:
            return B110;
        case 134:
            return B134;
        case 150:
            return B150;
        case 200:
            return B200;
        case 300:
            return B300;
        case 600:
            return B600;
        case 1200:
            return B1200;
        case 1800:
            return B1800;
        case 2400:
            return B2400;
        case 4800:
            return B4800;
        case 9600:
            return B9600;
        case 19200:
            return B19200;
        case 38400:
            return B38400;
        case 57600:
            return B57600;
        case 115200:
            return B115200;
        case 230400:
            return B230400;
        case 460800:
            return B460800;
        case 500000:
            return B500000;
        case 576000:
            return B576000;
        case 921600:
            return B921600;
        case 1000000:
            return B1000000;
        case 1152000:
            return B1152000;
        case 1500000:
            return B1500000;
        case 2000000:
            return B2000000;
        case 2500000:
            return B2500000;
        case 3000000:
            return B3000000;
        case 3500000:
            return B3500000;
        case 4000000:
            return B4000000;
        default:
            return -1;
    }
}

void *threadreadTtyData(void *arg) {
    LOGE("run read data");

    char buf[100]  ;
    int result = 0, ret;
    fd_set readfd;
    struct timeval timeout;
    while (1) {
        LOGE(".......run ..........");
        timeout.tv_sec = 2;//设定超时秒数
        timeout.tv_usec = 0;//设定超时毫秒数
        FD_ZERO(&readfd);//清空集合
        FD_SET(fd, &readfd);///* 把要检测的句柄mTtyfd加入到集合里 */
        ret = select(fd+1, &readfd, NULL, NULL, &timeout);/* 检测我们上面设置到集合readfd里的句柄是否有可读信息 */
        switch (ret) {
            case -1:/* 这说明select函数出错 */
                result = -1;
                LOGE("mTtyfd read failure");
                break;

            case 0:/* 说明在我们设定的时间值5秒加0毫秒的时间内，mTty的状态没有发生变化 */
                break;
            default:/* 说明等待时间还未到5秒加0毫秒，mTty的状态发生了变化 */
                if (FD_ISSET(fd, &readfd)) {/* 先判断一下mTty这外被监视的句柄是否真的变成可读的了 */
                    int len = read(fd, buf, sizeof(buf));
                    LOGE("mTtyfd read .......%d",+len);
/**发送数据**/
//                    LOGE("The array = [");
//                    int  i = 0 ;
//                    for(i=0;i<=len;i++)
//                    {
//                        LOGE("%02X   ",buf[i]);
//                    }

//                    LOGE("]\n");
//                    JNIMyObserver *l = static_cast<JNIMyObserver * >(arg);
//                    l->OnEvent(buf, len, RECEIVE_DATA_INDEX);

                    (*env)->CallVoidMethod(env, cls, method3,(*env)->NewStringUTF(env,"haha in C ."));
                    memset(buf, 0, sizeof(buf));
                }

                break;
        }
        if (result == -1) {
            break;
        }
    }
    LOGE("stop run!");
    return NULL;

}


JNIEXPORT jobject JNICALL Java_com_uurobot_serialportcompiler_NativeUtils_open
        (JNIEnv *env1, jobject cls1, jstring path, jint baudrate, jint iscopy) {
    env = env1 ;
    cls = cls1 ;
    speed_t speed;
    jobject mFileDescriptor;
    char *temp = "dddd";
    LOGD("Opening serial port %s", temp);
    /* Check arguments */
    {
        speed = getBaudrate(baudrate);
        if (speed == -1) {
            /* TODO: throw an exception */
            LOGE("Invalid baudrate");
            return NULL;
        }
    }

    /* Opening device */
    {
        jboolean iscopy;
        const char *path_utf = (*env)->GetStringUTFChars(env, path, &iscopy);
        LOGD("Opening serial port %s", path_utf);
        fd = open(path_utf, O_RDWR | O_SYNC);  //O_DIRECT选项    会报错
        LOGD("open() fd = %d", fd);
        (*env)->ReleaseStringUTFChars(env, path, path_utf);
        if (fd == -1) {
            /* Throw an exception */
            LOGE("Cannot open port");
            /* TODO: throw an exception */
            return NULL;
        }
    }

    /* Configure device */
    {
        struct termios cfg;
        LOGD("Configuring serial port");
        if (tcgetattr(fd, &cfg)) {
            LOGE("tcgetattr() failed");
            close(fd);
            /* TODO: throw an exception */
            return NULL;
        }

        cfmakeraw(&cfg);
        cfsetispeed(&cfg, speed);
        cfsetospeed(&cfg, speed);

        if (tcsetattr(fd, TCSANOW, &cfg)) {
            LOGE("tcsetattr() failed");
            close(fd);
            /* TODO: throw an exception */
            return NULL;
        }
    }

    /* Create a corresponding file descriptor */
    {
        jclass cFileDescriptor = (*env)->FindClass(env, "java/io/FileDescriptor");
        jmethodID iFileDescriptor = (*env)->GetMethodID(env, cFileDescriptor, "<init>", "()V");
        jfieldID descriptorID = (*env)->GetFieldID(env, cFileDescriptor, "descriptor", "I");
        mFileDescriptor = (*env)->NewObject(env, cFileDescriptor, iFileDescriptor);
        (*env)->SetIntField(env, mFileDescriptor, descriptorID, (jint) fd);
    }

    pthread_t id;
    int ret;
    ret = pthread_create(&id,NULL,threadreadTtyData,NULL);
    if(ret != 0){
        LOGE("create receiver thread failure ");
    }else{
        LOGE("create read data thred success");
    }

    jclass clazz = (*env)->FindClass(env, "com/uurobot/serialportcompiler/NativeUtils");
    if(clazz == 0){
        LOGE("find class error");
        return NULL;
    }
    LOGE("find class");

    method3 = (*env)->GetMethodID(env,clazz,"onReceiver","(Ljava/lang/String;)V");
    if(method3 == 0){
        LOGE("find method3 error");
        return NULL;
    }
    LOGE("find method3");
    (*env)->CallVoidMethod(env, cls, method3,(*env)->NewStringUTF(env,"haha in C ."));
    return mFileDescriptor;
}


JNIEXPORT void Java_com_uurobot_serialportcompiler_NativeUtils_close
        (JNIEnv *env, jobject thiz) {
    jclass NativeUtilsClass = (*env)->GetObjectClass(env, thiz);
    jclass FileDescriptorClass = (*env)->FindClass(env, "java/io/FileDescriptor");

    jfieldID mFdID = (*env)->GetFieldID(env, NativeUtilsClass, "mFd", "Ljava/io/FileDescriptor;");
    jobject mFd = (*env)->GetObjectField(env, thiz, mFdID);

    jfieldID descriptorID = (*env)->GetFieldID(env, FileDescriptorClass, "descriptor", "I");
    jint descriptor = (*env)->GetIntField(env, mFd, descriptorID);

    LOGD("close(fd = %d)", descriptor);
    close(descriptor);
}



