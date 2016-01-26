package com.antoine.startupproject.MapsActivityClass;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.antoine.startupproject.R;
import com.antoine.startupproject.SavedToPreferences;

import de.hdodenhof.circleimageview.CircleImageView;


/**
 * Created by Antoine on 29/11/2015.
 */
public class NavigationDrawerFragment extends Fragment {


    public static final String PREF_FILE_NAME = "testpref";
    public static final  String KEY_USER_LEARNED_DRAWER = "user_learned_drawer";
    private ActionBarDrawerToggle mDrawerToggle;
    private DrawerLayout mDrawerLayout;
    private boolean mUserLearnedDrawer;
    private boolean mFromSavedInstanceState;
    private View containerView;
    private SavedToPreferences savedToPreferences;
    private CircleImageView profile_image;

    public NavigationDrawerFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        savedToPreferences = new SavedToPreferences(getActivity());

        mUserLearnedDrawer =  savedToPreferences.isDrawerWasSee();


        if (savedInstanceState != null){

            mFromSavedInstanceState = true;
        }


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View layout = inflater.inflate(R.layout.fragment_navigation_drawer, container, false);



        return layout;
    }




    public void setUp(int fragmentId, DrawerLayout drawerLayout, Toolbar toolbar){

        containerView = getActivity().findViewById(fragmentId);
        mDrawerLayout = drawerLayout;
        mDrawerToggle = new ActionBarDrawerToggle(getActivity(), drawerLayout, toolbar, R.string.drawer_open, R.string.drawer_close){


            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);

                if (!mUserLearnedDrawer){

                    mUserLearnedDrawer = true;

                    savedToPreferences.storeDrawerAsSaw(true);

                }

                getActivity().invalidateOptionsMenu();


            }


            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {
                super.onDrawerSlide(drawerView, slideOffset);


                ((MapsActivity) getActivity()).onDrawerSlide(slideOffset);

            }

            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);

                getActivity().invalidateOptionsMenu();
            }
        };



        mDrawerLayout.setDrawerListener(mDrawerToggle);


        mDrawerLayout.post(new Runnable() {
            @Override
            public void run() {

                mDrawerToggle.syncState();


                if (!mUserLearnedDrawer && !mFromSavedInstanceState){


                    mDrawerLayout.openDrawer(containerView);

                }
            }
        });


    }

}
