package vision.RM;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

/**
 * Created by Vision on 15/6/30.<br>
 * Email:Vision.lsm.2012@gmail.com
 */
public class AdapterRM extends FragmentPagerAdapter {

//    private static final int TAB_INDEX_ONE = 0;
//    private static final int TAB_INDEX_TWO = 1;
//    private static final int TAB_INDEX_THREE = 2;
//    private static final int TAB_INDEX_FOUR = 3;
//    private static final int TAB_INDEX_COUNT = 4;


//    private final FragmentImage mFragment1;
//    private final FragmentMusic mFragment2;
//    private final FragmentVideo mFragment3;
//    private final FragmentText mFragment4;

    private final Fragment[] mFragments;

    public AdapterRM(FragmentManager fm) {
        super(fm);
        this.mFragments = new Fragment[4];
        mFragments[0] = FragmentImage.newInstance("", "");
        mFragments[1] = FragmentMusic.newInstance("", "");
        mFragments[2] = FragmentVideo.newInstance("", "");
        mFragments[3] = FragmentText.newInstance("", "");
    }

    public AdapterRM(FragmentManager fm,Fragment[] mFragments) {
        super(fm);
        this.mFragments = mFragments;
//        mFragment1 = FragmentImage.newInstance("", "");
//        mFragment2 = FragmentMusic.newInstance("", "");
//        mFragment3 = FragmentVideo.newInstance("", "");
//        mFragment4 = FragmentText.newInstance("", "");
    }

    @Override
    public Fragment getItem(int position) {
//        switch (position) {
//            case TAB_INDEX_ONE:
//                return mFragment1;
//            case TAB_INDEX_TWO:
//                return mFragment2;
//            case TAB_INDEX_THREE:
//                return mFragment3;
//            case TAB_INDEX_FOUR:
//                return mFragment4;
//        }
        try {
            return mFragments[position];
        } catch (ArrayIndexOutOfBoundsException e) {
            throw new IllegalStateException("No fragment at position " + position);
        }
    }

    @Override
    public int getCount() {
        return mFragments.length;
//        return TAB_INDEX_COUNT;
    }

//    @Override
//    public CharSequence getPageTitle(int position) {
//        String tabLabel = null;
//        switch (position) {
//            case TAB_INDEX_ONE:
//                tabLabel = getString(R.string.tab_1);
//                break;
//            case TAB_INDEX_TWO:
//                tabLabel = getString(R.string.tab_2);
//                break;
//            case TAB_INDEX_THREE:
//                tabLabel = getString(R.string.tab_3);
//                break;
//        }
//        return tabLabel;
//    }
}
