package za.ac.uct.cs.powerqope.adapter;


import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import za.ac.uct.cs.powerqope.fragment.InternetSpeedReportsFragent;
import za.ac.uct.cs.powerqope.fragment.VideoSpeedReportsFragment;
import za.ac.uct.cs.powerqope.fragment.WebSpeedReportsFragment;

public class TabPagerAdapter extends FragmentPagerAdapter {

    int tabCount;

    public TabPagerAdapter(FragmentManager fm, int numberOfTabs) {
        super(fm);
        this.tabCount = numberOfTabs;
    }

    @Override
    public Fragment getItem(int position) {

        switch (position) {
            case 0:
                InternetSpeedReportsFragent tab1 = new InternetSpeedReportsFragent();
                return tab1;
            case 1:
                WebSpeedReportsFragment tab2 = new WebSpeedReportsFragment();
                return tab2;
            case 2:
                VideoSpeedReportsFragment tab3 = new VideoSpeedReportsFragment();
                return tab3;

            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return tabCount;
    }
}