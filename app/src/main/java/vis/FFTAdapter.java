package vis;

import android.os.Handler;
import android.widget.BaseAdapter;

/**
 * Created by Vision on 15/6/23.<br>
 * Email:Vision.lsm.2012@gmail.com
 */

public abstract class FFTAdapter extends BaseAdapter {
    abstract public Handler getHandler();

    abstract public void put(int key, Object obj);

    abstract public void remove(int key);

    abstract public Object getObject(int index);
}
