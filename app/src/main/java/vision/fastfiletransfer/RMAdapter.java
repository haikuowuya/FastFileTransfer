package vision.fastfiletransfer;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import vision.RM.FragmentImage;
import vision.RM.FragmentMusic;
import vision.RM.FragmentText;
import vision.RM.FragmentVideo;

/**
 * Created by Vision on 15/6/30.<br>
 * Email:Vision.lsm.2012@gmail.com
 */
public class RMAdapter extends FragmentPagerAdapter {

    private final Fragment[] mFragments;

    public RMAdapter(FragmentManager fm) {
        super(fm);
        this.mFragments = new Fragment[4];
        mFragments[0] = FragmentImage.newInstance("", "");
        mFragments[1] = FragmentMusic.newInstance("", "");
        mFragments[2] = FragmentVideo.newInstance("", "");
        mFragments[3] = FragmentText.newInstance("", "");
    }

    public RMAdapter(FragmentManager fm, Fragment[] mFragments) {
        super(fm);
        this.mFragments = mFragments;
    }

    @Override
    public Fragment getItem(int position) {
        try {
            return mFragments[position];
        } catch (ArrayIndexOutOfBoundsException e) {
            throw new IllegalStateException("No fragment at position " + position);
        }
    }

    @Override
    public int getCount() {
        return mFragments.length;
    }

}
