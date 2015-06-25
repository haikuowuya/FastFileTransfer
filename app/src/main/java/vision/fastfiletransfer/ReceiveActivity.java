package vision.fastfiletransfer;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.NetworkInfo;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import vis.net.protocol.FFTService;
import vis.net.wifi.ReceiveWifiManager;
import vis.net.wifi.WifiHelper;


public class ReceiveActivity extends Activity {
//    private ReceiveWifiManager mReceiveWifiManager;
    private WifiHelper mWifiHelper;
    private FFTService mFFTService;
    private WifiStateChangedReceiver wscr;
    private NetworkStateChangeReceiver nscr;
    private ScanResultsAvailableReceiver srar;
    private TextView tvTips;
    private TextView tvName;
    private ListView lvFiles;
    private boolean isConnected = false;
    private String ssid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_receive);

        mReceiveWifiManager = new ReceiveWifiManager(this);
        mWifiHelper = new WifiHelper(this);
        mFFTService = new FFTService(this, FFTService.SERVICE_RECEIVE);

        tvTips = (TextView) findViewById(R.id.tvTips);
        tvName = (TextView) findViewById(R.id.tvName);
        tvName.setText(new String(FFTService.LOCALNAME));
        lvFiles = (ListView) findViewById(R.id.lvFiles);
        lvFiles.setAdapter(mFFTService.getAdapter());

        wscr = new WifiStateChangedReceiver();
        registerReceiver(wscr, new IntentFilter(WifiManager.WIFI_STATE_CHANGED_ACTION));
        mReceiveWifiManager.setWifiEnabled(true);
        mWifiHelper.setWifiEnabled(true);
        tvTips.setText("正在打开wifi……");
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        mFFTService.sendLogout();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
//        mFFTService.disable();
        mReceiveWifiManager.disableNetwork();
        mWifiHelper.removeNetwork();

        if (wscr != null) {
            unregisterReceiver(wscr);
        }
        if (srar != null) {     //扫描监听不为空
            unregisterReceiver(srar);
        }
        if (nscr != null) {     //网络状态监听不为空
            unregisterReceiver(nscr);
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
//            Log.d(TAG, "ScanResultsAvailableReceiver");
//            ArrayList<String> al = mReceiveWifiManager.findSSID("YDZS_*");
           List<ScanResult> wifiList = mWifiManager.getScanResults();
            ArrayList<String> al = WifiHelper.findSSID(wifiList,"YDZS_*")
            if (al.size() > 0) {
//                Toast.makeText(ReceiveActivity.this, "找到AP了！", Toast.LENGTH_SHORT).show();
                Log.d(TAG, String.valueOf(al.size()));
                ssid = al.get(0);
                tvTips.setText("尝试连接" + ssid);
                unregisterReceiver(this);
                srar = null;
                //注册接收网络变化
                nscr = new NetworkStateChangeReceiver();
                registerReceiver(nscr, new IntentFilter(WifiManager.NETWORK_STATE_CHANGED_ACTION));
                boolean bln = mReceiveWifiManager.addNetworkWithoutPasswork(ssid);
                Log.d("bln", String.valueOf(bln));
            } else {
                Log.d("noFindCount", String.valueOf(noFindCount));
                if (++noFindCount < 30) {
                    tvTips.setText("第" + (noFindCount + 1) + "次扫描没有发现，开始第" + (noFindCount + 2) + "次扫描……");
                    mReceiveWifiManager.startScan();
                } else {
                    tvTips.setText("扫描了30次，没有发现可以连接的热点。");
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
//                    Toast.makeText(ReceiveActivity.this, "正在扫描附近AP", Toast.LENGTH_SHORT).show();
                    tvTips.setText("正在扫描附近热点……");
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
            if (NetworkInfo.State.CONNECTED.equals(info.getState()) && info.isConnected()) {
                Log.d(this.getClass().getName(), String.valueOf(info.getState()));
                isConnected = true;
                Toast.makeText(ReceiveActivity.this, String.valueOf(info.getState()), Toast.LENGTH_SHORT).show();
//                tvTips.setText("已连接:" + ssid);
                tvTips.setText("等待" + ssid.substring(5, ssid.length() - 6) + "发送文件");
                mFFTService.enable();
                mFFTService.sendLogin(mReceiveWifiManager.getServerAddressByStr());
            } else if (isConnected && NetworkInfo.State.DISCONNECTED.equals(info.getState()) && !info.isConnected()) {
                Log.d(this.getClass().getName(), String.valueOf(info.getState()));
                mFFTService.disable();
//                isConnected = false;
//                unregisterReceiver(this);
//                nscr = null;
//                Toast.makeText(ReceiveActivity.this, String.valueOf(info.getState()), Toast.LENGTH_SHORT).show();
//                tvTips.setText("断开了");
//                mReceiveWifiManager.recoveryNetwork();
            }
        }
    }

}
