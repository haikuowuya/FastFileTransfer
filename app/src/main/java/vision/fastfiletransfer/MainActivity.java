package vision.fastfiletransfer;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import java.lang.reflect.Method;

public class MainActivity extends Activity {

    private Button btnShare;
    private Button btnReceive;
    private TextView tvModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        tvModel = (TextView) findViewById(R.id.tvModel);
        btnShare = (Button) findViewById(R.id.btnShare);
        btnReceive = (Button) findViewById(R.id.btnReceive);
        tvModel.setText(android.os.Build.MODEL.replaceAll("-", ""));
        Log.d("SSID:", android.os.Build.MODEL + ","
                + getSerialNumber());
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

    public static String getSerialNumber() {
        String serial = null;
        try {
            Class<?> c = Class.forName("android.os.SystemProperties");
            Method get = c.getMethod("get", String.class);
            serial = (String) get.invoke(c, "ro.serialno");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return serial;
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

        return super.onOptionsItemSelected(item);
    }

}
