package vis;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.lang.ref.WeakReference;

import vis.widget.TextProgress;
import vision.fastfiletransfer.R;

/**
 * 用户列表的数据适配器
 */

public class UserFilesAdapter extends FFTAdapter {

    private SparseArray<UserFile> dataList = null;
    private LayoutInflater inflater = null;
    private Context mContext;

    public UserFilesAdapter(Context context) {
        this.inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.dataList = new SparseArray<UserFile>();
        this.mContext = context;
    }

    @Override
    public int getCount() {
        return dataList.size();
    }

    @Override
    public Object getItem(int position) {
        return dataList.get(position);
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
            convertView = inflater.inflate(R.layout.listitem_files, null);
            holder.layout = (LinearLayout) convertView
                    .findViewById(R.id.list_item_layout);
            holder.icon = (ImageView) convertView
                    .findViewById(R.id.image);
            holder.name = (TextView) convertView
                    .findViewById(R.id.name);
            holder.tips = (TextView) convertView.findViewById(R.id.app_tips);
            holder.size = (TextProgress) convertView.findViewById(R.id.app_size_progressBar);
            holder.btn = (Button) convertView
                    .findViewById(R.id.download_btn);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        // 这里position和app.id的值是相等的
//        final UserDevice userDevice = dataList.get(position);
        //这里并不关心key，不能用get(key)
        final UserFile userFile = dataList.valueAt(position);
        holder.name.setText(userFile.name);
        Drawable drawable = mContext.getResources().getDrawable(R.mipmap.app_icon);
        holder.icon.setImageDrawable(drawable);

        switch (userFile.state) {
            case UserFile.TRANSFER_STATE_NORMAL:
                holder.tips.setText("就绪");
                break;
            case UserFile.TRANSFER_STATE_TRANSFERRING:
                holder.size.setVisibility(View.VISIBLE);
                holder.tips.setVisibility(View.GONE);
                holder.size.setProgress((int) (userFile.completed * 100 / userFile.size));
                break;
            case UserFile.TRANSFER_STATE_FINISH:
                holder.tips.setText("传输完成");
                holder.tips.setVisibility(View.VISIBLE);
                holder.size.setVisibility(View.GONE);
                holder.btn.setVisibility(View.VISIBLE);
                holder.btn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Toast.makeText(mContext, userFile.name, Toast.LENGTH_SHORT)
                                .show();
                        openFile(userFile.name);
                    }
                });
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
        Button btn;
    }

    private static class MyHandler extends Handler {
        private final WeakReference<UserFilesAdapter> mUserFilesAdapter;

        public MyHandler(UserFilesAdapter ufa) {
            mUserFilesAdapter = new WeakReference<UserFilesAdapter>(ufa);
        }

        @Override
        public void handleMessage(Message msg) {
            UserFilesAdapter ufa = mUserFilesAdapter.get();
            if (ufa != null) {
                UserFile userFile = (UserFile)
                        msg.obj;
                if (ufa.dataList.size() <= userFile.id) {
                    ufa.dataList.put(((int)userFile.id), userFile);
                }
                // notifyDataSetChanged会执行getView函数，更新所有可视item的数据
                ufa.notifyDataSetChanged();
                // 只更新指定item的数据，提高了性能
//            updateView(msg.what);
            }
        }
    }

    /**
     * 交给其它线程控制的Handler
     */
    private final MyHandler mHandler = new MyHandler(this);

    public Handler getHandler() {
        return mHandler;
    }

    public void put(int key, Object obj) {

    }

    public void remove(int address) {
    }

    public Object getObject(int index) {
        return null;
    }

    private void openFile(String filename) {
        Intent intent = new Intent("android.intent.action.VIEW");
        intent.addCategory("android.intent.category.DEFAULT");
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        Uri uri = Uri.fromFile(new File(Environment.getExternalStorageDirectory().getPath() + "/FFT/" + filename));
        {
            //暂时只能打开图片
            intent.setDataAndType(uri, "image/*");
        }
        mContext.startActivity(intent);
    }

}
