package com.uurobot.serialportcompiler.newCode.pkg;

import com.uurobot.serialportcompiler.newCode.excption.UARTException;

/**
 * Created by Administrator on 2018/8/10.
 */

public class DataPacket implements Packet {
      public MsgPacket data;
      protected final static byte SYNC_BYTE = (byte) 0xa5;
      @Override
      public byte[] encodeBytes() throws UARTException {
            return new byte[0];
      }
      
      @Override
      public Packet decodeBytes(byte[] rawData) throws UARTException {
            return null;
      }
      
      public static DataPacket buildDataPacket(MsgPacket data) {
            DataPacket ret = new DataPacket();
            ret.data = data;
            return ret;
      }
      public static boolean isValid(byte[] data) {
            return checkCode(data, 0, data.length - 2) == data[data.length - 1];
      }
      
      private static byte checkCode(byte[] data, int start, int end) {
            byte sum = 0;
            for (int index = 0; index <= end; index++) {
                  sum += data[index];
            }
            
            return (byte) ((~(sum & 0xff) + 1) & 0xff);
      }
}
