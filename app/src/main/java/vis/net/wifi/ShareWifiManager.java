package vis.net.wifi;

import android.content.Context;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.util.Log;

import java.lang.reflect.Method;

/**
 * Created by Vision on 15/6/11.
 */
public class ShareWifiManager {

    private static final String TAG = APWifiManager.class.getName();

    //定义WifiManager对象
    private WifiManager mWifiManager;

    /**
     * 本地原来是否开启了wifi
     */
    private boolean isLocalWifiEnabled;

    /**
     * 本地原来的WiFi AP信息
     */
    private WifiConfiguration localWifiConfiguration;


    public ShareWifiManager(Context context) {
        //取得WifiManager对象
        mWifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
    }
    /**
     * 设置开启或关闭AP
     *
     * @param enabled
     * @return
     */
    public boolean setWifiApEnabled(boolean enabled) {
        if (enabled) { // disable WiFi in any case
            //记录原来的wifi状态
            this.isLocalWifiEnabled = mWifiManager.isWifiEnabled();
            //关闭wifi
            mWifiManager.setWifiEnabled(false);
            //备份本地原来的Wifi AP设置
            this.localWifiConfiguration = getWifiApConfiguration();
        } else {
            //恢复原来的wifi状态//失效
//            mWifiManager.setWifiEnabled(true);
            //恢复本地原来的Wifi AP设置
            setWifiApConfiguration(this.localWifiConfiguration);
        }
        try {
            WifiConfiguration apConfig = new WifiConfiguration();
            apConfig.SSID = "YDZS_" + Build.MODEL + "00";
            apConfig.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
//            apConfig.preSharedKey = "abcdefgh";
            Method method = mWifiManager.getClass().getMethod("setWifiApEnabled", WifiConfiguration.class, Boolean.TYPE);
            boolean open = (Boolean) method.invoke(mWifiManager, apConfig, enabled);
            return open;
        } catch (Exception e) {
            Log.e(TAG, "Cannot set WiFi AP state", e);
            return false;
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
     * 管理结束
     */
    public void finish() {
        //恢复原来的wifi状态
        mWifiManager.setWifiEnabled(this.isLocalWifiEnabled);
    }

}
