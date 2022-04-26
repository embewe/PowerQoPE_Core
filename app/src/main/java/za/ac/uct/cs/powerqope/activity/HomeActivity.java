package za.ac.uct.cs.powerqope.activity;


import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.PowerManager;
import android.view.MenuItem;
import com.google.android.material.navigation.NavigationView;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import za.ac.uct.cs.powerqope.R;
import za.ac.uct.cs.powerqope.fragment.GraphFragment;
import za.ac.uct.cs.powerqope.fragment.MonthFragment;
import za.ac.uct.cs.powerqope.service.DataService;
import za.ac.uct.cs.powerqope.utils.StoredData;

import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;


public class HomeActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    boolean doubleBackToExitPressedOnce = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
      if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
        String packageName = getPackageName();
        PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
        if (!pm.isIgnoringBatteryOptimizations(packageName)) {
          Intent intent = new Intent();
          intent.setAction(android.provider.Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS);
          intent.setFlags(FLAG_ACTIVITY_NEW_TASK);
          intent.setData(Uri.parse("package:" + packageName));
          startActivity(intent);
        }
      }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        if(!StoredData.isSetData) {
            StoredData.setZero();
        }
        if (!DataService.service_status) {
            Intent intent = new Intent(this, DataService.class);
            startService(intent);
        }
        Intent intentBC = new Intent();
        intentBC.setAction("com.tofabd.internetmeter");
        sendBroadcast(intentBC);
        Fragment fragment = new MonthFragment();
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.content_frame, fragment);
        ft.commit();

    }


    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();
        Fragment fragment = null;
        if (id == R.id.month) {
            fragment = new MonthFragment();
        }

        else if (id == R.id.graph) {
            fragment = new GraphFragment();
        }
        else if (id == R.id.speedtest) {
            startActivity(new Intent(HomeActivity.this, SpeedTestActivity.class));

        }
        else if (id == R.id.speedtestresult) {
            try {
                startActivity(new Intent(HomeActivity.this, SpeedTestResult.class));
            }catch (Exception e){

            }

        }
        else if (id == R.id.settings) {
            startActivity(new Intent(HomeActivity.this, SettingsActivity.class));

        }


        else if (id == R.id.rate) {
            Uri uri = Uri.parse("market://details?id=" + getPackageName());
            Intent myAppLinkToMarket = new Intent(Intent.ACTION_VIEW, uri);
            try {
                startActivity(myAppLinkToMarket);
            } catch (ActivityNotFoundException e) {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://play.google.com/store/apps/details?id=" + getPackageName())));
            }
        }

        else if (id == R.id.more) {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/developer?id=Nisha+Amit+Nimade")));

        }else if (id == R.id.nav_share) {
            Intent sendIntent = new Intent();
            sendIntent.setAction(Intent.ACTION_SEND);
            sendIntent.putExtra(Intent.EXTRA_TEXT,
                    "Hey check out my app at: https://play.google.com/store/apps/details?id="+getPackageName());
            sendIntent.setType("text/plain");
            startActivity(sendIntent);
            return true;
        }

        if (fragment != null) {
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.content_frame, fragment);
            ft.commit();

        }
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }



}
