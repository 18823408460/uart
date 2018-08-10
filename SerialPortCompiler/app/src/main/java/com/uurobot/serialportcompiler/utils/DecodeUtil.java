package com.uurobot.serialportcompiler.utils;

import android.util.SparseArray;

import java.util.HashMap;

/**
 * Created by Administrator on 2018/8/8.
 */

public class DecodeUtil {
      private static HashMap<Integer, PkgData> pkgDataHashMap = new HashMap<>();
      
      /**
       * 是否 是 独立的包
       *
       * @param data
       * @return
       */
      public static boolean isUnPack(byte[] data) {
            return data[9] == EncodeUtil.UnPack;
      }
      
      public static int getMsgType(byte[] data) {
            return DataUtils.getDataLen(data[7], data[8]);
      }
      
      public static int getPkgCount(byte[] data) {
            int count = data[11] & 0xff;
            return count >= 0 ? count : count + 256;
      }
      
      public static int getPkgPos(byte[] data) {
            int count = data[12] & 0xff;
            return count >= 0 ? count : count + 256;
      }
      
      private static int getUnPkgDataLen(byte[] data) {
            return DataUtils.getDataLen(data[2], data[3]) - 6;
      }
      
      private static int getPkgDataLen(byte[] data) {
            return DataUtils.getDataLen(data[2], data[3]) - 9;
      }
      
      private static int getPkgId(byte[] data) {
            return DataUtils.getDataLen(data[5], data[6]);
      }
      
      private static byte[] getUnPkgDataBytes(byte[] data) {
            int unPkgDataLen = getUnPkgDataLen(data);
            byte[] buf = new byte[unPkgDataLen];
            System.arraycopy(data, 10, buf, 0, unPkgDataLen);
            return buf;
      }
      
      private static byte[] getPkgDataBytes(byte[] data) {
            int pkgDataLen = getPkgDataLen(data);
            byte[] buf = new byte[pkgDataLen];
            System.arraycopy(data, 13, buf, 0, pkgDataLen);
            return buf;
      }
      
      public static void parsePkg(byte[] data) {
            boolean unPack = isUnPack(data);
            if (unPack) { //不分包
                  int msgType = getMsgType(data);
                  int pkgId = getPkgId(data);
                  byte[] unPkgDataBytes = getUnPkgDataBytes(data);
                  UnPkgData unPkgData = new UnPkgData(pkgId, msgType, new String(unPkgDataBytes));
                  System.out.println("unPkgData  : " + unPkgData);
            }
            else {
                  System.out.println("----------- is pkg --------------" + DataUtils.bytesToHexString(data));
                  int msgType = getMsgType(data);
                  int pkgCount = getPkgCount(data);
                  int pkgId = getPkgId(data);
                  PkgData pkgData = pkgDataHashMap.get(pkgId);
                  if (pkgData == null) { //从来没有组包
                        PkgData pkgData1 = new PkgData(pkgCount);
                        pkgData1.setMsgType(msgType);
                        int pkgPos = getPkgPos(data);
                        pkgData1.addData(pkgPos, getPkgDataBytes(data));
                        pkgDataHashMap.put(pkgId, pkgData1);
                  }
                  else {
                        int pkgPos = getPkgPos(data);
                        boolean b = pkgData.addData(pkgPos, getPkgDataBytes(data));
                        if (b) {
                              System.out.println("hashmap size 0 === " + pkgDataHashMap.size());
                              pkgDataHashMap.remove(pkgId);
                              System.out.println("hashmap size=== " + pkgDataHashMap.size());
                        }
                  }
            }
      }
      
}
