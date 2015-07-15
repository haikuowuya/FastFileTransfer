package vision.resourcemanager;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;

import vision.fastfiletransfer.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class RMGridFragmentApp extends Fragment {


    private GridView appGrid;
    private AdapterList mAdapterList;

    public RMGridFragmentApp() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_grid_app, container, false);
        appGrid = (GridView) rootView.findViewById(R.id.appGrid);
        return rootView;
    }


    public void setGridAdapter(AdapterList adapterList) {
        mAdapterList = adapterList;
        appGrid.setAdapter(mAdapterList);
    }
}
