package com.uurobot.serialportcompiler.newCode;

/**
 * Created by Administrator on 2018/8/10.
 */

public class UARTConnector {
      static {
            System.loadLibrary("uart_helper");
      }
      
      private static UARTManager sManager;
      
      // TODO 优化
      public static void setManager(UARTManager uartManager) {
            sManager = uartManager;
      }
      
      public static void onReceive(byte[] data) {
            sManager.onReceive(data);
      }
      
      public static native int init(String device, int speed);
      
      public static native int send(byte[] data);
      
      public static native void destroy();
}
