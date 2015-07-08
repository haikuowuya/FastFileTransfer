package vision.resourcemanager;

import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.widget.ListAdapter;

/**
 * Created by Vision on 15/7/1.<br>
 * Email:Vision.lsm.2012@gmail.com
 */
public class FragmentText extends ListFragment {
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;

//    private OnFragmentInteractionListener mListener;
//    private ListView lvText;
//    private AdapterText mAdapterText;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param adapter Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment FragmentImage.
     */
    public static FragmentText newInstance(ListAdapter adapter, String param2) {
        FragmentText fragment = new FragmentText();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, String.valueOf(adapter));
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    public FragmentText() {
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
    }

//    @Override
//    public View onCreateView(LayoutInflater inflater, ViewGroup container,
//                             Bundle savedInstanceState) {
//        // Inflate the layout for this fragment
//        View rootView = inflater.inflate(R.layout.fragment_discarded_text, container, false);
//        lvText = (ListView)
//                rootView.findViewById(R.id.lvText);
//        return rootView;
//    }

//    @Override
//    public void onActivityCreated(Bundle savedInstanceState) {
//        super.onActivityCreated(savedInstanceState);
//        mAdapterText = new AdapterText(getActivity());
//        lvText.setAdapter(mAdapterText);
//        new RefreshList().execute();
//    }

//    public void onButtonPressed(Uri uri) {
//        if (mListener != null) {
//            mListener.onFragmentInteraction(uri);
//        }
//    }

//    @Override
//    public void onDetach() {
//        super.onDetach();
//        mListener = null;
//    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
//    public interface OnFragmentInteractionListener {
//        public void onFragmentInteraction(Uri uri);
//    }


}