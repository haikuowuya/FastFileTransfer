package vision.RM;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.provider.MediaStore;
import android.util.SparseArray;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import vision.fastfiletransfer.R;

/**
 * Created by Vision on 15/7/1.<br>
 * Email:Vision.lsm.2012@gmail.com
 */
public class AdapterVideo extends AdapterList {

    private SparseArray<Video> videos;

    public AdapterVideo(Context context) {
        super(context);
    }

    @Override
    void initData() {
        videos = new SparseArray<Video>();
        Cursor curVideo = cr.query(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, new String[]{
                MediaStore.Video.Media._ID, MediaStore.Video.Media.DATA, MediaStore.Video.Media.DISPLAY_NAME
        }, null, null, null);
        if (curVideo.moveToFirst()) {
            Video video;
            int i = 0;
            do {
                video = new Video();
                video.id = curVideo.getInt(curVideo.getColumnIndex(MediaStore.Video.Media._ID));
                video.data = curVideo.getString(curVideo.getColumnIndex(MediaStore.Video.Media.DATA));
                video.name = curVideo.getString(curVideo
                        .getColumnIndex(MediaStore.Video.Media.DISPLAY_NAME));
                this.videos.put(i, video);
            } while (curVideo.moveToNext());
        }
        curVideo.close();
    }

    @Override
    public int getCount() {
        return videos.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final ViewHolder holder;
        if (null == convertView) {
            holder = new ViewHolder();
            convertView = inflater.inflate(R.layout.listitem_video, null);
            holder.layout = (LinearLayout) convertView
                    .findViewById(R.id.list_item_layout);
            holder.image = (ImageView) convertView
                    .findViewById(R.id.image);
            holder.name = (TextView) convertView
                    .findViewById(R.id.name);
            holder.checkBox = (CheckBox)
                    convertView.findViewById(R.id.checkBox);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        final Video video = this.videos.get(position);

        holder.checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                video.isSelected = isChecked;
            }
        });
        holder.name.setText(video.name);
        holder.checkBox.setChecked(video.isSelected);
        Bitmap bm = MediaStore.Video.Thumbnails.getThumbnail(cr, video.id, MediaStore.Video.Thumbnails.MICRO_KIND, null);
        holder.image.setImageBitmap(bm);
        return convertView;
    }


    /**
     * 暂存变量类
     */
    static class ViewHolder {
        LinearLayout layout;
        ImageView image;
        TextView name;
        CheckBox checkBox;
    }

    private class Video {
        public int id;
        public String data;
        public String name;
        public boolean isSelected;
    }

}
