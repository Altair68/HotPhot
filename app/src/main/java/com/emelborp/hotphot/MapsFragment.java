package com.emelborp.hotphot;

import android.app.Activity;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.Iterator;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link MapsFragment.OnMapsFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link MapsFragment#newInstance} factory method to
 * create an instance of this fragment.
 *
 */
public class MapsFragment extends Fragment implements AddMarkerDialog.NoticeDialogListener {

    public interface ShowDialog {
        public void showMarkerDialog(DialogFragment aDialogFragment);
    }

    /**
     * The fragment argument representing the section number for this
     * fragment.
     */
    private static final String ARG_SECTION_NUMBER = "section_number";

    private int mParam1;

    private static View view;

    private GoogleMap mMap;
    private MapView mapView;
    private Marker newMarker;
    private CameraPosition savedCameraPosition;

    private OnMapsFragmentInteractionListener mListener;
    private ShowDialog mShowDialogActivity;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment MapsFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static MapsFragment newInstance(int sectionNumber) {
        MapsFragment fragment = new MapsFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        fragment.setArguments(args);
        return fragment;
    }
    public MapsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MapsInitializer.initialize(getActivity());
        if (getArguments() != null) {
            mParam1 = getArguments().getInt(ARG_SECTION_NUMBER);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_maps, container, false);

        mapView = (MapView) view.findViewById(R.id.map);

        mapView.onCreate(savedInstanceState);

        mMap = mapView.getMap();
        mMap.getUiSettings().setMyLocationButtonEnabled(true);
        mMap.setMyLocationEnabled(true);
        mMap.getUiSettings().setZoomControlsEnabled(true);
        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);

        final boolean[] isInitiallyCentered = new boolean[] {false};
        mMap.setOnMyLocationChangeListener(new GoogleMap.OnMyLocationChangeListener() {
            @Override
            public void onMyLocationChange(Location aLocation) {
                if (!isInitiallyCentered[0]) {
                    LatLng theLatLng = null;
                    if (aLocation != null) {
                        theLatLng = new LatLng(aLocation.getLatitude(),
                                aLocation.getLongitude());
                    }
                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(theLatLng,
                            14.5f));
                    isInitiallyCentered[0] = true;
                }

            }
        });

        mMap.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
            @Override
            public void onMapLongClick(LatLng latLng) {
                showDialog();
                if (addMarker(latLng)) {
                    Log.d("Map", "Gut :)");
                } else {
                    Log.d("Map", "Schlecht :(");
                }
            }
        });

        loadSavedMarkers();

        return view;
    }

    public void loadSavedMarkers() {
        MainActivity theActivity = (MainActivity) getActivity();

        List<com.emelborp.hotphot.gen.Marker> theMarkerList = theActivity.getMarkerList();
        theMarkerList = theActivity.getMarkerDao().loadAll();

        for (Iterator<com.emelborp.hotphot.gen.Marker> theIterator = theMarkerList.iterator(); theIterator.hasNext(); ) {
            com.emelborp.hotphot.gen.Marker theNextElement = theIterator.next();

            mMap.addMarker(convertToMarker(theNextElement));
        }
        theActivity.setMarkerList(theMarkerList);
    }

    public void showDialog() {
        AddMarkerDialog dialog = new AddMarkerDialog();
        dialog.setTargetFragment(this, 1); //request code
        mShowDialogActivity.showMarkerDialog(dialog);
    }

    @Override
     public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnMapsFragmentInteractionListener) activity;
            ((MainActivity) activity).onSectionAttached(
                    getArguments().getInt(ARG_SECTION_NUMBER));
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }
        try {
            mShowDialogActivity = (ShowDialog) activity;
        } catch (ClassCastException e) {
            Log.e(this.getClass().getSimpleName(), "NoticeDialogListener interface needs to be implemented by Activity.", e);
            throw e;
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onResume()
    {
        mapView.onResume();

        super.onResume();
    }
    @Override
    public void onDestroy()
    {
        super.onDestroy();

        savedCameraPosition = mMap.getCameraPosition();
        mapView.onDestroy();
    }
    @Override
    public void onLowMemory()
    {
        super.onLowMemory();

        mapView.onLowMemory();
    }

    boolean addMarker(LatLng aPosition) {
        if (mMap != null) {
            MarkerOptions theNewMarker = new MarkerOptions();
            theNewMarker
                    .position(aPosition)
                    .title("TempMarker");

            Marker theMarker = mMap.addMarker(theNewMarker);
            newMarker = theMarker;
            return true;
        }
        return false;
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
    public interface OnMapsFragmentInteractionListener {
        public void onMapsFragmentInteraction(Uri uri);
    }

    @Override
    public void onDialogPositiveClick(DialogFragment dialog) {
        AddMarkerDialog theDialog = (AddMarkerDialog) dialog;
        newMarker.setTitle(theDialog.getName());
        newMarker.setSnippet(theDialog.getCategory());
        com.emelborp.hotphot.gen.Marker theDaoMarker = convertToDaoMarker(newMarker);
        ((MainActivity) getActivity()).getMarkerDao().insert(theDaoMarker);
        ((MainActivity) getActivity()).getMarkerList().add(theDaoMarker);
        newMarker = null;
    }

    private com.emelborp.hotphot.gen.Marker convertToDaoMarker(Marker aMarker) {
        return new com.emelborp.hotphot.gen.Marker(null, aMarker.getTitle(), aMarker.getPosition().latitude, aMarker.getPosition().longitude, aMarker.getSnippet());

    }

    private MarkerOptions convertToMarker(com.emelborp.hotphot.gen.Marker aMarker) {
        MarkerOptions theMarkerOptions = new MarkerOptions();
        theMarkerOptions
                .title(aMarker.getName())
                .position(new LatLng(aMarker.getLat(), aMarker.getLon()))
                .snippet(aMarker.getCat());
        return theMarkerOptions;

    }

    @Override
    public void onDialogNegativeClick(DialogFragment dialog) {
        newMarker.remove();
        newMarker = null;
    }
}
