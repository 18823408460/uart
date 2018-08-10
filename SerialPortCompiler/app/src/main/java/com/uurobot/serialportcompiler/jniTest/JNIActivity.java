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

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.util.LinkedList;

import android_serialport_api.SerialPort;

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
                        try {
                              testMgrOld();
                        }
                        catch (IOException e) {
                              e.printStackTrace();
                        }
                  }
            }, 5000);
      }
      
      int msgtype = 14 ;
      private void testMgrOld() throws IOException {
            final String msg = "hello world今天的天气的怎么样，今天的天气是非常的好，我是非常的喜欢，但是如果我们真的很好很好，世界就是很好很好,如果明天的天气" +
                                       "很好，我就去周游世界，哇哇，非常非常的期待啊，只要你开心，世界就是美好的，明天就是晴天；2我就去周游世界，哇哇，非常非常的期待啊，" +
                                       "只要你开心，世界就是美好的，明天就是晴天；3我就去周游世界，哇哇，非常非常的期待啊，只要你开心，世界就是美好的，明天就是晴天";
            final Handler handler = new Handler();
            SerialPort mSerialPortFpga = new SerialPort(new File("/dev/ttyS3"), 115200, 0);
            final OutputStream mOutputStreamFpga = mSerialPortFpga.getOutputStream();
            
            /*handler.post(new Runnable() {
                  @Override
                  public void run() {
                        LinkedList<byte[]> hello = EncodeUtil.getTouchuanData(msgtype++, msg);
                        for (byte[] data : hello) {
                              try {
                                    mOutputStreamFpga.write(data);
                                    System.out.println(": " + DataUtils.bytesToHexString(data));
                                    byte[] touchuanData3 = EncodeUtil.getUnpackData(msgtype++, "ddd");
                                    mOutputStreamFpga.write(touchuanData3);
                              }
                              catch (IOException e) {
                                    e.printStackTrace();
                              }
                        }
                        handler.postDelayed(this, 1000);
                  }
            });*/
      }
      
      private void sendTest() {
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
            /*serialPortMgr = SerialPortMgr.getInstance();
            serialPortMgr.initTtyDevice();
            serialPortMgr.uartSend(new byte[]{1, 1});*/
      }
      
      public void send(View v) {
            final String msg = "hello world今天的天气的怎么样，今天的天气是非常的好，我是非常的喜欢，但是如果我们真的很好很好，世界就是很好很好,如果明天的天气" +
                                       "很好，我就去周游世界，哇哇，非常非常的期待啊，只要你开心，世界就是美好的，明天就是晴天；2我就去周游世界，哇哇，非常非常的期待啊，" +
                                       "只要你开心，世界就是美好的，明天就是晴天；3我就去周游世界，哇哇，非常非常的期待啊，只要你开心，世界就是美好的，明天就是晴天";
            /*final Handler handler = new Handler();
            handler.post(new Runnable() {
                  @Override
                  public void run() {
                        LinkedList<byte[]> hello = EncodeUtil.getTouchuanData(14, msg);
                        for (byte[] data : hello) {
                              System.out.println(": " + DataUtils.bytesToHexString(data));
                              serialPortMgr.uartSend(data);
                        }
                        handler.postDelayed(this,1000);
                  }
            });*/
      }
      
      private void testStr() {
            DynamicReg dynamicReg = new DynamicReg();
            String s = dynamicReg.native_hello();
      }
}
