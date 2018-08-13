package com.uurobot.serialportcompiler.newCode.pkg.msgPkg;

import android.text.TextUtils;
import android.util.Log;

import com.uurobot.serialportcompiler.constant.MsgConChest;
import com.uurobot.serialportcompiler.newCode.bean.StringMsgBean;
import com.uurobot.serialportcompiler.newCode.pkg.MsgPacket;
import com.uurobot.serialportcompiler.utils.DataUtils;
import com.uurobot.serialportcompiler.utils.DecodeUtil;
import com.uurobot.serialportcompiler.utils.PkgData;
import com.uurobot.serialportcompiler.utils.UnPkgData;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Administrator on 2018/8/11.
 * 透传命令(1) +包ID(2)+ msgType(2)+ 是否分包(1)
 * * 1. 不分包: 后面直接是数据
 * 2. 分包：包标识符(1){判断是同一个包}+  分包数量(1)+ 分包偏移量(1)+ 数据
 * (分包时，如果超过1min没有收到完整的包，作废,)
 */

public class TouChuanPacket extends MsgPacket {
      public static final int Unpack = 0;
      public static final int Pack = 1;
      public static final int SendMaxSize = 10;
      private StringMsgBean stringMsgBean;
      
      public StringMsgBean getStringMsgBean() {
            return stringMsgBean;
      }
      
      public void setStringMsgBean(StringMsgBean stringMsgBean) {
            this.stringMsgBean = stringMsgBean;
      }
      
      @Override
      public byte getPkgCmdType() {
            return MsgConChest.Event.TouChuan;
      }
      
      //编码时使用
      @Override
      protected List<byte[]> getContent() {
            List<byte[]> listDst = new ArrayList<>();
            String msgData = stringMsgBean.getMsgData();
            int msgType = stringMsgBean.getMsgType();
            int seqID = getSeqID();
            if (TextUtils.isEmpty(msgData)) {
                  byte[] buf = getUnPackBuf(msgType, seqID);
                  listDst.add(buf);
            }
            else {
                  byte[] contentBytes = msgData.getBytes();
                  if (contentBytes.length > SendMaxSize) {
                        int length = contentBytes.length;
                        int packCount = length / SendMaxSize;
                        int packCountLast = length % SendMaxSize;
                        if (packCountLast != 0) { //不可以整除
                              Log.e("UartTestActivity", "getContent: ------------------packCountLast=" + packCountLast);
                              int packCountAll = packCount + 1;
                              byte[] bufTemp1 = new byte[SendMaxSize];
                              for (int i = 0; i < packCount; i++) {
                                    System.arraycopy(contentBytes, i * SendMaxSize, bufTemp1, 0, SendMaxSize);
                                    listDst.add(getPackBuf(msgType, MsgPacket.addId(), seqID, packCountAll, i, bufTemp1));
                              }
                              byte[] bufTemp2 = new byte[packCountLast];
                              System.arraycopy(contentBytes, packCount * SendMaxSize, bufTemp2, 0, packCountLast);
                              listDst.add(getPackBuf(msgType, MsgPacket.addId(), seqID, packCountAll, packCount, bufTemp2));
                        }
                        else {// 可以整除
                              byte[] bufTemp1 = new byte[SendMaxSize];
                              for (int i = 0; i < packCount; i++) {
                                    System.arraycopy(contentBytes, i * SendMaxSize, bufTemp1, 0, SendMaxSize);
                                    listDst.add(getPackBuf(msgType, MsgPacket.addId(), seqID, packCount, i, bufTemp1));
                              }
                        }
                  }
                  else {
                        byte[] unPackBuf = getUnPackBuf(msgType, seqID, contentBytes);
                        listDst.add(unPackBuf);
                  }
            }
            return listDst;
      }
      
      private static byte[] getUnPackBuf(int msgType, int seqID) {
            byte[] buf = new byte[6];
            int index = 0;
            buf[index++] = MsgConChest.Cmd.TouChuan;
            buf[index++] = (byte) ((seqID >> 8) & 0xff);
            buf[index++] = (byte) ((seqID) & 0xff);
            buf[index++] = (byte) ((msgType >> 8) & 0xff);
            buf[index++] = (byte) ((msgType) & 0xff);
            buf[index++] = Unpack;
            return buf;
      }
      
      private static byte[] getUnPackBuf(int msgType, int seqID, byte[] msgData) {
            int len = msgData.length;
            byte[] buf = new byte[6 + len];
            int index = 0;
            buf[index++] = MsgConChest.Cmd.TouChuan;
            buf[index++] = (byte) ((seqID >> 8) & 0xff);
            buf[index++] = (byte) ((seqID) & 0xff);
            buf[index++] = (byte) ((msgType >> 8) & 0xff);
            buf[index++] = (byte) ((msgType) & 0xff);
            buf[index++] = Unpack;
            System.arraycopy(msgData, 0, buf, index, len);
            return buf;
      }
      
      private static byte[] getPackBuf(int msgType, int pkgId, int seqID, int pkgAll, int pkgPos, byte[] msgData) {
            int len = msgData.length;
            byte[] buf = new byte[9 + len];
            int index = 0;
            buf[index++] = MsgConChest.Cmd.TouChuan;
            buf[index++] = (byte) ((pkgId >> 8) & 0xff);
            buf[index++] = (byte) ((pkgId) & 0xff);
            buf[index++] = (byte) ((msgType >> 8) & 0xff);
            buf[index++] = (byte) ((msgType) & 0xff);
            buf[index++] = Pack;
            buf[index++] = (byte) seqID;
            buf[index++] = (byte) pkgAll;
            buf[index++] = (byte) pkgPos;
            System.arraycopy(msgData, 0, buf, index, len);
            return buf;
      }
      
      @Override
      protected void decodeContent(byte[] data) {
            stringMsgBean = DecodeUtil.parsePkg(data);
      }
}
