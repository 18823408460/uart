package com.uurobot.serialportcompiler.utils;

import android.util.Log;
import android.util.SparseArray;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2018/8/8.
 */

public class PkgData {
      private int pkgId;
      private int msgType;
      private String data;
      private SparseArray<byte[]> list;
      private int index;
      private int pkgLen;
      private int dataLen = 0;
      
      public void setPkgId(int pkgId) {
            this.pkgId = pkgId;
      }
      
      public void setMsgType(int msgType) {
            this.msgType = msgType;
      }
      
      public PkgData(int len) {
            list = new SparseArray<>(len);
            this.pkgLen = len;
            Log.e("DecodeUtil", "size========= " + list.size() + "   code=" + hashCode());
            
      }
      
      public int getMsgType() {
            return msgType;
      }
      
      public String getData() {
            return data;
      }
      
      public boolean addData(int pos, byte[] data) {
            Log.e("DecodeUtil", "add  : " + pos + "   code=" + hashCode());
            byte[] bytes = list.get(pos);
            if (bytes == null) {
                  list.put(pos, data);
                  index++;
                  dataLen += data.length;
                  if (isFull()) {
                        parseData();
                        return true;
                  }
            }
            
            return false;
      }
      
      private boolean isFull() {
            Log.e("DecodeUtil", "index=" + index + "  pkgLen:" + pkgLen + "   code=" + hashCode());
            return index == pkgLen;
      }
      
      private void parseData() {
            byte[] bytes = new byte[dataLen];
            int index = 0;
            for (int i = 0; i < pkgLen; i++) {
                  byte[] bytes1 = list.get(i);
                  for (byte b : bytes1) {
                        bytes[index++] = b;
                  }
            }
            Log.e("DecodeUtil", "data==== " + new String(bytes) + "   code=" + hashCode());
      }
}
