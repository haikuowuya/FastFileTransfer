package vis.net.protocol;

import android.os.Handler;
import android.os.Message;

import vis.net.UDPHelper;

/**
 * Created by Vision on 15/6/12.
 */
public class FFTP {
    private final static byte[] LOGIN = new byte[]{100};
    private final static byte[] LOGOUT = new byte[]{99};
    private final static byte[] LOCALNAME = android.os.Build.MODEL.getBytes();
    private static UDPHelper mUDPHelper;
    public final static Handler handler = new Handler() {
        public void handleMessage(Message msg) {

        }

    };

    private static UDPHelper getUDPHelper() {
        if (mUDPHelper == null) {
            mUDPHelper = new UDPHelper(handler);
        }
        return mUDPHelper;

    }

    public static void sendLogin() {
        getUDPHelper().send(byteMerger(LOGIN, LOCALNAME));
    }

    public static void sendLogout() {
        getUDPHelper().send(byteMerger(LOGOUT, LOCALNAME));
    }

    public static void sendFlies() {

    }

    public static byte[] byteMerger(byte[] byte_1, byte[] byte_2) {
        byte[] byte_3 = new byte[byte_1.length + byte_2.length];
        System.arraycopy(byte_1, 0, byte_3, 0, byte_1.length);
        System.arraycopy(byte_2, 0, byte_3, byte_1.length, byte_2.length);
        return byte_3;
    }
}
