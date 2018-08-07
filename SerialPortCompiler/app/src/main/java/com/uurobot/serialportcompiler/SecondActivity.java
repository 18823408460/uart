package com.uurobot.serialportcompiler;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;

import com.uurobot.serialportcompiler.jniTest.Myclass;
import com.uurobot.serialportcompiler.jniTest.PersonTest;

/**
 * Created by Administrator on 2018/1/31.
 */

public class SecondActivity extends Activity {
      private static final String TAG = "SecondActivity";
      
      @Override
      protected void onCreate(@Nullable Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
      
            testStr();
      }
      
      private void testClassLoader() {
            PersonTest personTest = new PersonTest();
            personTest.start();
      }
      
      //(byte a, short b, int c, long d, double e, float f, boolean g, Object object, Myclass myclass, int[] array)
      private void testDataType() {
            DataType dataType = new DataType();
            byte a = 0x0a;
            short b = 1;
            int c = 2;
            long f = 22;
            double d = 3.0;
            float e = 3.3f;
            boolean g = false;
            Myclass myclass = new Myclass();
            int[] dd = {1, 2};
            dataType.native_byte(a);
            dataType.native_int(c);
            dataType.native_short(b);
            dataType.native_float(e);
            dataType.native_long(f);
            dataType.native_double(d);
            dataType.native_boolean(g);
            dataType.native_arrays(dd);
            
            dataType.native_char('c');
            dataType.native_obj(myclass);
      }
      private void testStr() {
            DynamicReg dynamicReg = new DynamicReg();
            String s = dynamicReg.native_hello();
      }
      
      
      private void testDynamic() {
            DynamicReg dynamicReg = new DynamicReg();
            String s = dynamicReg.native_hello();
            Log.e(TAG, "onCreate: " + s);
            System.out.println("data=== " + s);
            
            int i = dynamicReg.native_add(1, 1);
            System.out.println("add= " + i);
            Log.e(TAG, "onCreate: add=" + i);
      }
}
