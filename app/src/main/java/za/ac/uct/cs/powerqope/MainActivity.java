package za.ac.uct.cs.powerqope;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.net.VpnService;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.ColorInt;
import androidx.annotation.ColorRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import za.ac.uct.cs.powerqope.AdvancedActivity;
import za.ac.uct.cs.powerqope.dns.ConfigurationAccess;
import za.ac.uct.cs.powerqope.dns.DNSFilterService;
import za.ac.uct.cs.powerqope.fragment.HTTPTestFragment;
import za.ac.uct.cs.powerqope.fragment.HomeFragment;
import za.ac.uct.cs.powerqope.fragment.ReportsFragment;
import za.ac.uct.cs.powerqope.fragment.SettingsFragment;
import za.ac.uct.cs.powerqope.fragment.SpeedCheckerFragment;
import za.ac.uct.cs.powerqope.fragment.VideoTestFragment;
import za.ac.uct.cs.powerqope.menu.DrawerAdapter;
import za.ac.uct.cs.powerqope.menu.DrawerItem;
import za.ac.uct.cs.powerqope.menu.SimpleItem;
import za.ac.uct.cs.powerqope.util.PhoneUtils;
import za.ac.uct.cs.powerqope.util.Util;
import za.ac.uct.cs.powerqope.util.WebSocketConnector;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.yarolegovich.slidingrootnav.SlidingRootNav;
import com.yarolegovich.slidingrootnav.SlidingRootNavBuilder;

import java.security.Security;
import java.util.Arrays;
import java.util.Properties;


public class MainActivity extends AppCompatActivity implements DrawerAdapter.OnItemSelectedListener {

    private static final String TAG = "MainActivity";

    private static MainActivity app;

    private static final int POS_HOME = 0;
    private static final int POS_PRIVATE_DNS_OPTIONS = 1;
    private static final int POS_SPEED_CHECKER = 2;
    private static final int POS_SETTINGS = 3;
    private static final int POS_HELP = 4;
    private static final int POS_EXIT = 5;
    private static final int PERMISSIONS_REQUEST_CODE = 6789;
    protected static ConfigurationAccess CONFIG = ConfigurationAccess.getLocal();
    private String target;

    protected static Properties config = null;
    protected static boolean switchingConfig = false;

    private String[] screenTitles;
    private Drawable[] screenIcons;

    private SlidingRootNav slidingRootNav;
    BottomNavigationView bottomNavigationView;

    @Override
    protected void onStart() {
        requestPermissions();
        super.onStart();
    }

