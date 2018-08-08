package com.uurobot.serialportcompiler.jniTest;

import android.os.Handler;
import android.os.HandlerThread;
import android.os.SystemClock;
import android.util.Log;

import com.uurobot.serialportcompiler.constant.MsgConChest;
import com.uurobot.serialportcompiler.utils.CycleQueue;
import com.uurobot.serialportcompiler.utils.DataUtils;

import java.io.File;

/**
 * Created by Administrator on 2018/1/26.
 */

public class SerialPortMgr {
      
      private static final String TAG = "SerialPortMgr";
      
      private static SerialPortMgr serialPortMgr;
      
      private final String ttyName = "/dev/ttyS3";
      
      private File device;
      
      private SerialPortMgr() {
            HandlerThread handlerThread = new HandlerThread("dispatchDataThread");
            handlerThread.start();
            handler = new Handler(handlerThread.getLooper());
            
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
            if (open1) {
                  Log.e(TAG, "open success......");
            }
      }
      
      
      public static SerialPortMgr getInstance() {
            if (serialPortMgr == null) {
                  synchronized (SerialPortMgr.class) {
                        if (serialPortMgr == null) {
                              serialPortMgr = new SerialPortMgr();
                        }
                  }
            }
            return serialPortMgr;
      }
      
      private CycleQueue cycleQueue = new CycleQueue();
      
      //-----------jni相关---------------//
      public native boolean open(String path, int baudrate, int flags);
      
      static {
            System.loadLibrary("serialPort");
      }
      
      public native int uartSend(byte[] data);
      
      public native boolean close();
      
      private static int index = 0;
      private Handler handler;
      
      public void onReceiveData(final int len, final byte[] data) {
            Log.e(TAG, "onReceiveData: thread name = " + Thread.currentThread().getName());
            Log.e("serialport", "receiverData====index=" + (index++) + "    data=" + DataUtils.bytesToHexString(data, len));
            handler.post(new Runnable() {
                  @Override
                  public void run() {
                        dispathEvent(data);
                  }
            });
      }
      
      public void sendData() {
            // 检查是否要分包，
      }
      
      private void dispathEvent(byte[] data) {
            Log.e(TAG, "dispathEvent: " + Thread.currentThread().getName());
            int cmd = data[4] & 0xff;
            SystemClock.sleep(1400);
            switch (cmd) {
                  case MsgConChest.Cmd.Heart:
                        Log.e(TAG, "dispathEvent: heart bean   ");
                        break;
                  case MsgConChest.Cmd.Power:
                        Log.e(TAG, "dispathEvent: heart power   ");
                        break;
            }
      }
      
}
