package com.uurobot.serialportcompiler.constant;

/**
 * Created by Administrator on 2018/8/7.
 */
// 协议头(2) + 长度(2) + 数据 + 校验(1)+ 协议尾(2)
// 数据 = cmd + cmd数据
//如果cmd == 透传:
// 透传cmd数据 = 包ID(2)+ msgType(2)+ 是否分包(1) +

/**
 * 1. 不分包: 后面直接是数据
 * 2. 分包：包标识符(1){判断是同一个包}+  分包数量(1)+ 分包偏移量(1)+ 数据
 * (分包时，如果超过1min没有收到完整的包，作废,)
 */
public class MsgConChest {
      public static class Common {
            public static final int Head_H = 0x08;
            public static final int Head_L = 0x06;
            public static final int Tail_H = 0x0d;
            public static final int Tail_L = 0x0a;
      }
      
      public static class Cmd {
            public static final int Power = 0x90;
            public static final int Heart = 0x94;
            public static final int TouChuan = 0x13;
            public static final int ActionLibCtr = 0x0D; // 动作库控制
      }
      
      public static class Event {
            public static final byte Power = (byte) 0x90;
            public static final byte Heart = (byte) 0x94;
            public static final byte TouChuan = (byte) 0x84;
      }
}
