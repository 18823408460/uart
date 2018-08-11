package com.uurobot.serialportcompiler.newCode.interfaces;

import com.uurobot.serialportcompiler.newCode.UARTEventListener;
import com.uurobot.serialportcompiler.newCode.UARTManager;
import com.uurobot.serialportcompiler.newCode.pkg.MsgPacket;

/**
 * Created by Administrator on 2018/8/10.
 * <p>
 * 所有的msg 通过Callbacker 发送
 */

public class UARTListenerWrapper implements RequestListener {
      private UARTEventListener mListener;
      private UARTManager mUartManager;
      private Callbacker mCallbacker;
      
      public UARTListenerWrapper(UARTManager uartManager, Callbacker threadHelper, UARTEventListener listener) {
            this.mListener = listener;
            this.mUartManager = uartManager;
            this.mCallbacker = threadHelper;
      }
      
      @Override
      public void onReqeust(MsgPacket packet) {
            if (packet.getMsgType() != MsgPacket.HANDSHAKE_REQ_TYPE) {
                  mCallbacker.notifyRequest(mListener, packet);
            }
            else {
                  mUartManager.reset();
            }
      }
      
}