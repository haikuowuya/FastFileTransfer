package vis.net.protocol;

/**
 * 通讯服务信息包
 * Created by Vision on 15/5/24.
 * Email:Vision.lsm.2012@gmail.com
 */
public class SwapPackage {
    //命令代码
    /**
     * 登入
     */
    public final static byte LOGIN = 0x01;
    /**
     * 登出
     */
    public final static byte LOGOUT = 0x02;

    /**
     * 本地机型名
     */
    public final static byte[] LOCALNAME = android.os.Build.MODEL.getBytes();

    /**
     * 头部编码
     */
    protected final byte[] header = new byte[]{'$'};
    /**
     * 数据
     */
    protected byte[] data;
    /**
     * 命令
     */
    protected byte cmd;
    /**
     * 临时存放字节串
     */
    protected byte[] temp;

    /**
     * 输入命令与数据组装成对象
     *
     * @param cmd  命令
     * @param data 数据
     */
    public SwapPackage(byte cmd, byte[] data) {
        this.cmd = cmd;
        this.data = data;
        buildString();
    }

    /**
     * 输入字符组装成对象
     *
     * @param string 成串字符
     */
    public SwapPackage(byte[] string) {
        setString(string);
    }

    /**
     * 建造字节串
     */
    protected void buildString() {
        byte checkSum = 0;
        int i;
        temp = new byte[header.length + data.length + 3];
        for (i = 0; i < header.length; i++) {
            temp[i] = header[i];
        }
        temp[i] = (byte) (data.length & 0xff);
        checkSum ^= temp[i++];
        temp[i] = cmd;
        checkSum ^= temp[i++];
        for (byte aData : data) {
            temp[i] = aData;
            checkSum ^= temp[i++];
        }
        // temp[i++] = getChecksum();
        temp[i] = checkSum;
    }

    /**
     * 获取字节串
     * @return 本对象的字节串
     */
    public byte[] getString() {
        return temp;
    }

    /**
     * 设置字节串
     * @param string 字节串
     * @return 如果校验成功返回true
     */
    public boolean setString(byte[] string) {
        byte checkSum = 0;
        int dataLength = string[1];
        byte cmd = string[2];
        byte[] data = new byte[dataLength];
        checkSum ^= string[1];
        checkSum ^= cmd;
        for (int i = 0; i < dataLength; i++) {
            data[i] = string[3 + i];
            checkSum ^= data[i];
        }
        if (checkSum == string[3 + dataLength]) {
            this.data = data;
            this.cmd = cmd;
            buildString();
            return true;
        } else {
            return false;
        }
    }

    public void setData(byte[] data) {
        // this.dataSize = (byte) (data.length & 0xff);
        this.data = data;
        buildString();
    }

    /**
     * 获取数据
     * @return 数据字节串
     */
    public byte[] getData() {
        return this.data;
    }

    public void setCmd(byte cmd) {
        this.cmd = cmd;
        buildString();
    }

    /**
     * 获取命令
     * @return 命令 byte 类型
     */
    public byte getCmdByByte() {
        return cmd;
    }

    public int getCmdByInt() {
        return cmd & 0xff;
    }

    /**
     * 计算校验码<br>
     * 三个参数分别为： 数据长度 ， 指令代码 ， 实际数据数组
     *
     * @author verg
     * @version 1.00
     */
    /*
     * protected byte getChecksum(byte cmdMSP, byte data[]) { byte checksum = 0;
	 * checksum ^= (byte) (data.length & 0xff); checksum ^= cmdMSP; for (int i =
	 * 0; i < data.length; i++) { checksum ^= data[i]; } return checksum; }
	 * 
	 * protected byte getChecksum() { return getChecksum(cmd, data); }
	 */

    /**
     * byte数组转String
     *
     * @param src 源byte数组
     * @return 转换来的String
     */
    public String bytes2Hex(byte[] src) {
        char[] res = new char[src.length * 2];
        final char hexDigits[] = {'0', '1', '2', '3', '4', '5', '6', '7', '8',
                '9', 'A', 'B', 'C', 'D', 'E', 'F'};
        for (int i = 0, j = 0; i < src.length; i++) {
            res[j++] = hexDigits[src[i] >>> 4 & 0x0f];
            res[j++] = hexDigits[src[i] & 0x0f];
        }

        return new String(res);
    }

}
