package vision.fastfiletransfer;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import vis.net.protocol.FFTService;
import vis.net.protocol.SwapPackage;
import vis.net.wifi.ShareWifiManager;


public class ShareActivity extends Activity {

    private static final int FILE_SELECT_CODE = 55;

    private ShareWifiManager mShareWifiManager;
    private FFTService mFFTService;

    private  Uri fileUri;
    /**
     * 连接列表
     */
    private ListView lvConnectedDevices;
    private TextView tvFileName;
    private Button btnSelectFile;
    private Button btnSend;
    private TextView tvTips;
    private List<Map<String, String>> mData;
    private SimpleAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_share);

        mShareWifiManager = new ShareWifiManager(this);
        mFFTService = new FFTService();

        tvTips = (TextView) findViewById(R.id.tvTips);
        lvConnectedDevices = (ListView) findViewById(R.id.lvConnectedDevices);
        tvFileName = (TextView) findViewById(R.id.tvFileName);
        btnSelectFile = (Button) findViewById(R.id.btnSelectFile);
        btnSend = (Button) findViewById(R.id.btnSend);

        if (mShareWifiManager.setWifiApEnabled(true)) {
            Toast.makeText(ShareActivity.this, "热点开启", Toast.LENGTH_SHORT).show();
            tvTips.setText(mShareWifiManager.getSSID());
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
                    fileUri = data.getData();
//                    String path = FileUtils.getPath(this, uri);
                    this.tvFileName.setText(fileUri.toString());
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
        mFFTService.disableTransmission();
        mFFTService.setOnDataReceivedListener(null);
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

        mData = new ArrayList<Map<String, String>>();
        adapter = new SimpleAdapter(this, mData, android.R.layout.simple_list_item_2,
                new String[]{"title", "text"}, new int[]{android.R.id.text1, android.R.id.text2});
        lvConnectedDevices.setAdapter(adapter);

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
                mFFTService.sendFlies(ShareActivity.this, fileUri);
            }
        });
        mFFTService.enableTransmission();
        mFFTService.setOnDataReceivedListener(new FFTService.OnDataReceivedListener() {
            @Override
            public void onDataReceived(Map<String, String> devicesList) {
                devicesListIsChanged(devicesList);
            }

        });

    }

    private void devicesListIsChanged(Map<String, String> data) {
        //这里的效率有待考究
        mData.clear();
        for (Map.Entry<String, String> entry : data.entrySet()) {
            Map<String, String> map = new HashMap<String, String>();
            map.put("title", entry.getValue());
            map.put("text", entry.getKey());
            mData.add(map);
        }
        adapter.notifyDataSetChanged();
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
