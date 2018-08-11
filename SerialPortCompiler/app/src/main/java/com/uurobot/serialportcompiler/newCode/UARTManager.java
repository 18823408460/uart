package com.uurobot.serialportcompiler.newCode;

import com.uurobot.serialportcompiler.newCode.bean.PacketEntity;
import com.uurobot.serialportcompiler.newCode.excption.UARTException;
import com.uurobot.serialportcompiler.newCode.interfaces.ActionListener;
import com.uurobot.serialportcompiler.newCode.bean.Error;
import com.uurobot.serialportcompiler.newCode.interfaces.RequestListener;
import com.uurobot.serialportcompiler.newCode.pkg.DataPacket;
import com.uurobot.serialportcompiler.newCode.pkg.MsgPacket;
import com.uurobot.serialportcompiler.newCode.pkg.Packet;
import com.uurobot.serialportcompiler.newCode.pkg.PacketParseUtil;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

/**
 * Created by Administrator on 2018/8/10.
 * <p>
 * 数据的发送， 接受解析， 响应处理
 * <p>
 * 这里类只是被 UARTAgent操作。UARTAgent（代理）才对外发送接收
 */

public class UARTManager {
      private static UARTManager sManager;
      private ExecutorService mMainExecutor;
      private volatile boolean mIsRunning = false;
      private Thread mSendThread;
      private List<PacketEntity> mSendPool = new ArrayList<PacketEntity>();
      private static final int SEND_RETRY_COUNT = 3;
      private static final long SEND_TIME_OUT = 300;
      private Queue<Integer> mReceivePool = new LinkedList<Integer>();
      
      private void processSendPool() {
            while (mIsRunning) {
                  synchronized (mSendPool) {
                        long minSleepTime = SEND_TIME_OUT;
                        long currentTime = System.currentTimeMillis();
                        Iterator<PacketEntity> iterator = mSendPool.iterator();
                        while (iterator.hasNext()) {
                              PacketEntity entity = iterator.next();
                              //已收到确认的消息
                              if (entity.isHasAcked()) {
                                    entity.onSuccess();
                                    iterator.remove();
                                    continue;
                              }
                              
                              //重试次数超过的消息
                              if (entity.getRetryCount() >= SEND_RETRY_COUNT) {
                                    entity.onFailed(Error.NO_ACK);
                                    iterator.remove();
                                    continue;
                              }
                              
                              //未收到确认重试次数也未超过的消息
                              if (currentTime - entity.getLastSendTime() >= SEND_TIME_OUT) {
                                    sendPacket(entity.getPacket());
                                    if (entity.isNeedAck()) {
                                          entity.increaseRetryCoutn();
                                          entity.setLastSendTime(currentTime);
                                    }
                                    else {
                                          iterator.remove();
                                    }
                              }
                              else {
                                    long sendInterval = SEND_TIME_OUT - (currentTime - entity.getLastSendTime());
                                    if (sendInterval < minSleepTime) {
                                          minSleepTime = sendInterval;
                                    }
                              }
                        }
                        try {
                              mSendPool.wait(minSleepTime);
                        }
                        catch (InterruptedException e) {
                              //ignore
                        }
                  }
            }
      }
      
      public void setmRequestListener(RequestListener mRequestListener) {
            this.mRequestListener = mRequestListener;
      }
      
      private RequestListener mRequestListener;
      
      public static UARTManager getManager() {
            if (null == sManager) {
                  sManager = new UARTManager();
            }
            return sManager;
      }
      
      public void onReceive(byte[] data) {
            if (data == null)
                  return;
            
            if (!DataPacket.isValid(data)) {
                  return;
            }
            
            DataPacket dataPacket = new DataPacket();
            try {
                  dataPacket.decodeBytes(data);
            }
            catch (UARTException e) {
                  return;
            }
            final MsgPacket msgPacket = dataPacket.data;
            if (msgPacket.isReqType() && msgPacket.getMsgType() != MsgPacket.HANDSHAKE_REQ_TYPE && mReceivePool.contains(msgPacket.getSeqID())) {
                  DataPacket ackPacket = PacketParseUtil.getAckMsg(msgPacket);
                  sendResponse(ackPacket); //收到重复的消息（因为对方没有收到响应），所以这里只发送响应就可以
                  return;
            }
            
            if (msgPacket.isReqType()) {
                  addReceivePool(msgPacket.getSeqID());
                  processReqPacket(msgPacket);
            }
            else {
                  processAckPacket(msgPacket);
            }
      }
      
