package za.ac.uct.cs.powerqope;

import android.content.Intent;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import androidx.annotation.ColorInt;
import androidx.annotation.ColorRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import za.ac.uct.cs.powerqope.AdvancedActivity;
import za.ac.uct.cs.powerqope.fragment.HTTPTestFragment;
import za.ac.uct.cs.powerqope.fragment.HomeFragment;
import za.ac.uct.cs.powerqope.fragment.ReportsFragment;
import za.ac.uct.cs.powerqope.fragment.SettingsFragment;
import za.ac.uct.cs.powerqope.fragment.SpeedCheckerFragment;
import za.ac.uct.cs.powerqope.fragment.VideoTestFragment;
import za.ac.uct.cs.powerqope.menu.DrawerAdapter;
import za.ac.uct.cs.powerqope.menu.DrawerItem;
import za.ac.uct.cs.powerqope.menu.SimpleItem;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.yarolegovich.slidingrootnav.SlidingRootNav;
import com.yarolegovich.slidingrootnav.SlidingRootNavBuilder;

import java.util.Arrays;



public class MainActivity extends AppCompatActivity implements DrawerAdapter.OnItemSelectedListener {

    private static final int POS_HOME = 0;
    private static final int POS_PRIVATE_DNS_OPTIONS = 1;
    private static final int POS_SPEED_CHECKER = 2;
    private static final int POS_SETTINGS = 3;
    private static final int POS_HELP = 4;
    private static final int POS_EXIT = 5;

    private String[] screenTitles;
    private Drawable[] screenIcons;

    private SlidingRootNav slidingRootNav;
    BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        bottomNavigationView = findViewById(R.id.bottomNavigationView);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        bottomNavigationView.setOnNavigationItemSelectedListener(new
                                                                         BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()){
                    case R.id.internet_speed:
                        Fragment selectedScreen = new SpeedCheckerFragment();
                        showFragment(selectedScreen);
                        return true;
                    case R.id.http_test:
                        selectedScreen = new HTTPTestFragment();
                        showFragment(selectedScreen);
                        return true;
                    case R.id.video_test  :
                        selectedScreen = new VideoTestFragment();
                        showFragment(selectedScreen);
                        return true;
                    case R.id.report  :
                        selectedScreen = new ReportsFragment();
                        showFragment(selectedScreen);
                        return true;
                }

                return true;
            }
        });
        slidingRootNav = new SlidingRootNavBuilder(this)
                .withToolbarMenuToggle(toolbar)
                .withMenuOpened(false)
                .withContentClickableWhenMenuOpened(false)
                .withSavedState(savedInstanceState)
                .withMenuLayout(R.layout.menu_left_drawer)
                .inject();

        screenIcons = loadScreenIcons();
        screenTitles = loadScreenTitles();

        final DrawerAdapter adapter = new DrawerAdapter(Arrays.asList(
                createItemFor(POS_HOME).setChecked(true),
                createItemFor(POS_PRIVATE_DNS_OPTIONS),
                createItemFor(POS_SPEED_CHECKER),
                createItemFor(POS_SETTINGS),
                createItemFor(POS_HELP),
                createItemFor(POS_EXIT)));
        adapter.setListener(this);

        final RecyclerView list = findViewById(R.id.list);
        list.setNestedScrollingEnabled(false);
        list.setLayoutManager(new LinearLayoutManager(this));
        list.setAdapter(adapter);

        adapter.setSelected(POS_HOME);
       /* button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                list.setAdapter(adapter);
                adapter.setSelected(POS_SPEED_CHECKER);

                bottomNavigationView.setVisibility(View.VISIBLE);
                Fragment selectedScreen = new SpeedCheckerFragment();
            }
        });*/
    }
    @Override
    public void onItemSelected(int position) {
        if (position == POS_EXIT) {
            finish();
        }
        if (position == POS_HOME) {
            slidingRootNav.closeMenu();
            Fragment selectedScreen = new HomeFragment();
            showFragment(selectedScreen);
        }
        if (position == POS_PRIVATE_DNS_OPTIONS) {
            slidingRootNav.closeMenu();
            startActivity(new Intent(getApplicationContext(), AdvancedActivity.class));

        }
        if (position == POS_SETTINGS) {
            slidingRootNav.closeMenu();
            Fragment selectedScreen = new SettingsFragment();
            showFragment(selectedScreen);
        }
        if (position == POS_SPEED_CHECKER) {
            bottomNavigationView.setVisibility(View.VISIBLE);
            slidingRootNav.closeMenu();
            Fragment selectedScreen = new SpeedCheckerFragment();
            showFragment(selectedScreen);
        }

        if (position !=POS_SPEED_CHECKER ){
            bottomNavigationView.setVisibility(View.GONE);
        }

    }

    private void showFragment(Fragment fragment) {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.container, fragment)
                .commit();
    }

    @SuppressWarnings("rawtypes")
    private DrawerItem createItemFor(int position) {
        return new SimpleItem(screenIcons[position], screenTitles[position])
                .withIconTint(color(R.color.textColorSecondary))
                .withTextTint(color(R.color.textColorSecondary))
                .withSelectedIconTint(color(R.color.colorAccent))
                .withSelectedTextTint(color(R.color.colorAccent));
    }

    private String[] loadScreenTitles() {
        return getResources().getStringArray(R.array.ld_activityScreenTitles);
    }

    private Drawable[] loadScreenIcons() {
        TypedArray ta = getResources().obtainTypedArray(R.array.ld_activityScreenIcons);
        Drawable[] icons = new Drawable[ta.length()];
        for (int i = 0; i < ta.length(); i++) {
            int id = ta.getResourceId(i, 0);
            if (id != 0) {
                icons[i] = ContextCompat.getDrawable(this, id);
            }
        }
        ta.recycle();
        return icons;
    }

    @ColorInt
    private int color(@ColorRes int res) {
        return ContextCompat.getColor(this, res);
    }
}

