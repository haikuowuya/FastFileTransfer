package vision.fastfiletransfer;

import android.annotation.TargetApi;
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
import android.widget.TextView;

import vision.RM.AdapterAudio;
import vision.RM.AdapterImage;
import vision.RM.AdapterText;
import vision.RM.AdapterVideo;
import vision.RM.FileAudio;
import vision.RM.FileImage;
import vision.RM.FileText;
import vision.RM.FileVideo;
import vision.RM.FragmentAudio;
import vision.RM.FragmentImage;
import vision.RM.FragmentText;
import vision.RM.FragmentVideo;

public class RMFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private ListFragment[] mFragments;
    private AdapterImage mAdapterImage;
    private AdapterAudio mAdapterAudio;
    private AdapterVideo mAdapterVideo;
    private AdapterText mAdapterText;

    //    private OnFragmentInteractionListener mListener;
    private RMAdapter mViewPagerAdapter;
    private ViewPager vp;
    private Button btnShare;
    private Button btnCancel;
    private TextView[] tab;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment RMFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static RMFragment newInstance(String param1, String param2) {
        RMFragment fragment = new RMFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    public RMFragment() {
        // Required empty public constructor
    }

//    @Override
//    public void onAttach(Activity activity) {
//        super.onAttach(activity);
//        try {
//            mListener = (OnFragmentInteractionListener) activity;
//        } catch (ClassCastException e) {
//            throw new ClassCastException(activity.toString()
//                    + " must implement OnFragmentInteractionListener");
//        }
//    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
        mFragments = new ListFragment[4];
        mFragments[0] = FragmentImage.newInstance(null, null);
        mFragments[1] = FragmentAudio.newInstance(null, null);
        mFragments[2] = FragmentVideo.newInstance(null, null);
        mFragments[3] = FragmentText.newInstance(null, null);
        mViewPagerAdapter = new RMAdapter(getFragmentManager(), mFragments);

        mAdapterImage = new AdapterImage(getActivity());
        mAdapterAudio = new AdapterAudio(getActivity());
        mAdapterVideo = new AdapterVideo(getActivity());
        mAdapterText = new AdapterText(getActivity());
        mFragments[0].setListAdapter(mAdapterImage);
        mFragments[1].setListAdapter(mAdapterAudio);
        mFragments[2].setListAdapter(mAdapterVideo);
        mFragments[3].setListAdapter(mAdapterText);

        new RefreshImageList().execute();
        new RefreshAudioList().execute();
        new RefreshVideoList().execute();
        new RefreshTextList().execute();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_manager, container, false);
        tab = new TextView[4];
        tab[0] = (TextView)
                rootView.findViewById(R.id.tab_1);
        tab[1] = (TextView)
                rootView.findViewById(R.id.tab_2);
        tab[2] = (TextView)
                rootView.findViewById(R.id.tab_3);
        tab[3] = (TextView)
                rootView.findViewById(R.id.tab_4);
        vp = (ViewPager)
                rootView.findViewById(R.id.vp);
        btnShare = (Button)
                rootView.findViewById(R.id.btnShare);
        btnCancel = (Button)
                rootView.findViewById(R.id.btnCancel);
        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        vp.setAdapter(mViewPagerAdapter);

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
        btnShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                onButtonPressed(null);
                ((ShareActivity) getActivity()).jumpToFragment(ShareActivity.SHARE_FRAGMENT);
            }
        });
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mFragments = null;
        mAdapterImage = null;
        mAdapterAudio = null;
        mAdapterVideo = null;
        mAdapterText = null;
        mViewPagerAdapter = null;
    }


    private class RefreshImageList extends AsyncTask<Void, Void, SparseArray<?>> {
        SparseArray<FileImage> images;

        protected SparseArray<?> doInBackground(Void... params) {
            images = new SparseArray<FileImage>();
            Cursor curImage = getActivity().getContentResolver().query(
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                    new String[]{
                            MediaStore.Images.Media._ID,
                            MediaStore.Images.Media.DISPLAY_NAME,
                            MediaStore.Images.Media.DATA
                    },
                    null,
                    null,
                    MediaStore.Images.Media.DATE_MODIFIED + " DESC");
            if (curImage.moveToFirst()) {
                FileImage fileImage;
                int i = 0;
                do {
                    fileImage = new FileImage();
                    fileImage.id = curImage.getInt(curImage.getColumnIndex(MediaStore.Images.Media._ID));
                    fileImage.data = curImage.getString(curImage.getColumnIndex(MediaStore.Images.Media.DATA));
                    fileImage.name = curImage.getString(curImage
                            .getColumnIndex(MediaStore.Images.Media.DISPLAY_NAME));
                    this.images.put(i, fileImage);
                    i++;
                } while (curImage.moveToNext());
            }
            curImage.close();
            return images;
        }


        @Override
        protected void onPostExecute(SparseArray<?> sparseArray) {
            mAdapterImage.setData(sparseArray);
        }
    }

    private class RefreshAudioList extends AsyncTask<Void, Void, SparseArray<?>> {
        SparseArray<FileAudio> audios;

        protected SparseArray<?> doInBackground(Void... params) {
            audios = new SparseArray<FileAudio>();
            Cursor curAudio = getActivity().getContentResolver().query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, new String[]{
                    MediaStore.Audio.Media._ID, MediaStore.Audio.Media.DATA, MediaStore.Audio.Media.DISPLAY_NAME
            }, null, null, null);
            if (curAudio.moveToFirst()) {
                FileAudio fileAudio;
                int i = 0;
                do {
                    fileAudio = new FileAudio();
                    fileAudio.id = curAudio.getInt(curAudio.getColumnIndex(MediaStore.Audio.Media._ID));
                    fileAudio.data = curAudio.getString(curAudio.getColumnIndex(MediaStore.Audio.Media.DATA));
                    fileAudio.name = curAudio.getString(curAudio
                            .getColumnIndex(MediaStore.Audio.Media.DISPLAY_NAME));
                    this.audios.put(i++, fileAudio);
                } while (curAudio.moveToNext());
            }
            curAudio.close();

            return audios;
        }


        @Override
        protected void onPostExecute(SparseArray<?> sparseArray) {
//            super.onPostExecute(sparseArray);
            mAdapterAudio.setData(sparseArray);
        }
    }

    private class RefreshVideoList extends AsyncTask<Void, Void, SparseArray<?>> {
        SparseArray<FileVideo> videos;

        protected SparseArray<?> doInBackground(Void... params) {
            videos = new SparseArray<FileVideo>();
            Cursor curVideo = getActivity().getContentResolver().query(
                    MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
                    new String[]{
                            MediaStore.Video.Media._ID,
                            MediaStore.Video.Media.DATA,
                            MediaStore.Video.Media.DISPLAY_NAME
                    },
                    null,
                    null,
                    MediaStore.Video.Media.DATE_MODIFIED + " DESC");
            if (curVideo.moveToFirst()) {
                FileVideo fileVideo;
                int i = 0;
                do {
                    fileVideo = new FileVideo();
                    fileVideo.id = curVideo.getInt(curVideo.getColumnIndex(MediaStore.Video.Media._ID));
                    fileVideo.data = curVideo.getString(curVideo.getColumnIndex(MediaStore.Video.Media.DATA));
                    fileVideo.name = curVideo.getString(curVideo
                            .getColumnIndex(MediaStore.Video.Media.DISPLAY_NAME));
                    this.videos.put(i, fileVideo);
                } while (curVideo.moveToNext());
            }
            curVideo.close();
            return videos;
        }


        @Override
        protected void onPostExecute(SparseArray<?> sparseArray) {
//            super.onPostExecute(sparseArray);
            mAdapterVideo.setData(sparseArray);
        }

    }

    private class RefreshTextList extends AsyncTask<Void, Void, SparseArray<?>> {
        SparseArray<FileText> texts;

        @TargetApi(Build.VERSION_CODES.HONEYCOMB)
        protected SparseArray<?> doInBackground(Void... params) {
            texts = new SparseArray<>();
            Cursor curText = getActivity().getContentResolver().query(
                    MediaStore.Files.getContentUri("external"),
                    new String[]{
                            MediaStore.Files.FileColumns._ID,
                            MediaStore.Files.FileColumns.DATA,
                            MediaStore.Files.FileColumns.MIME_TYPE
                    },
                    MediaStore.Files.FileColumns.MIME_TYPE + " LIKE ?",
                    new String[]{"text/%"},
                    null);
            if (curText.moveToFirst()) {
                FileText fileText;
                int i = 0;
                do {
                    fileText = new FileText();
                    fileText.id = curText.getInt(curText.getColumnIndex(MediaStore.Images.Media._ID));
                    fileText.data = curText.getString(curText.getColumnIndex(MediaStore.Images.Media.DATA));
                    fileText.name = fileText.data.substring(fileText.data.lastIndexOf("/") + 1);
                    this.texts.put(i, fileText);
                    i++;
                } while (curText.moveToNext());
            }
            curText.close();
            return texts;
        }


        @Override
        protected void onPostExecute(SparseArray<?> sparseArray) {
//            super.onPostExecute(sparseArray);
            mAdapterText.setData(sparseArray);
        }

    }

}
