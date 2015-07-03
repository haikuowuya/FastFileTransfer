package vision.RM;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.AsyncTask;
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
import vision.fastfiletransfer.ShareActivity;

/**
 * Created by Vision on 15/7/1.<br>
 * Email:Vision.lsm.2012@gmail.com
 */
public class AdapterVideo extends AdapterList {

    private SparseArray<FileVideo> videos;

    public AdapterVideo(Context context) {
        super(context);
    }

    @Override
    public void setData(SparseArray<?> data) {
        this.videos = (SparseArray<FileVideo>) data;
        notifyDataSetChanged();
    }


    @Override
    public int getCount() {
        if (null == videos) {
            return 0;
        } else {
            return videos.size();
        }
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
        final FileVideo fileVideo = this.videos.get(position);

        holder.checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                fileVideo.isSelected = isChecked;
                if (isChecked) {
                    ((ShareActivity) context).mTransmissionQueue.add(fileVideo);
                } else {
                    ((ShareActivity) context).mTransmissionQueue.remove(fileVideo);
                }
            }
        });
        holder.name.setText(fileVideo.name);
        holder.checkBox.setChecked(fileVideo.isSelected);
//        Bitmap bm = MediaStore.FileVideo.Thumbnails.getThumbnail(cr, fileVideo.id, MediaStore.FileVideo.Thumbnails.MICRO_KIND, null);
//        holder.image.setImageBitmap(bm);
        new LoadImage(holder.image, fileVideo.id)
                .execute();

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

    private class LoadImage extends AsyncTask<Void, Void, Void> {

        private ImageView iv;
        private long origId;
        private Bitmap bm;

        public LoadImage(ImageView iv, long origId) {
            this.iv = iv;
            this.origId = origId;
        }

        @Override
        protected Void doInBackground(Void... params) {
            bm = MediaStore.Video.Thumbnails.getThumbnail(cr, origId, MediaStore.Video.Thumbnails.MICRO_KIND, null);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            iv.setImageBitmap(bm);
//            super.onPostExecute(aVoid);
        }
    }

}

