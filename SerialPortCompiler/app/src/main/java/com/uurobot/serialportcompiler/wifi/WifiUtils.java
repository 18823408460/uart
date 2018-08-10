package com.uurobot.serialportcompiler.wifi;

import android.content.Context;
import android.net.wifi.ScanResult;
import android.net.wifi.SupplicantState;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.text.TextUtils;
import android.util.Log;

import java.net.Inet4Address;
import java.util.List;

import static android.content.Context.WIFI_SERVICE;

/**
 * wifi 操作工具类
 * Created by xh on 2017/6/14.
 */

public class WifiUtils {
      
      // 定义一个WifiManager对象
      public WifiManager mWifiManager;
      
      // 定义一个WifiInfo对象
      private WifiInfo mWifiInfo;
      
      // 扫描出的网络连接列表
      private List<ScanResult> mScanWifiList;
      
      private android.net.wifi.WifiManager.WifiLock mWifiLock;
      
      public static final String TAG = "WifiUtils";
      
      public static final int WIFICIPHER_WEP = 1;
      
      public static final int WIFICIPHER_NOPASS = 2;
      
      public static final int WIFICIPHER_WPA = 3;
      
      public WifiUtils(Context context) {
            //取得WifiManager对象
            mWifiManager = (WifiManager) context.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
            //取得WifiInfo对象
            mWifiInfo = mWifiManager.getConnectionInfo();
      }
      
      /**
       * Function:关闭wifi<br>
       *
       * @return true：关闭成功 false：关闭失败<br>
       */
      public boolean closeWifi() {
            return mWifiManager.isWifiEnabled() && mWifiManager.setWifiEnabled(false);
      }
      
      /**
       * Gets the Wi-Fi enabled state.检查当前wifi状态
       */
      public int checkState() {
            return mWifiManager.getWifiState();
      }
      
      // 锁定wifiLock
      public void acquireWifiLock() {
            mWifiLock.acquire();
      }
      
      // 解锁wifiLock
      public void releaseWifiLock() {
            // 判断是否锁定
            if (mWifiLock.isHeld()) {
                  mWifiLock.acquire();
            }
      }
      
      // 创建一个wifiLock
      public void createWifiLock() {
            mWifiLock = mWifiManager.createWifiLock("test");
      }
      
      public void startScan() {
            // 开启wifi
            openWifi();
            // 开始扫描
            mWifiManager.startScan();
            // 得到扫描结果
            mScanWifiList = mWifiManager.getScanResults();
      }
      
      // 得到网络列表
      public List<ScanResult> getWifiList() {
            return mScanWifiList;
      }
      
      // 查看扫描结果
      public StringBuffer lookUpScan() {
            StringBuffer sb = new StringBuffer();
            for (int i = 0; i < mScanWifiList.size(); i++) {
                  sb.append("Index_").append(Integer.valueOf(i + 1).toString()).append(":");
                  // 将ScanResult信息转换成一个字符串包
                  // 其中把包括：BSSID、SSID、capabilities、frequency、level
                  sb.append((mScanWifiList.get(i)).toString()).append("\n");
            }
            return sb;
      }
      
      public String getMacAddress() {
            return (mWifiInfo == null) ? null : mWifiInfo.getMacAddress();
      }
      
      public String getSSID() {
            if (mWifiInfo != null && SupplicantState.COMPLETED.equals(mWifiInfo.getSupplicantState())) {
                  return mWifiInfo.getSSID();
            }
            return null;
      }
      
      /**
       * Return the basic service set identifier (BSSID) of the current access
       * point. The BSSID may be {@code null} if there is no network currently
       * connected.
       *
       * @return the BSSID, in the form of a six-byte MAC address:
       * {@code XX:XX:XX:XX:XX:XX}
       */
      public String getBSSID() {
            return (mWifiInfo == null) ? null : mWifiInfo.getBSSID();
      }
      
      public int getIpAddress() {
            return (mWifiInfo == null) ? 0 : mWifiInfo.getIpAddress();
      }
      
      /**
       * the network ID, or -1 if there is no currently connected network
       */
      public int getNetWordId() {
            return (mWifiInfo == null) ? 0 : mWifiInfo.getNetworkId();
      }
      
