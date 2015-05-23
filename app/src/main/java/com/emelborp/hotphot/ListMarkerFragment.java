package com.emelborp.hotphot;

import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.emelborp.hotphot.gen.Marker;

import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link ListMarkerFragment.OnListMarkerFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link ListMarkerFragment#newInstance} factory method to
 * create an instance of this fragment.
 *
 */
public class ListMarkerFragment extends Fragment {

    class MarkerListAdapter extends BaseAdapter {

        Context context;
        List<Marker> data;
        private LayoutInflater inflater = null;

        public MarkerListAdapter(Context context, List<Marker> data) {
            this.context = context;
            this.data = data;
            inflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        @Override
        public int getCount() {
            return data.size();
        }

        @Override
        public Object getItem(int position) {
            return data.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View vi = convertView;
            if (vi == null)
                vi = inflater.inflate(R.layout.listitem, null);
            TextView theHeader = (TextView) vi.findViewById(R.id.listItemHeader);
            theHeader.setText(data.get(position).getName());
            TextView theLatContent = (TextView) vi.findViewById(R.id.listItemLat);
            theLatContent.setText("Latitude: " + data.get(position).getLat().toString());
            TextView theLonContent = (TextView) vi.findViewById(R.id.listItemLon);
            theLonContent.setText("Longitude: " + data.get(position).getLon().toString());
            return vi;
        }
    }

    /**
     * The fragment argument representing the section number for this
     * fragment.
     */
    private static final String ARG_SECTION_NUMBER = "section_number";

    // TODO: Rename and change types of parameters
    private int mParam1;
    private String mParam2;

    private OnListMarkerFragmentInteractionListener mListener;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment ProfileFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ListMarkerFragment newInstance(int sectionNumber) {
        ListMarkerFragment fragment = new ListMarkerFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        fragment.setArguments(args);
        return fragment;
    }
    public ListMarkerFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getInt(ARG_SECTION_NUMBER);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View theView = inflater.inflate(R.layout.fragment_list_markers, container, false);

        ListView theListView = (ListView) theView.findViewById(R.id.markerListView);

        MainActivity theActivity = (MainActivity) getActivity();

        List<Marker> theMarkerList = theActivity.getMarkerList();

        //Wenn Liste leer...
        if (theMarkerList.size() == 0) {
            theListView.setAdapter(new ArrayAdapter<String>(
                    getActivity(),
                    android.R.layout.simple_list_item_1,
                    android.R.id.text1,
                    new String[] {theActivity.getString(R.string.no_entries)}));

            return theView;
        }


        theListView.setAdapter(new MarkerListAdapter(
                getActivity(),
                theMarkerList));

        return theView;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onMarkerListFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnListMarkerFragmentInteractionListener) activity;
            ((MainActivity) activity).onSectionAttached(
                    getArguments().getInt(ARG_SECTION_NUMBER));
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnListMarkerFragmentInteractionListener {
        // TODO: Update argument type and name
        public void onMarkerListFragmentInteraction(Uri uri);
    }

}
