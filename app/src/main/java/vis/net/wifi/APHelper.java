package vis.net.wifi;

import android.content.Context;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.util.Log;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.UUID;

/**
 * Created by Vision on 15/6/24.<br>
 * Email:Vision.lsm.2012@gmail.com
 */
public class APHelper {

    private static final String TAG = APHelper.class.getName();
    public static final String SSID = "YDZS_" + Build.MODEL.replaceAll("\\s|-", "") + "00" + UUID.randomUUID().toString().substring(0, 4);

    public static final int WIFI_AP_STATE_ENABLING = 12;
    public static final int WIFI_AP_STATE_ENABLED = 13;
    public static final int WIFI_AP_STATE_FAILED = 14;
    private final WifiManager mWifiManager;

    private Context context;

    public APHelper(Context context) {
        this.context = context;
        mWifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
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


    public boolean setWifiApEnabled(WifiConfiguration wifiConfig, boolean enabled) {
        boolean result = false;
        if (mWifiManager.isWifiEnabled()) {
            mWifiManager.setWifiEnabled(false);
        }
        try {
            Method method = mWifiManager.getClass().getMethod("setWifiApEnabled", WifiConfiguration.class, Boolean.TYPE);
            result = (boolean) (Boolean) method.invoke(mWifiManager, wifiConfig, enabled);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return result;
    }


}
