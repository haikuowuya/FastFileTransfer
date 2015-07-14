package vision.resourcemanager;


import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import vis.SelectedFilesQueue;
import vision.fastfiletransfer.R;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link GridImageFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class GridImageFragment extends Fragment {
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
//    private static final String ARG_PARAM1 = "param1";
    //    private static final String ARG_PARAM2 = "param2";
    private static int INDEXOFFOLDER;
    private SelectedFilesQueue<vision.resourcemanager.File> mSelectedList;

    private String mParam1;
    //    private String mParam2;
    private GridView imageGrid;
    private AdapterGridImage mAdapterGridImage;
    private SparseArray<FileImage> mFileImage;
    private TextView tvTitle;
    private TextView tvSelectAll;

    private ResourceManagerInterface mListener;
    private LinearLayout btnLinearLayout;
    private Button btnLeft;
    private Button btnRight;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     * <p/>
     * //     * @param param1 Parameter 1.
     * //     * @param param2 Parameter 2.
     *
     * @return A new instance of fragment GridImageFragment.
     */
    public static GridImageFragment newInstance(int indexOfFolder, String param2) {
        GridImageFragment fragment = new GridImageFragment();
        INDEXOFFOLDER = indexOfFolder;
//        Log.d("", String.valueOf(indexOfFolder));
//        Bundle args = new Bundle(); //ArrayMap<String, Object> mMap = null;
//        args.putString(ARG_PARAM1, null);
////        args.putString(ARG_PARAM2, param2);
//        fragment.setArguments(args);
        return fragment;
    }

    public GridImageFragment() {
        // Required empty public constructor
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (ResourceManagerInterface) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement ResourceManagerInterface");
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        if (getArguments() != null) {
//            mParam1 = getArguments().getString(ARG_PARAM1);
////            mParam2 = getArguments().getString(ARG_PARAM2);
//        }
        mSelectedList = mListener.getSelectedFilesQueue();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_grid_image, container, false);
        tvTitle = (TextView) rootView.findViewById(R.id.tvGridTitle);
        tvSelectAll = (TextView) rootView.findViewById(R.id.tvSelectAll);
        imageGrid = (GridView) rootView.findViewById(R.id.imageGrid);
        View botBut = inflater.inflate(R.layout.bottom_rm_buttom, container, false);
        btnLinearLayout = (LinearLayout)
                botBut.findViewById(R.id.btnLinearLayout);
//        btnLinearLayout.setVisibility(View.VISIBLE);
        btnLeft = (Button)
                botBut.findViewById(R.id.btnLeft);
        btnRight = (Button)
                botBut.findViewById(R.id.btnRight);
        RelativeLayout relativeLayout = (RelativeLayout)
                rootView.findViewById(R.id.fragment_grid_image);
        relativeLayout.addView(botBut);
        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        final FileFolder fileFolder = mListener.getImageFolder().valueAt(INDEXOFFOLDER);
        SparseArray<FileImage> fileImage = fileFolder.mImages;
        tvTitle.setText(fileFolder.name);
        mAdapterGridImage = new AdapterGridImage(getActivity(), fileFolder, mListener.getSelectedFilesQueue());
        mAdapterGridImage.setData(fileImage);
        imageGrid.setAdapter(mAdapterGridImage);

        tvSelectAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (fileFolder.isSelected) {
                    fileFolder.cancelAll(mListener.getSelectedFilesQueue());
                } else {
                    fileFolder.selectAll(mListener.getSelectedFilesQueue());
                }
                mAdapterGridImage.notifyDataSetChanged();
            }
        });
    }

}
