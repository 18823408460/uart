package com.uurobot.serialportcompiler;

import android.util.Log;

import java.io.FileDescriptor;

/**
 * Created by Administrator on 2017/5/20.
 */

public class NativeUtils {
    public FileDescriptor mFd;
    static {
        System.loadLibrary("NativeExample");
    }

    public NativeUtils(String path, int baudrate,
                       int flags) {
        this.mFd = open(path,baudrate,flags);
    }

    /*
         */
    public native  FileDescriptor open(String path, int baudrate,
                                             int flags);

    public native  void close();

    public void onReceiver(String data){
        Log.e("serial_port", "onReceiver: ---------="+data);
    }

}
