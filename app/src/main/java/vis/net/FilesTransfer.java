package vis.net;

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

    public FilesTransfer() {
    }


    public void startAccept(int port) {
        new Thread(new Accept(port));
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

    public void receiveFile() {
        new Thread(new Receiver());
    }


    class Accept implements Runnable {
        private int port;

        public Accept(int port) {
            this.port = port;
        }

        @Override
        public void run() {
            try {
                mServerSocket = new ServerSocket(port);
                mSocket = mServerSocket.accept();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    class Receiver implements Runnable {
        private DataInputStream din = null;
        private FileOutputStream fout;
        private int length = 0;
        private byte[] inputByte = null;

        public Receiver() {

        }

        @Override
        public void run() {
            if (mSocket != null) {
                inputByte = new byte[1024];
                try {
                    din = new DataInputStream(mSocket.getInputStream());
                    fout = new FileOutputStream(new File("E:\\" + din.readUTF()));
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
                    }
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
