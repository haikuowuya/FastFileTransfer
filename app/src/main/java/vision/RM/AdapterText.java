package vision.RM;


import android.annotation.TargetApi;
import android.content.Context;
import android.database.Cursor;
import android.os.Build;
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
 * Created by Vision on 15/6/30.<br>
 * Email:Vision.lsm.2012@gmail.com
 */
public class AdapterText extends AdapterList {

    private SparseArray<Text> texts;

    public AdapterText(Context context) {
        super(context);
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    @Override
    void initData() {
        texts = new SparseArray<Text>();
        Cursor curText = cr.query(MediaStore.Files.getContentUri("external"), new String[]{
                MediaStore.Files.FileColumns._ID, MediaStore.Files.FileColumns.DATA, MediaStore.Files.FileColumns.MIME_TYPE, MediaStore.Files.FileColumns.TITLE
        }, MediaStore.Files.FileColumns.MIME_TYPE + " LIKE ?", new String[]{"text/%"}, null);
        if (curText.moveToFirst()) {
            Text text;
            int i = 0;
            do {
                text = new Text();
                text.id = curText.getInt(curText.getColumnIndex(MediaStore.Images.Media._ID));
                text.data = curText.getString(curText.getColumnIndex(MediaStore.Images.Media.DATA));
                text.name = curText.getString(curText
                        .getColumnIndex(MediaStore.Images.Media.TITLE));
                this.texts.put(i, text);
                i++;
            } while (curText.moveToNext());
        }
        curText.close();
    }


    @Override
    public int getCount() {
        return texts.size();
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
            convertView = inflater.inflate(R.layout.listitem_text, null);
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
        final Text text = this.texts.get(position);

        holder.checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                text.isSelected = isChecked;
            }
        });
        holder.name.setText(text.name);
        holder.checkBox.setChecked(text.isSelected);
//        Bitmap bm = MediaStore.Images.Thumbnails.getThumbnail(cr, text.id, MediaStore.Images.Thumbnails.MICRO_KIND, null);
//        holder.image.setImageBitmap(bm);
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


    private class Text {
        public int id;
        public String data;
        public String name;
        public boolean isSelected;
    }
}
