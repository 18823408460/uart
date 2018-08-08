package com.uurobot.serialportcompiler.utils;

import com.uurobot.serialportcompiler.constant.MsgConChest;

import java.util.LinkedList;

/**
 * Created by Administrator on 2018/8/8.
 */

public class EncodeUtil {
      public static final int UnPack = 0; //不分包
      public static final int Pack = 1; // 分包
      private static int pkgId = 0;
      private static final int defaultPkgSize = 10;
      
      
      private static int getPkgId() {
            return pkgId++ % Integer.MAX_VALUE;
      }
      
      public static byte[] getTouchuanData(int msgType) {
            int index = 0;
            int dataLen = 6;
            int pkgLen = dataLen + 7;
            byte[] buf = new byte[pkgLen];
            buf[index++] = MsgConChest.Common.Head_H;
            buf[index++] = MsgConChest.Common.Head_L;
            buf[index++] = (byte) ((dataLen >> 8) & 0xff);
            buf[index++] = (byte) (dataLen & 0xff);
            buf[index++] = MsgConChest.Cmd.TouChuan;
            int pkgId = getPkgId();
            buf[index++] = (byte) ((pkgId >> 8) & 0xff);
            buf[index++] = (byte) (pkgId & 0xff);
            buf[index++] = (byte) ((msgType >> 8) & 0xff);
            buf[index++] = (byte) (msgType & 0xff);
            buf[index++] = UnPack; //0 表示不分包
            buf[index++] = getCheckData(buf);
            buf[index++] = MsgConChest.Common.Tail_H;
            buf[index++] = MsgConChest.Common.Tail_L;
            return buf;
      }
      
      //不分包数据
      public static byte[] getUnpackData(int msgType, String msgData) {
            byte[] content = msgData.getBytes();
            int index = 0;
            int contentLen = content.length;
            int dataLen = 6 + contentLen;
            int pkgLen = dataLen + 7;
            byte[] buf = new byte[pkgLen];
            buf[index++] = MsgConChest.Common.Head_H;
            buf[index++] = MsgConChest.Common.Head_L;
            buf[index++] = (byte) ((dataLen >> 8) & 0xff);
            buf[index++] = (byte) (dataLen & 0xff);
            buf[index++] = MsgConChest.Cmd.TouChuan;
            int pkgId = getPkgId();
            buf[index++] = (byte) ((pkgId >> 8) & 0xff);
            buf[index++] = (byte) (pkgId & 0xff);
            buf[index++] = (byte) ((msgType >> 8) & 0xff);
            buf[index++] = (byte) (msgType & 0xff);
            buf[index++] = UnPack; //0 表示不分包
            for (int i = 0; i < contentLen; i++) {
                  buf[index++] = content[i];
            }
            buf[index++] = getCheckData(buf);
            buf[index++] = MsgConChest.Common.Tail_H;
            buf[index++] = MsgConChest.Common.Tail_L;
            return buf;
      }
      
      //分包数据
      private static byte[] getPackData(int msgType, byte[] msgData, byte pkgFlag, byte pkgAll, byte pkgPos) {
            int index = 0;
            int contentLen = msgData.length;
            int dataLen = 9 + contentLen;
            int pkgLen = dataLen + 7;
            byte[] buf = new byte[pkgLen];
            buf[index++] = MsgConChest.Common.Head_H;
            buf[index++] = MsgConChest.Common.Head_L;
            buf[index++] = (byte) ((dataLen >> 8) & 0xff);
            buf[index++] = (byte) (dataLen & 0xff);
            buf[index++] = MsgConChest.Cmd.TouChuan;
            int pkgId = getPkgId();
            buf[index++] = (byte) ((pkgId >> 8) & 0xff);
            buf[index++] = (byte) (pkgId & 0xff);
            buf[index++] = (byte) ((msgType >> 8) & 0xff);
            buf[index++] = (byte) (msgType & 0xff);
            buf[index++] = Pack; //1 表示分包
            buf[index++] = pkgFlag; //
            buf[index++] = pkgAll; //
            buf[index++] = pkgPos; //
            for (int i = 0; i < contentLen; i++) {
                  buf[index++] = msgData[i];
            }
            buf[index++] = getCheckData(buf);
            buf[index++] = MsgConChest.Common.Tail_H;
            buf[index++] = MsgConChest.Common.Tail_L;
            return buf;
      }
      
      public static LinkedList<byte[]> getTouchuanData(int msgType, String msgData) {
            LinkedList<byte[]> linkedList = new LinkedList<>();
            if (msgData == null) {
                  linkedList.add(getTouchuanData(msgType));
            }
            else {
                  if (needUnpack(msgData)) { //需要分包
                        byte[] contentBytes = msgData.getBytes();
                        int length = contentBytes.length;
                        int packCount = length / defaultPkgSize;
                        int packCountLast = length % defaultPkgSize;
                        if (packCountLast != 0) { //不可以整除
                              int packCountAll = packCount + 1;
                              byte[] buf = new byte[defaultPkgSize];
                              int pkgId = getPkgId();
                              for (int i = 0; i < packCount; i++) {
                                    System.arraycopy(contentBytes, i * defaultPkgSize, buf, 0, defaultPkgSize);
                                    byte[] packData = getPackData(msgType, buf, (byte) pkgId, (byte) packCountAll, (byte) i);
                                    linkedList.add(packData);
                              }
                              byte[] buf2 = new byte[packCountLast];
                              System.arraycopy(contentBytes, packCount * defaultPkgSize, buf2, 0, packCountLast);
                              byte[] packData = getPackData(msgType, buf2, (byte) pkgId, (byte) packCountAll, (byte) packCount);
                              linkedList.add(packData);
                        }
                        else {// 可以整除
                              byte[] buf = new byte[defaultPkgSize];
                              int pkgId = getPkgId();
                              for (int i = 0; i < packCount; i++) {
                                    System.arraycopy(contentBytes, i * defaultPkgSize, buf, 0, defaultPkgSize);
                                    byte[] packData = getPackData(msgType, buf, (byte) pkgId, (byte) packCount, (byte) i);
                                    linkedList.add(packData);
                              }
                        }
                  }
                  else {// 不需要分包
                        byte[] unpackData = getUnpackData(msgType, msgData);
                        linkedList.add(unpackData);
                  }
            }
            return linkedList;
      }
      
      private static boolean needUnpack(String data) {
            if (data == null) {
                  return false;
            }
            byte[] bytes = data.getBytes();
            if (bytes.length < defaultPkgSize) {
                  return false;
            }
            return true;
      }
      
      private static byte getCheckData(byte[] data) {
            byte sum = 0;
            int checkLen = data.length - 3;
            for (int i = 4; i < checkLen; i++) {
                  sum += data[i];
            }
            return sum;
      }
}
