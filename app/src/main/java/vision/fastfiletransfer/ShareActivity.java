package vision.fastfiletransfer;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import vis.net.protocol.FFTService;
import vis.net.protocol.SwapPackage;
import vis.net.wifi.ShareWifiManager;


public class ShareActivity extends Activity {

    private static final int FILE_SELECT_CODE = 55;

    private ShareWifiManager mShareWifiManager;
    private FFTService FFTService;
    private ListView lvConnectedDevices;
    private TextView tvFileName;
    private Button btnSelectFile;
    private Button btnSend;
    private TextView tvTips;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_share);

        mShareWifiManager = new ShareWifiManager(this);
        FFTService = new FFTService();

        tvTips = (TextView) findViewById(R.id.tvTips);
        lvConnectedDevices = (ListView) findViewById(R.id.lvConnectedDevices);
        tvFileName = (TextView) findViewById(R.id.tvFileName);
        btnSelectFile = (Button) findViewById(R.id.btnSelectFile);
        btnSend = (Button) findViewById(R.id.btnSend);

        if (mShareWifiManager.setWifiApEnabled(true)) {
            Toast.makeText(ShareActivity.this, "热点开启", Toast.LENGTH_SHORT).show();
            tvTips.setText(Build.MODEL + "00");
        } else {
            tvTips.setText("打开热点失败");
        }
        this.setAllTheThing();
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case FILE_SELECT_CODE:
                if (resultCode == RESULT_OK) {
                    // Get the Uri of the selected file
                    Uri uri = data.getData();
//                    String path = FileUtils.getPath(this, uri);
                    this.tvFileName.setText(uri.toString());
                }
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onResume() {
        super.onResume();
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
        if (mShareWifiManager.setWifiApEnabled(false))
            Toast.makeText(ShareActivity.this, "热点关闭", Toast.LENGTH_SHORT).show();
        FFTService.setOnDataReceivedListener(null);
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

    /**
     * set好所有的东西
     */
    private void setAllTheThing() {
        btnSelectFile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showFileChooser();
            }
        });

        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(ShareActivity.this,"send to which connected devices",Toast.LENGTH_SHORT).show();
            }
        });

        FFTService.setOnDataReceivedListener(new FFTService.OnDataReceivedListener() {
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

    /**
     * 显示文件选择器
     */
    private void showFileChooser() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("*/*");
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        try {
            startActivityForResult(Intent.createChooser(intent, "Select a File to Upload"), FILE_SELECT_CODE);
        } catch (android.content.ActivityNotFoundException ex) {
            Toast.makeText(this, "Please install a File Manager.", Toast.LENGTH_SHORT).show();
        }
    }



}
