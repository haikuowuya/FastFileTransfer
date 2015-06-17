package vis.net;

import android.os.Environment;
import android.util.Log;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * 文件传输类
 * Created by Vision on 15/6/16.<br>
 * Email:Vision.lsm.2012@gmail.com
 */
public class FilesTransfer {
    private ServerSocket mServerSocket;
    private Socket mSocket;
    /**
     * 是否在接收模式
     */
    private boolean isReceiving = false;

    public FilesTransfer() {
    }

    /**
     * 发送文件
     *
     * @param file    要发送的文件
     * @param address 要发送往的地址
     * @param port    目标地址的端口
     */
    public void sendFile(File file, String address, int port) {
        new Thread(new Sender(file, address, port)).start();
    }

    /**
     * 接收文件
     *
     * @param dirName 文件存放位置，文件夹名
     */
    public void receiveFile(int port, String dirName) {
        if (Environment.getExternalStorageState().equals(
                Environment.MEDIA_MOUNTED)) {
//            Log.d(this.getClass().getName(), Environment.getExternalStorageState());
            File dir = new File(Environment.getExternalStorageDirectory().getPath());
            //FIXME 总是没有发现目录
            if (dir.exists()) {
                if (dir.canWrite()) {
                    Log.d(this.getClass().getName(), "the dir is OK!");
                    new Thread(new Receiver(port, dir));
                } else {
                    Log.e(this.getClass().getName(), "the dir can not write");
                }
            } else {
                Log.d(this.getClass().getName(), "没有这个目录");
            }
        } else {
            Log.e(this.getClass().getName(), "请检查SD卡是否正确安装");
        }
    }

    public boolean isReceiving() {
        return this.isReceiving;
    }

    class Receiver implements Runnable {
        private DataInputStream din = null;
        private FileOutputStream fout;
        private int length = 0;
        private byte[] inputByte = null;
        private int port;
        private File dir;

        public Receiver(int port, File dir) {
            this.port = port;
            this.dir = dir;
        }

        @Override
        public void run() {
            isReceiving = true;
            inputByte = new byte[1024];
            try {
                mServerSocket = new ServerSocket(port);
                mServerSocket.setSoTimeout(2000);
                Log.d(this.getClass().getName(), "accepting the connect");
                mSocket = mServerSocket.accept();

                din = new DataInputStream(mSocket.getInputStream());
                fout = new FileOutputStream(new File(dir.getAbsolutePath() + "/" + din.readUTF()));
                while (true) {
                    if (din != null) {
                        length = din.read(inputByte, 0, inputByte.length);
                    }
                    if (length == -1) {
                        break;
                    }
//                        System.out.println(length);
                    fout.write(inputByte, 0, length);
                    fout.flush();
                }
//                    System.out.println("完成接收");
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    if (fout != null)
                        fout.close();
                    if (din != null)
                        din.close();
                    if (mSocket != null)
                        mSocket.close();
                    if (mServerSocket != null) {
                        mServerSocket.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    isReceiving = false;
                }
            }
        }
    }

    class Sender implements Runnable {
        private int length = 0;
        private byte[] sendByte = null;
        private Socket socket = null;
        private DataOutputStream dout = null;
        private FileInputStream fin = null;

        private File file;
        private String address;
        private int port;

        public Sender(File file, String address, int port) {
            this.file = file;
            this.address = address;
            this.port = port;
        }

        @Override
        public void run() {
            try {
                socket = new Socket();
                socket.connect(new InetSocketAddress(address, port), 10 * 1000);
                dout = new DataOutputStream(socket.getOutputStream());
//                File file = new File("E:\\TU\\DSCF0320.JPG");
                fin = new FileInputStream(file);
                sendByte = new byte[1024];
                dout.writeUTF(file.getName());
                while ((length = fin.read(sendByte, 0, sendByte.length)) > 0) {
                    dout.write(sendByte, 0, length);
                    dout.flush();
                }
            } catch (IOException e) {

            } finally {
                try {
                    if (dout != null)
                        dout.close();
                    if (fin != null)
                        fin.close();
                    if (socket != null)
                        socket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }


}
