package com.uurobot.serialportcompiler.utils;

/**
 * Created by Administrator on 2018/8/7.
 */

public class DataUtils {
      public static String bytesToHexString(byte src) {
            return Integer.toHexString(src);
      }
      
      public static String bytesToHexString(byte[] src, int len) {
            StringBuilder stringBuilder = new StringBuilder("");
            if (src == null || src.length <= 0) {
                  return null;
            }
            for (int i = 0; i < len; i++) {
                  int v = src[i] & 0xFF;
                  String hv = Integer.toHexString(v);
                  stringBuilder.append("0x");
                  if (hv.length() < 2) {
                        stringBuilder.append(0);
                  }
                  stringBuilder.append(hv);
                  stringBuilder.append(", ");
            }
            return stringBuilder.toString();
      }
      
      public static String bytesToHexString(byte[] src) {
            StringBuilder stringBuilder = new StringBuilder("");
            if (src == null || src.length <= 0) {
                  return null;
            }
            for (int i = 0; i < src.length; i++) {
                  int v = src[i] & 0xFF;
                  String hv = Integer.toHexString(v);
                  stringBuilder.append("0x");
                  if (hv.length() < 2) {
                        stringBuilder.append(0);
                  }
                  stringBuilder.append(hv);
                  stringBuilder.append(", ");
            }
            return stringBuilder.toString();
      }
      
      public static int getDataLen(byte hight, byte low) {
            int iHight = hight & 0xff;
            int iLow = low & 0xff;
            return iHight * 256 + iLow;
      }
}
