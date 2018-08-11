package com.uurobot.serialportcompiler.newCode.pkg;

import java.util.List;

/**
 * Created by Administrator on 2018/8/10.
 */

public class ACKPacket extends MsgPacket {
      
      @Override
      public byte getMsgType() {
            return MsgPacket.ACK_TYPE;
      }
      
      @Override
      protected List<byte[]> getContent() {
            return null;
      }
      
      @Override
      protected void decodeContent(byte[] data) {
      
      }
      
}