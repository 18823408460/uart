package com.uurobot.serialportcompiler.newCode.impl;

import com.uurobot.serialportcompiler.newCode.UARTEvent;
import com.uurobot.serialportcompiler.newCode.UARTEventListener;
import com.uurobot.serialportcompiler.newCode.bean.UARTConstant;
import com.uurobot.serialportcompiler.newCode.interfaces.Callbacker;
import com.uurobot.serialportcompiler.newCode.pkg.MsgPacket;

import java.util.EventListener;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

/**
 * Created by Administrator on 2018/8/10.
 */

public class ExecutorCallbacker implements Callbacker {
      private ExecutorService mCallbackWorker;
      
      public ExecutorCallbacker(final String workerName) {
            mCallbackWorker = Executors.newSingleThreadExecutor(new ThreadFactory() {
                  
                  @Override
                  public Thread newThread(Runnable r) {
                        Thread thread = new Thread(r, "callback_thread");
                        thread.setDaemon(true);
                        return thread;
                  }
            });
      }
      
      @Override
      public void notifyRequest(final UARTEventListener listener, final MsgPacket packet) {
            postCallback(new Runnable() {
                  
                  @Override
                  public void run() {
                        listener.onEvent(new UARTEvent(UARTConstant.EVENT_MSG, packet));
                  }
            });
      }
      
      @Override
      public void notifyEvent(final UARTEventListener listener, final UARTEvent event) {
            postCallback(new Runnable() {
                  
                  @Override
                  public void run() {
                        listener.onEvent(event);
                  }
            });
      }
      
      private void postCallback(Runnable r) {
            mCallbackWorker.execute(r);
      }
      
      @Override
      public void destroy() {
            mCallbackWorker.shutdownNow();
      }
}
