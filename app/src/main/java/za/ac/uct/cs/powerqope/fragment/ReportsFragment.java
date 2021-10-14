package za.ac.uct.cs.powerqope.fragment;


import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;
import za.ac.uct.cs.powerqope.adapter.TabPagerAdapter;
import za.ac.uct.cs.powerqope.R;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.tabs.TabLayout;

/**
 * A simple {@link Fragment} subclass.
 */
public class ReportsFragment extends Fragment {


    public ReportsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_reports, container, false);
        TabLayout tabLayout = v.findViewById(R.id.tab_layout);
        final ViewPager viewPager =
                 v.findViewById(R.id.pager);
        tabLayout.addTab(tabLayout.newTab().setText("Internet Speed reports").setIcon(R.drawable.ic_av_timer_black_24dp));
        tabLayout.addTab(tabLayout.newTab().setText("Web Speed Reports").setIcon(R.drawable.ic_language_black_24dp));
        tabLayout.addTab(tabLayout.newTab().setText("Video Speed Reports").setIcon(R.drawable.ic_video_library_black_24dp));


        final PagerAdapter adapter = new TabPagerAdapter
                (getFragmentManager(),
                        tabLayout.getTabCount());
        viewPager.setAdapter(adapter);

        viewPager.addOnPageChangeListener(new
                TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.setOnTabSelectedListener(new
                                                   TabLayout.OnTabSelectedListener() {
                                                       @Override
                                                       public void onTabSelected(TabLayout.Tab tab) {
                                                           viewPager.setCurrentItem(tab.getPosition());
                                                       }

                                                       @Override
                                                       public void onTabUnselected(TabLayout.Tab tab) {

                                                       }

                                                       @Override
                                                       public void onTabReselected(TabLayout.Tab tab) {

                                                       }

                                                   });





        return v;
    }

}
