package vision.fastfiletransfer;

import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import vis.TransmissionQueue;
import vis.net.wifi.APHelper;
import vision.RM.FragmentImage;
import vision.RM.FragmentMusic;
import vision.RM.FragmentText;
import vision.RM.FragmentVideo;


public class ShareActivity extends FragmentActivity implements
        RMFragment.OnFragmentInteractionListener,
        ShareFragment.OnFragmentInteractionListener,
        FragmentImage.OnRMFragmentListener,
        FragmentMusic.OnFragmentInteractionListener,
        FragmentVideo.OnFragmentInteractionListener,
        FragmentText.OnFragmentInteractionListener {

    public static final int RM_FRAGMENT = 0;
    public static final int SHARE_FRAGMENT = 1;

    public TransmissionQueue mTransmissionQueue;

    private APHelper mAPHelper;
    private FragmentManager fragmentManager = getSupportFragmentManager();
    private RMFragment mRMFragment;
    private ShareFragment mShareFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_share);

        mAPHelper = new APHelper(this);
        mTransmissionQueue = new TransmissionQueue();
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
        switch (fragmentType) {
            case RM_FRAGMENT: {
                mRMFragment = RMFragment.newInstance("hello", "hi");
                fragmentTransaction.replace(R.id.shareContain, mRMFragment);
                break;
            }
            case SHARE_FRAGMENT: {
                mShareFragment = ShareFragment.newInstance("", "");
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

    @Override
    public void onFragmentInteraction(Uri uri) {
//        jumpToFragment(SHARE_FRAGMENT);
    }


    @Override
    public void onSelectionChanged() {
        //        mTransmissionQueue.put();

    }
}
