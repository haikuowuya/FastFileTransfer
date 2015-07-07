package vis;

import android.support.annotation.NonNull;
import android.util.SparseArray;

/**
 * Created by Vision on 15/7/7.<br>
 * Email:Vision.lsm.2012@gmail.com
 */
public class DevicesList<E> extends SparseArray {
    private OnDataChangedListener onDataChangedListener;

    public void setOnDataChangedListener(OnDataChangedListener onDataChangedListener) {
        this.onDataChangedListener = onDataChangedListener;
    }

    public interface OnDataChangedListener {
        void onAddedListener(int size);

        void onRemovedListener(int size);
    }

    @NonNull
    @Override
    public E valueAt(int index) {
        return (E)super.valueAt(index);
    }

    @Override
    public void put(int key, Object value) {
        super.put(key, value);
        onDataChangedListener.onAddedListener(this.size());
    }

    @Override
    public void remove(int key) {
        super.remove(key);
        onDataChangedListener.onRemovedListener(this.size());
    }
}
