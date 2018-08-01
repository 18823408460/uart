package com.uurobot.serialportcompiler.jniTest;

import android.util.Log;

/**
 * Created by Administrator on 2018/1/19.
 */

public class Myclass {
      private static final String TAG = "Myclass";
      private static String name = "staticName";
      private String pName = "MyclassPName";
      
      private int count = 1;
      
      public void printName() {
            Log.e(TAG, "printName=" + pName);
      }
      
      public void setName(String name) {
            this.pName = name;
            Log.e(TAG, "setName: " + name + "   name==" + Thread.currentThread().getName());
      }
      
      private void test() {
            Log.e(TAG, "test: "+hashCode());
      }
}
