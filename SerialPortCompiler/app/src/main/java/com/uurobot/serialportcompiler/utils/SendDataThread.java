package com.uurobot.serialportcompiler.utils;

import android.os.Handler;
import android.os.HandlerThread;
import android.text.TextUtils;

import com.uurobot.serialportcompiler.jniTest.SerialPortMgr;

/**
 * Created by Administrator on 2018/8/7.
 * 消息最终通过消息队列发送
 * <p>
 * 1.原始消息 ---> 转换成要发送的消息(可能会分包) ---> 添加到消息队列-->每隔30ms发送一个包
 * --> 等待回复
 * 2.
 */
public class SendDataThread {
      private SerialPortMgr serialPortMgr;
      private Handler handler;
      
      public SendDataThread() {
            HandlerThread handlerThread = new HandlerThread("sendDataThread");
            handlerThread.start();
            handler = new Handler(handlerThread.getLooper());
      }
      
      /**
       * 发送透传数据
       *
       * @param pkg
       */
      public void sendTouChuanData(TouchuanPkg pkg) {
      
      }
      
      private void pullData(TouchuanPkg pkg) {
      
      }
}
