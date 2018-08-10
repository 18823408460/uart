package com.uurobot.serialportcompiler.wifi;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;

import com.uurobot.serialportcompiler.R;

// wifi的配置文件在  cat /data/misc/wifi/wpa_supplicant.conf,可以先将其pull出来
public class WifiActivity extends Activity {
      WifiUtils wifiUtils ;
      @Override
      protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.content_wifi);
            wifiUtils = new WifiUtils(this);
            wifiUtils.openWifi();
      }
      
      
      public void connect(View v) {
//            wifiUtils.connect("uurobotyin","987654321");
//            wifiUtils.connect("U05E-000003","1223334444");
//            wifiUtils.connect("U05-100006","1223334444");
            wifiUtils.connect("U05-100437","1223334444");
      }
      
}
