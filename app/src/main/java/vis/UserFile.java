package vis;

/**
 * Created by Vision on 15/6/23.<br>
 * Email:Vision.lsm.2012@gmail.com
 */
public class UserFile {

    public static final int TRANSFER_STATE_NORMAL = 0x00;
    public static final int TRANSFER_STATE_TRANSFERRING = 0x01;
    public static final int TRANSFER_STATE_FINISH = 0x02;

    public static final int TYPE_IMAGE = 0;
    public static final int TYPE_AUDIO = 1;
    public static final int TYPE_VIDEO = 2;
    public static final int TYPE_TEXT = 3;

    /**
     * id
     */
    public long id;
    /**
     * file 名
     */
    public String name;

    /**
     * 数据，这里一般是指绝对地址
     */
    public String data;

    /**
     * file的大小
     */
    public long size;
    /**
     * 文件类型
     */
    public int type;
    /**
     * 是否被选中
     */
    public boolean isSelected;
    /**
     * 完成百分比
     */
    public long completed;
    /**
     * 下载状态:正常,正在下载，暂停，等待，已下载
     */
    public int state;

}
