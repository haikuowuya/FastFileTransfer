package vis;

import java.util.HashSet;
import java.util.Set;

import vision.resourcemanager.File;

/**
 * 已经选择的文件队列
 * Created by Vision on 15/7/2.<br>
 * Email:Vision.lsm.2012@gmail.com
 */
public class SelectedFilesQueue<T> {

    public Set<T> data;
    private OnDataChangedListener onDataChangedListener;

    public SelectedFilesQueue() {
        this.data = new HashSet<T>();
    }

    public boolean add(T t) {
        boolean b = data.add(t);
        this.onDataChangedListener.onAddedListener(data.size());
        return b;
    }

    public boolean remove(T t) {
        boolean b = data.remove(t);
        this.onDataChangedListener.onRemovedListener(data.size());
        return b;
    }

    public void setOnDataChangedListener(OnDataChangedListener onDataChangedListener) {
        this.onDataChangedListener = onDataChangedListener;
    }

    public String[] getPaths() {
        String[] strings = new String[data.size()];
        int i = 0;
        for (T t : data) {
            strings[i++] = ((File) t).data;
        }
        return strings;
    }

    public void removeAll() {
        data.removeAll(data);
    }

    public int size() {
        return data.size();
    }

    public boolean isEmpty() {
        return data.isEmpty();
    }

    public interface OnDataChangedListener {
        void onAddedListener(int size);

        void onRemovedListener(int size);
    }
}
