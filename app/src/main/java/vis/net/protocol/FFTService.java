package vis.net.protocol;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.util.Log;
import android.util.SparseArray;
import android.widget.BaseAdapter;
import android.widget.Toast;

import java.io.File;

import vis.FFTAdapter;
import vis.UserDevice;
import vis.UserDevicesAdapter;
import vis.UserFilesAdapter;
import vis.net.CommandsTransfer;
import vis.net.FilesTransfer;

/**
 * FastFileTransfer 通讯服务<br>
 * 发送，接收，处理<br>
 * Created by Vision on 15/6/12.<br>
 * Email:Vision.lsm.2012@gmail.com
 */
public class FFTService {

    public static final int SERVICE_RECEIVE = 0x01;
    public static final int SERVICE_SHARE = 0x02;

    /**
     * 文件传输类
     */
    private final FilesTransfer mFilesTransfer;
    /**
     * 命令传输类
     */
    private CommandsTransfer mCommandsTransfer;
    private OnDataReceivedListener mOnDataReceivedListener;
    /**
     * 本地机型名
     */
    public final static byte[] LOCALNAME = android.os.Build.MODEL.replaceAll("\\s|-", "").getBytes();

    /**
     * 发送列表，设备连接列表，key为IP，value为设备名
     */
//    private Map<String, String> mDevices;
//    private SparseArray<UserDevice> mDevices;
    private FFTAdapter mAdapter;

    /**
     * 目标地址
     */
    private String targetAddress;

    private Handler mCommandHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
//            if (MESSAGE_FROM_FILESTRANSFER == msg.what) {
//            } else if (MESSAGE_FROM_COMMANDSTRANSFER == msg.what) {
            Object[] objects = (Object[]) msg.obj;
            byte[] address = (byte[]) objects[0];
            byte[] data = (byte[]) objects[1];
            SwapPackage sp = new SwapPackage(data);
            if (sp.getCmdByByte() == SwapPackage.LOGIN) {
                //设备登入
                UserDevice us = new UserDevice();
                us.ip = byteArray2IpAddress(address);
                us.name = new String(sp.getData());
                int ip = byteArray2Int(address);
//                addDevice(ip, us);
                mAdapter.put(ip, us);
//                addDevice(address, new String(sp.getData()));
                Log.d("Login", us.ip + "->" + new String(sp.getData()));
//              mOnDataReceivedListener.onLogin(address, new String(sp.getData()));
            } else if (sp.getCmdByByte() == SwapPackage.LOGOUT) {
                int ip = byteArray2Int(address);
                mAdapter.remove(ip);
                Log.d("Logout", new String(address) + "->" + new String(sp.getData()));
//              mOnDataReceivedListener.onLogout(address, new String(sp.getData()));
            }
            mOnDataReceivedListener.onDataReceived(null);
        }
