package vis;

import java.util.HashSet;

import vision.RM.File;
import vision.RM.FileAudio;
import vision.RM.FileImage;
import vision.RM.FileText;
import vision.RM.FileVideo;

/**
 * Created by Vision on 15/7/2.<br>
 * Email:Vision.lsm.2012@gmail.com
 */
public class TransmissionQueue<E> extends HashSet {

    private OnDataChangedListener onDataChangedListener;

    public TransmissionQueue() {
    }

    public void add(FileImage fileImage) {
        super.add(fileImage);
    }

    public void add(FileAudio fileAudio) {
        super.add(fileAudio);
    }

    public void add(FileVideo fileVideo) {
        super.add(fileVideo);
    }

    public void add(FileText fileText) {
        super.add(fileText);
    }

    @Override
    public boolean add(Object object) {
        boolean b = super.add(object);
        this.onDataChangedListener.onAddedListener(this.size());
        return b;
    }

    @Override
    public boolean remove(Object object) {
        boolean b = super.remove(object);
        this.onDataChangedListener.onRemovedListener(this.size());
        return b;
    }

    public void setOnDataChangedListener(OnDataChangedListener onDataChangedListener) {
        this.onDataChangedListener = onDataChangedListener;
    }

    public String[] getPaths() {
        String[] strings = new String[this.size()];
        int i = 0;
        for (Object obj : this) {
            strings[i++] = ((File) obj).data;
        }
        return strings;
    }

    public interface OnDataChangedListener {
        void onAddedListener(int size);
        void onRemovedListener(int size);
    }
}
