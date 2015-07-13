package vision.resourcemanager;


import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.TextView;

import vis.UserFile;
import vision.fastfiletransfer.R;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link GridImageFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class GridImageFragment extends Fragment {
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;
    private GridView imageGrid;
    private AdapterGridImage mAdapterGridImage;
    private SparseArray<FileImage> mFileImage;
    private TextView tvTitle;


    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment GridImageFragment.
     */
    public static GridImageFragment newInstance(String param1, String param2) {
        GridImageFragment fragment = new GridImageFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    public GridImageFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
        mAdapterGridImage = new AdapterGridImage(getActivity(), ((ResourceManagerInterface) getActivity()).getSelectedFilesQueue());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_grid_image, container, false);
        tvTitle = (TextView) rootView.findViewById(R.id.tvGridTitle);
        imageGrid = (GridView) rootView.findViewById(R.id.imageGrid);
        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        tvTitle.setText(mParam1);
        new RefreshImageGrid<FileImage>(imageGrid, mAdapterGridImage).execute();
    }

    private class RefreshImageGrid<T> extends AsyncTask<Void, Void, SparseArray<?>> {
        SparseArray<T> images;
        private GridView mImageGrid;
        private AdapterGridImage mAdapterGridImage;

        public RefreshImageGrid(GridView mImageGrid, AdapterGridImage adapterGridImage) {
            this.mImageGrid = mImageGrid;
            this.mAdapterGridImage = adapterGridImage;
        }

        protected SparseArray<T> doInBackground(Void... params) {
            images = new SparseArray<T>();
            Cursor curImage = getActivity().getContentResolver().query(
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                    new String[]{
                            MediaStore.Images.ImageColumns._ID,
                            MediaStore.Images.ImageColumns.DATA,
                            MediaStore.Images.ImageColumns.SIZE,
                            MediaStore.Images.ImageColumns.DISPLAY_NAME,
                            MediaStore.Images.ImageColumns.DATE_MODIFIED,
                            MediaStore.Images.ImageColumns.BUCKET_DISPLAY_NAME
                    },
                    MediaStore.Images.ImageColumns.BUCKET_DISPLAY_NAME + "=?",
                    new String[]{mParam1},
                    MediaStore.Images.Media.DATE_MODIFIED + " DESC");
            if (curImage.moveToFirst()) {
                FileImage file;
                int i = 0;
                do {
                    file = new FileImage();
                    file.oid = curImage.getLong(curImage.getColumnIndex(MediaStore.Images.Media._ID));
                    file.id = i;
                    file.data = curImage.getString(curImage.getColumnIndex(MediaStore.Images.Media.DATA));
                    if (!new java.io.File(file.data).exists()) {
                        continue;
                    }
                    file.type = UserFile.TYPE_IMAGE;
                    file.size = curImage.getLong(curImage.getColumnIndex(MediaStore.Images.Media.SIZE));
                    file.strSize = UserFile.bytes2kb(file.size);
//                    file.name = curImage.getString(curImage
//                            .getColumnIndex(MediaStore.Images.Media.DISPLAY_NAME));
                    file.name = curImage.getString(curImage.getColumnIndex(MediaStore.Images.ImageColumns.BUCKET_DISPLAY_NAME));
                    file.date = curImage.getLong(curImage.getColumnIndex(MediaStore.Images.Media.DATE_MODIFIED));
                    file.strDate = UserFile.dateFormat(file.date);
                    this.images.put(i++, (T) file);
                } while (curImage.moveToNext());
            }
            curImage.close();
            mFileImage = (SparseArray<FileImage>) images;
            return images;
        }


        @Override
        protected void onPostExecute(SparseArray<?> sparseArray) {
            mAdapterGridImage.setData((SparseArray<FileImage>) sparseArray);
            mImageGrid.setAdapter(mAdapterGridImage);
        }
    }

}