    ActivityResultLauncher<Intent> activityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        startDNSSvc();
                    } else{
                        Log.e(TAG, "VPN dialog not accepted!\r\nPress restart to display dialog again!");
                    }
                }
            });

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        app = this;
        setContentView(R.layout.activity_main);

        System.setProperty("networkaddress.cache.ttl", "0");
        System.setProperty("networkaddress.cache.negative.ttl", "0");
        Security.setProperty("networkaddress.cache.ttl", "0");
        Security.setProperty("networkaddress.cache.negative.ttl", "0");

        AndroidEnvironment.initEnvironment(this);

        if (target == null) {
            target = Util.getWebSocketTarget();
            SharedPreferences prefs = getSharedPreferences(Config.PREF_KEY_RESOLVED_TARGET, MODE_PRIVATE);
            SharedPreferences.Editor editor = prefs.edit();
            editor.putString(Config.PREF_KEY_RESOLVED_TARGET, target);
            editor.apply();
            WebSocketConnector webSocketConnector = WebSocketConnector.getInstance();
            WebSocketConnector.setContext(getBaseContext());
            if (!webSocketConnector.isConnected())
                webSocketConnector.connectWebSocket(target);
        }

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

    private void requestPermissions() {
        if ((ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE)
                + ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                + ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                + ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE))
                != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this,
                    Manifest.permission.READ_PHONE_STATE) ||
                    ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this,
                            Manifest.permission.ACCESS_COARSE_LOCATION) ||
                    ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this,
                            Manifest.permission.ACCESS_FINE_LOCATION) ||
                    ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE)
            ) {
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setTitle("Please grant the following permissions");
                builder.setMessage("Read phone state, Access Storage");
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        ActivityCompat.requestPermissions(MainActivity.this,
                                new String[]{
                                        Manifest.permission.READ_PHONE_STATE,
                                        Manifest.permission.WRITE_EXTERNAL_STORAGE
                                },
                                PERMISSIONS_REQUEST_CODE
                        );
                    }
                });
                builder.setNegativeButton("Cancel", null);
                AlertDialog alertDialog = builder.create();
                alertDialog.show();
            } else {
                ActivityCompat.requestPermissions(MainActivity.this,
                        new String[]{
                                Manifest.permission.READ_PHONE_STATE,
                                Manifest.permission.WRITE_EXTERNAL_STORAGE
                        },
                        PERMISSIONS_REQUEST_CODE
                );
            }
        }
    }

    @Override
    public void onItemSelected(int position) {
        if (position == POS_EXIT) {
            finish();
        }
        if (position == POS_HOME) {
            slidingRootNav.closeMenu();
            Intent intent = new Intent(getApplicationContext(), getClass());
            Fragment selectedScreen = new HomeFragment();
            showFragment(selectedScreen);
        }
        if (position == POS_PRIVATE_DNS_OPTIONS) {
            slidingRootNav.closeMenu();
            Intent intent = new Intent(getApplicationContext(), AdvancedActivity.class);
            startActivity(intent);
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

    public static void reloadLocalConfig() {
        if (app != null && CONFIG.isLocal())
            app.loadAndApplyConfig(false);
    }

    protected void loadAndApplyConfig(boolean startApp) {

        config = getConfig();

        if (config != null) {

            if (startApp)
                startup();

        } else
            switchingConfig = false;
    }

    protected void startup() {

        if (DNSFilterService.SERVICE != null) {
            Log.i(TAG, "DNS filter service is running!");
            Log.i(TAG, "Filter statistic since last restart:");
            return;
        }

        try {
            boolean vpnInAdditionToProxyMode = Boolean.parseBoolean(getConfig().getProperty("vpnInAdditionToProxyMode", "true"));
            boolean vpnDisabled = !vpnInAdditionToProxyMode && Boolean.parseBoolean(getConfig().getProperty("dnsProxyOnAndroid", "false"));
            Intent intent = null;
            if (!vpnDisabled)
                intent = VpnService.prepare(this.getApplicationContext());
            if (intent != null) {
                activityResultLauncher.launch(intent);
            } else { //already prepared or VPN disabled
                startDNSSvc();
            }
        } catch (NullPointerException e) { // NullPointer might occur on Android 4.4 when VPN already initialized
            Log.i(TAG, "Seems we are on Android 4.4 or older!");
            startDNSSvc(); // assume it is ok!
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
        }

    }

    private void startDNSSvc() {
        startService(new Intent(this, DNSFilterService.class));
    }

    protected Properties getConfig() {
        try {
            return CONFIG.getConfig();
        } catch (Exception e) {
            Log.e(TAG, e.toString());
            return null;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (grantResults.length > 0) {
            boolean allPermissionsGranted = true;
            for (int i = 0; i < grantResults.length; i++) {
                if (grantResults[i] == PackageManager.PERMISSION_DENIED)
                    allPermissionsGranted = false;
            }
            if (allPermissionsGranted) {
                PhoneUtils.setGlobalContext(this.getApplicationContext());
                PhoneUtils phoneUtils = PhoneUtils.getPhoneUtils();
                phoneUtils.registerSignalStrengthListener();
                loadAndApplyConfig(true);
            }
        } else {
            if (grantResults.length == 0)
                Log.e(TAG, "grantResults is empty - Assuming permission denied!");
            System.exit(-1);
        }
    }
}

