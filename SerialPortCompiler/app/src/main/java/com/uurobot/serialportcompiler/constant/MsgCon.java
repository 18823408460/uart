package com.uurobot.serialportcompiler.constant;

/**
 * Created by Administrator on 2018/8/7.
 */
// 协议头(2) + 长度(2) + 数据 + 校验(1)+ 协议尾(2)
// 数据 = cmd + cmd数据
//如果cmd == 透传:
// 透传数据 = 包ID(2)+ msgType(2)+ 是否分包(1) +

/**
 * 1. 不分包: 后面直接是数据
 * 2. 分包：包标识符(1){判断是同一个包}+  分包数量(1)+ 分包偏移量(1)+ 数据
 * (分包时，如果超过1min没有收到完整的包，作废,)
 */
public class MsgCon {

    public static interface Cmd {
        public static final int Power = 0x90;
        public static final int Heart = 0x94;
        public static final int TouChuan = 0x91;
    }

    public static interface Event {
        public static final int Power = 0x90;
        public static final int Heart = 0x94;
    }
}