      /**
       * Function: 得到wifiInfo的所有信息
       */
      public String getWifiInfo() {
            return (mWifiInfo == null) ? null : mWifiInfo.toString();
      }
      
      // 添加一个网络并连接
      public void addNetWork(WifiConfiguration configuration) {
            int wcgId = mWifiManager.addNetwork(configuration);
            mWifiManager.enableNetwork(wcgId, true);
      }
      
      // 断开指定ID的网络
      public void disConnectionWifi(int netId) {
            mWifiManager.disableNetwork(netId);
            mWifiManager.disconnect();
      }
      
      /**
       * Function: 打开wifi功能<br>
       *
       * @return true:打开成功；false:打开失败<br>
       */
      public boolean openWifi() {
            boolean bRet = true;
            if (!mWifiManager.isWifiEnabled()) {
                  bRet = mWifiManager.setWifiEnabled(true);
            }
            return bRet;
      }
      
      /**
       * 给外部提供一个借口，连接无线网络
       *
       * @param SSID     ssid
       * @param Password pwd
       * @return true：开始连接 false：无法连接<br>
       */
      public boolean connect(String SSID, String Password) {
            Log.e(TAG, "SSID = " + SSID);
            int Type = getCipherType(mWifiManager, SSID);
     
            
            // 查看以前是否也配置过这个网络
            WifiConfiguration tempConfig = this.isExsits(SSID);
            if (tempConfig != null) {
                  mWifiManager.removeNetwork(tempConfig.networkId);
            }
            
            WifiConfiguration wifiConfig = createWifiInfo(SSID, Password, Type);
            
            // 添加一个新的网络描述为一组配置的网络。
            int netID = mWifiManager.addNetwork(wifiConfig);
            // 设置为true,使其他的连接断开
            boolean mConnectConfig = mWifiManager.enableNetwork(netID, true);
            mWifiManager.saveConfiguration();
            
            return mConnectConfig;
      }
      
      // 查看以前是否也配置过这个网络
      private WifiConfiguration isExsits(String SSID) {
            List<WifiConfiguration> existingConfigs = mWifiManager.getConfiguredNetworks();
            for (WifiConfiguration existingConfig : existingConfigs) {
                  if (existingConfig.SSID.equals(SSID) || existingConfig.SSID.equals("\"" + SSID + "\"")) {
                        return existingConfig;
                  }
            }
            return null;
      }
      
