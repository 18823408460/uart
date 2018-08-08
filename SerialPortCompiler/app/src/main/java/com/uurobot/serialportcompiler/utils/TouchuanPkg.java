package com.uurobot.serialportcompiler.utils;

import android.text.TextUtils;

import java.util.LinkedList;

/**
 * Created by Administrator on 2018/8/7.
 */

public class TouchuanPkg {
      private int msgType;
      private String msgData;
      
      private LinkedList<byte[]> linkedListData;
      
      public TouchuanPkg(int msgType, String msgData) {
            this.msgType = msgType;
            this.msgData = msgData;
            linkedListData = new LinkedList<>();
            packetData();
      }
      
      private void packetData() {
            if (TextUtils.isEmpty(msgData)) {
            
            }
            else {
            
            }
      }
}
