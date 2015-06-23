package vis;

/**
 * Created by Vision on 15/6/23.<br>
 * Email:Vision.lsm.2012@gmail.com
 */
public class UserFile {

    public static final int TRANSFER_STATE_NORMAL = 0x00;
    public static final int TRANSFER_STATE_TRANSFERRING = 0x01;
    public static final int TRANSFER_STATE_FINISH = 0x02;

    /**
     * id
     */
    public int id;
    /**
     * file 名
     */
    public String name;

    /**
     * file的大小
     */
    public long size;
    /**
     * 完成百分比
     */
    public long completed;
    /**
     * 下载状态:正常,正在下载，暂停，等待，已下载
     */
    public int state;

}
