package com.antoine.startupproject.MyPlacesActivity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.antoine.startupproject.R;

/**
 * Created by Antoine on 01/12/2015.
 */
public class FriendsPLaceActivity extends ActionBarActivity {


    private Toolbar toolbar;
    private ViewPager mPager;
    private SlidingTabLayout mTabs;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friends_places);

        toolbar = (Toolbar) findViewById(R.id.app_bar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayShowHomeEnabled(true);

        mPager = (ViewPager) findViewById(R.id.pager);
        mPager.setAdapter(new MyPagerAdapter(getSupportFragmentManager()));
        mTabs = (SlidingTabLayout) findViewById(R.id.tabs);
        mTabs.setViewPager(mPager);

    }

    class MyPagerAdapter extends FragmentPagerAdapter{

        String[] tabs;
        int [] icons = {R.drawable.ic_perm_contact_calendar_black_24dp,R.drawable.ic_person_add_black_24dp};

        public MyPagerAdapter(FragmentManager fm) {
            super(fm);

            tabs = getResources().getStringArray(R.array.tabs);

        }

        @Override
        public Fragment getItem(int position) {

            MyFragment fragment = MyFragment.getInstance(position);

            return fragment;
        }


        @Override
        public CharSequence getPageTitle(int position) {
            return tabs[position];
        }

        @Override
        public int getCount() {
            return 2;
        }
    }

    public static class MyFragment extends Fragment{


        private TextView textView;

        public static MyFragment getInstance(int position){

            MyFragment fragment = new MyFragment();

            Bundle args = new Bundle();
            args.putInt("position", position);
            fragment.setArguments(args);

            return fragment;
        }

        @Nullable
        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

            View layout = inflater.inflate(R.layout.fragment_my, container, false);

            textView = (TextView) layout.findViewById(R.id.position);
            Bundle bundle = getArguments();

            if (bundle != null){

                textView.setText("Page selectionée: " + bundle.getInt("position"));

            }

            return layout;
        }
    }


}












