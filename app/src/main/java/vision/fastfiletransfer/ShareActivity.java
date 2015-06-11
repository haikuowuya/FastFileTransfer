package vision.fastfiletransfer;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import vis.net.UDPHelper;
import vis.net.wifi.ShareWifiManager;


public class ShareActivity extends Activity {
    private ShareWifiManager mShareWifiManager;
    private UDPHelper mUDPHelper;
    public Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            Toast.makeText(ShareActivity.this, new String((byte[]) msg.obj).trim(), Toast.LENGTH_LONG).show();
        }

    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_share);

        mShareWifiManager = new ShareWifiManager(this);
        mUDPHelper = new UDPHelper(handler);
    }

    @Override
    protected void onResume() {
        super.onResume();
//        if (!mShareWifiManager.isApEnabled())
        if (mShareWifiManager.setWifiApEnabled(true))
            Toast.makeText(ShareActivity.this, "热点开启", Toast.LENGTH_SHORT).show();
        mUDPHelper.enableReceiver();
    }

    @Override
    protected void onPause() {
        super.onPause();
//        if (mShareWifiManager.isApEnabled())
        if (mShareWifiManager.setWifiApEnabled(false))
            Toast.makeText(ShareActivity.this, "热点关闭", Toast.LENGTH_SHORT).show();
        mUDPHelper.setLife(false);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mShareWifiManager.finish();
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

}