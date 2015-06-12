package vis.net.protocol;

import android.util.Log;

import vis.net.UDPHelper;

/**
 * Created by Vision on 15/6/12.
 * Email:Vision.lsm.2012@gmail.com
 */
public class FFTP {
    private UDPHelper mUDPHelper;
    private OnDataReceivedListener mOnDataReceivedListener;

    private UDPHelper getUDPHelper() {
        if (mUDPHelper == null) {
            mUDPHelper = new UDPHelper();
        }
        return mUDPHelper;
    }

    public void sendLogin() {
        SwapPackage sp = new SwapPackage(SwapPackage.LOGIN, SwapPackage.LOCALNAME);
        getUDPHelper().send(sp.getString());
    }

    public void sendLogout() {
        SwapPackage sp = new SwapPackage(SwapPackage.LOGOUT, SwapPackage.LOCALNAME);
        getUDPHelper().send(sp.getString());
    }

    public static void sendFlies() {

    }


    public static byte[] byteMerger(byte[] byte_1, byte[] byte_2) {
        byte[] byte_3 = new byte[byte_1.length + byte_2.length];
        System.arraycopy(byte_1, 0, byte_3, 0, byte_1.length);
        System.arraycopy(byte_2, 0, byte_3, byte_1.length, byte_2.length);
        return byte_3;
    }

    /**
     * 暂时想不到怎么处理
     *
     * @param listener
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
                    if (sp.getCmd() == SwapPackage.LOGIN) {
                        Log.d("Login",new String(sp.getData()));
                        mOnDataReceivedListener.onLogin(new String(sp.getData()));
                    }
                    if (sp.getCmd() == SwapPackage.LOGOUT) {
                        Log.d("Logout",new String(sp.getData()));
                        mOnDataReceivedListener.onLogout(new String(sp.getData()));
                    }
                }
            });

        }
    }

    /**
     * 暂时没有想到怎么用
     */
    public interface OnDataReceivedListener {
        void onDataReceived(SwapPackage sp);
        void onLogin(String name);
        void onLogout(String name);
    }
}
