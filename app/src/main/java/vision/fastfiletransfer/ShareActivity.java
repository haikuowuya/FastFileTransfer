package vision.fastfiletransfer;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.SparseArray;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;

import vis.UserDevice;
import vis.net.protocol.FFTService;
import vis.net.wifi.APHelper;
import vis.net.wifi.ShareWifiManager;


public class ShareActivity extends Activity {

    private static final int FILE_SELECT_CODE = 55;
    private static final String ICICLE_KEY = "ShareActivity";

    //    private ShareWifiManager mShareWifiManager;
    private APHelper mAPHelper;
    private FFTService mFFTService;

    private String filePath;
    /**
     * 连接列表
     */
    private TextView tvFileName;
    private Button btnSelectFile;
    private Button btnSend;
    private TextView tvName;

    /**
     * 设备连接ListView
     */
    private ListView lvDevices;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_share);

//        mShareWifiManager = new ShareWifiManager(this);
        mAPHelper = new APHelper(this);
        mFFTService = new FFTService(this, FFTService.SERVICE_SHARE);

        tvName = (TextView) findViewById(R.id.tvTips);
        lvDevices = (ListView) findViewById(R.id.lvDevices);
        tvFileName = (TextView) findViewById(R.id.tvFileName);
        btnSelectFile = (Button) findViewById(R.id.btnSelectFile);
        btnSend = (Button) findViewById(R.id.btnSend);

        lvDevices.setAdapter(mFFTService.getAdapter());

        this.setAllTheThing();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case FILE_SELECT_CODE:
                if (resultCode == RESULT_OK) {
                    // Get the Uri of the selected file
                    filePath = FFTService.getRealPathFromURI(this, data.getData());
                    this.tvFileName.setText(filePath.substring(filePath.lastIndexOf("/") + 1));
                    tvFileName.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent("android.intent.action.VIEW");
                            intent.addCategory("android.intent.category.DEFAULT");
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            Uri uri = Uri.fromFile(new File(filePath));
                            {
                                //暂时只能打开图片
                                intent.setDataAndType(uri, "image/*");
                            }
                            startActivity(intent);
                        }
                    });
                }
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        //关闭AP
//        if (mShareWifiManager.setWifiApEnabled(false)) {
        if (mAPHelper.setWifiApEnabled(null, false)) {
            Toast.makeText(ShareActivity.this, "热点关闭", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mFFTService.setOnDataReceivedListener(null);
        mFFTService.disable();
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
//                Toast.makeText(ShareActivity.this, tvFileName.getText().toString(), Toast.LENGTH_SHORT).show();
                mFFTService.sendFlies(ShareActivity.this, filePath);
            }
        });
        if (!mAPHelper.isApEnabled()) {
            //开启AP
            if (mAPHelper.setWifiApEnabled(APHelper.createWifiCfg(APHelper.SSID), true)) {
                Toast.makeText(ShareActivity.this, "热点开启", Toast.LENGTH_SHORT).show();
                tvName.setText("本机：" + new String(FFTService.LOCALNAME));
                mFFTService.enable();
                mFFTService.setOnDataReceivedListener(new FFTService.OnDataReceivedListener() {
                    @Override
                    public void onDataReceived(SparseArray<UserDevice> devicesList) {
                        //保留这个接口
//                devicesListIsChanged(devicesList);
                    }
                });
            } else {
                tvName.setText("打开热点失败");
            }
        }
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
