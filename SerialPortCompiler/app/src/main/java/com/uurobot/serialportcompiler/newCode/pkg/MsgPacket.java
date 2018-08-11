package com.uurobot.serialportcompiler.newCode.pkg;

import com.uurobot.serialportcompiler.newCode.excption.UARTException;

import java.util.List;

/**
 * Created by Administrator on 2018/8/10.
 */

public abstract class MsgPacket implements Packet {
      public final static byte HANDSHAKE_REQ_TYPE = 0x01;
      public final static byte TOUCHUAN_MSG_TYPE = 0x01;
      public final static byte WIFI_CONF_TYPE = 0x02;
      public final static byte AIUI_CONF_TYPE = 0x03;
      public final static byte AIUI_PACKET_TYPE = 0x04;
      public final static byte CTR_PACKET_TYPE = 0x05;
      public final static byte CUSTOM_PACKET_TYPE = 0x2A;
      private static int seqIDAchor = 0;
      protected final static byte[] RESERVED_DATA = new byte[]{DataPacket.SYNC_BYTE, 0x00, 0x00, 0x00};
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
      
      public abstract byte getMsgType();
      
      protected abstract List<byte[]> getContent();
      
      protected abstract void decodeContent(byte[] data);
      
      public boolean isReqType() {
            if (getMsgType() != MsgPacket.ACK_TYPE) {
                  return true;
            }
            else {
                  return false;
            }
            
      }
      
      @Override
      public List<byte[]> encodeBytes() throws UARTException {
            return getContent();
      }
      
      @Override
      public Packet decodeBytes(byte[] rawData) throws UARTException {
            return decodeBytes(rawData);
      }
}
