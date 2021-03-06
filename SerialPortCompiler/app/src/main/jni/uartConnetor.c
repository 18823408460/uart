#include <termios.h>
#include <unistd.h>
#include <string.h>
#include <sys/types.h>
#include <sys/stat.h>
#include <fcntl.h>
#include <jni.h>
#include <pthread.h>
#include <stdlib.h>
#include <stdio.h>
#include <math.h>
#include <sys/ioctl.h>
#include "android/log.h"

#ifndef _Included_com_uurobot_serialportcompiler_jniTest_SerialPortMgr
#define _Included_com_uurobot_serialportcompiler_jniTest_SerialPortMgr
#ifdef __cplusplus
extern "C" {
#endif
static const char *TAG = "SerialPortMgrNative";
#define LOGI(fmt, args...) __android_log_print(ANDROID_LOG_INFO,  TAG, fmt, ##args)
#define LOGD(fmt, args...) __android_log_print(ANDROID_LOG_DEBUG, TAG, fmt, ##args)
#define LOGE(fmt, args...) __android_log_print(ANDROID_LOG_ERROR, TAG, fmt, ##args)
#define  bufSize 1024
int fd = -1;
static jobject g_obj;
static JavaVM *gVm;
int readHeadH = 0;
int readHeadL = 0;
int readLenH = 0;
int readLenL = 0;
int outBufIndex = 0;
unsigned char outBuf[512];
unsigned char cacheBuf[bufSize];
int tailIndex = 0;
int headIndex = 0;
int dataLenH, dataLenL, dataLen;
jboolean isStopReceiver = 1;
jclass jclass1;
jmethodID jmethodID1;
jbyteArray bytes;
JNIEnv *env;
jbyte headH = 0x08;
jbyte headL = 0x06;
// https://juejin.im/post/5b4c0a09f265da0f955cc1c7?utm_source=gold_browser_extension

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

void copyData(int len, unsigned char buf[]) {
    int tailCanWrite = bufSize - tailIndex; //剩余的可写
    int i;
    if (len < tailCanWrite) { //如果可以装
        for (i = 0; i < len; ++i) {
            cacheBuf[tailIndex++] = buf[i];
        }
    } else { // 如果装不下
        int headCanWrite = len - tailCanWrite; // 头部需要写
        for (i = 0; i < tailCanWrite; i++) {
            cacheBuf[tailIndex++] = buf[i];
        }
        tailIndex = 0;
        for (; i < len; i++) {
            cacheBuf[tailIndex++] = buf[i];
        }
    }
}

void parseData() {
    int canReadData;
    if (tailIndex > headIndex) {
        canReadData = tailIndex - headIndex;
    } else {
        int tailLast = bufSize - headIndex;
        canReadData = tailIndex + tailLast;
    }
    // Log.e(TAG, "parseData: canreadData len = " + canReadData + "  tailIndex =" + tailIndex + "   headIndex=" + headIndex);
    while (canReadData-- > 0) {
        if (readHeadH == 0) {
            // Log.e(TAG, "parseData: b=============" + b + "   headIndex=" + headIndex);
            if (cacheBuf[getIndex()] == headH) {
                readHeadH = 1;
                outBuf[outBufIndex++] = headH;
                // Log.e(TAG, "parseData: read head h =================== ");
                continue;
            }
        }
        if (readHeadH == 1 && readHeadL == 0) {
            if (cacheBuf[getIndex()] == headL) {
                readHeadL = 1;
                outBuf[outBufIndex++] = headL;
                //  Log.e(TAG, "parseData: read head l =================");
                continue;
            }
        }
        if (readHeadL == 1 && readHeadH == 1) {
            if (readLenH == 0) {
                readLenH = 1;
                dataLenH = cacheBuf[getIndex()];
                outBuf[outBufIndex++] = (unsigned char) dataLenH;
                //  Log.e(TAG, "parseData: read dataLenH  =================" + DataUtils.bytesToHexString((byte) dataLenH));
                continue;
            }
            if (readLenH == 1 && readLenL == 0) {
                readLenL = 1;
                dataLenL = cacheBuf[getIndex()];
                dataLen = dataLenH * 256 + dataLenL;
                outBuf[outBufIndex++] = (unsigned char) dataLenL;
                //Log.e(TAG, "parseData: read dataLenL  =================" + DataUtils.bytesToHexString((byte) dataLenL) + "   len=" + dataLenL);
                continue;
            }
            if (readLenH == 1 && readLenL == 1) {
                if (outBufIndex < (dataLen + 7)) {
                    outBuf[outBufIndex++] = cacheBuf[getIndex()];
                    //   Log.e(TAG, "parseData: content==== " + outBufIndex);
                    continue;
                } else {
                    (*env)->SetByteArrayRegion(env, bytes, 0, (dataLen + 7), outBuf);
                    (*env)->CallVoidMethod(env, g_obj, jmethodID1, (dataLen + 7), bytes);
                    dataLen = 0;
                    readHeadH = 0;
                    readHeadL = 0;
                    readLenH = 0;
                    readLenL = 0;
                    outBufIndex = 0;
                    dataLenH = 0;
                    dataLenL = 0;
                    break;
                }
            }
            //Log.e(TAG, "parseData: 11111111111111111111: readLenH=" + readLenH + "  readLenL=" + readLenL);
        }
        // Log.e(TAG, "parseData: 2222222222222222222: readHeadL=" + readHeadL + "   readHeadL=" + readHeadL);
    }
}


int getIndex() {
    int temp = headIndex;
    if (temp >= bufSize) {
        temp = headIndex = 0;
    }
    headIndex++;
    return temp;
}


void *threadreadTtyData(void *arg) {
    jbyte inBuf[512] = {0};
    int result = 0, ret;
    fd_set readfd;
    struct timeval timeout;
    (*gVm)->AttachCurrentThread(gVm, &env, NULL);
    jclass1 = (*env)->GetObjectClass(env, g_obj);
    jmethodID1 = (*env)->GetMethodID(env, jclass1, "onReceiveData", "(I[B)V");
    bytes = (*env)->NewByteArray(env, 512);
    while (isStopReceiver) {
        timeout.tv_sec = 2;//设定超时秒数
        timeout.tv_usec = 0;//设定超时毫秒数
        FD_ZERO(&readfd);//清空集合
        FD_SET(fd, &readfd);///* 把要检测的句柄mTtyfd加入到集合里 */
        ret = select(fd + 1, &readfd, NULL, NULL, &timeout);/* 检测我们上面设置到集合readfd里的句柄是否有可读信息 */
        switch (ret) {
            case -1:/* 这说明select函数出错 */
                result = -1;
                LOGE("mTtyfd read failure");
                break;

            case 0:/* 说明在我们设定的时间值5秒加0毫秒的时间内，mTty的状态没有发生变化 */
                break;
            default:/* 说明等待时间还未到5秒加0毫秒，mTty的状态发生了变化 */
                if (FD_ISSET(fd, &readfd)) {/* 先判断一下mTty这外被监视的句柄是否真的变成可读的了 */
                    int len = read(fd, inBuf, sizeof(inBuf));
                    if (len > 0 && len < 512) {
                        //LOGE("--------------len ============= %d", len);
//                        jbyte *c_array = (*env)->GetByteArrayElements(env,bytes, 0);
//                        (*env)->ReleaseByteArrayElements(env,bytes,c_array,len);
                        copyData(len, inBuf);
                        parseData();
//                      usleep(200);
                    } else {
                        usleep(100);
                    }
                }
                break;
        }
        if (result == -1) {
            break;
        }
    }
    LOGE("----------------------will be exit--------------------");
//    (*env)->DeleteLocalRef(env, jmethodID1);// 这里会报错
//    (*env)->DeleteLocalRef(env, jclass1);// 这里会报错
    (*gVm)->DetachCurrentThread(gVm);
    LOGE("stop run!");
    return NULL;

}




/**
 * https://www.cnblogs.com/winfu/p/5629873.html
 * @param env  jni中使用到了线程 和 管道，最开始打算使用线程和消息队列，可是失败了，android不支持消息队列
 * @param obj
 * @param path
 * @param baudrate
 * @param flag
 * @return
 */
/*
 * Class:     com_uurobot_serialportcompiler_jniTest_SerialPortMgr
 * Method:    open
 * Signature: (Ljava/lang/String;II)Z
 */
JNIEXPORT jboolean JNICALL Java_com_uurobot_serialportcompiler_newCode_UARTConnector_init
        (JNIEnv *env, jobject obj, jstring path, jint baudrate) {
    LOGE("--------------------open--------------------");
    speed_t speed;
    g_obj = (*env)->NewGlobalRef(env, obj);
    /* Check arguments */
    {
        speed = getBaudrate(baudrate);
        if (speed == -1) {
            /* TODO: throw an exception */
            LOGE("Invalid baudrate");
            return 0;
        }
    }

    /* Opening device */
    {
        jboolean iscopy;
        const char *path_utf = (*env)->GetStringUTFChars(env, path, &iscopy);
        LOGD("Opening serial port %s", path_utf);
//        fd = open(path_utf, O_RDWR | flag);  //这样也可以
        fd = open(path_utf, O_RDWR | O_SYNC);  //O_DIRECT选项    会报错
        LOGD("open() fd = %d", fd);
        (*env)->ReleaseStringUTFChars(env, path, path_utf);
        if (fd == -1) {
            /* Throw an exception */
            LOGE("Cannot open port");
            /* TODO: throw an exception */
            return 0;
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
            return 0;
        }

        cfmakeraw(&cfg);
        cfsetispeed(&cfg, speed);
        cfsetospeed(&cfg, speed);

        if (tcsetattr(fd, TCSANOW, &cfg)) {
            LOGE("tcsetattr() failed");
            close(fd);
            /* TODO: throw an exception */
            return 0;
        }
    }

    pthread_t id;
    int ret;
    ret = pthread_create(&id, NULL, threadreadTtyData, NULL);
    if (ret != 0) {
        LOGE("create receiver thread failure ");
    } else {
        LOGE("create read data thred success");
    }
    return 1;
}

struct Msg {
    char type;
    char *data;
};

/*
 * Class:     com_uurobot_serialportcompiler_jniTest_SerialPortMgr
 * Method:    close
 * Signature: ()V
 */
JNIEXPORT jboolean JNICALL Java_com_uurobot_serialportcompiler_newCode_UARTConnector_destroy
        (JNIEnv *env, jobject obj) {
    LOGE("--------------------close--------------------");
/*    struct Msg msg;
    msg.type = 1;
    msg.data = "hello";
    LOGE("close msg type==== %c", msg.type);
    LOGE("close msg type==== %s", msg.data);

    struct Msg msg1 = msg;
    LOGE("close msg1 type==== %c", msg1.type);
    LOGE("close msg1 type==== %s", msg1.data);*/
    isStopReceiver = 0;
    close(fd);
    return 0;
}

JNIEXPORT void JNICALL Java_com_uurobot_serialportcompiler_jniTest_SerialPortMgr_setHead
        (JNIEnv *env, jobject obj, jbyte h, jbyte l) {
    headH = h;
    headL = l;
    LOGE("--------------------init-------------h=%d, l = %d", headH, headL);
}

JNIEXPORT jint JNICALL Java_com_uurobot_serialportcompiler_jniTest_SerialPortMgr_send
        (JNIEnv *env, jobject obj, jbyteArray jbyteArray1) {
    if (fd == -1) {
        return -1;
    }
    jint len = (*env)->GetArrayLength(env, jbyteArray1);
    LOGE("send  data len ========= %d", len);
    unsigned char buff[len];
    memset(buff, 0, len);

//    memcpy(buff, jbyteArray1, len);/这样有问题
    (*env)->GetByteArrayRegion(env, jbyteArray1, 0, len, buff);
    len = write(fd, buff, len);
    usleep(100);
    if (len > 0) {
        LOGI("write device success len= %d", len);
        return len;
    } else {
        LOGE("write device error");
    }
    return -1;
}

JNIEXPORT jint JNI_OnLoad(JavaVM *vm, void *reserved) {
    //OnLoad方法是没有JNIEnv参数的，需要通过vm获取。
    gVm = vm;
    return JNI_VERSION_1_4;
}

#ifdef __cplusplus
}
#endif
#endif
