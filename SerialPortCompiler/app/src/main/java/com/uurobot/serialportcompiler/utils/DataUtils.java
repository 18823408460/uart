package com.uurobot.serialportcompiler.utils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

/**
 * Created by Administrator on 2018/8/7.
 */

public class DataUtils {
      public static String bytesToHexString(byte src) {
            return Integer.toHexString(src);
      }
      /**
       * 解压数据
       * @param packet 待解压数据
       * @return 解压后数据
       */
      public static byte[] unCompress(byte[] packet) {
            if (null != packet) {
                  GZIPInputStream gzipIn = null;
                  ByteArrayInputStream byteArrayInputStream = null;
                  ByteArrayOutputStream byteArrayOutputStream = null;
                  try {
                        byteArrayInputStream = new ByteArrayInputStream(packet);
                        gzipIn = new GZIPInputStream(byteArrayInputStream);
                        byteArrayOutputStream = new ByteArrayOutputStream();
                        byte[] buffer = new byte[2048];
                        int length = -1;
                        while (-1 !=(length = gzipIn.read(buffer, 0, buffer.length))) {
                              byteArrayOutputStream.write(buffer, 0, length);
                        }
                        return byteArrayOutputStream.toByteArray();
                  } catch (IOException e) {
                        e.printStackTrace();
                  }
                  finally {
                        try {
                              byteArrayInputStream.close();
                              byteArrayOutputStream.close();
                              gzipIn.close();
                        } catch (IOException e) {
                              e.printStackTrace();
                        }
                  }
            }
            return null;
      }
      
      public static byte[] compress(byte[] packet) {
            if (null != packet) {
                  ByteArrayInputStream byteArrayInputStream = null;
                  ByteArrayOutputStream byteArrayOutputStream = null;
                  GZIPOutputStream gzipOut = null;
                  try {
                        byteArrayInputStream = new ByteArrayInputStream(packet);
                        byteArrayOutputStream = new ByteArrayOutputStream();
                        gzipOut = new GZIPOutputStream(byteArrayOutputStream);
                        byte[] buffer = new byte[2048];
                        int length = -1;
                        while (-1 != (length = byteArrayInputStream.read(buffer, 0, buffer.length))) {
                              gzipOut.write(buffer, 0, length);
                        }
                        gzipOut.finish();
                        return byteArrayOutputStream.toByteArray();
                  } catch (IOException e) {
                        e.printStackTrace();
                  }
                  finally {
                        try {
                              byteArrayInputStream.close();
                              byteArrayOutputStream.close();
                              gzipOut.close();
                        } catch (IOException e) {
                              e.printStackTrace();
                        }
                  }
            }
            return null;
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
