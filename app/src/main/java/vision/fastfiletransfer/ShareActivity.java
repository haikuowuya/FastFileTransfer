package vision.fastfiletransfer;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import vis.DevicesList;
import vis.TransmissionQueue;
import vis.UserDevice;
import vis.net.protocol.ShareServer;
import vis.net.wifi.APHelper;
import vision.resourcemanager.File;


public class ShareActivity extends FragmentActivity {

    public static final int RM_FRAGMENT = 0;
    public static final int SHARE_FRAGMENT = 1;

    private APHelper mAPHelper;
    //    public FFTService mFFTService;
    public ShareServer mShareServer;
    /**
     * 文件发送队列
     */
    public TransmissionQueue<File> mTransmissionQueue;
    /**
     * 用户设备接入列表
     */
    public DevicesList<UserDevice> mDevicesList;

    private FragmentManager fragmentManager;
    private RMFragment mRMFragment;
    private ShareFragment mShareFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_CUSTOM_TITLE); //声明使用自定义标题
        setContentView(R.layout.activity_share);
        getWindow().setFeatureInt(
                Window.FEATURE_CUSTOM_TITLE,  //设置此样式为自定义样式
                R.layout.activity_titlebar //设置对应的布局
        );//自定义布局赋值

        fragmentManager = getSupportFragmentManager();
        Button btnTitleBarLeft = (Button) findViewById(R.id.titlebar_btnLeft);
        btnTitleBarLeft.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (fragmentManager.getBackStackEntryCount() > 0) {
                    fragmentManager.popBackStack();
                } else {
                    finish();
                }
            }
        });
        TextView tvTitle = (TextView) findViewById(R.id.titlebar_tvtitle);
        tvTitle.setText("我要分享");

        mTransmissionQueue = new TransmissionQueue<File>();
//        mDevicesList = new SparseArray<UserDevice>();
        mDevicesList = new DevicesList<UserDevice>();

        mAPHelper = new APHelper(this);
        mShareServer = new ShareServer(this, mDevicesList);
        mShareServer.enable();

        if (!mAPHelper.isApEnabled()) {
            //开启AP
            if (mAPHelper.setWifiApEnabled(APHelper.createWifiCfg(APHelper.SSID), true)) {
                Toast.makeText(ShareActivity.this, "热点开启", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(ShareActivity.this, "打开热点失败", Toast.LENGTH_SHORT).show();
            }
        }
        jumpToFragment(RM_FRAGMENT);
    }


    @Override
    protected void onDestroy() {
//        mFFTService.setOnDataReceivedListener(null);
//        mFFTService.disable();
        mShareServer.disable();
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
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        switch (fragmentType) {
            case RM_FRAGMENT: {
                mRMFragment = RMFragment.newInstance(mTransmissionQueue, null);
                fragmentTransaction.replace(R.id.shareContain, mRMFragment);
                break;
            }
            case SHARE_FRAGMENT: {
                mShareFragment = ShareFragment.newInstance(null, null);
                //隐藏
                fragmentTransaction.hide(mRMFragment);
                fragmentTransaction.add(R.id.shareContain, mShareFragment);
                //这里可以回退
                fragmentTransaction.addToBackStack(null);
                break;
            }
            default: {
                return;
            }
        }
        fragmentTransaction.commit();
    }

}