      private void addReceivePool(int seqID) {
            if (mReceivePool.size() > 200) {
                  mReceivePool.remove();
            }
            mReceivePool.add(seqID);
      }
      
      public void init(final String deviceName, final int speed, final ActionListener listener) {
            mIsRunning = true;
            UARTConnector.setManager(this);
            initSendThread();
            
            mMainExecutor = Executors.newSingleThreadExecutor(new ThreadFactory() {
                  @Override
                  public Thread newThread(Runnable r) {
                        Thread ret = new Thread(r, "UART_Manager_Main_Thread");
                        ret.setDaemon(true);
                        return ret;
                  }
            });
            uartInit(deviceName, speed, listener);
      }
      
      private void uartInit(final String deviceName, final int speed, final ActionListener listener) {
            mMainExecutor.submit(new Runnable() {
                  @Override
                  public void run() {
                        int init_status = UARTConnector.init(deviceName, speed);
                        if (init_status != -1) {
                              listener.onSuccess();
                        }
                        else {
                              listener.onFailed(Error.OPEN_FAILED);
                        }
                  }
            });
      }
      
      
      //请求响应包
      private void processAckPacket(final MsgPacket msgPacket) {
            mMainExecutor.execute(new Runnable() {
                  @Override
                  public void run() {
                        synchronized (mSendPool) {
                              Iterator<PacketEntity> iterator = mSendPool.iterator();
                              while (iterator.hasNext()) {
                                    PacketEntity entity = iterator.next();
                                    if (entity.getPacket().data.getSeqID() == msgPacket.getSeqID()) {
                                          entity.setAcked();
                                          break;
                                    }
                              }
                        }
                  }
            });
      }
      
      //收到请求包，
      private void processReqPacket(final MsgPacket packet) {
            mMainExecutor.execute(new Runnable() {
                  @Override
                  public void run() {
                        if (mRequestListener == null)
                              return;
                        DataPacket ackPacket = PacketParseUtil.getAckMsg(packet);
                        sendResponse(ackPacket);
                        mRequestListener.onReqeust(packet);
                  }
            });
      }
      
      /**
       * 发送数据
       *
       * @param packet
       * @param listener
       */
      public void sendRequest(final DataPacket packet, final ActionListener listener) {
            mMainExecutor.execute(new Runnable() {
                  
                  @Override
                  public void run() {
                        PacketEntity entity = new PacketEntity(packet, listener); //发送请求包需要确认
                        addToSendPool(entity);
                  }
            });
      }
      
      private void addToSendPool(PacketEntity entity) {
            synchronized (mSendPool) {
                  mSendPool.add(entity);
                  mSendPool.notify();
            }
      }
      
      private void sendResponse(final DataPacket packet) {
            mMainExecutor.execute(new Runnable() {
                  
                  @Override
                  public void run() {
                        PacketEntity entity = new PacketEntity(packet); //发送响应包不需要确认
                        addToSendPool(entity);
                  }
            });
      }
      
      private void sendPacket(Packet packet) {
            int retry_count = 0;
            do {
                  try {
                        byte[] encodeBytes = packet.encodeBytes();
                        int status = UARTConnector.send(encodeBytes);
                        if (status == 0) {
                              break;
                        }
                  }
                  catch (UARTException e) {
                        break;
                  }
            }
            while (retry_count++ < 3);
      }
      
      private void initSendThread() {
            mSendThread = new Thread(new Runnable() {
                  @Override
                  public void run() {
                        processSendPool();
                  }
            }, "UART_Manager_Write_Thread");
            mSendThread.setDaemon(true);
            mSendThread.start();
      }
      
      
      public void reset() {
      
      }
}
