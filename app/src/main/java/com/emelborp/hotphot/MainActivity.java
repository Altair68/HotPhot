package com.emelborp.hotphot;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.emelborp.hotphot.gen.DaoMaster;
import com.emelborp.hotphot.gen.DaoSession;
import com.emelborp.hotphot.gen.Marker;
import com.emelborp.hotphot.gen.MarkerDao;
import com.emelborp.hotphot.gen.Profile;
import com.emelborp.hotphot.gen.ProfileDao;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class MainActivity extends FragmentActivity
        implements NavigationDrawerFragment.NavigationDrawerCallbacks, ProfileFragment.OnProfileFragmentInteractionListener,
        MapsFragment.OnMapsFragmentInteractionListener, ListMarkerFragment.OnListMarkerFragmentInteractionListener,
        MapsFragment.ShowDialog {

    /**
     * Fragment managing the behaviors, interactions and presentation of the navigation drawer.
     */
    private NavigationDrawerFragment mNavigationDrawerFragment;

    /**
     * Used to store the last screen title. For use in {@link #restoreActionBar()}.
     */
    private CharSequence mTitle;

    public static FragmentManager fragmentManager;

    /**
     * DAO für die Datenspeicherung.
     */
    private SQLiteDatabase db;
    private DaoMaster daoMaster;
    private DaoSession daoSession;
    private MarkerDao markerDao;
    private ProfileDao profileDao;

    private List<Marker> markerList;
    private Profile profile;

    public List<Marker> getMarkerList() {
        return markerList;
    }

    public void setMarkerList(List<Marker> markerList) {
        this.markerList = markerList;
    }

    public ProfileDao getProfileDao() {
        return profileDao;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // initialising the object of the FragmentManager. Here I'm passing getSupportFragmentManager(). You can pass getFragmentManager() if you are coding for Android 3.0 or above.
        fragmentManager = getSupportFragmentManager();

        mNavigationDrawerFragment = (NavigationDrawerFragment)
                getSupportFragmentManager().findFragmentById(R.id.navigation_drawer);
        mTitle = getTitle();

        DaoMaster.DevOpenHelper helper = new DaoMaster.DevOpenHelper(this, "hotphot-db", null);
        db = helper.getWritableDatabase();
        daoMaster = new DaoMaster(db);
        daoSession = daoMaster.newSession();
        markerDao = daoSession.getMarkerDao();
        profileDao = daoSession.getProfileDao();

        markerList = new ArrayList<Marker>();

        // Set up the drawer.
        mNavigationDrawerFragment.setUp(
                R.id.navigation_drawer,
                (DrawerLayout) findViewById(R.id.drawer_layout));
    }

    public Profile getProfile() {
        if (profile == null) {
            List<Profile> theProfileList = daoSession.getProfileDao().loadAll();
            if (theProfileList.size() == 1) {
                profile = theProfileList.get(0);
            } else if (theProfileList.size() == 0) {
                Bitmap theImage = BitmapFactory.decodeResource(getResources(), R.drawable.no_image_available);
                profile = new Profile(null, "Name", "mail@change.com", new Date(), Profile.bitmap2bytes(theImage));
                profileDao.insertOrReplace(profile);
            } else {
                //TODO Error!!!
            }
        }
        return profile;
    }

    @Override
    public void onNavigationDrawerItemSelected(int position) {
        switch (position) {
            case 0:
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.container, MapsFragment.newInstance(position + 1))
                        .commit();
                break;
            case 1:
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.container, ProfileFragment.newInstance(position + 1))
                        .commit();
                break;
            case 2:
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.container, ListMarkerFragment.newInstance(position + 1))
                        .commit();
                break;
            default:
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.container, PlaceholderFragment.newInstance(position + 1))
                        .commit();
                break;
        }
    }

    public void onSectionAttached(int number) {
        mTitle = getResources().getStringArray(R.array.sections)[number-1];
    }

    public void restoreActionBar() {
        ActionBar actionBar = getActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setTitle(mTitle);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (!mNavigationDrawerFragment.isDrawerOpen()) {
            // Only show items in the action bar relevant to this screen
            // if the drawer is not showing. Otherwise, let the drawer
            // decide what to show in the action bar.
            getMenuInflater().inflate(R.menu.main, menu);
            restoreActionBar();
            return true;
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            startActivityForResult(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS), 0);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_NUMBER = "section_number";

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static PlaceholderFragment newInstance(int sectionNumber) {
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_placeholder, container, false);
            return rootView;
        }

        @Override
        public void onAttach(Activity activity) {
            super.onAttach(activity);
            ((MainActivity) activity).onSectionAttached(
                    getArguments().getInt(ARG_SECTION_NUMBER));
        }
    }

    public void showMarkerDialog(DialogFragment aDialogFragment) {
        FragmentManager theFragMan = getSupportFragmentManager();
        aDialogFragment.show(theFragMan, "MarkerDialog");
    }

    public MarkerDao getMarkerDao() {
        if (markerDao == null) throw new NullPointerException();
        return markerDao;
    }

    @Override
    public void onProfileFragmentInteraction(Uri uri) {
        //TODO Erstmal nix...
    }

    @Override
    public void onMapsFragmentInteraction(Uri uri) {
        //TODO Erstmal nix...
    }

    @Override
    public void onMarkerListFragmentInteraction(Uri uri) {
        //TODO Erstmal nix...
    }
}
