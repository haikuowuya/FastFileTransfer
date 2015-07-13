package vision.resourcemanager;

import android.support.annotation.Nullable;

import vis.SelectedFilesQueue;

/**
 * Created by Vision on 15/7/13.<br>
 * Email:Vision.lsm.2012@gmail.com
 */
public interface ResourceManagerInterface {

    int RM_FRAGMENT = 0;
    int SHARE_FRAGMENT = 1;
    int RM_IMAGE_GRID = 2;

    /**
     * @param arg1   要跳转到的fragment
     * @param bucket 携带的信息，要显示的文件夹名
     */
    void onFragmentInteraction(int arg1, String bucket);

    SelectedFilesQueue<File> getSelectedFilesQueue();

    void jumpToFragment(int fragmentType, @Nullable String bucket);

}
