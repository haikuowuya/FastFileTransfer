package vis;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Message;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.lang.ref.WeakReference;

import vis.widget.TextProgress;
import vision.fastfiletransfer.R;

/**
 * 用户列表的数据适配器
 */

public class UserDevicesAdapter extends FFTAdapter {

    private SparseArray<UserDevice> mDevicesList = null;
    private LayoutInflater inflater = null;
    private Context mContext;

    /**
     * 交给其它线程控制的Handler
     */
    private Handler mHandler = new MyHandler(this);

    private static class MyHandler extends Handler {
        private final WeakReference<UserDevicesAdapter> mUserDevicesAdapter;

        public MyHandler(UserDevicesAdapter uda) {
            mUserDevicesAdapter = new WeakReference<UserDevicesAdapter>(uda);
        }

        @Override
        public void handleMessage(Message msg) {
            UserDevicesAdapter uda = mUserDevicesAdapter.get();
            if (uda != null) {
                UserDevice ud = uda.mDevicesList.valueAt(msg.what);
                ud.completed = msg.arg1;
                ud.state = msg.arg2;
//                if (ud.completed == 100) {
////                    Toast.makeText(uda.mContext, "传输完成", Toast.LENGTH_SHORT)
////                            .show();
//                }
                // notifyDataSetChanged会执行getView函数，更新所有可视item的数据
                uda.notifyDataSetChanged();
                // 只更新指定item的数据，提高了性能
//            updateView(msg.what);
            }
        }
    }

    public UserDevicesAdapter(Context context) {
        this.inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.mDevicesList = new SparseArray<UserDevice>();
        this.mContext = context;
    }

    @Override
    public int getCount() {
        return mDevicesList.size();
    }

    @Override
    public Object getItem(int position) {
        return mDevicesList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        final ViewHolder holder;
        if (null == convertView) {
            holder = new ViewHolder();
            convertView = inflater
                    .inflate(R.layout.listitem_devices, null);
            holder.layout = (LinearLayout) convertView
                    .findViewById(R.id.list_item_layout);
            holder.icon = (ImageView) convertView
                    .findViewById(R.id.image);
            holder.name = (TextView) convertView
                    .findViewById(R.id.name);
            holder.tips = (TextView) convertView
                    .findViewById(R.id.app_tips);
            holder.size = (TextProgress) convertView
                    .findViewById(R.id.app_size_progressBar);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        // 这里position和app.id的值是相等的
//        final UserDevice userDevice = mDevicesList.get(position);
        //这里并不关心key，不能用get(key)
        final UserDevice userDevice = mDevicesList.valueAt(position);
        holder.name.setText(userDevice.name);
        Drawable drawable = mContext.getResources().getDrawable(R.mipmap.app_icon);
        holder.icon.setImageDrawable(drawable);

        switch (userDevice.state) {
            case UserDevice.TRANSFER_STATE_NORMAL:
                holder.tips.setText("就绪");
                break;
            case UserDevice.TRANSFER_STATE_TRANSFERRING:
                holder.size.setVisibility(View.VISIBLE);
                holder.tips.setVisibility(View.GONE);
                holder.size.setProgress(userDevice.completed);
                break;
            case UserDevice.TRANSFER_STATE_FINISH:
                holder.tips.setText("传输完成");
                holder.tips.setVisibility(View.VISIBLE);
                holder.size.setVisibility(View.GONE);
                break;
        }
        return convertView;
    }

    /**
     * 暂存变量类
     */
    static class ViewHolder {
        LinearLayout layout;
        ImageView icon;
        TextView name;
        TextView tips;
        TextProgress size;
    }

    public Handler getHandler() {
        return this.mHandler;
    }

    public void put(int key, Object obj) {
        mDevicesList.put(key, (UserDevice) obj);
        notifyDataSetChanged();
    }

    public void remove(int address) {
        mDevicesList.remove(address);
        notifyDataSetChanged();
    }

    public Object getObject(int index) {
        return mDevicesList.valueAt(index);
    }

}
