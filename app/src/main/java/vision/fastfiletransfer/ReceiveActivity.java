package vision.fastfiletransfer;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import vis.net.UDPHelper;
import vis.net.wifi.ReceiveWifiManager;


public class ReceiveActivity extends Activity {
    private ReceiveWifiManager mReceiveWifiManager;
    private boolean isConnected = false;
    private NetworkStateChangeReceiver nscr;
    private ScanResultsAvailableReceiver srar;
    private UDPHelper mUDPHelper;

    private String ssid;
    public Handler handler = new Handler() {
        public void handleMessage(Message msg) {
//            switch (msg.what) {
//
//            }
            Toast.makeText(ReceiveActivity.this, String.valueOf(msg.what), Toast.LENGTH_SHORT).show();
        }

    };
    private WifiStateChangedReceiver wscr;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_receive);

        mReceiveWifiManager = new ReceiveWifiManager(this);
        mUDPHelper = new UDPHelper(handler);
    }

    @Override
    protected void onResume() {
        super.onResume();
        wscr = new WifiStateChangedReceiver();
        registerReceiver(wscr, new IntentFilter(WifiManager.WIFI_STATE_CHANGED_ACTION));
        mReceiveWifiManager.setWifiEnabled(true);
//        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (isConnected) {      //连接状态
            isConnected = false;
            mReceiveWifiManager.disableNetwork();
//            mUDPHelper.setLife(false);
//            mUDPHelper.send("byebye".getBytes());
            Log.d("isConnected", String.valueOf(isConnected));
        }
        if (nscr != null) {     //网络状态监听不为空
            unregisterReceiver(nscr);
        }
        if (wscr != null) {
            unregisterReceiver(wscr);
        }
        if (srar != null) {     //扫描监听不为空
            unregisterReceiver(srar);
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_receive, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    class ScanResultsAvailableReceiver extends BroadcastReceiver {

        private final String TAG = ScanResultsAvailableReceiver.class.getName();
        private int noFindCount = 0;

        @Override
        public void onReceive(Context context, Intent intent) {
            ssid = mReceiveWifiManager.findSSID("YDZS_*");

            if (ssid != null) {
                Toast.makeText(ReceiveActivity.this, "找到AP了！", Toast.LENGTH_SHORT).show();
                Log.d(TAG, ssid);
                unregisterReceiver(this);
                srar = null;
                //注册接收网络变化
                nscr = new NetworkStateChangeReceiver();
                registerReceiver(nscr, new IntentFilter(WifiManager.NETWORK_STATE_CHANGED_ACTION));
                mReceiveWifiManager.addNetworkWithoutPasswork(ssid);
            } else {
                Log.d("noFindCount", String.valueOf(noFindCount));
                if (++noFindCount >= 3) {
                    Toast.makeText(ReceiveActivity.this, "没有发现AP", Toast.LENGTH_SHORT).show();
                    srar = null;
                    unregisterReceiver(this);
                }
            }

        }
    }

    class WifiStateChangedReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            int wifiState = intent.getIntExtra(WifiManager.EXTRA_WIFI_STATE, 0);
            switch (wifiState) {
                case WifiManager.WIFI_STATE_DISABLING:
                    break;
                case WifiManager.WIFI_STATE_DISABLED:
                    break;
                case WifiManager.WIFI_STATE_ENABLING:
                    break;
                case WifiManager.WIFI_STATE_ENABLED:
                    unregisterReceiver(this);
                    wscr = null;
                    srar = new ScanResultsAvailableReceiver();
                    registerReceiver(srar,
                            new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));
                    Toast.makeText(ReceiveActivity.this, "正在扫描附近AP", Toast.LENGTH_SHORT).show();
                    mReceiveWifiManager.startScan();
                    break;
                case WifiManager.WIFI_STATE_UNKNOWN:
                    break;
            }
        }
    }

    class NetworkStateChangeReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            NetworkInfo info = intent.getParcelableExtra(WifiManager.EXTRA_NETWORK_INFO);
            if (NetworkInfo.State.CONNECTED == info.getState()) {
                WifiInfo wifiInfo = intent.getParcelableExtra(WifiManager.EXTRA_WIFI_INFO);
                Log.d(wifiInfo.getSSID(), String.valueOf(info.getState()));
                if (!isConnected) {
                    isConnected = true;
                    Toast.makeText(ReceiveActivity.this, String.valueOf(info.getState()), Toast.LENGTH_SHORT).show();
                    Log.d(wifiInfo.getSSID(), String.valueOf(isConnected));
//                    mUDPHelper.setLife(true);
//                    mUDPHelper.send(android.os.Build.MODEL.getBytes());
                }
            } else if (NetworkInfo.State.DISCONNECTED == info.getState()) {
                if (isConnected) {
                    isConnected = false;
                    unregisterReceiver(this);
                    nscr = null;
                    Toast.makeText(ReceiveActivity.this, String.valueOf(info.getState()), Toast.LENGTH_SHORT).show();
//                    Log.d(wifiInfo.getSSID(), String.valueOf(isConnected));
                }
            }
        }
    }

}