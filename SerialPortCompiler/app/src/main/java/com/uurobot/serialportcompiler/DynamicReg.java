package com.uurobot.serialportcompiler;

import android.util.Log;

/**
 * Created by Administrator on 2018/7/30.
 */

public class DynamicReg {
      private static final String TAG = "SecondActivityamicReg";
      static {
            System.loadLibrary("dynamic");
      }
      
      public native String native_hello();
      
      public native int native_add(int a, int b);
      
      public void onAudio(int data) {
            Log.e(TAG, "onAudio: data== " + data + "    name="+Thread.currentThread().getName());
      }
}
