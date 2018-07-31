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

//                NativeTest1 nativeTest1 = new NativeTest1() ;
//                nativeTest1.sayHello("im android app");
//
//                Log.e("serialport", nativeTest1.getString()) ;
//
//                nativeTest1.typeTest(true,(byte)0xaa,'A',(short) 1,1,1, 1.0f,2.2);
//
//                Log.e("serialport", "add="+nativeTest1.add(3,6 )) ;
//
//
//                Myclass myclass = new Myclass() ;
//                nativeTest1.setClass(myclass);
//                myclass.printName();
//
//               byte[] datas = nativeTest1.testBytes(new byte[]{0x01,0x02,0x03, (byte) 0xaa});
//
//                for (int i=0 ; i<datas.length ; i++){
//                        Log.e("serialport", "activity datas="+datas[i]) ;
//                }


                SerialPortMgr.getInstance();

        }
}
