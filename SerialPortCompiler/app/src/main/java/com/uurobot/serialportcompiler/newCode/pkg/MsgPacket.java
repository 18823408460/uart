package com.uurobot.serialportcompiler.newCode.pkg;

import com.uurobot.serialportcompiler.newCode.excption.UARTException;
import com.uurobot.serialportcompiler.utils.DataUtils;
import com.uurobot.serialportcompiler.utils.EncodeUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2018/8/10.
 */

public abstract class MsgPacket implements Packet {
      private static int seqIDAchor = 0;
      private int seqID;
      
      public final static byte ACK_TYPE = (byte) 0xff;
      
      protected static final int TYPE_BIT = 2;
      
      public MsgPacket() {
            seqID = seqIDAchor;
            seqIDAchor += 1;
            if (seqIDAchor > 65536) {
                  seqIDAchor = 0;
            }
      }
      
      public static int addId() {
            return seqIDAchor++;
      }
      
      /**
       * 获取消息ID
       *
       * @return
       */
      public int getSeqID() {
            return seqID;
      }
      
      
      /**
       * 设置消息ID
       *
       * @return
       */
      public void setSeqID(int seqID) {
            this.seqID = seqID;
      }
      
      public abstract byte getPkgCmdType();
      
      protected abstract List<byte[]> getContent();
      
      protected abstract void decodeContent(byte[] data);
      
      public boolean isReqType() {
            if (getPkgCmdType() != MsgPacket.ACK_TYPE) {
                  return true;
            }
            else {
                  return false;
            }
            
      }
      
      @Override
      public List<byte[]> encodeBytes() throws UARTException {
            List<byte[]> content = getContent();
            List<byte[]> sendData = new ArrayList<>();
            for (byte[] data : content) {
                  byte[] bytes = EncodeUtil.pkgData(data);
                  sendData.add(bytes);
            }
            return sendData;
      }
      
      @Override
      public Packet decodeBytes(byte[] rawData) throws UARTException {
            int dataLen = DataUtils.getDataLen(rawData[2], rawData[3]);
            byte[] bytes = new byte[dataLen];
            System.arraycopy(rawData,4,bytes,0,dataLen);
            
            //待优化
            decodeContent(rawData);
            return this;
      }
}
