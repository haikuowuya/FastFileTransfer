package vision.RM;

import android.content.Context;
import android.database.Cursor;
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
public class AdapterAudio extends AdapterList {

    private SparseArray<Audio> audios;

    public AdapterAudio(Context context) {
        super(context);
    }

    @Override
    void initData() {
        audios = new SparseArray<Audio>();
        Cursor curAudio = cr.query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, new String[]{
                MediaStore.Audio.Media._ID, MediaStore.Audio.Media.DATA, MediaStore.Audio.Media.DISPLAY_NAME
        }, null, null, null);
        if (curAudio.moveToFirst()) {
            Audio audio;
            int i = 0;
            do {
                audio = new Audio();
                audio.id = curAudio.getInt(curAudio.getColumnIndex(MediaStore.Audio.Media._ID));
                audio.data = curAudio.getString(curAudio.getColumnIndex(MediaStore.Audio.Media.DATA));
                audio.name = curAudio.getString(curAudio
                        .getColumnIndex(MediaStore.Audio.Media.DISPLAY_NAME));
                this.audios.put(i++, audio);
            } while (curAudio.moveToNext());
        }
        curAudio.close();
    }

    @Override
    public int getCount() {
        return audios.size();
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
            convertView = inflater.inflate(R.layout.listitem_music, null);
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
        final Audio audio = this.audios.get(position);

        holder.checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                audio.isSelected = isChecked;
            }
        });
        holder.name.setText(audio.name);
        holder.checkBox.setChecked(audio.isSelected);
//        Drawable drawable = context.getResources().getDrawable(R.mipmap.app_icon);
//        holder.images.setImageDrawable(drawable);
//        BitmapFactory.Options options = new BitmapFactory.Options();
//        options.inSampleSize = 32;
//        Bitmap bm = BitmapFactory.decodeFile(audio.data, options);
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

    private class Audio {
        public int id;
        public String data;
        public String name;
        public boolean isSelected;
    }


}
