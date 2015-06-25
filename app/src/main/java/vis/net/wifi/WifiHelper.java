package vis.net.wifi;

import android.content.Context;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Vision on 15/6/24.<br>
 * Email:Vision.lsm.2012@gmail.com
 */
public class WifiHelper {
    //定义WifiManager对象
    private WifiManager mWifiManager;
    /**
     * 目标AP的NetID
     */
    private int targetNetID = 0;

    public WifiHelper(Context context) {
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
    /**
     * 登入网络
     */
    public boolean addNetwork(WifiConfiguration wifiCfg) {
//        mWifiManager.disconnect();
        //添加一个网络并连接
        targetNetID = mWifiManager.addNetwork(wifiCfg);
        //加入网络
        return mWifiManager.enableNetwork(targetNetID, true);
    }

    /**
     * 除移指定ID的网络
     */
    public void removeNetwork() {
        if (0 != targetNetID) {
            mWifiManager.removeNetwork(targetNetID);
        }
    }

    public static WifiConfiguration createWifiCfg(String ssid) {
        WifiConfiguration wifiCfg = new WifiConfiguration();
        wifiCfg.SSID = ssid;
        wifiCfg.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
//        wifiCfg.preSharedKey = "abcdefgh";
        return wifiCfg;
    }

    /**
     * 匹配SSID
     *
     * @param pattern 正则表达式
     * @return 如果匹配成功返回SSID，如果不成功返回Null
     */
//    public String findSSID(String pattern) {
    public static ArrayList<String> findSSID(List<ScanResult> wifiList,String pattern) {
//        String foundSSID = null;
        ArrayList<String> al = new ArrayList<String>();
//        List<ScanResult> wifiList = mWifiManager.getScanResults();
        Pattern p = Pattern.compile(pattern);
        for (int i = 0; i < wifiList.size(); i++) {
            //匹配SSID
            Matcher m = p.matcher(wifiList.get(i).SSID);
            if (m.find()) {//匹配成功
//                foundSSID = wifiList.get(i).SSID;
                al.add(wifiList.get(i).SSID);
//                break;
            }
        }
//        return foundSSID;
        return al;
    }



}
