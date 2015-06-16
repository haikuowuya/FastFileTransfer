package vis.net.wifi;

import android.content.Context;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.util.Log;

import java.lang.reflect.Method;

/**
 * Created by Vision on 15/6/9.<br>
 * Email:Vision.lsm.2012@gmail.com
 */
public class APWifiManager {
    private static final String TAG = APWifiManager.class.getName();
    //定义WifiManager对象
    private WifiManager mWifiManager;
    //定义WifiInfo对象
//    private WifiInfo mWifiInfo;
    //扫描出的网络连接列表
//    private List<ScanResult> mWifiList;
    //网络连接列表
//    private List<WifiConfiguration> mWifiConfiguration;
    //定义一个WifiLock
    WifiManager.WifiLock mWifiLock;

    private static final int WIFI_AP_STATE_UNKNOWN = -1;
    // boolean mIsWifiEnabled = false;
    public static final int WIFI_AP_STATE_DISABLING = 10;
    //    private static final int WIFI_AP_STATE_DISABLING = 0;
    public static final int WIFI_AP_STATE_DISABLED = 11;
    //    private static final int WIFI_AP_STATE_DISABLED = 1;
    public static final int WIFI_AP_STATE_ENABLING = 12;
    //    private static final int WIFI_AP_STATE_ENABLING = 2;
    public static final int WIFI_AP_STATE_ENABLED = 13;
    //    private static final int WIFI_AP_STATE_ENABLED = 3;
    public static final int WIFI_AP_STATE_FAILED = 14;
    //    private static final int WIFI_AP_STATE_FAILED = 4;
    private final String[] WIFI_STATE_TEXTSTATE = new String[]{
            "DISABLING", "DISABLED", "ENABLING", "ENABLED", "FAILED"
    };
    /**
     * 本地原来的WiFi AP信息
     */
    private WifiConfiguration localWifiConfiguration;
    /**
     * 本地原来是否开启了wifi
     */
    private boolean isLocalWifiEnabled;

    //构造器
    public APWifiManager(Context context) {
        //取得WifiManager对象
        mWifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        //取得WifiInfo对象
//        mWifiInfo = mWifiManager.getConnectionInfo();
    }

    public boolean isApEnabled() {
        int state = getWifiApState();
//        System.out.println("****" + state);
        return WIFI_AP_STATE_ENABLING == state || WIFI_AP_STATE_ENABLED == state;
    }

    public int getWifiApState() {
        try {
            Method method = mWifiManager.getClass().getMethod("getWifiApState");
            int i = (Integer) method.invoke(mWifiManager);
            return i;
        } catch (Exception e) {
            Log.i(TAG, "Cannot get WiFi AP state" + e);
            return WIFI_AP_STATE_FAILED;
        }
    }

    public WifiConfiguration getWifiApConfiguration() {
        try {
            Method method = mWifiManager.getClass().getMethod(
                    "getWifiApConfiguration");
            return (WifiConfiguration) method.invoke(mWifiManager);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public boolean setWifiApConfiguration(WifiConfiguration config) {
        try {
            Method method = mWifiManager.getClass().getMethod(
                    "setWifiApConfiguration", WifiConfiguration.class);
            return (Boolean) method.invoke(mWifiManager, config);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 锁定WifiLock，当下载大文件时需要锁定
     */
    public void AcquireWifiLock() {
        mWifiLock.acquire();
    }

    /**
     * 解锁WifiLock
     */
    public void ReleaseWifiLock() {
        //判断时候锁定
        if (mWifiLock.isHeld()) {
            mWifiLock.acquire();
        }
    }

    //创建一个WifiLock
    public void CreatWifiLock() {
        mWifiLock = mWifiManager.createWifiLock("Test");
    }

    //得到配置好的网络
//    public List<WifiConfiguration> GetConfiguration() {
//        return mWifiConfiguration;
//    }

    //指定配置好的网络进行连接
//    public void ConnectConfiguration(int index) {
//        //索引大于配置好的网络索引返回
//        if (index > mWifiConfiguration.size()) {
//            return;
//        }
//        //连接配置好的指定ID的网络
//        mWifiManager.enableNetwork(mWifiConfiguration.get(index).networkId, true);
//    }

//    public void StartScan() {
//        mWifiManager.startScan();
//        //得到扫描结果
//        mWifiList = mWifiManager.getScanResults();
//        //得到配置好的网络连接
//        mWifiConfiguration = mWifiManager.getConfiguredNetworks();
//    }

//    //得到网络列表
//    public List<ScanResult> GetWifiList() {
//        return mWifiList;
//    }

    //得到IP地址
//    public int GetIPAddress() {
//        return (mWifiInfo == null) ? 0 : mWifiInfo.getIpAddress();
//    }

    //得到连接的ID
//    public int GetNetworkId() {
//        return (mWifiInfo == null) ? 0 : mWifiInfo.getNetworkId();
//    }

    //得到WifiInfo的所有信息包
//    public String GetWifiInfo() {
//        return (mWifiInfo == null) ? "NULL" : mWifiInfo.toString();
//    }

//    //添加一个网络并连接
//    public void addNetwork(WifiConfiguration wcfg) {
//        int wcgID = mWifiManager.addNetwork(wcfg);
//        mWifiManager.enableNetwork(wcgID, true);
//    }

//    //断开指定ID的网络
//    public void disconnectWifi(int netId) {
//        mWifiManager.disableNetwork(netId);
//        mWifiManager.disconnect();
//    }

    private int getWifiAPState() {
        int state = WIFI_AP_STATE_UNKNOWN;
        try {
            Method method2 = mWifiManager.getClass().getMethod("getWifiApState");
            state = (Integer) method2.invoke(mWifiManager);
        } catch (Exception e) {
        }
        Log.d("WifiAP", "getWifiAPState.state " + (state == -1 ? "UNKNOWN" : WIFI_STATE_TEXTSTATE[state]));
        return state;
    }

}
/*
    设计方案
    AP      <---------->    Child
                        连接热点，获取AP.IP
                        开启TCP监听
                        往IP发送登入命令+设备名称
    从包里获得IP，登记在发送列表，显示名称
    TCP连接IP，如果连接无效，抛出异常，并在发送列表中删除无效项
    传送文件
                        往IP发送登出命令+设备名称
    从包里获得IP，读取名称，在发送列表匹配，如果存在则删除，删除显示名称

 */


