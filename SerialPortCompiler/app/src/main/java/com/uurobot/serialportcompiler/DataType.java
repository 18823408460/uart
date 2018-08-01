package com.uurobot.serialportcompiler;

import com.uurobot.serialportcompiler.jniTest.Myclass;

/**
 * Created by Administrator on 2018/8/1.
 */

public class DataType {
      static {
            System.loadLibrary("datatypetest");
      }
      
      public native void native_byte(byte a);
      
      public native void native_int(int a);
      
      public native void native_short(short a);
      
      public native void native_long(long a);
      
      public native void native_double(double a);
      
      public native void native_float(float a);
      
      public native void native_boolean(boolean a);
      
      public native void native_char(char a);
      
      public native void native_obj(Myclass a);
      
      public native void native_arrays(int[] a);
      
      public native void native_objs(Object[] a);
}
