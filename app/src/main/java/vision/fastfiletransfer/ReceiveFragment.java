package vision.fastfiletransfer;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import vis.FilesList;
import vis.UserFile;
import vis.UserFilesAdapter;
import vis.net.protocol.ReceiveServer;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ReceiveFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ReceiveFragment extends Fragment {
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;

    private UserFilesAdapter mUserFilesAdapter;

    private TextView tvTips;
    private TextView tvName;
    private ListView lvFiles;
    private FilesList<UserFile> mFilesList;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ReceiveFragment.
     */
    public static ReceiveFragment newInstance(String param1, String param2) {
        ReceiveFragment fragment = new ReceiveFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    public ReceiveFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_receive, container, false);

        tvTips = (TextView) rootView.findViewById(R.id.tvTips);
        tvName = (TextView) rootView.findViewById(R.id.tvName);
        lvFiles = (ListView) rootView.findViewById(R.id.lvFiles);

        return rootView;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        tvName.setText(new String(ReceiveServer.LOCALNAME));

        mFilesList = ((ReceiveActivity) getActivity()).mFilesList;
        mUserFilesAdapter = new UserFilesAdapter(getActivity(), mFilesList);
        lvFiles.setAdapter(mUserFilesAdapter);

        mFilesList.setOnDataChangedListener(new FilesList.OnDataChangedListener() {
            @Override
            public void onDataChanged() {
                mUserFilesAdapter.notifyDataSetChanged();
            }
        });

    }
    /**
     * 通知界面更新
     *
     * @param text 更新信息
     */
    public void setTips(CharSequence text) {
        if (tvTips != null) {
            tvTips.setText(text);
        }
    }


}
