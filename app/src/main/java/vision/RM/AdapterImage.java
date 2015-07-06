package vision.RM;


import android.content.Context;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.provider.MediaStore;
import android.util.SparseArray;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.Set;

import vision.fastfiletransfer.R;

/**
 * Created by Vision on 15/6/30.<br>
 * Email:Vision.lsm.2012@gmail.com
 */
public class AdapterImage extends AdapterList {

    private SparseArray<FileImage> images;
    private Context mContext;
    private Set mSelectedList;

    public AdapterImage(Context context, Set selectedList) {
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
            holder.checkBox = (CheckBox)
                    convertView.findViewById(R.id.checkBox);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
            holder.image.setImageResource(R.mipmap.explorer_c_icon_image_p);
        }

        final FileImage fileImage = this.images.get(position);

        holder.layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (fileImage.isSelected) {
                    fileImage.isSelected = false;
                    mSelectedList.remove(fileImage);
                    holder.checkBox.setChecked(false);
                } else {
                    fileImage.isSelected = true;
                    mSelectedList.add(fileImage);
                    holder.checkBox.setChecked(true);
                }
            }
        });

//        holder.checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
//            @Override
//            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
//                fileImage.isSelected = isChecked;
////                Log.d("OnChecked","look at that");
//                if (isChecked) {
//                    mSelectedList.add(fileImage);
//                } else {
//                    mSelectedList.remove(fileImage);
//                }
//            }
//        });
        holder.name.setText(fileImage.name);
        holder.checkBox.setChecked(fileImage.isSelected);
//        Bitmap bm = MediaStore.Images.Thumbnails.getThumbnail(cr, fileImage.id, MediaStore.Images.Thumbnails.MICRO_KIND, null);
//        holder.fileImage.setImageBitmap(bm);
        holder.image.setTag(fileImage.id);
        new LoadImage(holder.image, fileImage.id)
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
            bm = MediaStore.Images.Thumbnails.getThumbnail(cr, origId, MediaStore.Images.Thumbnails.MICRO_KIND, null);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            if (iv.getTag() != null && ((long) iv.getTag()) == origId) {
                iv.setImageBitmap(bm);
            }
//            super.onPostExecute(aVoid);
        }
    }

}

