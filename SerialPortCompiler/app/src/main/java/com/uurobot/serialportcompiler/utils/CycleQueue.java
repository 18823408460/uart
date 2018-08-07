package com.uurobot.serialportcompiler.utils;

import android.util.Log;

/**
 * Created by Administrator on 2018/8/7.
 */

public class CycleQueue {
      private final int bufSize = 1024;
      private int beyondSize = 0;
      private int readHeadH = 0;
      private int readHeadL = 0;
      private int readLenH = 0;
      private int readLenL = 0;
      private int outBufIndex = 0;
      private byte outBuf[] = new byte[512];
      private byte cacheBuf[] = new byte[bufSize];
      private int tailIndex = 0;
      private int headIndex = 0;
      private int dataLenH, dataLenL, dataLen;
      
      private int cacheHaveData() {
            int result = 0;
            if (beyondSize == 0) {
                  if (headIndex < tailIndex) {
                        result = 1;
                  }
            }
            else {
                  if (headIndex < bufSize) {
                        result = 1;
                  }
                  else {
                        beyondSize = 0;
                        headIndex = 0;
                        if (headIndex < tailIndex) {
                              result = 1;
                        }
                  }
            }
            return result;
      }
      
      public  void copyData(int len, byte buf[]) {
            int tailCanWrite = bufSize - tailIndex; //剩余的可写
            int i;
            if (len < tailCanWrite) { //如果可以装
                  for (i = 0; i < len; ++i) {
                        cacheBuf[tailIndex++] = buf[i];
                  }
            }
            else { // 如果装不下
                  int headCanWrite = len - tailCanWrite; // 头部需要写
                  for (i = 0; i < tailCanWrite; i++) {
                        cacheBuf[tailIndex++] = buf[i];
                  }
                  beyondSize = 1;
                  tailIndex = 0;
                  for (; i < len; i++) {
                        cacheBuf[tailIndex++] = buf[i];
                  }
            }
      }
      
      int getIndex() {
            headIndex++;
            if (headIndex >= bufSize) {
                  headIndex = 0;
            }
            return headIndex;
      }
      
      public  void parseData() {
            int canReadData;
            if (tailIndex > headIndex) {
                  canReadData = tailIndex - headIndex;
            }
            else {
                  int tailLast = bufSize - headIndex;
                  canReadData = tailIndex + tailLast;
            }
            //Log.e(TAG, "parseData: canreadData len = " + canReadData + "  tailIndex =" + tailIndex + "   headIndex=" + headIndex);
            while (canReadData-- > 0) {
                  if (readHeadH == 0) {
                        if (cacheBuf[getIndex()] == 0x08) {
                              readHeadH = 1;
                              outBuf[outBufIndex++] = 0x08;
                              Log.e(TAG, "parseData: read head h =================== ");
                              continue;
                        }
                  }
                  if (readHeadH == 1 && readHeadL == 0) {
                        if (cacheBuf[getIndex()] == 0x06) {
                              readHeadL = 1;
                              outBuf[outBufIndex++] = 0x06;
                              Log.e(TAG, "parseData: read head l =================");
                              continue;
                        }
                  }
                  if (readHeadL == 1 && readHeadH == 1) {
                        if (readLenH == 0) {
                              readLenH = 1;
                              dataLenH = cacheBuf[getIndex()];
                              outBuf[outBufIndex++] = (byte) dataLenH;
                              Log.e(TAG, "parseData: read dataLenH  =================" + DataUtils.bytesToHexString((byte) dataLenH));
                              continue;
                        }
                        if (readLenH == 1 && readLenL == 0) {
                              readLenL = 1;
                              dataLenL = cacheBuf[getIndex()];
                              dataLen = dataLenH * 256 + dataLenL;
                              outBuf[outBufIndex++] = (byte) dataLenL;
                              //Log.e(TAG, "parseData: read dataLenL  =================" + DataUtils.bytesToHexString((byte) dataLenL) + "   len=" + dataLenL);
                              continue;
                        }
                        if (readLenH == 1 && readLenL == 1) {
                              if (outBufIndex < (dataLen + 7)) {
                                    outBuf[outBufIndex++] = cacheBuf[getIndex()];
                                    continue;
                              }
                              else {
                                    Log.e(TAG, "parseData: data=" + DataUtils.bytesToHexString(outBuf, dataLen + 7));
                                    dataLen = 0;
                                    readHeadH = 0;
                                    readHeadL = 0;
                                    readLenH = 0;
                                    readLenL = 0;
                                    outBufIndex = 0;
                                    dataLenH = 0;
                                    dataLenL = 0;
                                    break;
                              }
                        }
                  }
            }
      }
      
      private static final String TAG = "serialportCycleQueue";
}
