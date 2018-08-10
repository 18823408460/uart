package com.uurobot.serialportcompiler.newCode.bean;

import com.uurobot.serialportcompiler.newCode.interfaces.ActionListener;
import com.uurobot.serialportcompiler.newCode.pkg.DataPacket;

/**
 * Created by Administrator on 2018/8/10.
 */

public class PacketEntity {
      private DataPacket sendPacket = null;
      private boolean isNeedAck = false;
      private long lastSendTime = 0;
      private int retryCount = 0;
      private ActionListener callback = null;
      private boolean isHasAcked = false;
      
      public PacketEntity(DataPacket packet, ActionListener callback) {
            this.sendPacket = packet;
            this.callback = callback;
            this.isNeedAck = true;
      }
      
      public PacketEntity(DataPacket packet) {
            this.sendPacket = packet;
            this.isNeedAck = false;
      }
      
      public DataPacket getPacket() {
            return sendPacket;
      }
      
      public boolean isNeedAck() {
            return isNeedAck;
      }
      
      public boolean isHasAcked() {
            return isHasAcked;
      }
      
      public void setAcked() {
            isHasAcked = true;
      }
      
      public long getLastSendTime() {
            return lastSendTime;
      }
      
      public void setLastSendTime(long lastSendTime) {
            this.lastSendTime = lastSendTime;
      }
      
      public void increaseRetryCoutn() {
            retryCount += 1;
      }
      
      public void resetRetryCount() {
            retryCount = 0;
      }
      
      public int getRetryCount() {
            return retryCount;
      }
      
      
      public void onSuccess() {
            if (callback != null) {
                  callback.onSuccess();
            }
      }
      
      public void onFailed(int error) {
            if (callback != null) {
                  callback.onFailed(error);
            }
      }
}
