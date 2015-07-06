package vision.RM;


import android.content.Context;
import android.util.SparseArray;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.Set;

import vision.fastfiletransfer.R;

/**
 * Created by Vision on 15/6/30.<br>
 * Email:Vision.lsm.2012@gmail.com
 */
public class AdapterText extends AdapterList {

    private SparseArray<FileText> texts;
    private Set mSelectedList;

    public AdapterText(Context context, Set selectedList) {
        super(context);
        this.mSelectedList = selectedList;
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

        holder.layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (fileText.isSelected) {
                    fileText.isSelected = false;
                    mSelectedList.remove(fileText);
                    holder.checkBox.setChecked(false);
                } else {
                    fileText.isSelected = true;
                    mSelectedList.add(fileText);
                    holder.checkBox.setChecked(true);
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

