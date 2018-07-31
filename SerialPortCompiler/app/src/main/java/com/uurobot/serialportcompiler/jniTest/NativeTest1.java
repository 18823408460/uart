package com.uurobot.serialportcompiler.jniTest;

import android.util.Log;

/**
 * Created by Administrator on 2018/1/19.
 */

public class NativeTest1 {
        private int count =1 ;
        private String name = "NativeTest1Name";
        static {
                System.loadLibrary("serialPort");
        }
        public native void sayHello(String data);

        public native String getString() ;

        public native void typeTest(boolean b,byte b1, char c, short s,int i,long l,float f,double d);

        public native void objTypeTest(String s,int[] ints);

        public native int add(int x,int y );

        public void receiverData(String data){
                Log.e("serialport","receiverData。。。"+data) ;
        }


        public native byte[] testBytes(byte[] datas);
        public native void setClass(Myclass myclass) ;
}
