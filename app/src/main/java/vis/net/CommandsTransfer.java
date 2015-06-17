package vis.net;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;

/**
 * 命令传输类，<br>
 * 使用前必须先调用enable使能，使用完毕必须再调用disable失能；
 * 发送直接调用 {@code send()} ；
 * 接收只需要设置好监听 {@code setDateReceivedListener()} 就能开启，设置为Null即关闭<br>
 * <p/>
 * <br>
 * Created by Vision on 15/6/9.<br>
 * Email:Vision.lsm.2012@gmail.com
 */
public class CommandsTransfer {

    //    private static final int PORT = 2048;
    private static final int RECEIVEPACKETLENGTH = 64;
    /**
     * 是否使能UDP
     */
    private boolean isEnable = false;
    /**
     * 是否使能接收
     */
    private boolean isReceive = false;

    /**
     * 接收监听端口
     */
    private int recvPort;
    private DatagramSocket mDatagramSocket = null;
    private DatagramPacket sendPacket;
    private OnDataReceivedListener mOnDataReceivedListener;

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            Object[] objects = (Object[]) msg.obj;
            mOnDataReceivedListener.onDataReceived(((String)(objects[0])), ((byte[]) objects[1]));
        }
    };

    public CommandsTransfer() {
        //默认接收端口为2048
        this(2048);
    }

    public CommandsTransfer(int recvPort) {
        byte[] sendData = new byte[RECEIVEPACKETLENGTH];
        sendPacket = new DatagramPacket(sendData, sendData.length);
        this.recvPort = recvPort;
    }

    /**
     * 使能UDP
     */
    public void enable() {
        this.isEnable = true;
    }

    /**
     * @return the isEnable
     */
    public boolean isEnable() {
        return isEnable;
    }

    /**
     * 失能UDP
     */
    public void disable() {
        this.isReceive = false;
        this.isEnable = false;
        closeSocket();
    }

    /**
     * 获得DatagramSocket对象，单例模式
     *
     * @return DatagramSocket
     */
    private DatagramSocket getSocket() {
        if (this.mDatagramSocket == null) {
            try {
                mDatagramSocket = new DatagramSocket(recvPort);
                mDatagramSocket.setSoTimeout(5000);
            } catch (SocketException e) {
                e.printStackTrace();
            }
        }
        return mDatagramSocket;
    }

    /**
     * 关闭DatagramSocket
     */
    private void closeSocket() {
        if (mDatagramSocket != null) {
            if (mDatagramSocket.isConnected()) {
                mDatagramSocket.disconnect();
            }
            if (!mDatagramSocket.isClosed()) {
                mDatagramSocket.close();
            }
            mDatagramSocket = null;
        }
    }

    /**
     * 发送
     *
     * @param msg 信息字节数组
     */
    public void send(byte[] msg, String address, int port) {
        if (isEnable) {
            new Thread(new Sender(msg, address, port)).start();
        }
    }

    /**
     * 开启接收器
     */
    private void startReceiver() {
//        ExecutorService exec = Executors.newCachedThreadPool();
//        Receiver server = new Receiver();
//        exec.execute(server);
        if (isEnable && !isReceive) {
            isReceive = true;
            new Thread(new Receiver()).start();
        }
    }

    /**
     * 停止接收
     */
    private void stopReceiver() {
        isReceive = false;
    }

    /**
     * 设置数据接收监听
     *
     * @param listener 监听器
     */
    public void setDateReceivedListener(OnDataReceivedListener listener) {
        this.mOnDataReceivedListener = listener;
        if (null == listener) {
            stopReceiver();
        } else {
            startReceiver();
        }

    }

    /**
     * 监听接口
     */
    public interface OnDataReceivedListener {
        void onDataReceived(String sourceAddress, byte[] data);
    }

    /**
     * 发送线程
     */
    class Sender implements Runnable {

        private final byte[] msg;
        private final String TAG = Sender.class.getName();
        private final String address;
        private final int port;

        public Sender(byte[] msg, String address, int port) {
            this.address = address;
            this.port = port;
            this.msg = msg;
        }

        @Override
        public void run() {
            try {//InetAddress.getByName("255.255.255.255")
                sendPacket = new DatagramPacket(msg, msg.length, InetAddress.getByName(address),
                        port);
            } catch (UnknownHostException e) {
                e.printStackTrace();
            }
            try {
                getSocket().send(sendPacket);
                Log.d("send", new String(msg));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 接收线程
     */
    class Receiver implements Runnable {
        private Message msg;

        @Override
        public void run() {
            byte[] receiveBuf = new byte[RECEIVEPACKETLENGTH];
            DatagramPacket receivePacket = new DatagramPacket(receiveBuf, receiveBuf.length);
            while (isEnable && isReceive) {
                try {
                    getSocket().receive(receivePacket);
                    Object[] objects = new Object[2];
                    byte[] address = receivePacket.getAddress().getAddress();
                    objects[0] = (address[0] & 0xff) + "." + (address[1] & 0xff) + "." + (address[2] & 0xff) + "." + (address[3] & 0xff);
                    objects[1] = receivePacket.getData();
                    msg = Message.obtain();
                    msg.obj = objects;
                    mHandler.sendMessage(msg);
                    Log.i("received", objects[0] + "->" + new String(receivePacket.getData()).trim());
                } catch (IOException e) {
                    Log.d("", "I am receiving!");
                }
            }
            isReceive = false;
        }
    }

}
