package vision.resourcemanager;


import android.content.Context;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.provider.MediaStore;
import android.util.SparseArray;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import vis.SelectedFilesQueue;
import vision.fastfiletransfer.R;

/**
 * Created by Vision on 15/6/30.<br>
 * Email:Vision.lsm.2012@gmail.com
 */
public class AdapterImage extends AdapterList {

    private SparseArray<FileImage> images;
    private Context mContext;
    private SelectedFilesQueue mSelectedList;

    public AdapterImage(Context context, SelectedFilesQueue selectedList) {
        super(context);
        this.mContext = context;
        this.mSelectedList = selectedList;
    }

    @Override
    public void setData(SparseArray<?> data) {
        this.images = (SparseArray<FileImage>) data;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        if (null == images) {
            return 0;
        } else {
            return images.size();
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
            convertView = inflater.inflate(R.layout.listitem_image, null);
            holder.layout = (LinearLayout) convertView
                    .findViewById(R.id.list_item_layout);
            holder.image = (ImageView) convertView
                    .findViewById(R.id.image);
            holder.name = (TextView) convertView
                    .findViewById(R.id.name);
            holder.size = (TextView)
                    convertView.findViewById(R.id.tvSize);
            holder.date = (TextView)
                    convertView.findViewById(R.id.tvDate);
            holder.ivCheckBox = (ImageView)
                    convertView.findViewById(R.id.ivCheckBox);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
            holder.image.setImageResource(R.mipmap.listitem_icon_image);
        }

        final FileImage file = this.images.valueAt(position);
        holder.layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (file.isSelected) {
                    file.isSelected = false;
                    mSelectedList.remove(file);
                    holder.ivCheckBox.setImageResource(R.mipmap.listitem_checkbox_off_normal);
                } else {
                    file.isSelected = true;
                    mSelectedList.add(file);
                    holder.ivCheckBox.setImageResource(R.mipmap.listitem_checkbox_on_normal);
                }
            }
        });

        holder.name.setText(file.name);
        holder.size.setText(file.strSize);
        holder.date.setText(file.strDate);
        if (file.isSelected) {
            holder.ivCheckBox.setImageResource(R.mipmap.listitem_checkbox_on_normal);
        } else {
            holder.ivCheckBox.setImageResource(R.mipmap.listitem_checkbox_off_normal);
        }

        holder.image.setTag(file.id);
        new LoadImage(holder.image, file.id)
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
        TextView size;
        TextView date;
        ImageView ivCheckBox;
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
            bm = MediaStore.Images.Thumbnails.getThumbnail(cr, origId, MediaStore.Images.Thumbnails.MICRO_KIND, null);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            if (iv.getTag() != null && ((long) iv.getTag()) == origId) {
                iv.setImageBitmap(bm);
            }
        }
    }

}

