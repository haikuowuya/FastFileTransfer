package vis.net.wifi;

import android.content.Context;
import android.net.DhcpInfo;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * wifi控制类<br>
 * Created by Vision on 15/6/9.<br>
 * Email:Vision.lsm.2012@gmail.com
 */
public class ReceiveWifiManager {
    private static final String TAG = APWifiManager.class.getName();
    /**
     * 本地原来的NetworkID
     */
    private int localNetworkID;
    /**
     * 目标AP的设置
     */
    private WifiConfiguration targetConfig;
    /**
     * 目标AP的NetID
     */
    private int targetNetID;

    //定义WifiManager对象
    private WifiManager mWifiManager;

    public ReceiveWifiManager(Context context) {
        //取得WifiManager对象
        mWifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
    }

    /**
     * 打开/关闭WIFI
     */
    public void setWifiEnabled(boolean isEnable) {
        if (isEnable != mWifiManager.isWifiEnabled()) {
            mWifiManager.setWifiEnabled(isEnable);
        }
    }

    public boolean startScan() {
        return mWifiManager.startScan();
    }

    /**
     * 匹配SSID
     *
     * @param pattern 正则表达式
     * @return 如果匹配成功返回SSID，如果不成功返回Null
     */
    public String findSSID(String pattern) {
        String foundSSID = null;
        List<ScanResult> wifiList = mWifiManager.getScanResults();
        Pattern p = Pattern.compile(pattern);
        for (int i = 0; i < wifiList.size(); i++) {
            //匹配SSID
            Matcher m = p.matcher(wifiList.get(i).SSID);
            if (m.find()) {//匹配成功
                foundSSID = wifiList.get(i).SSID;
                break;
            }
        }
        return foundSSID;
    }

    /**
     * 登入网络
     */
    public void addNetworkWithoutPasswork(String ssid) {
        mWifiManager.disconnect();
        targetConfig = new WifiConfiguration();
        targetConfig.SSID = "\"" + ssid + "\"";
        targetConfig.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
        //添加一个网络并连接
        targetNetID = mWifiManager.addNetwork(targetConfig);
        //保存本地NetworkID
        localNetworkID = mWifiManager.getConnectionInfo().getNetworkId();
        //加入网络
        mWifiManager.enableNetwork(targetNetID, true);
    }

    /**
     * 断开指定ID的网络
     */
    public void disableNetwork() {
        mWifiManager.removeNetwork(targetNetID);
//        mWifiManager.disconnect();
//        mWifiManager.disableNetwork(targetNetID);
        mWifiManager.reconnect();
    }

    /**
     * 恢复原来的网络
     */
    public void recoveryNetwork() {
        mWifiManager.enableNetwork(localNetworkID, true);
    }

//    public String getSSID() {
//        return mWifiManager.getConnectionInfo().getSSID();
//    }

    public DhcpInfo getDhcpInfo() {
        return mWifiManager.getDhcpInfo();
    }

    public int getServerAddressByInt() {
        return getDhcpInfo().serverAddress;
    }

    public String getServerAddressByStr() {
        return intToIp(getServerAddressByInt());
    }

    /**
     * 把IP地址从int转换成String
     * @param i int 型的IP地址
     * @return String 型的IP地址，如255.255.255.255
     */
    private String intToIp(int i) {
        return (i & 0xFF) + "." + ((i >> 8) & 0xFF) + "." + ((i >> 16) & 0xFF) + "."
                + ((i >> 24) & 0xFF);
    }

}
