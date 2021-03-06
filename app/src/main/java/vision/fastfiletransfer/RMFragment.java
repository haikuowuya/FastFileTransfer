package vision.fastfiletransfer;

import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v4.app.ListFragment;
import android.support.v4.view.ViewPager;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.util.List;

import vis.OpenFile;
import vis.SelectedFilesQueue;
import vis.UserFile;
import vision.resourcemanager.AdapterApp;
import vision.resourcemanager.AdapterAudio;
import vision.resourcemanager.AdapterImage;
import vision.resourcemanager.AdapterList;
import vision.resourcemanager.AdapterText;
import vision.resourcemanager.AdapterVideo;
import vision.resourcemanager.FileApp;
import vision.resourcemanager.FileAudio;
import vision.resourcemanager.FileImage;
import vision.resourcemanager.FileText;
import vision.resourcemanager.FileVideo;

public class RMFragment extends Fragment {
    private static final String ARG_PARAM1 = "type";
    private static final String ARG_PARAM2 = "page";

    public static final byte TYPE_RESOURCE_MANAGER = 1;
    public static final byte TYPE_FILE_TRANSFER = 2;

    public static final int PAGE_IMAGE = 0x01;
    public static final int PAGE_AUDIO = 0x02;
    public static final int PAGE_VIDEO = 0x04;
    public static final int PAGE_TEXT = 0x08;
    public static final int PAGE_APP = 0x10;

    private byte type;
    private int page;
    private int pageCount;

    private SelectedFilesQueue<vision.resourcemanager.File> mSelectedList;

    private ListFragment[] mFragments;
    private AdapterList[] mAdapterLists;
    private SparseArray<FileImage> mFileImage;
    private SparseArray<FileAudio> mFileAudio;
    private SparseArray<FileVideo> mFileVideo;
    private SparseArray<FileText> mFileText;
    private SparseArray<FileApp> mFileApp;

    private RMAdapter mViewPagerAdapter;
    private TextView[] tab;
    private ViewPager vp;
    private LinearLayout btnLinearLayout;
    private Button btnLeft;
    private Button btnRight;

    private View.OnClickListener mShareListener;
    private View.OnClickListener mCancelListener;
    private View.OnClickListener mOpenFileListener;
    private View.OnClickListener mDeleteFileListener;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     * <p>用法： RMFragment.newInstance(
     * RMFragment.TYPE_FILE_TRANSFER,
     * RMFragment.PAGE_AUDIO | RMFragment.PAGE_IMAGE | RMFragment.PAGE_APP | RMFragment.PAGE_VIDEO | RMFragment.PAGE_TEXT);
     * </p>
     * <p>另外，在父Activity中必需要声明和实例化一个公共类：SelectedFilesQueue<vision.resourcemanager.File> mSelectedList，以存放用户选择的文件类。</p>
     *
     * @param type 使用类型.
     * @param page 需要显示的页面
     * @return A new instance of fragment RMFragment.
     */
    public static RMFragment newInstance(byte type, int page) {
        RMFragment fragment = new RMFragment();
        Bundle args = new Bundle();
        args.putByte(ARG_PARAM1, type);
        args.putInt(ARG_PARAM2, page);
        fragment.setArguments(args);
        return fragment;
    }

