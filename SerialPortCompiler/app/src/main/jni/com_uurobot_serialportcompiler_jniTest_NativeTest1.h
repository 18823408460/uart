/* DO NOT EDIT THIS FILE - it is machine generated */
#include <jni.h>
/* Header for class com_uurobot_serialportcompiler_jniTest_NativeTest1 */

#ifndef _Included_com_uurobot_serialportcompiler_jniTest_NativeTest1
#define _Included_com_uurobot_serialportcompiler_jniTest_NativeTest1
#ifdef __cplusplus
extern "C" {
#endif
/*
 * Class:     com_uurobot_serialportcompiler_jniTest_NativeTest1
 * Method:    sayHello
 * Signature: (Ljava/lang/String;)V
 */
JNIEXPORT void JNICALL Java_com_uurobot_serialportcompiler_jniTest_NativeTest1_sayHello
  (JNIEnv *, jobject, jstring);

/*
 * Class:     com_uurobot_serialportcompiler_jniTest_NativeTest1
 * Method:    getString
 * Signature: ()Ljava/lang/String;
 */
JNIEXPORT jstring JNICALL Java_com_uurobot_serialportcompiler_jniTest_NativeTest1_getString
  (JNIEnv *, jobject);

/*
 * Class:     com_uurobot_serialportcompiler_jniTest_NativeTest1
 * Method:    typeTest
 * Signature: (ZBCSIJFD)V
 */
JNIEXPORT void JNICALL Java_com_uurobot_serialportcompiler_jniTest_NativeTest1_typeTest
  (JNIEnv *, jobject, jboolean, jbyte, jchar, jshort, jint, jlong, jfloat, jdouble);

/*
 * Class:     com_uurobot_serialportcompiler_jniTest_NativeTest1
 * Method:    objTypeTest
 * Signature: (Ljava/lang/String;[I)V
 */
JNIEXPORT void JNICALL Java_com_uurobot_serialportcompiler_jniTest_NativeTest1_objTypeTest
  (JNIEnv *, jobject, jstring, jintArray);

/*
 * Class:     com_uurobot_serialportcompiler_jniTest_NativeTest1
 * Method:    add
 * Signature: (II)I
 */
JNIEXPORT jint JNICALL Java_com_uurobot_serialportcompiler_jniTest_NativeTest1_add
  (JNIEnv *, jobject, jint, jint);

/*
 * Class:     com_uurobot_serialportcompiler_jniTest_NativeTest1
 * Method:    testBytes
 * Signature: ([B)[B
 */
JNIEXPORT jbyteArray JNICALL Java_com_uurobot_serialportcompiler_jniTest_NativeTest1_testBytes
  (JNIEnv *, jobject, jbyteArray);

/*
 * Class:     com_uurobot_serialportcompiler_jniTest_NativeTest1
 * Method:    setClass
 * Signature: (Lcom/uurobot/serialportcompiler/jniTest/Myclass;)V
 */
JNIEXPORT void JNICALL Java_com_uurobot_serialportcompiler_jniTest_NativeTest1_setClass
  (JNIEnv *, jobject, jobject);

#ifdef __cplusplus
}
#endif
#endif