      private WifiConfiguration createWifiInfo(String SSID, String Password, int Type) {
            WifiConfiguration config = new WifiConfiguration();
            
            config.SSID = "\"" + SSID + "\"";
            
            if (Type == WIFICIPHER_NOPASS) { // WIFICIPHER_NOPASS
                  Log.e(TAG, "createWifiInfo: WIFICIPHER_NOPASS" );
                  config.preSharedKey = null;
                  config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
            }
            if (Type == WIFICIPHER_WEP) { // WIFICIPHER_WEP
                  Log.e(TAG, "createWifiInfo: WIFICIPHER_WEP" );
                  config.preSharedKey = "\"" + Password + "\"";
                  config.hiddenSSID = true;
                  config.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.SHARED);
                  config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);
                  config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);
                  config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP40);
                  config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP104);
                  config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
                  config.wepTxKeyIndex = 0;
            }
            if (Type == WIFICIPHER_WPA) { //WIFICIPHER_WPA  // WPA-PSK WPA-EAP。
                  Log.e(TAG, "createWifiInfo: WIFICIPHER_WPA" );
                  config.preSharedKey = "\"" + Password + "\"";
     /*             config.hiddenSSID = true;
                  config.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.OPEN);
                  config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);
                  config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_PSK);
                  config.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.TKIP);
                  config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);
                  config.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.CCMP);*/
            }
            return config;
      }
      
      /**
       * Function:判断扫描结果是否连接上<br>
       *
       * @param ssid 扫描到的指定 wifi
       * @return true：已经连接 false：连接失败<br>
       */
      public boolean isConnect(String ssid) {
            if (ssid == null || ssid.isEmpty()) {
                  return false;
            }
            
            mWifiInfo = mWifiManager.getConnectionInfo();
            String curConnectedSSID = mWifiInfo.getSSID();
            if (!curConnectedSSID.contains("\"\"")) {
                  curConnectedSSID = "\"" + curConnectedSSID + "\"";
            }
            if (!ssid.contains("\"\"")) {
                  ssid = "\"" + ssid + "\"";
            }
            
            return curConnectedSSID.equals(ssid);
      }
      
      /**
       * Function: 将int类型的IP转换成字符串形式的IP<br>
       *
       * @param ip ip
       * @return 转换后的 ip 地址<br>
       */
      public String ipIntToString(int ip) {
            try {
                  byte[] bytes = new byte[4];
                  bytes[0] = (byte) (0xff & ip);
                  bytes[1] = (byte) ((0xff00 & ip) >> 8);
                  bytes[2] = (byte) ((0xff0000 & ip) >> 16);
                  bytes[3] = (byte) ((0xff000000 & ip) >> 24);
                  return Inet4Address.getByAddress(bytes).getHostAddress();
            }
            catch (Exception e) {
                  return "";
            }
      }
      
      public int getConnNetId() {
            // result.SSID;
            mWifiInfo = mWifiManager.getConnectionInfo();
            return mWifiInfo.getNetworkId();
      }
      
      /**
       * 忘记指定 ssid 的 wifi 配置
       *
       * @param ssid wifi 名称
       */
      public void forgetNet(String ssid) {
            WifiConfiguration configuration = isExsits(ssid);
            if (configuration != null) {
                  mWifiManager.removeNetwork(configuration.networkId);
            }
      }
      
      /**
       * Function:信号强度转换为字符串
       *
       * @param level level
       */
      public static String singlLevToStr(int level) {
            String resuString = "无信号";
            
            if (Math.abs(level) > 100) {
            }
            else if (Math.abs(level) > 80) {
                  resuString = "弱";
            }
            else if (Math.abs(level) > 70) {
                  resuString = "强";
            }
            else if (Math.abs(level) > 60) {
                  resuString = "强";
            }
            else if (Math.abs(level) > 50) {
                  resuString = "较强";
            }
            else {
                  resuString = "极强";
            }
            return resuString;
      }
      
      /**
       * 添加到网络
       *
       * @param wcg wcg
       */
      public boolean addNetwork(WifiConfiguration wcg) {
            if (wcg == null) {
                  return false;
            }
            int wcgID = mWifiManager.addNetwork(wcg);
            boolean b = mWifiManager.enableNetwork(wcgID, true);
            mWifiManager.saveConfiguration();
            System.out.println(b);
            return b;
      }
      
      /**
       * 获取指定 ssid wifi 的加密方式
       *
       * @param mWifiManager mWifiManager
       * @param ssid         wifi 名称
       * @return 加密方式
       */
      private int getCipherType(WifiManager mWifiManager, String ssid) {
            List<ScanResult> list = mWifiManager.getScanResults();
            for (ScanResult scResult : list) {
                  if (!TextUtils.isEmpty(scResult.SSID) && scResult.SSID.equals(ssid)) {
                        String capabilities = scResult.capabilities;
                        Log.e(TAG, "capabilities=" + capabilities);
                        if (!TextUtils.isEmpty(capabilities)) {
                              if (capabilities.contains("WPA") || capabilities.contains("wpa")) {
                                    Log.e(TAG, "wpa");
                                    return WIFICIPHER_WPA;
                              }
                              else if (capabilities.contains("WEP") || capabilities.contains("wep")) {
                                    Log.e(TAG, "wep");
                                    return WIFICIPHER_WEP;
                              }
                              else {
                                    Log.e(TAG, "no");
                                    return WIFICIPHER_NOPASS;
                              }
                        }
                        else {
                              return WIFICIPHER_NOPASS;
                        }
                  }
            }
            return WIFICIPHER_NOPASS;
      }
      
      /**
       * 获取当前网络的ssid
       *
       * @param context
       * @return
       */
      public String currentNetworkSSID(Context context) {
            WifiManager wifiManager = (WifiManager) context.getSystemService(WIFI_SERVICE);
            WifiInfo connectionInfo = wifiManager.getConnectionInfo();
            if (connectionInfo != null) {
                  Log.e(TAG, "当前网络信息" + connectionInfo.toString());
                  String ssid = connectionInfo.getSSID();
                  ssid = ssid.replace("\"", "");
                  Log.e(TAG, ssid);
                  return ssid;
            }
            return null;
      }
}
