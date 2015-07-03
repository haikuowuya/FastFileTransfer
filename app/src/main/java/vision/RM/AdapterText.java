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
 * Created by Vision on 15/6/30.<br>
 * Email:Vision.lsm.2012@gmail.com
 */
public class AdapterText extends AdapterList {

    private SparseArray<FileText> texts;

    public AdapterText(Context context) {
        super(context);
    }

    @Override
    public void setData(SparseArray<?> data) {
        this.texts = (SparseArray<FileText>) data;
        notifyDataSetChanged();
    }


    @Override

    public int getCount() {
        if (null == texts) {
            return 0;
        } else {
            return texts.size();
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
            convertView = inflater.inflate(R.layout.listitem_text, null);
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
        final FileText fileText = this.texts.get(position);

        holder.checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                fileText.isSelected = isChecked;
                if (isChecked) {
                    ((ShareActivity) context).mTransmissionQueue.add(fileText);
                } else {
                    ((ShareActivity) context).mTransmissionQueue.remove(fileText);
                }
            }
        });
        holder.name.setText(fileText.name);
        holder.checkBox.setChecked(fileText.isSelected);
//        Bitmap bm = MediaStore.Images.Thumbnails.getThumbnail(cr, fileText.id, MediaStore.Images.Thumbnails.MICRO_KIND, null);
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

