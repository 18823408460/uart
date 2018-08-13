package com.uurobot.serialportcompiler.newCode.pkg;

import com.uurobot.serialportcompiler.constant.MsgConChest;
import com.uurobot.serialportcompiler.newCode.excption.UARTException;
import com.uurobot.serialportcompiler.utils.DataUtils;
import com.uurobot.serialportcompiler.utils.EncodeUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2018/8/10.
 * <p>
 * 包含完整协议的数据，，，最终发到串口的
 * <p>
 * 协议头 + 长度 + 校验 +  MsgPacket + 协议尾
 */

public class DataPacket implements Packet {
      private static final int defaultLen = 7;
      public MsgPacket data;
      
      @Override
      public List<byte[]> encodeBytes() throws UARTException {
            List<byte[]> listDst = new ArrayList<>();
            List<byte[]> listSrc = data.encodeBytes();
            for (byte[] sendpkg : listSrc) {
                  int length = sendpkg.length;
                  byte[] buf = new byte[length + defaultLen];
                  int index = 0;
                  buf[index++] = MsgConChest.Common.Head_H;
                  buf[index++] = MsgConChest.Common.Head_L;
                  buf[index++] = (byte) ((length >> 8) & 0xff);
                  buf[index++] = (byte) ((length >> 0) & 0xff);
                  System.arraycopy(sendpkg, 0, buf, index, length);
                  index += length;
                  buf[index++] = EncodeUtil.getCheckData(buf);
                  buf[index++] = MsgConChest.Common.Tail_H;
                  buf[index++] = MsgConChest.Common.Tail_L;
                  listDst.add(buf);
            }
            return listDst;
      }
      
      @Override
      public Packet decodeBytes(byte[] rawData) throws UARTException {
            int len = DataUtils.getDataLen(rawData[2], rawData[3]);
            byte[] buff = new byte[len];
            System.arraycopy(rawData, 4, buff, 0, len);
            data = PacketParseUtil.parse(rawData);
            return this;
      }
      
      public static DataPacket buildDataPacket(MsgPacket data) {
            DataPacket ret = new DataPacket();
            ret.data = data;
            return ret;
      }
      
      public static boolean isValid(byte[] data) {
            return checkCode(data, 4, data.length - 4) == data[data.length - 3];
      }
      
      private static byte checkCode(byte[] data, int start, int end) {
            byte sum = 0;
            for (int index = 0; index <= end; index++) {
                  sum += data[index];
            }

//          return (byte) ((~(sum & 0xff) + 1) & 0xff);
            return sum;
      }
}
