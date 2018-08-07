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
int fd;
static jobject g_obj;
static JavaVM *gVm;
unsigned char cacheBuf[bufSize] = {0};
int tailIndex = 0;
int headIndex = 0;
// https://juejin.im/post/5b4c0a09f265da0f955cc1c7?utm_source=gold_browser_extension

// 使用前先定义
/*void copyData(int len, unsigned char buf[512]);
void parseData();*/

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


void getCompleteFrame(unsigned char *inBuf, int inCnt, unsigned char *outBuf, int *destCnt,
                      int *readStatus) {
    int i;
    int len = 0;
    for (i = 0; i < inCnt; i++) {
        if (*readStatus == 0) {
            if (inBuf[i] == 0x08 && inBuf[i + 1] == 0x06)//header
            {
                outBuf[(*destCnt)++] = inBuf[i];
                outBuf[(*destCnt)++] = inBuf[i + 1];
                i += 1;
                *readStatus = 1;
                LOGE("----------read header-----------");
                continue;
            }
        } else {
            if (*readStatus == 1)//body
            {
                outBuf[(*destCnt)++] = inBuf[i];
                //print_frame("body",dest,dest_cnt);
            }
            if (*destCnt == outBuf[1])//tail
            {
                *readStatus = 0;
                *destCnt = 0;
                memset(outBuf, -1, sizeof(outBuf));
                memset(inBuf, 0, sizeof(inBuf));
                continue;
            }
        }
    }
}

int getIndex() {
    int index = (headIndex++) % bufSize;
    LOGE("-----------------------------index= %d, tail = %d, head= %d", index,
         tailIndex, headIndex);
    return index;
}

static int beyondSize = 0;
static int dataLen = 0;
static int readHead = 0;
static int outBufIndex = 0;
unsigned char outBuf[512];

int cacheHaveData() {
    int result = 0;
    if (beyondSize == 0) {
        if (headIndex < tailIndex) {
            result = 1;
        }
    } else {
        if (headIndex < bufSize) {
            result = 1;
        } else {
            beyondSize = 0;
            headIndex = 0;
            if (headIndex < tailIndex) {
                result = 1;
            }
        }
    }
    return result;
}

void parseData() {
    int diffData = 0;
    if (tailIndex > headIndex) {
        diffData = tailIndex - headIndex;
    } else {
        int tailLast = bufSize - headIndex;
        diffData = tailIndex + tailLast;
    }
    if (diffData > 10) {
        if (cacheBuf[getIndex()] == 0x08 && cacheBuf[getIndex()] == 0x06) {
            int lenH = cacheBuf[getIndex()];
            int lenL = cacheBuf[getIndex()];
            dataLen = lenH * 256 + lenL;
            readHead = 1;
            LOGE("-----------dataLen = %d, H = %d, L = %d", dataLen, lenH, lenL);
            outBuf[outBufIndex++] = 0x08;
            outBuf[outBufIndex++] = 0x06;
            outBuf[outBufIndex++] = (unsigned char) lenH;
            outBuf[outBufIndex++] = (unsigned char) lenL;
        }
        if (readHead == 1) { // 如果已经读取到头部，开始读取body
            while (cacheHaveData()) {
                if (outBufIndex < (dataLen + 7)) {
                    outBuf[outBufIndex++] = cacheBuf[getIndex()];
                } else {
                    LOGE(">>>>>>>>>>>>>>>>>>>> pkg read end <<<<<<<<<<<<<<<<");
                    int i = 0;
                    for (i = 0; i < outBufIndex; i++) {
                        LOGE("data ============    %d", outBuf[i]);
                    }
                    dataLen = 0;
                    readHead = 0;
                    outBufIndex = 0;
                }
            }
        }
    }
}


void copyData(int len, unsigned char buf[512]) {
    int tailCanWrite = bufSize - tailIndex; //剩余的可写
    int i = 0;
    if (len < tailCanWrite) { //如果可以装
        for (i = 0; i < len; ++i) {
            cacheBuf[tailIndex++] = buf[i];
        }
    } else { // 如果装不下
        int headCanWrite = len - tailCanWrite; // 头部需要写
        for (i = 0; i < tailCanWrite; i++) {
            cacheBuf[tailIndex++] = buf[i];
        }
        beyondSize = 1;
        tailIndex = 0;
        for (; i < len; i++) {
            cacheBuf[tailIndex++] = buf[i];
        }
    }
}


void *threadreadTtyData(void *arg) {
    jbyte inBuf[512] = {0};
    int read_status = 0;
    int dest_cnt = 0;
    int result = 0, ret;
    fd_set readfd;
    struct timeval timeout;
    JNIEnv *env;
    (*gVm)->AttachCurrentThread(gVm, &env, NULL);
    jclass jclass1 = (*env)->GetObjectClass(env, g_obj);
    jmethodID jmethodID1 = (*env)->GetMethodID(env, jclass1, "onReceiveData", "(I[B)V");
    jbyteArray  bytes = (*env)->NewByteArray(env,512);
    while (1) {
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
                        (*env)->SetByteArrayRegion(env,bytes,0,len,inBuf);
                        (*env)->CallVoidMethod(env,g_obj,jmethodID1,len,bytes);

//                        jbyte *c_array = (*env)->GetByteArrayElements(env,bytes, 0);
//                        (*env)->ReleaseByteArrayElements(env,bytes,c_array,len);
                        //copyData(len, inBuf);
                        //parseData();
                        usleep(200);
                    } else {
                        usleep(100);
                    }
                    // getCompleteFrame(inBuf, len, outBuf, &dest_cnt, &read_status);
                }
                break;
        }
        if (result == -1) {
            break;
        }
    }
    (*gVm)->DetachCurrentThread(gVm);
    (*env)->DeleteLocalRef(env,jmethodID1);
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
JNIEXPORT jboolean JNICALL Java_com_uurobot_serialportcompiler_jniTest_SerialPortMgr_open
        (JNIEnv *env, jobject obj, jstring path, jint baudrate, jint flag) {
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
JNIEXPORT jboolean JNICALL Java_com_uurobot_serialportcompiler_jniTest_SerialPortMgr_close
        (JNIEnv *env, jobject obj) {
    LOGE("--------------------close--------------------");
    struct Msg msg;
    msg.type = 1;
    msg.data = "hello";
    LOGE("close msg type==== %c", msg.type);
    LOGE("close msg type==== %s", msg.data);

    struct Msg msg1 = msg;
    LOGE("close msg1 type==== %c", msg1.type);
    LOGE("close msg1 type==== %s", msg1.data);
    return 0;
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
