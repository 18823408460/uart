package com.uurobot.serialportcompiler.utils;

/**
 * Created by Administrator on 2018/8/8.
 */

public class UnPkgData {
      private int pkgId;
      private int msgType;
      private String data;
      
      public UnPkgData(int pkgId, int msgType, String data) {
            this.pkgId = pkgId;
            this.msgType = msgType;
            this.data = data;
      }
      
      @Override
      public String toString() {
            return "UnPkgData{" +
                           "pkgId=" + pkgId +
                           ", msgType=" + msgType +
                           ", data='" + data + '\'' +
                           '}';
      }
}
