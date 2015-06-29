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
import java.lang.ref.WeakReference;

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
    private FFTAdapter mAdapter;

    /**
     * 目标地址
     */
    private String targetAddress;

    private Handler mCommandHandler = new MyHandler(this);

    private static class MyHandler extends Handler {
        private final WeakReference<FFTService> mFFTService;

        public MyHandler(FFTService ffts) {
            mFFTService = new WeakReference<FFTService>(ffts);
        }

        @Override
        public void handleMessage(Message msg) {
            FFTService ffts = mFFTService.get();
            if (ffts != null) {
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
                    ffts.mAdapter.put(ip, us);
                    Log.d("Login", us.ip + "->" + new String(sp.getData()));
                } else if (sp.getCmdByByte() == SwapPackage.LOGOUT) {
                    int ip = byteArray2Int(address);
                    ffts.mAdapter.remove(ip);
                    Log.d("Logout", new String(address) + "->" + new String(sp.getData()));
                }
                ffts.mOnDataReceivedListener.onDataReceived(null);
            }
        }
    }


    public FFTService(Context context, int serviceType) {
        mCommandsTransfer = new CommandsTransfer(2222);
        mFilesTransfer = new FilesTransfer(context,serviceType);
        if (SERVICE_SHARE == serviceType) {
            mAdapter = new UserDevicesAdapter(context);
        } else if (SERVICE_RECEIVE == serviceType) {
            mAdapter = new UserFilesAdapter(context);
        }
        //把适配器的handler交给mFilesTransfer，以便transfer控制适配器
//        Log.d("FFTService", String.valueOf(mAdapter.getHandler()));
        mFilesTransfer.setCallbackHandler(mAdapter.getHandler());
    }

    /**
     * 使能
     */
    public void enable() {
//        mCommandsTransfer.enable();
    }

    /**
     * 失能
     */
    public void disable() {
//        mCommandsTransfer.disable();
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
//            Toast.makeText(context, file.toString(), Toast.LENGTH_SHORT).show();
            for (int i = 0, nsize = mAdapter.getCount(); i < nsize; i++) {
                UserDevice ud = (UserDevice) mAdapter.getObject(i);
                mFilesTransfer.sendFile(i, file, ud.ip, 2223);
                Log.d(this.getClass().getName(), ud.ip + ":2333->" + file.toString());
            }
        }
    }

    public static String getRealPathFromURI(Context context, Uri contentUri) {
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
    }

    /**
     * 获取用于显示列表的适配器
     *
     * @return ListAdapter
     */
    public FFTAdapter getAdapter() {
        return this.mAdapter;
    }

    /**
     * @param array
     * @return int
     */
    private static int byteArray2Int(byte[] array) {
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
    private static String byteArray2IpAddress(byte[] array) {
        return (array[0] & 0xff) + "." + (array[1] & 0xff) + "." + (array[2] & 0xff) + "." + (array[3] & 0xff);
    }

}
