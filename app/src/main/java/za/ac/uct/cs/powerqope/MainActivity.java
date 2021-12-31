package za.ac.uct.cs.powerqope;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.net.VpnService;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;

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
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.yarolegovich.slidingrootnav.SlidingRootNav;
import com.yarolegovich.slidingrootnav.SlidingRootNavBuilder;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.security.Security;
import java.util.Arrays;
import java.util.Properties;

import de.blinkt.openvpn.VpnProfile;
import de.blinkt.openvpn.core.OpenVPNService;
import de.blinkt.openvpn.core.VPNLaunchHelper;
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
import za.ac.uct.cs.powerqope.util.WebSocketConnector;


public class MainActivity extends AppCompatActivity implements DrawerAdapter.OnItemSelectedListener {

    private static final String TAG = "MainActivity";
    private static final int START_REMOTE_VPN_PROFILE = 70;

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
    private boolean remoteVpnEnabled = false;
    private boolean isBoundRemoteVpnService = false;

    protected static Properties config = null;
    protected static boolean switchingConfig = false;
    private static VpnProfile remoteVpnProfile;
    private static OpenVPNService remoteVPNService;

    private String[] screenTitles;
    private Drawable[] screenIcons;

    private SlidingRootNav slidingRootNav;
    BottomNavigationView bottomNavigationView;

    @Override
    protected void onStart() {
        requestPermissions();
        bindToRemoteVpnService();
        super.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (isBoundRemoteVpnService) {
            isBoundRemoteVpnService = false;
            unbindService(vpnConnection);
        }
    }

    ActivityResultLauncher<Intent> activityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK) {
                    if(remoteVpnEnabled)
                        startRemoteVpnSvc();
                    else
                        startDNSSvc();
                } else{
                    Log.e(TAG, "VPN dialog not accepted!\r\nPress restart to display dialog again!");
                }
            });

    private ServiceConnection vpnConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            OpenVPNService.LocalBinder binder = (OpenVPNService.LocalBinder) service;
            remoteVPNService = binder.getService();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            remoteVPNService = null;
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        app = this;
        setContentView(R.layout.activity_main);

        ResolveTarget job = new ResolveTarget();
        job.execute();

        System.setProperty("networkaddress.cache.ttl", "0");
        System.setProperty("networkaddress.cache.negative.ttl", "0");
        Security.setProperty("networkaddress.cache.ttl", "0");
        Security.setProperty("networkaddress.cache.negative.ttl", "0");

        AndroidEnvironment.initEnvironment(this);

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
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setTitle("Please grant the following permissions");
                builder.setMessage("Read phone state, Access Storage");
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        ActivityCompat.requestPermissions(MainActivity.this,
                                new String[]{
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
        String secLevel = getConfig().getProperty("secLevel", "default");
        try {
            remoteVpnEnabled = secLevel.equalsIgnoreCase("high");
            Intent intent = VpnService.prepare(this.getApplicationContext());
            if (intent != null) {
                activityResultLauncher.launch(intent);
            } else {//already prepared or VPN disabled
                if(remoteVpnEnabled)
                    startRemoteVpnSvc();
                else
                    startDNSSvc();
            }
        } catch (NullPointerException e) { // NullPointer might occur on Android 4.4 when VPN already initialized
            Log.i(TAG, "Seems we are on Android 4.4 or older!");
            if(remoteVpnEnabled)
                startRemoteVpnSvc();
            else
                startDNSSvc();
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
        }

    }
    private void startRemoteVpnSvc(){
        VPNLaunchHelper.startOpenVpn(remoteVpnProfile, getBaseContext());
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
                loadAndApplyConfig(true);
            }
        } else {
            if (grantResults.length == 0)
                Log.e(TAG, "grantResults is empty - Assuming permission denied!");
            System.exit(-1);
        }
    }

    private class ResolveTarget extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String[] params) {
            String serverIP = null, labAddress = null;
            try {
                InetAddress inetAddress = InetAddress.getByName(Config.SERVER_HOST_ADDRESS);
                serverIP = inetAddress.getHostAddress();
                labAddress = InetAddress.getByName(Config.DNS_PROXY_ADDRESS).getHostAddress();
            }
            catch (UnknownHostException e){
                e.printStackTrace();
            }
            return serverIP+"-"+labAddress;
        }

        @Override
        protected void onPostExecute(String message) {
            String[] mSplit = message.split("-");
            target = "wss://" + mSplit[0] + ":" + Config.SERVER_PORT + Config.STOMP_SERVER_CONNECT_ENDPOINT;
            SharedPreferences prefs = getSharedPreferences(Config.PREF_KEY_RESOLVED_TARGET, MODE_PRIVATE);
            SharedPreferences.Editor editor = prefs.edit();
            editor.putString(Config.PREF_KEY_RESOLVED_TARGET, target);
            editor.putString(Config.PREF_KEY_RESOLVED_DNS_PROXY, mSplit[1]);
            editor.apply();
            WebSocketConnector webSocketConnector = WebSocketConnector.getInstance();
            WebSocketConnector.setContext(getBaseContext());
            if (!webSocketConnector.isConnected())
                webSocketConnector.connectWebSocket(target);
        }
    }

    private void bindToRemoteVpnService() {
        Intent intent = new Intent(this, OpenVPNService.class);
        intent.setAction(OpenVPNService.START_SERVICE);
        isBoundRemoteVpnService = bindService(intent, vpnConnection, Context.BIND_AUTO_CREATE);
    }
}

