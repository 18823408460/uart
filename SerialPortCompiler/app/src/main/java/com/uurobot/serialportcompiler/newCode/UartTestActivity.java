package com.uurobot.serialportcompiler.newCode;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;

import com.uurobot.serialportcompiler.newCode.excption.UARTException;
import com.uurobot.serialportcompiler.newCode.pkg.DataPacket;
import com.uurobot.serialportcompiler.newCode.pkg.MsgPacket;
import com.uurobot.serialportcompiler.newCode.pkg.PacketBuilder;
import com.uurobot.serialportcompiler.utils.DataUtils;

import java.util.List;

/**
 * Created by Administrator on 2018/8/10.
 */

public class UartTestActivity extends Activity {
      private static final String TAG = "UartTestActivity";
      
      @Override
      protected void onCreate(@Nullable Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);

//            test1();
              testUart();
      }
      
      private void testUart() {
      }
      
      private void compress() {
            String hello = "我是中国人，时发生大洪水的念佛水电费是的佛山佛山的囧是建瓯盘圣诞节是打飞机哦派送的风景哦派送对方,水电费水电费是" +
                                   "水电费而皮皮狗而突然突然突然又突然破恐怕没破解" +
                                   "明天的天气怎么样，你说对不对";
            Log.e(TAG, "onCreate: " + hello.getBytes().length);
            
            byte[] compress = DataUtils.compress(hello.getBytes());
            Log.e(TAG, "onCreate: " + compress.length);
            
            byte[] bytes = DataUtils.unCompress(compress);
            Log.e(TAG, "onCreate: " + bytes.length);
      }
      
      private void test1() {
            MsgPacket hello = PacketBuilder.obtainTouchuanMsg(1, "hello");
            MsgPacket hello1 = PacketBuilder.obtainTouchuanMsg(2, "hello");
            MsgPacket hello2 = PacketBuilder.obtainTouchuanMsg(3, "hello");
            
            MsgPacket hello4 = PacketBuilder.obtainTouchuanMsg(4, "我是中国人你呢");
            try {
                  DataPacket dataPacket = DataPacket.buildDataPacket(hello);
                  List<byte[]> list1 = dataPacket.encodeBytes();
                  for (int i = 0; i < list1.size(); i++) {
                        Log.e(TAG, "onCreate1: " + DataUtils.bytesToHexString(list1.get(i)));
                  }
                  
                  dataPacket = DataPacket.buildDataPacket(hello1);
                  list1 = dataPacket.encodeBytes();
                  for (int i = 0; i < list1.size(); i++) {
                        Log.e(TAG, "onCreate2: " + DataUtils.bytesToHexString(list1.get(i)));
                  }
                  
                  dataPacket = DataPacket.buildDataPacket(hello2);
                  list1 = dataPacket.encodeBytes();
                  for (int i = 0; i < list1.size(); i++) {
                        Log.e(TAG, "onCreate3: " + DataUtils.bytesToHexString(list1.get(i)));
                  }
                  
                  dataPacket = DataPacket.buildDataPacket(hello4);
                  list1 = dataPacket.encodeBytes();
                  for (int i = 0; i < list1.size(); i++) {
                        Log.e(TAG, "onCreate4: " + DataUtils.bytesToHexString(list1.get(i)));
                  }
                  MsgPacket hello5 = PacketBuilder.obtainTouchuanMsg(5, "hello");
                  dataPacket = DataPacket.buildDataPacket(hello5);
                  list1 = dataPacket.encodeBytes();
                  for (int i = 0; i < list1.size(); i++) {
                        Log.e(TAG, "onCreate5: " + DataUtils.bytesToHexString(list1.get(i)));
                  }
            }
            catch (UARTException e) {
                  e.printStackTrace();
            }
      }
}
