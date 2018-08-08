package com.uurobot.serialportcompiler.jniTest;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;

import com.uurobot.serialportcompiler.DynamicReg;
import com.uurobot.serialportcompiler.R;
import com.uurobot.serialportcompiler.utils.DataUtils;
import com.uurobot.serialportcompiler.utils.DecodeUtil;
import com.uurobot.serialportcompiler.utils.EncodeUtil;

import java.util.LinkedList;

/**
 * Created by Administrator on 2018/1/19.
 */

public class JNIActivity extends Activity {
      SerialPortMgr serialPortMgr;
      
      @Override
      protected void onCreate(@Nullable Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_main);
            new Handler().postDelayed(new Runnable() {
                  @Override
                  public void run() {
                        testMgr();
                  }
            }, 5000);
            byte[] touchuanData = EncodeUtil.getTouchuanData(11);
            System.out.println(":" + DataUtils.bytesToHexString(touchuanData));
            DecodeUtil.parsePkg(touchuanData);
            
            byte[] touchuanData1 = EncodeUtil.getTouchuanData(12);
            System.out.println(":" + DataUtils.bytesToHexString(touchuanData1));
            DecodeUtil.parsePkg(touchuanData1);
            
            byte[] touchuanData2 = EncodeUtil.getTouchuanData(13);
            System.out.println(":" + DataUtils.bytesToHexString(touchuanData2));
            DecodeUtil.parsePkg(touchuanData2);
            
            byte[] touchuanData3 = EncodeUtil.getUnpackData(13, "ddd");
            System.out.println(":" + DataUtils.bytesToHexString(touchuanData3));
            DecodeUtil.parsePkg(touchuanData3);

//            String msgData = "hello my name is xiaowei, today the weather is good, i am very like today";
            String msgData = "你加什么名字，我的名字叫中国，你的名字呢，很高兴见到你";
            LinkedList<byte[]> hello = EncodeUtil.getTouchuanData(14, msgData);
            System.out.println("size : " + hello.size());
            for (byte[] data : hello) {
                  System.out.println(": " + DataUtils.bytesToHexString(data));
                  DecodeUtil.parsePkg(touchuanData3);
                  DecodeUtil.parsePkg(data);
            }
            System.out.println("data=" + DataUtils.bytesToHexString(msgData.getBytes()));
      }
      
      private void testMgr() {
            serialPortMgr = SerialPortMgr.getInstance();
            serialPortMgr.initTtyDevice();
            serialPortMgr.uartSend(new byte[]{1, 1});
      }
      
      public void send(View v) {
            String msg = "hello world";
            LinkedList<byte[]> hello = EncodeUtil.getTouchuanData(14, msg);
            for (byte[] data : hello) {
                  System.out.println(": " + DataUtils.bytesToHexString(data));
                  serialPortMgr.uartSend(data);
            }
            
      }
      
      private void testStr() {
            DynamicReg dynamicReg = new DynamicReg();
            String s = dynamicReg.native_hello();
      }
}
