package com.uurobot.serialportcompiler.jniTest;

import android.util.Log;

/**
 * Created by Administrator on 2018/8/1.
 */

public class PersonTest {
      private static final String TAG = "PersonTest";
      
      private int age = 10;
      private String name = "UU";
      
      static {
            System.loadLibrary("threadTest");
      }
      
      public PersonTest() {
            Log.e(TAG, "PersonTest: construct---------------" + hashCode());
      }
      
      public PersonTest(int data) {
            Log.e(TAG, "PersonTest int : construct---------------" + hashCode());
      }
      
      public void cloneTest(PersonTest test) {
            Log.e(TAG, "cloneTest: " + test.hashCode());
      }
      
      public void stringGet(String data) {
            Log.e(TAG, "stringGet: " + data);
      }
      
      public native void start();
      public native void stop();
}
