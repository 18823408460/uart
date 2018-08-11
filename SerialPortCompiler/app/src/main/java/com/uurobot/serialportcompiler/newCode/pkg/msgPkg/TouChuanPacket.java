package com.uurobot.serialportcompiler.newCode.pkg.msgPkg;

import com.uurobot.serialportcompiler.newCode.bean.StringMsgBean;
import com.uurobot.serialportcompiler.newCode.pkg.MsgPacket;

/**
 * Created by Administrator on 2018/8/11.
 */

public class TouChuanPacket extends MsgPacket {
      private StringMsgBean stringMsgBean;
      
      public void setStringMsgBean(StringMsgBean stringMsgBean) {
            this.stringMsgBean = stringMsgBean;
      }
      
      public TouChuanPacket() {
      }
      
      @Override
      public byte getMsgType() {
            return TOUCHUAN_MSG_TYPE;
      }
      
      //编码时使用
      @Override
      protected byte[] getContent() {
            
            return new byte[0];
      }
      
      @Override
      protected void decodeContent(byte[] data) {
      
      }
}
