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

    public String[] getPaths() {
        String[] strings = new String[this.size()];
        int i = 0;
        for (Object obj : this) {
            strings[i++] = ((File)obj).data;
        }
        return strings;
    }
}
