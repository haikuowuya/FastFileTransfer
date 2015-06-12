package vision.fastfiletransfer;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import vis.net.UDPHelper;
import vis.net.protocol.FFTP;
import vis.net.protocol.SwapPackage;
import vis.net.wifi.ShareWifiManager;


public class ShareActivity extends Activity {
    private ShareWifiManager mShareWifiManager;
    private FFTP fftp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_share);

        mShareWifiManager = new ShareWifiManager(this);
        fftp = new FFTP();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mShareWifiManager.setWifiApEnabled(true))
            Toast.makeText(ShareActivity.this, "热点开启", Toast.LENGTH_SHORT).show();
        fftp.setOnDataReceivedListener(new FFTP.OnDataReceivedListener() {
            @Override
            public void onDataReceived(SwapPackage sp) {

            }

            @Override
            public void onLogin(String name) {
                Toast.makeText(ShareActivity.this, name + "登入", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onLogout(String name) {
                Toast.makeText(ShareActivity.this, name + "登出", Toast.LENGTH_SHORT).show();
            }

        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mShareWifiManager.setWifiApEnabled(false))
            Toast.makeText(ShareActivity.this, "热点关闭", Toast.LENGTH_SHORT).show();
        fftp.setOnDataReceivedListener(null);
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