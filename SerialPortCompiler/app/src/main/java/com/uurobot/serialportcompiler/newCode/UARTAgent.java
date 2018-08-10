package com.uurobot.serialportcompiler.newCode;

import com.uurobot.serialportcompiler.newCode.bean.UARTConstant;
import com.uurobot.serialportcompiler.newCode.impl.ExecutorCallbacker;
import com.uurobot.serialportcompiler.newCode.interfaces.ActionListener;
import com.uurobot.serialportcompiler.newCode.interfaces.Callbacker;
import com.uurobot.serialportcompiler.newCode.interfaces.UARTListenerWrapper;
import com.uurobot.serialportcompiler.newCode.pkg.DataPacket;
import com.uurobot.serialportcompiler.newCode.pkg.PacketBuilder;

import java.util.EventListener;

/**
 * Created by Administrator on 2018/8/10.
 */

public class UARTAgent {
      private static UARTAgent sInstance;
      private UARTManager mUARTManager;
      private Callbacker mCallbacker;
      private UARTEventListener mEventListener;
      private static final int MAX_FAILED_COUNT = 5;
      private static final int STATUS_OK = 0;
      private static final int STATUS_HANDSHAKING = 1;
      private static final int STATUS_FAILED = 2;
      private int mStatus = STATUS_FAILED;
      
      private UARTAgent() {
      
      }
      
      public static UARTAgent createAgent(String device, int speed, UARTEventListener listener) {
            if (sInstance == null) {
                  sInstance = new UARTAgent(device, speed, listener);
            }
            return sInstance;
      }
      
      public static UARTAgent getUARTAgent() {
            if (sInstance == null) {
                  throw new IllegalStateException("Please invoke createAgent firstly");
            }
            
            return sInstance;
      }
      
      private UARTAgent(String device, int speed, UARTEventListener listener) {
            mUARTManager = UARTManager.getManager();
            mCallbacker = new ExecutorCallbacker("Uart_AIUI_Thread");
            mEventListener = listener;
            mUARTManager.setmRequestListener(new UARTListenerWrapper(mUARTManager, mCallbacker, mEventListener));
            mUARTManager.init(device, speed, new ActionListener() {
                  @Override
                  public void onSuccess() {
                        handShake(new ActionListener() {
                        
                              @Override
                              public void onSuccess() {
                                    notifyEvent(new UARTEvent(UARTConstant.EVENT_INIT_SUCCESS));
                              }
                        
                              @Override
                              public void onFailed(int errorCode) {
                                    notifyEvent(new UARTEvent(UARTConstant.EVENT_INIT_FAILED));
                              }
                        });
                  }
            
                  @Override
                  public void onFailed(int errorCode) {
                        notifyEvent(new UARTEvent(UARTConstant.EVENT_INIT_FAILED));
                  }
            });
      }
      
      private void handShake(final ActionListener listener) {
            mStatus = STATUS_HANDSHAKING;
            final DataPacket handshakePacket = DataPacket.buildDataPacket(PacketBuilder.obtainHandShakeMsg());
            sendHandshake(new ActionListener() {
                  @Override
                  public void onSuccess() {
                        if(listener != null){
                              listener.onSuccess();
                        }
                        mStatus = STATUS_OK;
                  }
                  
                  @Override
                  public void onFailed(int errorCode) {
                        try {
                              Thread.sleep(100);
                        } catch (InterruptedException e) {
                              // ignore
                        }
                        sendHandshake(this, handshakePacket);
                  }
            }, handshakePacket);
      }
      private void sendHandshake(final ActionListener listener, DataPacket handshakePacket) {
            mUARTManager.sendRequest(handshakePacket, new ActionListener() {
                  
                  @Override
                  public void onSuccess() {
                        listener.onSuccess();
                  }
                  
                  @Override
                  public void onFailed(int errorCode) {
                        listener.onFailed(errorCode);
                  }
            });
      }
      
      private void notifyEvent(UARTEvent event) {
            mCallbacker.notifyEvent(mEventListener, event);
      }
}
