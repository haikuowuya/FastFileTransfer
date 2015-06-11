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
 * Created by Vision on 15/6/9.
 */
public class UDPHelper {

    private static final int PORT = 2048;
    private static final int PACKETLENGTH = 12;
    private Handler handler;
    private boolean life = true;
    private DatagramSocket mDatagramSocket = null;
    private DatagramPacket sendPacket;

    public UDPHelper(Handler handler) {
        this.handler = handler;
        byte[] sendData = new byte[PACKETLENGTH];
        sendPacket = new DatagramPacket(sendData, sendData.length);
    }

    /**
     * 获得DatagramSocket对象，单例模式
     *
     * @return DatagramSocket
     */
    private DatagramSocket getSocket() {
        if (this.mDatagramSocket == null) {
            try {
                mDatagramSocket = new DatagramSocket(PORT);
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
     * @param life the life to set
     */
    public void setLife(final boolean life) {
        this.life = life;
    }

    /**
     * @return the life
     */
    public boolean isLife() {
        return life;
    }

    public void send(byte[] msg) {
        new Thread(new Sender(msg)).start();
    }

    /**
     * 开启接收器
     */
    public void enableReceiver() {
//        ExecutorService exec = Executors.newCachedThreadPool();
//        Receiver server = new Receiver();
//        exec.execute(server);
        new Thread(new Receiver()).start();
    }

    class Sender implements Runnable {

        private final byte[] msg;
        private final String TAG = Sender.class.getName();

        public Sender(byte[] msg) {
            this.msg = msg;
        }

        @Override
        public void run() {
            try {
                sendPacket = new DatagramPacket(msg, msg.length, InetAddress.getByName("255.255.255.255"),
                        PORT);
            } catch (UnknownHostException e) {
                e.printStackTrace();
            }
            try {
                getSocket().send(sendPacket);
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (!life) {
                closeSocket();
            }
        }
    }

    class Receiver implements Runnable {
        private Message msg;

        public Receiver() {
        }

        @Override
        public void run() {
            byte[] receiveBuf = new byte[PACKETLENGTH];
            DatagramPacket receivePacket = new DatagramPacket(receiveBuf, receiveBuf.length);
            while (life) {
                try {
                    getSocket().receive(receivePacket);
                    byte[] recData = receivePacket.getData();
                    msg = Message.obtain();
                    msg.obj = recData;
                    handler.sendMessage(msg);
                    Log.i("msg sever received", new String(recData).trim() + "," + String.valueOf(recData.length));
                } catch (IOException e) {
                    Log.d("", "I am receiving!");
                }
            }
            closeSocket();
        }
    }

}
