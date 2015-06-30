package vision.RM;


import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.provider.MediaStore;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import vision.fastfiletransfer.R;

/**
 * Created by Vision on 15/6/30.<br>
 * Email:Vision.lsm.2012@gmail.com
 */
public class AdapterImage extends BaseAdapter {

    private LayoutInflater inflater = null;
    private SparseArray<Image> data;
    private Context context;

    public AdapterImage(Context context) {
        this.context = context;
        this.inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        data = new SparseArray<Image>();
        ContentResolver cr = context.getContentResolver();
        Cursor cursor = cr.query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, null, null, null, MediaStore.Images.Media.DEFAULT_SORT_ORDER);
        cursor.moveToFirst();
        Image image;
        for (int i = 0; cursor.moveToNext(); i++) {
            image = new Image();
            image.id = i;
            image.name = cursor.getString(cursor
                    .getColumnIndex(MediaStore.Images.Media.TITLE));
            data.put(i, image);
        }
        cursor.close();
    }

    @Override
    public int getCount() {
        return data.size();
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
            convertView = inflater.inflate(R.layout.listitem_images, null);
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
        final Image image = data.get(position);

        holder.checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
//                Toast.makeText(context, String.valueOf(isChecked), Toast.LENGTH_SHORT)
//                        .show();
                image.isSelected = isChecked;
            }
        });
        holder.name.setText(image.name);
//        holder.checkBox.setOnClickListener(null);
        holder.checkBox.setChecked(image.isSelected);
//        Drawable drawable = mContext.getResources().getDrawable(R.mipmap.app_icon);
//        holder.image.setImageDrawable(drawable);
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

    private class Image {
        public int id;
        public String name;
        public boolean isSelected;
    }
}
