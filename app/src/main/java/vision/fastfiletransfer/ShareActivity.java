package vision.fastfiletransfer;

import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import vis.net.wifi.APHelper;
import vision.RM.RMFragment;


public class ShareActivity extends FragmentActivity implements RMFragment.OnFragmentInteractionListener, ShareFragment.OnFragmentInteractionListener {

    public static final int RM_FRAGMENT = 0;
    public static final int SHARE_FRAGMENT = 1;
    //    private static final String ICICLE_KEY = "ShareActivity";
    //    private ShareWifiManager mShareWifiManager;
    private APHelper mAPHelper;

//    private String filePath;
    /**
     * 连接列表
     */
//    private TextView tvFileName;
//    private Button btnSelectFile;
//    private Button btnSend;
//    private TextView tvName;

    /**
     * 设备连接ListView
     */
//    private ListView lvDevices;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_share);

        jumpToFragment(RM_FRAGMENT);

//        mShareWifiManager = new ShareWifiManager(this);
        mAPHelper = new APHelper(this);
//        mFFTService = new FFTService(this, FFTService.SERVICE_SHARE);

//        tvName = (TextView) findViewById(R.id.tvTips);
//        lvDevices = (ListView) findViewById(R.id.lvDevices);
//        tvFileName = (TextView) findViewById(R.id.tvFileName);
//        btnSelectFile = (Button) findViewById(R.id.btnSelectFile);
//        btnSend = (Button) findViewById(R.id.btnSend);

//        lvDevices.setAdapter(mFFTService.getAdapter());

//        this.setAllTheThing();
        if (!mAPHelper.isApEnabled()) {
            //开启AP
            if (mAPHelper.setWifiApEnabled(APHelper.createWifiCfg(APHelper.SSID), true)) {
                Toast.makeText(ShareActivity.this, "热点开启", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(ShareActivity.this, "打开热点失败", Toast.LENGTH_SHORT).show();
            }
        }

    }

    @Override
    protected void onDestroy() {
        //关闭AP
//        if (mShareWifiManager.setWifiApEnabled(false)) {
        if (mAPHelper.setWifiApEnabled(null, false)) {
            Toast.makeText(ShareActivity.this, "热点关闭", Toast.LENGTH_SHORT).show();
        }
        super.onDestroy();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_share, menu);
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

    public void jumpToFragment(int fragmentType) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        switch (fragmentType) {
            case RM_FRAGMENT: {
                RMFragment mRMFragment = RMFragment.newInstance("hello", "hi");
                fragmentTransaction.replace(R.id.shareContain, mRMFragment);
                break;
            }
            case SHARE_FRAGMENT: {
                ShareFragment shareFragment = ShareFragment.newInstance("", "");
                fragmentTransaction.replace(R.id.shareContain, shareFragment);
                break;
            }
            default: {
                return;
            }
        }
        fragmentTransaction.commit();
    }

    @Override
    public void onFragmentInteraction(Uri uri) {
        jumpToFragment(SHARE_FRAGMENT);
    }


}

