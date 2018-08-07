package com.uurobot.serialportcompiler.jniTest;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.util.Log;

import com.uurobot.serialportcompiler.DynamicReg;

/**
 * Created by Administrator on 2018/1/19.
 */

public class JNIActivity extends Activity {
      
      @Override
      protected void onCreate(@Nullable Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            new Handler().postDelayed(new Runnable() {
                  @Override
                  public void run() {
                        testMgr();
                  }
            },5000);
        
      }
      
      private void testMgr() {
            final SerialPortMgr instance =  SerialPortMgr.getInstance();
            instance.initTtyDevice();
            new Handler().postDelayed(new Runnable() {
                  @Override
                  public void run() {
                        instance.close();
                  }
            },3000);
           
      }
      
      private void testStr() {
            DynamicReg dynamicReg = new DynamicReg();
            String s = dynamicReg.native_hello();
      }
}
