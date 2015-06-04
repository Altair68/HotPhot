package com.emelborp.hotphot;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import com.emelborp.hotphot.gen.Profile;
import com.google.android.gms.maps.model.Circle;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Date;

import de.hdodenhof.circleimageview.CircleImageView;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link ProfileFragment.OnProfileFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link ProfileFragment#newInstance} factory method to
 * create an instance of this fragment.
 *
 */
public class ProfileFragment extends Fragment {
    /**
     * The fragment argument representing the section number for this
     * fragment.
     */
    private static final String ARG_SECTION_NUMBER = "section_number";
    private static final int SELECT_PHOTO = 100;

    // TODO: Rename and change types of parameters
    private int mParam1;

    private boolean editable;
    private View rootView;
    private SimpleDateFormat sdf;

    private OnProfileFragmentInteractionListener mListener;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment ProfileFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ProfileFragment newInstance(int sectionNumber) {
        ProfileFragment fragment = new ProfileFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        fragment.setArguments(args);
        return fragment;
    }
    public ProfileFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getInt(ARG_SECTION_NUMBER);
        }
        editable = false;
        setHasOptionsMenu(true);
    }

    public Profile getProfile() {
        return ((MainActivity) getActivity()).getProfile();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View theView = inflater.inflate(R.layout.fragment_profile, container, false);
        fillPage(theView);

        rootView = theView;
        return theView;
    }

    private void fillPage(View theView) {
        EditText theNameText = (EditText) theView.findViewById(R.id.nameText);
        EditText theMailText = (EditText) theView.findViewById(R.id.mailText);
        EditText theDateText = (EditText) theView.findViewById(R.id.dateText);

        theNameText.setText(getProfile().getName());
        theMailText.setText(getProfile().getEmail());

        sdf = new SimpleDateFormat("dd.MM.yyyy");
        theDateText.setText(sdf.format(getProfile().getBirthdate()));

        CircleImageView theImage = (CircleImageView) theView.findViewById(R.id.circleView);
        theImage.setImageBitmap(Profile.bytes2Bitmap(getProfile().getPicture()));

        theImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
                photoPickerIntent.setType("image/*");
                startActivityForResult(photoPickerIntent, SELECT_PHOTO);
            }
        });
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onProfileFragmentInteraction(uri);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch(requestCode) {
            case SELECT_PHOTO:
                if(resultCode == Activity.RESULT_OK){
                    Uri selectedImage = data.getData();
                    InputStream imageStream = null;
                    try {
                        imageStream = getActivity().getContentResolver().openInputStream(selectedImage);
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                    Bitmap yourSelectedImage = null;
                    try {
                        yourSelectedImage = decodeUri(selectedImage);
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }

                    CircleImageView theCIView = (CircleImageView) rootView.findViewById(R.id.circleView);
                    theCIView.setImageBitmap(yourSelectedImage);

                    getProfile().setPicture(Profile.bitmap2bytes(yourSelectedImage));
                    ((MainActivity) getActivity()).getProfileDao().insertOrReplace(getProfile());
                }
        }
    }

    private Bitmap decodeUri(Uri selectedImage) throws FileNotFoundException {

        // Decode image size
        BitmapFactory.Options o = new BitmapFactory.Options();
        o.inJustDecodeBounds = true;
        BitmapFactory.decodeStream(getActivity().getContentResolver().openInputStream(selectedImage), null, o);

        // The new size we want to scale to
        final int REQUIRED_SIZE = 140;

        // Find the correct scale value. It should be the power of 2.
        int width_tmp = o.outWidth, height_tmp = o.outHeight;
        int scale = 1;
        while (true) {
            if (width_tmp / 2 < REQUIRED_SIZE
                    || height_tmp / 2 < REQUIRED_SIZE) {
                break;
            }
            width_tmp /= 2;
            height_tmp /= 2;
            scale *= 2;
        }

        // Decode with inSampleSize
        BitmapFactory.Options o2 = new BitmapFactory.Options();
        o2.inSampleSize = scale;
        return BitmapFactory.decodeStream(getActivity().getContentResolver().openInputStream(selectedImage), null, o2);

    }

    @Override
    public void onPause() {
        super.onPause();
        updateProfile();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.add(200, R.id.action_editable, Menu.NONE, R.string.action_editable);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();
        if (id == R.id.action_editable) {
            setEnablement();
        }
        return super.onOptionsItemSelected(item);
    }

    private void updateProfile() {
        EditText theNameText = (EditText) rootView.findViewById(R.id.nameText);
        EditText theMailText = (EditText) rootView.findViewById(R.id.mailText);
        EditText theDateText = (EditText) rootView.findViewById(R.id.dateText);

        String theName = theNameText.getText().toString();
        String theMail = theMailText.getText().toString();
        Date theDate;
        try {
            theDate = sdf.parse(theDateText.getText().toString());
        } catch (ParseException e) {
            e.printStackTrace();
            return;
        }
        getProfile().setName(theName);
        getProfile().setEmail(theMail);
        getProfile().setBirthdate(theDate);

        ((MainActivity) getActivity()).getProfileDao().insertOrReplace(getProfile());
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnProfileFragmentInteractionListener) activity;
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
    public interface OnProfileFragmentInteractionListener {
        // TODO: Update argument type and name
        public void onProfileFragmentInteraction(Uri uri);
    }

    private void setEnablement() {
        EditText theNameText = (EditText) rootView.findViewById(R.id.nameText);
        EditText theMailText = (EditText) rootView.findViewById(R.id.mailText);
        EditText theDateText = (EditText) rootView.findViewById(R.id.dateText);

        editable = !editable;

        theNameText.setEnabled(editable);
        theMailText.setEnabled(editable);
        theDateText.setEnabled(editable);

        if (!editable) {
            updateProfile();
        }
    }

}
