package vision.fastfiletransfer;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;

import vis.net.protocol.FFTService;
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
        requestWindowFeature(Window.FEATURE_CUSTOM_TITLE); //声明使用自定义标题
        setContentView(R.layout.activity_receive);
        getWindow().setFeatureInt(
                Window.FEATURE_CUSTOM_TITLE,  //设置此样式为自定义样式
                R.layout.activity_titlebar //设置对应的布局
        );//自定义布局赋值

        Button btnTitleBarLeft = (Button) findViewById(R.id.titlebar_btnLeft);
        btnTitleBarLeft.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try{
                    Runtime runtime=Runtime.getRuntime();
                    runtime.exec("input keyevent " + KeyEvent.KEYCODE_BACK);
                }catch(IOException e){
                    Log.e("Exception when doBack", e.toString());
                }
            }
        });
        TextView tvTitle = (TextView) findViewById(R.id.titlebar_tvtitle);
        tvTitle.setText("我要接收");

//        mReceiveWifiManager = new ReceiveWifiManager(this);
        mWifiHelper = new WifiHelper(this);
        mFFTService = new FFTService(this, FFTService.SERVICE_RECEIVE);

        tvTips = (TextView) findViewById(R.id.tvTips);
        tvName = (TextView) findViewById(R.id.tvName);
        tvName.setText(new String(FFTService.LOCALNAME));
        lvFiles = (ListView) findViewById(R.id.lvFiles);
        lvFiles.setAdapter(mFFTService.getAdapter());

        wscr = new WifiStateChangedReceiver();
        registerReceiver(wscr, new IntentFilter(WifiManager.WIFI_STATE_CHANGED_ACTION));
//        mReceiveWifiManager.setWifiEnabled(true);
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
//        mReceiveWifiManager.disableNetwork();
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
        private boolean bln = false;

        @Override
        public void onReceive(Context context, Intent intent) {
//            Log.d(TAG, "ScanResultsAvailableReceiver");
//            ArrayList<String> al = mReceiveWifiManager.findSSID("YDZS_*");
//           List<ScanResult> wifiList = mWifiManager.getScanResults();
            ArrayList<String> al = mWifiHelper.findSSID("YDZS_*");
//                Toast.makeText(ReceiveActivity.this, "找到AP了！", Toast.LENGTH_SHORT).show();
            Log.d(TAG, String.valueOf(al.size()));
            for (int i = 0; i < al.size(); i++) {
                ssid = al.get(i);
                tvTips.setText("尝试连接" + ssid);
                Log.d(TAG, String.valueOf("尝试连接" + ssid));
                if (mWifiHelper.addNetwork(WifiHelper.createWifiCfg(ssid))) {
                    bln = mWifiHelper.enableNetwork(true);
                    //注销搜索广播接收
//                    srar !=null
                    unregisterReceiver(this);
                    srar = null;
                    //注册接收网络变化
                    nscr = new NetworkStateChangeReceiver();
                    registerReceiver(nscr, new IntentFilter(WifiManager.NETWORK_STATE_CHANGED_ACTION));
                    Log.d("bln", String.valueOf(bln));
                }
//                boolean bln = mReceiveWifiManager.addNetworkWithoutPasswork(ssid);
            }
            if (!bln) {
                Log.d("noFindCount", String.valueOf(noFindCount));
                if (++noFindCount < 30) {
                    tvTips.setText("第" + (noFindCount + 1) + "次扫描没有发现，开始第" + (noFindCount + 2) + "次扫描……");
//                    mReceiveWifiManager.startScan();
                    mWifiHelper.startScan();
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
//                    mReceiveWifiManager.startScan();
                    mWifiHelper.startScan();
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
//                mFFTService.sendLogin(mReceiveWifiManager.getServerAddressByStr());
                mFFTService.sendLogin(mWifiHelper.getServerAddressByStr());
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
