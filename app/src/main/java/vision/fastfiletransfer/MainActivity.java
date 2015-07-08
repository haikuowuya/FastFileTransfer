package vision.fastfiletransfer;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends FragmentActivity {

    private Button btnShare;
    private Button btnReceive;
    private TextView tvModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_CUSTOM_TITLE); //声明使用自定义标题
        setContentView(R.layout.activity_main);
        //设置窗体样式
        getWindow().setFeatureInt(
                Window.FEATURE_CUSTOM_TITLE,  //设置此样式为自定义样式
                R.layout.activity_titlebar //设置对应的布局
        );//自定义布局赋值
        Button btnTitleBarLeft = (Button) findViewById(R.id.titlebar_btnLeft);
        btnTitleBarLeft.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        TextView tvTitle = (TextView) findViewById(R.id.titlebar_tvtitle);
        tvTitle.setText("文件快传");

        tvModel = (TextView) findViewById(R.id.tvModel);
        btnShare = (Button) findViewById(R.id.btnShare);
        btnReceive = (Button) findViewById(R.id.btnReceive);
        tvModel.setText("本机: "+android.os.Build.MODEL.replaceAll("\\s|-", ""));
        Log.d("SSID:", android.os.Build.MODEL.replaceAll("\\s|-", ""));
        btnShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent shareIntent = new Intent(MainActivity.this, ShareActivity.class);
                startActivity(shareIntent);
            }
        });
        btnReceive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent receiveIntent = new Intent(MainActivity.this, ReceiveActivity.class);
                startActivity(receiveIntent);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
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
        if (id == R.id.res_mag) {
            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            RMFragment mRMFragment = RMFragment.newInstance(null, null);
            fragmentTransaction.replace(R.id.fragment_container, mRMFragment);
            fragmentTransaction.commit();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
