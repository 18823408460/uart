package com.uurobot.serialportcompiler.newCode;

/**
 * Created by Administrator on 2018/8/10.
 */

public class UARTEvent {
      public int eventType;
      
      public Object data;
      
      public UARTEvent(int eventType, Object data) {
            this.eventType = eventType;
            this.data = data;
      }
      
      public UARTEvent(int eventType) {
            this.eventType = eventType;
            this.data = null;
      }
}
