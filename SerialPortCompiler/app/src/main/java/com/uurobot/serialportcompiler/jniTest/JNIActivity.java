package com.uurobot.serialportcompiler.jniTest;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;

/**
 * Created by Administrator on 2018/1/19.
 */

public class JNIActivity extends Activity {
      
      @Override
      protected void onCreate(@Nullable Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            
            SerialPortMgr instance = new SerialPortMgr();
            instance.initTtyDevice();
      }
}
