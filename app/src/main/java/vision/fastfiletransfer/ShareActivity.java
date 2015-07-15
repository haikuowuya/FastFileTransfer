package vision.fastfiletransfer;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.SparseArray;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import vis.DevicesList;
import vis.SelectedFilesQueue;
import vis.UserDevice;
import vis.net.protocol.ShareServer;
import vis.net.wifi.APHelper;
import vision.resourcemanager.File;
import vision.resourcemanager.FileFolder;
import vision.resourcemanager.RMGridFragmentImage;
import vision.resourcemanager.ResourceManagerInterface;


public class ShareActivity extends FragmentActivity implements ResourceManagerInterface {

    private APHelper mAPHelper;
    public ShareServer mShareServer;
    private SparseArray<FileFolder> mImagesFolder;
    /**
     * 文件选择队列
     */
    public SelectedFilesQueue<File> mSelectedFilesQueue;
    /**
     * 用户设备接入列表
     */
    public DevicesList<UserDevice> mDevicesList;

    private FragmentManager fragmentManager;
    private RMFragment mRMFragment;
    private TextView tvTitle;
    private Button btnTitleBarRight;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
        setContentView(R.layout.activity_share);
        getWindow().setFeatureInt(
                Window.FEATURE_CUSTOM_TITLE,
                R.layout.activity_titlebar
        );

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
        tvTitle = (TextView) findViewById(R.id.titlebar_tvtitle);
        tvTitle.setText("我要分享");

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

        btnTitleBarRight = (Button) findViewById(R.id.titlebar_btnRight);

        jumpToFragment(RM_FRAGMENT, 0);
    }

    @Override
    protected void onResume() {
        super.onResume();
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    }

    @Override
    protected void onPause() {
        super.onPause();
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    }

    @Override
    protected void onDestroy() {
        mShareServer.disable();
        //关闭AP
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

    @Override
    public void jumpToFragment(int fragmentType, int indexOfFolder) {
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        switch (fragmentType) {
            case RM_FRAGMENT: {
                mRMFragment = RMFragment.newInstance(
                        ResourceManagerInterface.TYPE_FILE_TRANSFER,
                        /*RMFragment.TYPE_RESOURCE_MANAGER,*/
                        RMFragment.PAGE_AUDIO | RMFragment.PAGE_IMAGE | RMFragment.PAGE_APP | RMFragment.PAGE_VIDEO | RMFragment.PAGE_TEXT);

                fragmentTransaction.replace(R.id.shareContain, mRMFragment);
                break;
            }
            case SHARE_FRAGMENT: {
                ShareFragment mShareFragment = ShareFragment.newInstance(null, null);
                //隐藏
                fragmentTransaction.hide(mRMFragment);
                fragmentTransaction.add(R.id.shareContain, mShareFragment);
                //这里可以回退
                fragmentTransaction.addToBackStack(null);
                break;
            }
            case RM_IMAGE_GRID: {
                RMGridFragmentImage rmGridFragmentImage = RMGridFragmentImage.newInstance(indexOfFolder, null);
                fragmentTransaction.hide(mRMFragment);
                fragmentTransaction.add(R.id.shareContain, rmGridFragmentImage);
                fragmentTransaction.addToBackStack(null);
                break;
            }
            default: {
                return;
            }
        }
        fragmentTransaction.commit();
    }

    @Override
    public SelectedFilesQueue<File> getSelectedFilesQueue() {
        if (null == mSelectedFilesQueue) {
            mSelectedFilesQueue = new SelectedFilesQueue<File>();
        }
        return this.mSelectedFilesQueue;
    }

    @Override
    public SparseArray<FileFolder> getImageFolder() {
        if (null == mImagesFolder) {
            mImagesFolder = new SparseArray<FileFolder>();
        }
        return mImagesFolder;
    }

    @Override
    public void setTitleText(String string) {
        this.tvTitle.setText(string);
    }

    @Override
    public String getTitleText() {
        return this.tvTitle.getText().toString();
    }

    @Override
    public Button getTitleRightBtn() {
        return this.btnTitleBarRight;
    }

}
