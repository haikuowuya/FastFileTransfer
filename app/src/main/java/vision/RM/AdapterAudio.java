package vision.RM;

import android.content.Context;
import android.util.SparseArray;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import vision.fastfiletransfer.R;
import vision.fastfiletransfer.ShareActivity;

/**
 * Created by Vision on 15/7/1.<br>
 * Email:Vision.lsm.2012@gmail.com
 */
public class AdapterAudio extends AdapterList {

    private SparseArray<FileAudio> audios;

    public AdapterAudio(Context context) {
        super(context);
    }

    @Override
    public void setData(SparseArray<?> data) {
        this.audios = (SparseArray<FileAudio>) data;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        if (null == audios) {
            return 0;
        } else {
            return audios.size();
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
            convertView = inflater.inflate(R.layout.listitem_music, null);
            holder.layout = (LinearLayout) convertView
                    .findViewById(R.id.list_item_layout);
            holder.name = (TextView) convertView
                    .findViewById(R.id.name);
            holder.checkBox = (CheckBox)
                    convertView.findViewById(R.id.checkBox);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        final FileAudio fileAudio = this.audios.get(position);

        holder.checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                fileAudio.isSelected = isChecked;
                if (isChecked) {
                    ((ShareActivity) context).mTransmissionQueue.add(fileAudio);
                } else {
                    ((ShareActivity) context).mTransmissionQueue.remove(fileAudio);
                }
            }
        });
        holder.name.setText(fileAudio.name);
        holder.checkBox.setChecked(fileAudio.isSelected);
//        Drawable drawable = context.getResources().getDrawable(R.mipmap.app_icon);
//        holder.images.setImageDrawable(drawable);
//        BitmapFactory.Options options = new BitmapFactory.Options();
//        options.inSampleSize = 32;
//        Bitmap bm = BitmapFactory.decodeFile(fileAudio.data, options);
//        holder.image.setImageBitmap(bm);
        return convertView;
    }

    /**
     * 暂存变量类
     */
    static class ViewHolder {
        LinearLayout layout;
        TextView name;
        CheckBox checkBox;
    }

}
