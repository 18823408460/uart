package com.uurobot.serialportcompiler;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;

/**
 * Created by Administrator on 2018/1/31.
 */

public class SecondActivity extends Activity {
      private static final String TAG = "SecondActivity";
      
      @Override
      protected void onCreate(@Nullable Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            
            DynamicReg dynamicReg = new DynamicReg();
            String s = dynamicReg.native_hello();
            Log.e(TAG, "onCreate: " + s);
            System.out.println("data=== " + s);
            
            int i = dynamicReg.native_add(1, 1);
            System.out.println("add= " + i);
            Log.e(TAG, "onCreate: add=" + i);
      }
}
