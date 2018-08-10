package com.uurobot.serialportcompiler.newCode.interfaces;

import com.uurobot.serialportcompiler.newCode.pkg.MsgPacket;

/**
 * Created by Administrator on 2018/8/10.
 */

public interface RequestListener {
      void onReqeust(MsgPacket packet);
}