//        }
    };

    public FFTService(Context context, int serviceType) {
        mCommandsTransfer = new CommandsTransfer(2222);
        mFilesTransfer = new FilesTransfer(context);
        if (SERVICE_SHARE == serviceType) {
//            mDevices = new SparseArray<UserDevice>();
            mAdapter = new UserDevicesAdapter(context);
        } else if (SERVICE_RECEIVE == serviceType) {
            mAdapter = new UserFilesAdapter(context);
        }
        //把适配器的handler交给mFilesTransfer，以便transfer控制适配器
//        Log.d("FFTService", String.valueOf(mAdapter.getHandler()));
        mFilesTransfer.setCallbackHandler(mAdapter.getHandler());
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
     *
     * @param address 地址
     */
    public void sendLogin(String address) {
        //默认端口2222
        sendLogin(address, 2222);
    }

    /**
     * 发送登入信息
     *
     * @param address 地址
     * @param port    端口
     */
    public void sendLogin(String address, int port) {
        this.targetAddress = address;
        if (!mFilesTransfer.isReceiving()) {
            mFilesTransfer.receiveFile(2223, "/FFT");
//            Log.d(this.getClass().getName(), Environment.getExternalStorageDirectory().getAbsolutePath());
//            Log.d(this.getClass().getName(),Environment.getExternalStorageDirectory().getPath());
        }
        SwapPackage sp = new SwapPackage(address, port, SwapPackage.LOGIN, LOCALNAME);
        mCommandsTransfer.send(sp);
    }

    /**
     * 发送登出信息
     */
    public void sendLogout() {
        //默认端口2222
        sendLogout(this.targetAddress, 2222);
    }

    /**
     * 发送登出信息
     *
     * @param address 地址
     * @param port    端口
     */
    public void sendLogout(String address, int port) {
        SwapPackage sp = new SwapPackage(address, port, SwapPackage.LOGOUT, LOCALNAME);
        mCommandsTransfer.send(sp);
        mFilesTransfer.stopReceiving();
    }

    public void sendFlies(Context context, String filePath) {
        if (null != filePath) {
            sendFlies(context, new File(filePath));
        } else {
            Toast.makeText(context, "没有选择文件", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * 发送文件信息
     *
     * @param context 上下文
     * @param file    文件
     */
    public void sendFlies(Context context, File file) {
        if (null == file) {
            Toast.makeText(context, "没有选择文件", Toast.LENGTH_SHORT).show();
        } else if (mAdapter.getCount() == 0) {
            Toast.makeText(context, "没有设备连接", Toast.LENGTH_SHORT).show();
        } else {
            //发送文件
            Toast.makeText(context, file.toString(), Toast.LENGTH_SHORT).show();
            for (int i = 0, nsize = mAdapter.getCount(); i < nsize; i++) {
                UserDevice ud = (UserDevice) mAdapter.getObject(i);
                mFilesTransfer.sendFile(i, file, ud.ip, 2223);
                Log.d(this.getClass().getName(), ud.ip + ":2333->" + file.toString());
            }
//            for (Map.Entry<String, String> entry : mDevices.entrySet()) {
//            }

        }

    }

    public String getRealPathFromURI(Context context, Uri contentUri) {
        String res = null;
        String[] proj = {MediaStore.Images.Media.DATA};
        Cursor cursor = context.getContentResolver().query(contentUri, proj, null, null, null);
        if (cursor.moveToFirst()) {
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            res = cursor.getString(column_index);
        }
        cursor.close();
        return res;
    }

    /**
     * 设置接收到数据时的监听器<br>
     *
     * @param listener 监听器
     */
    public void setOnDataReceivedListener(OnDataReceivedListener listener) {
        this.mOnDataReceivedListener = listener;
        if (listener == null) {
            mCommandsTransfer.setCallbackHandler(null);
        } else {
            mCommandsTransfer.setCallbackHandler(this.mCommandHandler);
        }
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
        void onDataReceived(SparseArray<UserDevice> devicesList);
//        void onLogin(String address, String name);
//        void onLogout(String address, String name);
    }

    /**
     * 添加设备进列表中
     *
     * @param address    int型的IP地址
     * @param userDevice 设备类，作为value
     */
//    private void addDevice(int address, UserDevice userDevice) {
//        mAdapter.put(address, userDevice);
//    }

    /**
     * 在列表中除移设备
     *
     * @param address int型的IP地址，作为key
     */
//    private void removeDevice(int address) {
//        mAdapter.remove(address);
//    }

    /**
     * 获取用于显示列表的适配器
     *
     * @return ListAdapter
     */
    public FFTAdapter getAdapter() {
        return this.mAdapter;
    }

    /**
     * 合并数组
     *
     * @param byte_1 数组一
     * @param byte_2 数组二
     * @return 合并之后的数组
     */
//    public byte[] byteMerger(byte[] byte_1, byte[] byte_2) {
//        byte[] byte_3 = new byte[byte_1.length + byte_2.length];
//        System.arraycopy(byte_1, 0, byte_3, 0, byte_1.length);
//        System.arraycopy(byte_2, 0, byte_3, byte_1.length, byte_2.length);
//        return byte_3;
//    }

    /**
     * @param array
     * @return int
     */
    private int byteArray2Int(byte[] array) {
        int temp = 0;
        for (int i = 0; i < 4; i++) {
            temp |= array[i] << (24 - (i * 8));
        }
        return temp;
    }

    /**
     * byte数组型IP地址转成String型IP地址
     *
     * @param array
     * @return String
     */
    private String byteArray2IpAddress(byte[] array) {
        return (array[0] & 0xff) + "." + (array[1] & 0xff) + "." + (array[2] & 0xff) + "." + (array[3] & 0xff);
    }

}
