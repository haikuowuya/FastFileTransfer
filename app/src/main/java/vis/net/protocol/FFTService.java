package vis.net.protocol;

import android.util.Log;

import vis.net.UDPHelper;

/**
 * FastFileTransfer 通讯服务<br>
 * 发送，接收，处理<br>
 * Created by Vision on 15/6/12.<br>
 * Email:Vision.lsm.2012@gmail.com
 */
public class FFTService {
    /**
     * 请不要直接调用这个类
     */
    private UDPHelper mUDPHelper;
    private OnDataReceivedListener mOnDataReceivedListener;
    /**
     * 目标IP地址
     */
    private String address;
    /**
     * 目标端口,默认2048
     */
    private int recvPort = 2048;

    public FFTService() {
    }

    private UDPHelper getUDPHelper() {
        if (mUDPHelper == null) {
            mUDPHelper = new UDPHelper(recvPort);
        }
        return mUDPHelper;
    }

    /**
     * 使能传输
     */
    public void enableTransmission() {
        getUDPHelper().enable();
    }

    /**
     * 失能传输
     */
    public void disableTransmission() {
        getUDPHelper().disable();
    }


    /**
     * 发送登入信息
     */
    public void sendLogin() {
        SwapPackage sp = new SwapPackage(SwapPackage.LOGIN, SwapPackage.LOCALNAME);
        getUDPHelper().send(sp.getString(), address, recvPort);
    }

    /**
     * 发送登出信息
     */
    public void sendLogout() {
        SwapPackage sp = new SwapPackage(SwapPackage.LOGOUT, SwapPackage.LOCALNAME);
        getUDPHelper().send(sp.getString(), address, recvPort);
    }

    /**
     * 发送文件信息
     */
    public static void sendFlies() {

    }

    /**
     * 合并数组
     *
     * @param byte_1 数组一
     * @param byte_2 数组二
     * @return 合并之后的数组
     */
    public byte[] byteMerger(byte[] byte_1, byte[] byte_2) {
        byte[] byte_3 = new byte[byte_1.length + byte_2.length];
        System.arraycopy(byte_1, 0, byte_3, 0, byte_1.length);
        System.arraycopy(byte_2, 0, byte_3, byte_1.length, byte_2.length);
        return byte_3;
    }

    /**
     * 设置接收到数据时的监听器<br>
     *
     * @param listener 监听器
     */
    public void setOnDataReceivedListener(OnDataReceivedListener listener) {
        this.mOnDataReceivedListener = listener;
        if (listener == null) {
            getUDPHelper().setDateReceivedListener(null);
        } else {
            getUDPHelper().setDateReceivedListener(new UDPHelper.OnDataReceivedListener() {

                @Override
                public void onDataReceived(byte[] data) {
                    SwapPackage sp = new SwapPackage(data);
//                    Log.d("SwapPackage", sp.getString().toString());
                    if (sp.getCmdByByte() == SwapPackage.LOGIN) {
                        Log.d("Login", new String(sp.getData()));
                        mOnDataReceivedListener.onLogin(new String(sp.getData()));
                    }
                    if (sp.getCmdByByte() == SwapPackage.LOGOUT) {
                        Log.d("Logout", new String(sp.getData()));
                        mOnDataReceivedListener.onLogout(new String(sp.getData()));
                    }
                }
            });

        }
    }

    /**
     * 设置目标
     *
     * @param address 目标IP
     */
    public void setTarget(String address) {
        this.address = address;
    }

    public void setTarget(String address, int port) {
        this.address = address;
        this.recvPort = port;
    }

    /**
     * 接收到数据时的监听接口
     */
    public interface OnDataReceivedListener {
        void onDataReceived(SwapPackage sp);

        void onLogin(String name);

        void onLogout(String name);
    }
}
