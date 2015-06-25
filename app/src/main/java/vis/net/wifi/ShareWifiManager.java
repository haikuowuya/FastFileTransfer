package vis.net.wifi;

import android.content.Context;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.util.Log;

import java.lang.reflect.Method;
import java.util.UUID;

/**
 * Created by Vision on 15/6/11.
 * Email:Vision.lsm.2012@gmail.com
 */
public class ShareWifiManager {

    private static final String TAG = APWifiManager.class.getName();
    private static final String SSID = "YDZS_" + Build.MODEL.replaceAll("\\s|-", "") + "00" + UUID.randomUUID().toString().substring(0, 4);

    public static final int WIFI_AP_STATE_ENABLING = 12;
    public static final int WIFI_AP_STATE_ENABLED = 13;
    public static final int WIFI_AP_STATE_FAILED = 14;


    //定义WifiManager对象
    private WifiManager mWifiManager;

    /**
     * 备份本地原来的wifi状态
     */
    private boolean backupStatus;

    /**
     * 备份本地原来的WiFi AP信息
     */
    private WifiConfiguration backupCfg;


    public ShareWifiManager(Context context) {
        //取得WifiManager对象
        mWifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
    }

    /**
     * 设置开启或关闭AP
     *
     * @param enabled 设置开启或关闭
     * @return 如果开启成功返回true
     */
    public boolean setWifiApEnabled(boolean enabled) {
        if (enabled) { // disable WiFi in any case
            //记录原来的wifi状态
            this.backupStatus = mWifiManager.isWifiEnabled();
            //备份本地原来的Wifi AP设置
            this.backupCfg = getWifiApConfiguration();
            //关闭wifi // disable WiFi in any case
            mWifiManager.setWifiEnabled(false);
        } else {
            //恢复原来的wifi状态//失效
//            mWifiManager.setWifiEnabled(true);
            //恢复本地原来的Wifi AP设置
            setWifiApConfiguration(this.backupCfg);
        }

        boolean success = false;

        try {
            WifiConfiguration apConfig = new WifiConfiguration();
            apConfig.SSID = SSID;
            apConfig.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
//            apConfig.preSharedKey = "abcdefgh";
            Method method = mWifiManager.getClass().getMethod("setWifiApEnabled", WifiConfiguration.class, Boolean.TYPE);
            success = (boolean) (Boolean) method.invoke(mWifiManager, apConfig, enabled);
        } catch (Exception e) {
            Log.e(TAG, "Cannot set WiFi AP state", e);
            success = false;
        }
        if (!enabled) {
            //恢复原来的wifi状态
            mWifiManager.setWifiEnabled(this.backupStatus);
        }
        return success;
    }

    public boolean isApEnabled() {
        int state = getWifiApState();
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

    public String getSSID() {
        return SSID;
    }


}
