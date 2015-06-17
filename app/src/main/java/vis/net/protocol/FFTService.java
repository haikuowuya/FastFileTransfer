package vis.net.protocol;

import android.util.Log;

import java.util.HashMap;
import java.util.Map;

import vis.net.CommandsTransfer;

/**
 * FastFileTransfer 通讯服务<br>
 * 发送，接收，处理<br>
 * Created by Vision on 15/6/12.<br>
 * Email:Vision.lsm.2012@gmail.com
 */
public class FFTService {
    /**
     * UDP类，用来发送命令
     */
    private CommandsTransfer mCommandsTransfer;
    private OnDataReceivedListener mOnDataReceivedListener;
    /**
     * 目标IP地址
     */
    private String address;
    /**
     * 目标端口,默认2048
     */
    private int recvPort = 2048;

    /**
     * 发送列表，设备连接列表，key为IP，value为设备名
     */
    private Map<String, String> mConnectedDevices;

    public FFTService() {
        mConnectedDevices = new HashMap<String, String>();
        mCommandsTransfer = new CommandsTransfer(recvPort);
    }

    /**
     * 使能传输
     */
    public void enableTransmission() {
        mCommandsTransfer.enable();
    }

    /**
     * 失能传输
     */
    public void disableTransmission() {
        mCommandsTransfer.disable();
    }


    /**
     * 发送登入信息
     */
    public void sendLogin() {
        SwapPackage sp = new SwapPackage(SwapPackage.LOGIN, SwapPackage.LOCALNAME);
        mCommandsTransfer.send(sp.getString(), address, recvPort);
    }

    /**
     * 发送登出信息
     */
    public void sendLogout() {
        SwapPackage sp = new SwapPackage(SwapPackage.LOGOUT, SwapPackage.LOCALNAME);
        mCommandsTransfer.send(sp.getString(), address, recvPort);
    }

    /**
     * 发送文件信息
     */
    public static void sendFlies() {

    }

    /**
     * 设置接收到数据时的监听器<br>
     *
     * @param listener 监听器
     */
    public void setOnDataReceivedListener(OnDataReceivedListener listener) {
        this.mOnDataReceivedListener = listener;
        if (listener == null) {
            mCommandsTransfer.setDateReceivedListener(null);
        } else {
            mCommandsTransfer.setDateReceivedListener(new CommandsTransfer.OnDataReceivedListener() {

                @Override
                public void onDataReceived(String address, byte[] data) {
                    SwapPackage sp = new SwapPackage(data);
//                    Log.d("SwapPackage", sp.getString().toString());
                    if (sp.getCmdByByte() == SwapPackage.LOGIN) {
                        addDevice(address, new String(sp.getData()));
                        Log.d("Login", address + "->" + new String(sp.getData()));
//                        mOnDataReceivedListener.onLogin(address, new String(sp.getData()));
                    } else if (sp.getCmdByByte() == SwapPackage.LOGOUT) {
                        removeDevice(address);
                        Log.d("Logout", address + "->" + new String(sp.getData()));
//                        mOnDataReceivedListener.onLogout(address, new String(sp.getData()));
                    }
                    mOnDataReceivedListener.onDataReceived(mConnectedDevices);
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
        /**
         * 有数据时回调
         *
         * @param devicesList 设备连接集合
         */
        void onDataReceived(Map<String, String> devicesList);
//        void onLogin(String address, String name);
//        void onLogout(String address, String name);
    }

    /**
     * 添加设备进列表中
     *
     * @param address IP地址，作为key
     * @param name    设备名称，作为value
     * @return 如果添加成功返回value，即为name;如果不成功返回null
     */
    private String addDevice(String address, String name) {
        return mConnectedDevices.put(address, name);
    }

    /**
     * 在列表中除移设备
     *
     * @param address IP地址，作为key
     * @return 如果添加成功返回对应的value，即为对应的name;如果不成功返回null
     */
    private String removeDevice(String address) {
        return mConnectedDevices.remove(address);
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


}
