package vision.fastfiletransfer;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
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

    private UserFilesAdapter mUserFilesAdapter;

    private TextView tvTips;
    private TextView tvName;
    private ListView lvFiles;
    private FilesList<UserFile> mFilesList;
    private ImageView ivArrow;

    public static ReceiveFragment newInstance() {
        ReceiveFragment fragment = new ReceiveFragment();
        return fragment;
    }

    public ReceiveFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_receive, container, false);

        tvTips = (TextView) rootView.findViewById(R.id.tvTips);
        tvName = (TextView) rootView.findViewById(R.id.tvName);
        lvFiles = (ListView) rootView.findViewById(R.id.lvFiles);
        ivArrow = (ImageView) rootView.findViewById(R.id.arrow);
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
            public void onAdded(int size) {
                lvFiles.setVisibility(View.VISIBLE);
                ivArrow.setVisibility(View.GONE);
            }

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
