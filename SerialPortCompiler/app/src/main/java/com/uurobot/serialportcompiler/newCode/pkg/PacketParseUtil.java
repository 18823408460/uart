package com.uurobot.serialportcompiler.newCode.pkg;

import com.uurobot.serialportcompiler.constant.MsgConChest;
import com.uurobot.serialportcompiler.newCode.excption.UARTException;
import com.uurobot.serialportcompiler.newCode.pkg.msgPkg.TouChuanPacket;

/**
 * Created by Administrator on 2018/8/10.
 */

public class PacketParseUtil {
      public static MsgPacket parse(byte[] rawData) throws UARTException {
            int packetType = rawData[0] & 0xff;
            switch (packetType) {
                  case MsgConChest.Event.TouChuan:
                        return (MsgPacket) new TouChuanPacket().decodeBytes(rawData);
            }
           /* switch (packetType) {
                  case MsgPacket.AIUI_PACKET_TYPE:
                        return (MsgPacket)new AIUIPacket().decodeBytes(rawData);
                  case MsgPacket.HANDSHAKE_REQ_TYPE:
                        return (MsgPacket)new HandShakePacket().decodeBytes(rawData);
                  case MsgPacket.WIFI_CONF_TYPE:
                        return (MsgPacket)new WIFIConfPacket().decodeBytes(rawData);
                  case MsgPacket.AIUI_CONF_TYPE:
                        return (MsgPacket)new AIUIConfPacket().decodeBytes(rawData);
                  case MsgPacket.CTR_PACKET_TYPE:
                        return (MsgPacket)new ControlPacket().decodeBytes(rawData);
                  case MsgPacket.ACK_TYPE:
                        return (MsgPacket)new ACKPacket().decodeBytes(rawData);
                  case MsgPacket.CUSTOM_PACKET_TYPE:
                        return (MsgPacket)new CustomPacket().decodeBytes(rawData);
                  default:
                        return null;
            }*/
            return null;
      }
      
      //FIXME 只返回对应类型 msgPacket
      public static DataPacket getAckMsg(MsgPacket packet) {
            MsgPacket ackPacket = new ACKPacket();
            ackPacket.setSeqID(packet.getSeqID());
            
            return DataPacket.buildDataPacket(ackPacket);
      }
      
      public static boolean isMatchAck(MsgPacket req, MsgPacket ack) {
            return req.getSeqID() == ack.getSeqID();
      }
}
