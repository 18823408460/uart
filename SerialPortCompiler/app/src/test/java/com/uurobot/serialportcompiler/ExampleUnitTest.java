package com.uurobot.serialportcompiler;

import android.util.Log;

import com.uurobot.serialportcompiler.utils.DataUtils;
import com.uurobot.serialportcompiler.utils.DecodeUtil;
import com.uurobot.serialportcompiler.utils.EncodeUtil;

import org.junit.Test;

import java.util.LinkedList;

import static org.junit.Assert.*;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {
      @Test
      public void addition_isCorrect() throws Exception {
            
            byte a = 12;
            String string = Integer.toHexString(a);
            System.out.println("str=== " + string);
            
            
      }
      
      @Test
      public void main(String[] arg) {
            byte[] touchuanData = EncodeUtil.getTouchuanData(11);
            System.out.println(":" + DataUtils.bytesToHexString(touchuanData));
            DecodeUtil.parsePkg(touchuanData);
            
            byte[] touchuanData1 = EncodeUtil.getTouchuanData(12);
            System.out.println(":" + DataUtils.bytesToHexString(touchuanData1));
            DecodeUtil.parsePkg(touchuanData1);
            
            byte[] touchuanData2 = EncodeUtil.getTouchuanData(13);
            System.out.println(":" + DataUtils.bytesToHexString(touchuanData2));
            DecodeUtil.parsePkg(touchuanData2);
            
            byte[] touchuanData3 = EncodeUtil.getUnpackData(13, "ddd");
            System.out.println(":" + DataUtils.bytesToHexString(touchuanData3));
            DecodeUtil.parsePkg(touchuanData3);
            
            String msgData = "hello my name is xiaowei";
            LinkedList<byte[]> hello = EncodeUtil.getTouchuanData(14, msgData);
            System.out.println("size : " + hello.size());
            for (byte[] data : hello) {
                  System.out.println(": " + DataUtils.bytesToHexString(data));
                  DecodeUtil.parsePkg(data);
            }
            System.out.println("data=" + DataUtils.bytesToHexString(msgData.getBytes()));
      }
}