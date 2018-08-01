package com.uurobot.serialportcompiler.jniTest;

import android.util.Log;

import java.io.File;

/**
 * Created by Administrator on 2018/1/26.
 */

public class SerialPortMgr {

        private static final String TAG = "SerialPortMgr";

//        private static SerialPortMgr serialPortMgr;

        private final String ttyName = "/dev/ttyS3";

        private File device;

        public SerialPortMgr() {
        
        }
        
        public void initTtyDevice() {
                device = new File(ttyName);
                if (!device.canRead() || !device.canWrite()) {
                        try {
                                Process su;
                                su = Runtime.getRuntime().exec("/system/bin/su");
                                String cmd = "chmod 666 " + device.getAbsolutePath() + "\n"
                                        + "exit\n";
                                su.getOutputStream().write(cmd.getBytes());
                                if ((su.waitFor() != 0) || !device.canRead()
                                        || !device.canWrite()) {
                                        Log.e(TAG, "not root,please check ");
                                }
                        }
                        catch (Exception e) {
                                e.printStackTrace();
                                Log.e(TAG, "not root,please check ");
                                device = null;
                        }
                }
                boolean open1 = open(device.getAbsolutePath(), 115200, 0);
                if (open1){
                        Log.e(TAG,"open success......");
                }
        }


 /*       public static SerialPortMgr getInstance() {
                if (serialPortMgr == null) {
                        synchronized (SerialPortMgr.class) {
                                if (serialPortMgr == null) {
                                        serialPortMgr = new SerialPortMgr();
                                }
                        }
                }
                return serialPortMgr;
        }*/


        //-----------jni相关---------------//
        public native static boolean open(String path, int baudrate,int flags);
        static {
                System.loadLibrary("serialPort");
        }
        public native  void close();
        public void onReceiveData(){
                Log.e("serialport","receiverData。。。") ;
        }
}
