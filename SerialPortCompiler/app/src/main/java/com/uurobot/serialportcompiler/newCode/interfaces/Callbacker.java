package com.uurobot.serialportcompiler.newCode.interfaces;

import com.uurobot.serialportcompiler.newCode.UARTEvent;
import com.uurobot.serialportcompiler.newCode.UARTEventListener;
import com.uurobot.serialportcompiler.newCode.pkg.MsgPacket;

import java.util.EventListener;

/**
 * Created by Administrator on 2018/8/10.
 */

public interface Callbacker {
      void notifyRequest(UARTEventListener listener, MsgPacket packet);
      
      void notifyEvent(UARTEventListener listener, UARTEvent event);
      
      void destroy();
}
