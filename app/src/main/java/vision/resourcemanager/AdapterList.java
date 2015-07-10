package vision.resourcemanager;

import android.content.ContentResolver;
import android.content.Context;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.widget.BaseAdapter;


/**
 * Created by Vision on 15/7/1.<br>
 * Email:Vision.lsm.2012@gmail.com
 */
public abstract class AdapterList extends BaseAdapter {

    protected Context context;
    protected LayoutInflater inflater = null;
    protected ContentResolver cr;

    public AdapterList(Context context) {
        this.context = context;
        this.inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        cr = context.getContentResolver();
    }

    public abstract void setData(SparseArray<?> data);


}
