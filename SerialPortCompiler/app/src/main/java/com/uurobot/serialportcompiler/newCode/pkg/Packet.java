package com.uurobot.serialportcompiler.newCode.pkg;

import com.uurobot.serialportcompiler.newCode.excption.UARTException;

/**
 * Created by Administrator on 2018/8/10.
 */

public interface Packet {
      /**
       * 数据包编码成byte数组
       *
       * @return
       */
      public abstract byte[] encodeBytes() throws UARTException;
      
      /**
       * 从byte数组解码数据包
       *
       * @param rawData 原始byte数组
       * @return 解码包
       */
      public abstract Packet decodeBytes(byte[] rawData) throws UARTException;
}
