package vis;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Message;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import vis.widget.MyProgress;
import vision.fastfiletransfer.R;

/**
 * 用户列表的数据适配器
 */

public class UserListAdapter extends BaseAdapter {

    private SparseArray<UserDevice> dataList = null;
    private LayoutInflater inflater = null;
    private Context mContext;
    //    private DownloadManager downloadManager;
    private ListView listView;

    public UserListAdapter(Context context, SparseArray<UserDevice> dataList) {
        this.inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.dataList = dataList;
        this.mContext = context;
//        this.downloadManager = DownloadManager.getInstance();
//        this.downloadManager.setHandler(mHandler);
    }

    public void setListView(ListView view) {
        this.listView = view;
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

    // 改变下载按钮的样式
//    private void changeBtnStyle(Button btn, boolean enable) {
//        if (enable) {
//            btn.setBackgroundResource(R.drawable.btn_download_norm);
//        } else {
//            btn.setBackgroundResource(R.drawable.btn_download_disable);
//        }
//        btn.setEnabled(enable);
//    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        final ViewHolder holder;
        if (null == convertView) {
            holder = new ViewHolder();
            convertView = inflater.inflate(R.layout.listitem_user, null);
            holder.layout = (LinearLayout) convertView
                    .findViewById(R.id.gamelist_item_layout);
            holder.icon = (ImageView) convertView
                    .findViewById(R.id.app_icon);
            holder.name = (TextView) convertView
                    .findViewById(R.id.app_name);
//			holder.size = (TextView) convertView
//					.findViewById(R.id.app_size);
            holder.size = (MyProgress) convertView.findViewById(R.id.app_size_progressBar);
//            holder.btn = (Button) convertView
//                    .findViewById(R.id.download_btn);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        // 这里position和app.id的值是相等的
//        final UserDevice userDevice = dataList.get(position);
        //这里并不关心key，不能用get(key)
        final UserDevice userDevice = dataList.valueAt(position);

        //Log.e("", "id="+app.id+", name="+app.name);

        holder.name.setText(userDevice.name);
//		holder.size.setText((app.downloadSize * 100.0f / app.size) + "%");
//        holder.size.setProgress(userDevice.downloadSize * 100 / userDevice.size);
        Drawable drawable = mContext.getResources().getDrawable(R.drawable.app_icon);
        holder.icon.setImageDrawable(drawable);

        switch (userDevice.transferState) {
            case UserDevice.TRANSFER_STATE_NORMAL:
                holder.tips = (TextView) convertView.findViewById(R.id.app_tips);
                holder.tips.setText("就绪");
                break;
            case UserDevice.TRANSFER_STATE_TRANSFERRING:
//                Toast.makeText(mContext,"传输中",Toast.LENGTH_SHORT)
//                        .show();

//                holder.btn.setText("下载中");
//                this.changeBtnStyle(holder.btn, false);
                break;
            case UserDevice.TRANSFER_STATE_FINISH:
                holder.tips = (TextView) convertView.findViewById(R.id.app_tips);
                holder.tips.setText("传输完成");
//                holder.btn.setText("打开");
//                this.changeBtnStyle(holder.btn, false);
                break;
        }
//        holder.btn.setOnClickListener(new OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//            }
//        });
        return convertView;
    }

    /**
     * 暂存变量类
     */
    static class ViewHolder {
        LinearLayout layout;
        ImageView icon;
        TextView name;
        //        TextView size;
        TextView tips;
        MyProgress size;
//        Button btn;
    }

    /**
     * 开始传输
     *
     * @param position 项目
     */
    public void startTransfer(int position) {

        final UserDevice userDevice = dataList.get(position);
        if (userDevice.transferState == UserDevice.TRANSFER_STATE_NORMAL) {
//            DownloadFile downloadFile = new DownloadFile();
//            downloadFile.downloadID = userDevice.id;
//            downloadFile.transferState = UserDevice.DOWNLOAD_STATE_WAITING;
//            //改变状态
//            userDevice.transferState = UserDevice.DOWNLOAD_STATE_WAITING;
//            downloadFile.downloadSize = userDevice.downloadSize;
//            downloadFile.totalSize = userDevice.size;

//        holder.btn.setText("排队中");
//        changeBtnStyle(holder.btn, false);
//            downloadManager.startDownload(downloadFile);

        } else if (userDevice.transferState == UserDevice.TRANSFER_STATE_FINISH) {
            Toast.makeText(mContext, "已经下载完毕", Toast.LENGTH_SHORT)
                    .show();
            return;
        }
    }

    /**
     * 交给其它线程控制的Handler
     */
    private Handler mHandler = new Handler() {

        public void handleMessage(Message msg) {
//            DownloadFile downloadFile = (DownloadFile) msg.obj;
//            UserDevice user = dataList.get(downloadFile.downloadID);
//            user.downloadSize = downloadFile.downloadSize;
//            user.transferState = downloadFile.transferState;
            UserDevice ud = dataList.valueAt(msg.what);
            ud.completionPercentage = msg.arg1;
            ud.transferState = msg.arg2;
            // notifyDataSetChanged会执行getView函数，更新所有可视item的数据
            notifyDataSetChanged();
            // 只更新指定item的数据，提高了性能
//            updateView(msg.what);
        }
    };

    public Handler getHandler() {
        return mHandler;
    }

    /**
     * 更新指定item的数据
     *
     * @param position 数据项
     */
    private void updateView(int position) {
        int headerViewsCount = listView.getHeaderViewsCount();
        // 得到第1个可显示控件的位置,记住是第1个可显示控件噢。而不是第1个控件
        int visiblePosition = listView.getFirstVisiblePosition();
        int end = listView.getLastVisiblePosition();
        if (!(position >= visiblePosition - headerViewsCount && position <= end)) {
            return;
        }
        // 得到你需要更新item的View
        int p = headerViewsCount != 0 ? headerViewsCount : 0;
        int pos = position - visiblePosition + p;
        View view = listView.getChildAt(pos);

        final UserDevice app = dataList.valueAt(position);
        ViewHolder holder = (ViewHolder) view.getTag();
        //Log.e("", "id="+app.id+", name="+app.name);

        holder.name.setText(app.name);
//        holder.size.setText((app.downloadSize * 100.0f / app.size) + "%");
        Drawable drawable = mContext.getResources().getDrawable(R.drawable.app_icon);
        holder.icon.setImageDrawable(drawable);

        switch (app.transferState) {
            case UserDevice.TRANSFER_STATE_TRANSFERRING:
                holder.size.setVisibility(View.VISIBLE);
                holder.tips.setVisibility(View.GONE);
                holder.size.setProgress(app.completionPercentage);
                break;
            case UserDevice.TRANSFER_STATE_FINISH:
                holder.tips.setText("传输完成");
                holder.tips.setVisibility(View.VISIBLE);
                holder.size.setVisibility(View.GONE);
                Toast.makeText(mContext, "传输完成", Toast.LENGTH_SHORT)
                        .show();
                break;
        }

    }
}
