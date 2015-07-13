package vision.fastfiletransfer;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import vis.SelectedFilesQueue;
import vision.resourcemanager.File;
import vision.resourcemanager.GridImageFragment;
import vision.resourcemanager.ResourceManagerInterface;


public class ResourceManagerActivity extends FragmentActivity implements ResourceManagerInterface {
    private FragmentManager fragmentManager;
    private RMFragment mRMFragment;
    /**
     * 文件选择队列
     */
    public SelectedFilesQueue<File> mSelectedFilesQueue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
        setContentView(R.layout.activity_resource_manager);
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
        TextView tvTitle = (TextView) findViewById(R.id.titlebar_tvtitle);
        tvTitle.setText("资源管理");

        mSelectedFilesQueue = new SelectedFilesQueue<File>();
        jumpToFragment(0, null);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_resource_manager, menu);
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
    public void jumpToFragment(int fragmentType, @Nullable String bucket) {
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        switch (fragmentType) {
            case RM_FRAGMENT: {
                mRMFragment = RMFragment.newInstance(
                        /*RMFragment.TYPE_FILE_TRANSFER,*/
                        RMFragment.TYPE_RESOURCE_MANAGER,
                        RMFragment.PAGE_AUDIO | RMFragment.PAGE_IMAGE /*| RMFragment.PAGE_APP*/ | RMFragment.PAGE_VIDEO | RMFragment.PAGE_TEXT);
                fragmentTransaction.replace(R.id.rmContain, mRMFragment);
                break;
            }
            case RM_IMAGE_GRID: {
                GridImageFragment gridImageFragment = GridImageFragment.newInstance(bucket, null);
                fragmentTransaction.hide(mRMFragment);
                fragmentTransaction.add(R.id.rmContain, gridImageFragment);
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
    public void onFragmentInteraction(int arg1, String bucket) {
        jumpToFragment(arg1, bucket);
    }

    @Override
    public SelectedFilesQueue<File> getSelectedFilesQueue() {
        return this.mSelectedFilesQueue;
    }
}
