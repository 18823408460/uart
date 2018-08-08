package com.uurobot.serialportcompiler.utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

/**
 * Created by Administrator on 2018/8/8.
 */

public class PkgData {
      private int pkgId;
      private int msgType;
      private String data;
      private List<byte[]> list;
      private int index;
      private int pkgLen;
      private int dataLen = 0;
      
      public PkgData(int len) {
            list = new ArrayList<>(len);
            this.pkgLen = len;
            System.out.println("size========= " + list.size());
      }
      
      public boolean addData(int pos, byte[] data) {
            System.out.println("add  : " + pos);
            list.add(pos, data);
            index++;
            dataLen += data.length;
            if (isFull()) {
                  parseData();
                  return true;
            }
            return false;
      }
      
      private boolean isFull() {
            System.out.println("index=" + index + "  pkgLen:" + pkgLen);
            return index == pkgLen;
      }
      
      private void parseData() {
            byte[] bytes = new byte[dataLen];
            int index = 0;
            for (byte[] data : list) { // 待优化
                  for (byte b : data) {
                        bytes[index++] = b;
                  }
            }
            System.out.println("data==== " + new String(bytes));
      }
}