    public RMFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            type = getArguments().getByte(ARG_PARAM1);
            page = getArguments().getInt(ARG_PARAM2);
        }

        if (android.os.Build.VERSION.SDK_INT < 11) {
            //FIXME 低版本手机兼容
            if ((page & PAGE_TEXT) != 0) {
                //去掉PAGE_TEXT位
                page &= ~PAGE_TEXT;
            }
        }

        this.pageCount = NumCount2(page);

        mFragments = new ListFragment[this.pageCount];
        mAdapterLists = new AdapterList[this.pageCount];


        for (int i = 0; i < this.pageCount; i++) {
            mFragments[i] = new ListFragment();
        }

        if (TYPE_FILE_TRANSFER == type) {
            mSelectedList = ((ShareActivity) getActivity()).mSelectedFilesQueue;
        } else if (TYPE_RESOURCE_MANAGER == type) {
            mSelectedList = ((ResourceManagerActivity) getActivity()).mSelectedFilesQueue;
        }

        if (TYPE_FILE_TRANSFER == type) {
            mShareListener = new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ((ShareActivity) getActivity()).jumpToFragment(ShareActivity.SHARE_FRAGMENT);
                }
            };
        } else if (TYPE_RESOURCE_MANAGER == type) {
            mOpenFileListener = new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    OpenFile.openFile(getActivity(), mSelectedList.getPaths()[0]);
                }
            };
            mDeleteFileListener = new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    AlertDialog dialog = new AlertDialog.Builder(getActivity())
                            .setTitle("确认删除吗？")
                            .setIcon(android.R.drawable.ic_dialog_info)
                            .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    // 点击“确认”后的操作
                                    boolean delete = false;
                                    for (vision.resourcemanager.File file : mSelectedList.data) {
                                        File trueFile = new File(file.data);
                                        if (trueFile.exists() && trueFile.delete()) {
                                            switch (file.type) {
                                                case UserFile.TYPE_IMAGE: {
                                                    mFileImage.remove(file.id);
                                                    break;
                                                }
                                                case UserFile.TYPE_AUDIO: {
                                                    mFileAudio.remove(file.id);
                                                    break;
                                                }
                                                case UserFile.TYPE_VIDEO: {
                                                    mFileVideo.remove(file.id);
                                                    break;
                                                }
                                                case UserFile.TYPE_TEXT: {
                                                    mFileText.remove(file.id);
                                                    break;
                                                }
                                                case UserFile.TYPE_APP:{
                                                    mFileApp.remove(file.id);
                                                    break;
                                                }
                                            }
                                            delete = true;
                                            mSelectedList.remove(file);
                                        }
                                    }
                                    if (delete) {
                                        Toast.makeText(getActivity(), "删除成功", Toast.LENGTH_SHORT).show();
                                        refreshAll();
                                    } else {
                                        Toast.makeText(getActivity(), "删除失败", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            })
                            .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    // 点击“返回”后的操作,这里不设置没有任何操作
                                }
                            })
                            .create();
                    dialog.show();
                }
            };
        }

        mCancelListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cancelAll();
            }
        };

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_manager, container, false);
        tab = new TextView[this.pageCount];
        for (int i = 0; i < this.pageCount; i++) {
            tab[i] = (TextView) rootView.findViewById(R.id.tab_1 + i);
            tab[i].setOnClickListener(new TxListener(i));
            tab[i].setVisibility(View.VISIBLE);
        }
        vp = (ViewPager)
                rootView.findViewById(R.id.vp);
        btnLinearLayout = (LinearLayout)
                rootView.findViewById(R.id.btnLinearLayout);
        btnLeft = (Button)
                rootView.findViewById(R.id.btnLeft);
        btnRight = (Button)
                rootView.findViewById(R.id.btnRight);
        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        mViewPagerAdapter = new RMAdapter(getFragmentManager(), mFragments);
        vp.setAdapter(mViewPagerAdapter);

        int pageIndex = 0;
        if ((page & PAGE_IMAGE) != 0) {
            mAdapterLists[pageIndex] = new AdapterImage(getActivity(), mSelectedList);
            tab[pageIndex].setText("图片");
            mFragments[pageIndex].setListAdapter(mAdapterLists[pageIndex]);
            new RefreshImageList(mAdapterLists[pageIndex]).execute();
            pageIndex++;
        }
        if ((page & PAGE_AUDIO) != 0) {
            mAdapterLists[pageIndex] = new AdapterAudio(getActivity(), mSelectedList);
            tab[pageIndex].setText("音乐");
            mFragments[pageIndex].setListAdapter(mAdapterLists[pageIndex]);
            new RefreshAudioList(mAdapterLists[pageIndex]).execute();
            pageIndex++;
        }
        if ((page & PAGE_VIDEO) != 0) {
            mAdapterLists[pageIndex] = new AdapterVideo(getActivity(), mSelectedList);
            tab[pageIndex].setText("视频");
            mFragments[pageIndex].setListAdapter(mAdapterLists[pageIndex]);
            new RefreshVideoList(mAdapterLists[pageIndex]).execute();
            pageIndex++;
        }
        if ((page & PAGE_TEXT) != 0) {
            mAdapterLists[pageIndex] = new AdapterText(getActivity(), mSelectedList);
            tab[pageIndex].setText("文档");
            mFragments[pageIndex].setListAdapter(mAdapterLists[pageIndex]);
            new RefreshTextList(mAdapterLists[pageIndex]).execute();
            pageIndex++;
        }
        if ((page & PAGE_APP) != 0) {
            mAdapterLists[pageIndex] = new AdapterApp(getActivity(), mSelectedList);
            tab[pageIndex].setText("应用");
            mFragments[pageIndex].setListAdapter(mAdapterLists[pageIndex]);
            new RefreshAppList(mAdapterLists[pageIndex]).execute();
        }

        vp.setCurrentItem(0);
        tab[0].setTextColor(Color.parseColor("#ffffff"));

        vp.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                for (int i = 0; i < mViewPagerAdapter.getCount(); i++) {
                    tab[i].setTextColor(Color.parseColor("#000000"));
                }
                tab[position].setTextColor(Color.parseColor("#ffffff"));
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        if (TYPE_FILE_TRANSFER == type) {
            btnLeft.setOnClickListener(mShareListener);
            btnRight.setOnClickListener(mCancelListener);
        } else if (TYPE_RESOURCE_MANAGER == type) {
            btnRight.setOnClickListener(mDeleteFileListener);
        }

        mSelectedList.setOnDataChangedListener(new SelectedFilesQueue.OnDataChangedListener() {
            @Override
            public void onAddedListener(int size) {
                btnLinearLayout.setVisibility(View.VISIBLE);
                if (TYPE_FILE_TRANSFER == type) {
                    btnLeft.setText("分享(" + size + ")");
                } else if (TYPE_RESOURCE_MANAGER == type) {
                    if (size == 1) {
                        btnLeft.setText("打开");
                        btnLeft.setOnClickListener(mOpenFileListener);
                    } else {
                        btnLeft.setText("取消");
                        btnLeft.setOnClickListener(mCancelListener);
                    }
                    btnRight.setText("删除(" + size + ")");
                }
            }

            @Override
            public void onRemovedListener(int size) {
                if (size == 0) {
                    btnLinearLayout.setVisibility(View.GONE);
                    return;
                }
                if (type == TYPE_FILE_TRANSFER) {
                    btnLeft.setText("分享(" + mSelectedList.size() + ")");
                } else if (TYPE_RESOURCE_MANAGER == type) {
                    if (size == 1) {
                        btnLeft.setText("打开");
                        btnLeft.setOnClickListener(mOpenFileListener);
                    } else {
                        btnLeft.setText("取消");
                        btnLeft.setOnClickListener(mCancelListener);
                    }
                    btnRight.setText("删除(" + size + ")");
                }
            }
        });

    }

    public void cancelAll() {
        for (vision.resourcemanager.File file : mSelectedList.data) {
            file.isSelected = false;
        }
        mSelectedList.removeAll();
        btnLinearLayout.setVisibility(View.GONE);
        refreshAll();
    }

    public void refreshAll() {
        for (AdapterList adapterList : mAdapterLists) {
            adapterList.notifyDataSetChanged();
        }
    }

    private class RefreshImageList extends AsyncTask<Void, Void, SparseArray<?>> {
        SparseArray<FileImage> images;
        private AdapterList mAdapterList;

        public RefreshImageList(AdapterList adapterList) {
            this.mAdapterList = adapterList;
        }

        protected SparseArray<?> doInBackground(Void... params) {
            images = new SparseArray<FileImage>();
            Cursor curImage = getActivity().getContentResolver().query(
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                    new String[]{
                            MediaStore.Images.Media._ID,
                            MediaStore.Images.Media.DATA,
                            MediaStore.Images.Media.SIZE,
                            MediaStore.Images.Media.DISPLAY_NAME,
                            MediaStore.Images.Media.DATE_MODIFIED
                    },
                    null,
                    null,
                    MediaStore.Images.Media.DATE_MODIFIED + " DESC");
            if (curImage.moveToFirst()) {
                FileImage file;
                int i = 0;
                do {
                    file = new FileImage();
                    file.oid = curImage.getLong(curImage.getColumnIndex(MediaStore.Images.Media._ID));
                    file.id = i;
                    file.data = curImage.getString(curImage.getColumnIndex(MediaStore.Images.Media.DATA));
                    if (!new File(file.data).exists()) {
                        continue;
                    }
                    file.type = UserFile.TYPE_IMAGE;
                    file.size = curImage.getLong(curImage.getColumnIndex(MediaStore.Images.Media.SIZE));
                    file.strSize = UserFile.bytes2kb(file.size);
                    file.name = curImage.getString(curImage
                            .getColumnIndex(MediaStore.Images.Media.DISPLAY_NAME));
                    file.date = curImage.getLong(curImage.getColumnIndex(MediaStore.Images.Media.DATE_MODIFIED));
                    file.strDate = UserFile.dateFormat(file.date);
                    this.images.put(i++, file);
                } while (curImage.moveToNext());
            }
            curImage.close();
            mFileImage = images;
            return images;
        }


        @Override
        protected void onPostExecute(SparseArray<?> sparseArray) {
            mAdapterList.setData(sparseArray);
        }
    }

    private class RefreshAudioList extends AsyncTask<Void, Void, SparseArray<?>> {
        SparseArray<FileAudio> audios;
        private AdapterList mAdapterList;

        public RefreshAudioList(AdapterList adapterList) {
            this.mAdapterList = adapterList;
        }

        protected SparseArray<?> doInBackground(Void... params) {
            audios = new SparseArray<FileAudio>();
            Cursor curAudio = getActivity().getContentResolver().query(
                    MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                    new String[]{
                            MediaStore.Audio.Media._ID,
                            MediaStore.Audio.Media.DATA,
                            MediaStore.Audio.Media.SIZE,
                            MediaStore.Audio.Media.DISPLAY_NAME,
                            MediaStore.Audio.Media.DATE_ADDED,
                            MediaStore.Audio.Media.DATE_MODIFIED
                    }, null, null, null);
            if (curAudio.moveToFirst()) {
                FileAudio file;
                int i = 0;
                do {
                    file = new FileAudio();
//                    fileAudio.id = curAudio.getLong(curAudio.getColumnIndex(MediaStore.Audio.Media._ID));
                    file.id = i;
                    file.data = curAudio.getString(curAudio.getColumnIndex(MediaStore.Audio.Media.DATA));
                    if (!new File(file.data).exists()) {
                        continue;
                    }
                    file.type = UserFile.TYPE_AUDIO;
                    file.size = curAudio.getLong(curAudio.getColumnIndex(MediaStore.Audio.Media.SIZE));
                    file.strSize = UserFile.bytes2kb(file.size);
                    file.name = curAudio.getString(curAudio
                            .getColumnIndex(MediaStore.Audio.Media.DISPLAY_NAME));
                    file.date = curAudio.getLong(curAudio.getColumnIndex(MediaStore.Audio.Media.DATE_MODIFIED));
                    file.strDate = UserFile.dateFormat(file.date);
                    this.audios.put(i++, file);
                } while (curAudio.moveToNext());
            }
            curAudio.close();
            mFileAudio = audios;
            return audios;
        }


        @Override
        protected void onPostExecute(SparseArray<?> sparseArray) {
//            super.onPostExecute(sparseArray);
            mAdapterList.setData(sparseArray);
        }
    }

    private class RefreshVideoList extends AsyncTask<Void, Void, SparseArray<?>> {
        SparseArray<FileVideo> videos;
        private AdapterList mAdapterList;

        public RefreshVideoList(AdapterList adapterList) {
            mAdapterList = adapterList;
        }

        protected SparseArray<?> doInBackground(Void... params) {
            videos = new SparseArray<FileVideo>();
            Cursor curVideo = getActivity().getContentResolver().query(
                    MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
                    new String[]{
                            MediaStore.Video.Media._ID,
                            MediaStore.Video.Media.DATA,
                            MediaStore.Video.Media.SIZE,
                            MediaStore.Video.Media.DISPLAY_NAME,
                            MediaStore.Video.Media.DATE_MODIFIED
                    },
                    null,
                    null,
                    MediaStore.Video.Media.DATE_MODIFIED + " DESC");
            if (curVideo.moveToFirst()) {
                FileVideo file;
                int i = 0;
                do {
                    file = new FileVideo();
                    file.oid = curVideo.getLong(curVideo.getColumnIndex(MediaStore.Video.Media._ID));
                    file.id = i;
                    file.data = curVideo.getString(curVideo.getColumnIndex(MediaStore.Video.Media.DATA));
                    if (!new File(file.data).exists()) {
                        continue;
                    }
                    file.type = UserFile.TYPE_VIDEO;
                    file.size = curVideo.getLong(curVideo.getColumnIndex(MediaStore.Video.Media.SIZE));
                    file.strSize = UserFile.bytes2kb(file.size);
                    file.name = curVideo.getString(curVideo
                            .getColumnIndex(MediaStore.Video.Media.DISPLAY_NAME));
                    file.date = curVideo.getLong(curVideo.getColumnIndex(MediaStore.Audio.Media.DATE_MODIFIED));
                    file.strDate = UserFile.dateFormat(file.date);
                    this.videos.put(i++, file);
                } while (curVideo.moveToNext());
            }
            curVideo.close();
            mFileVideo = videos;
            return videos;
        }


        @Override
        protected void onPostExecute(SparseArray<?> sparseArray) {
//            super.onPostExecute(sparseArray);
            mAdapterList.setData(sparseArray);
        }

    }

    /**
     *
     */
    private class RefreshTextList extends AsyncTask<Void, Void, SparseArray<?>> {
        SparseArray<FileText> texts;
        private AdapterList mAdapterList;

        public RefreshTextList(AdapterList adapterList) {
            mAdapterList = adapterList;
        }

        @TargetApi(Build.VERSION_CODES.HONEYCOMB)
        protected SparseArray<?> doInBackground(Void... params) {
            texts = new SparseArray<>();
            Cursor curText = getActivity().getContentResolver().query(
                    MediaStore.Files.getContentUri("external"),
                    new String[]{
                            MediaStore.Files.FileColumns._ID,
                            MediaStore.Files.FileColumns.DATA,
                            MediaStore.Files.FileColumns.SIZE,
                            MediaStore.Files.FileColumns.MIME_TYPE,
                            MediaStore.Files.FileColumns.DATE_MODIFIED
                    },
                    MediaStore.Files.FileColumns.MIME_TYPE + " LIKE ?",
                    new String[]{"text/%"},
                    null);
            if (curText.moveToFirst()) {
                FileText file;
                int i = 0;
                do {
                    file = new FileText();
//                    file.id = curText.getLong(curText.getColumnIndex(MediaStore.Files.FileColumns._ID));
                    file.id = i;
                    file.data = curText.getString(curText.getColumnIndex(MediaStore.Files.FileColumns.DATA));
                    if (!new File(file.data).exists()) {
                        continue;
                    }
                    file.type = UserFile.TYPE_TEXT;
                    file.size = curText.getLong(curText.getColumnIndex(MediaStore.Files.FileColumns.SIZE));
                    file.strSize = UserFile.bytes2kb(file.size);
                    file.name = file.data.substring(file.data.lastIndexOf("/") + 1);
                    file.date = curText.getLong(curText.getColumnIndex(MediaStore.Files.FileColumns.DATE_MODIFIED));
                    file.strDate = UserFile.dateFormat(file.date);
                    this.texts.put(i++, file);
                } while (curText.moveToNext());
            }
            curText.close();
            mFileText = texts;
            return texts;
        }

        @Override
        protected void onPostExecute(SparseArray<?> sparseArray) {
            mAdapterList.setData(sparseArray);
        }

    }

    private class RefreshAppList extends AsyncTask<Void, Void, SparseArray<?>> {
        SparseArray<FileApp> apps;
        private AdapterList mAdapterList;

        public RefreshAppList(AdapterList adapterList) {
            mAdapterList = adapterList;
        }

        protected SparseArray<?> doInBackground(Void... params) {
            apps = new SparseArray<FileApp>();
            PackageManager packageManager = getActivity().getPackageManager();
            List<ApplicationInfo> applicationInfos = packageManager.getInstalledApplications(0);

            FileApp file;

            ApplicationInfo applicationInfo;
            for (int i = 0, j = 0; i < applicationInfos.size(); i++) {
                applicationInfo = applicationInfos.get(i);
                boolean isUserApp = false;
                if ((applicationInfo.flags & ApplicationInfo.FLAG_UPDATED_SYSTEM_APP) != 0) {
                    isUserApp = true;
                } else if ((applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) == 0) {
                    isUserApp = true;
                }
                if (isUserApp) {
                    file = new FileApp();
                    file.id = i;
                    file.icon = packageManager.getApplicationIcon(applicationInfo);
                    file.name = (String) packageManager.getApplicationLabel(applicationInfo);
                    file.data = applicationInfo.publicSourceDir;
                    if (!new File(file.data).exists()) {
                        continue;
                    }
                    file.type = UserFile.TYPE_APP;
                    file.strSize = UserFile.bytes2kb(new File(file.data).length());
                    file.strDate = file.data.substring(file.data.lastIndexOf("/") + 1);
                    try {
                        file.data = getActivity().getPackageManager().getApplicationInfo("vision.fastfiletransfer", 0).sourceDir;
                    } catch (PackageManager.NameNotFoundException e) {
                        e.printStackTrace();
                    }
                    apps.put(j++, file);
                }
            }
            mFileApp = apps;
            return apps;
        }

        @Override
        protected void onPostExecute(SparseArray<?> sparseArray) {
            mAdapterList.setData(sparseArray);
        }

    }

    public class TxListener implements View.OnClickListener {
        private int index = 0;

        public TxListener(int i) {
            index = i;
        }

        @Override
        public void onClick(View v) {
            vp.setCurrentItem(index);
        }
    }

    public static int NumCount2(int a) {
        int num = 0;
        while (a != 0) {
            a &= (a - 1);
            num++;
        }
        return num;
    }
}
