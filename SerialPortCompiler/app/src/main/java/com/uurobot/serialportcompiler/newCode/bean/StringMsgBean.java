package com.uurobot.serialportcompiler.newCode.bean;

/**
 * Created by Administrator on 2018/8/11.
 */

public class StringMsgBean {
      /**
       * 消息类型，详见MsgType，，和串口保持一致
       */
      private int msgType;
      
      /**
       * 数据
       */
      private String msgData;
      
      public StringMsgBean(int msgType, String msgData) {
            super();
            this.msgType = msgType;
            this.msgData = msgData;
      }
      
      public StringMsgBean() {
            super();
            // TODO Auto-generated constructor stub
      }
      
      public int getMsgType() {
            return msgType;
      }
      
      public void setMsgType(int msgType) {
            this.msgType = msgType;
      }
      
      public String getMsgData() {
            return msgData;
      }
      
      public void setMsgData(String msgData) {
            this.msgData = msgData;
      }
      
      @Override
      public String toString() {
            return "StringMsgBean [msgType=" + msgType + ", msgData=" + msgData + "]";
      }
}
