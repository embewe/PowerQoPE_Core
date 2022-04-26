package za.ac.uct.cs.powerqope.fragment;


import static android.content.Context.MODE_PRIVATE;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.widget.SwitchCompat;
import androidx.fragment.app.Fragment;

import com.github.amlcurran.showcaseview.ShowcaseView;
import com.github.amlcurran.showcaseview.targets.Target;
import com.github.amlcurran.showcaseview.targets.ViewTarget;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

import za.ac.uct.cs.powerqope.AdvancedActivity;
import za.ac.uct.cs.powerqope.Config;
import za.ac.uct.cs.powerqope.R;
import za.ac.uct.cs.powerqope.activity.PrefManager;
import za.ac.uct.cs.powerqope.dns.ConfigurationAccess;
import za.ac.uct.cs.powerqope.dns.DNSCommunicator;
import za.ac.uct.cs.powerqope.util.PhoneUtils;
import za.ac.uct.cs.powerqope.util.Util;
import za.ac.uct.cs.powerqope.util.WebSocketConnector;


/**
 * A simple {@link Fragment} subclass.
 */
public class HomeFragment extends Fragment implements View.OnClickListener {

    private static final String TAG = "HomeFragment";
    private static final ConfigurationAccess CONFIG = ConfigurationAccess.getLocal();

    public HomeFragment() {
        // Required empty public constructor
    }
    SettingsFragment sf = new SettingsFragment();
    private ShowcaseView showcaseView;
    private int counter = 0;
    private ShowcaseView mGuideView;
    private ShowcaseView.Builder builder;
    private PrefManager prefManager;
    Switch costInformation, visuals;
    LinearLayout connected, high, medium,low;
    SwitchCompat switchCompat;
    RadioButton radioButton, radioButton1, radioButton2, radioButton3;
    TextView txtHead;
    static TextView serverInfoTxt;
    LinearLayout linearLayout, linearLayout1, linearLayout2, linearLayout3, linearLayoutV1, linearLayoutV2, linearLayoutV3;

    String MY_PREFS_NAME = "preferences";
    static String selectedConfig;
    public static String vpnHost;
    static SharedPreferences prefs;

    private JSONObject acquireVpnServerInfo() {
        try {
            InputStream is = getActivity().getAssets().open("vpn.json");
            byte[] buffer = new byte[is.available()];
            is.read(buffer);
            is.close();
            JSONArray arr = new JSONArray(new String(buffer, StandardCharsets.UTF_8));
            for (int i = 0; i < arr.length(); i++) {
                JSONObject server = arr.getJSONObject(i);
                if (server.getString("hostName").equals(vpnHost))
                    return server;
            }
        } catch (IOException e) {
            Log.e(TAG, "acquireVpnServerInfo: IOException occurred " + e.getMessage());
        } catch (JSONException e) {
            Log.e(TAG, "acquireVpnServerInfo: JSONException occurred " + e.getMessage());
        }
        return null;
    }

    @SuppressLint("ResourceAsColor")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_home, container, false);
        View view = inflater.inflate(R.layout.fragment_settings, container, false);
        switchCompat = v.findViewById(R.id.switch1);
        connected = v.findViewById(R.id.connected);
        radioButton = v.findViewById(R.id.radioButton);
        radioButton1 = v.findViewById(R.id.radioButton1);
        radioButton2 = v.findViewById(R.id.radioButton2);
        radioButton3 = v.findViewById(R.id.radioButton3);
        serverInfoTxt = v.findViewById(R.id.measurements);
        txtHead = v.findViewById(R.id.txtHead);
        high = v.findViewById(R.id.high_cost_info);
        medium = v.findViewById(R.id.medium_cost_info);
        low = v.findViewById(R.id.low_cost_info);
        prefs = getActivity().getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE);
        switchCompat.setChecked(prefs.getBoolean("connect", false));
        radioButton.setChecked(prefs.getBoolean("radioButton", false));
        radioButton1.setChecked(prefs.getBoolean("radioButton1", false));
        radioButton2.setChecked(prefs.getBoolean("radioButton2", false));
        radioButton3.setChecked(prefs.getBoolean("radioButton3", false));
        costInformation= view.findViewById(R.id.switch34);
        costInformation.setChecked(prefs.getBoolean("cost", true));
        visuals = view.findViewById(R.id.switch50);
        visuals.setChecked(prefs.getBoolean("visuals", true));
        linearLayoutV1 = v.findViewById(R.id.visual_high);
        linearLayoutV2 = v.findViewById(R.id.visual_medium);
        linearLayoutV3 = v.findViewById(R.id.visual_low);

        if(costInformation.isChecked()==true){
            high.setVisibility(View.VISIBLE);
            low.setVisibility(View.VISIBLE);
            medium.setVisibility(View.VISIBLE);
            txtHead.setVisibility(View.GONE);
            if(visuals.isChecked()==true){
                linearLayoutV1.setVisibility(View.VISIBLE);
                linearLayoutV2.setVisibility(View.VISIBLE);
                linearLayoutV3.setVisibility(View.VISIBLE);
            }
            else {
                linearLayoutV1.setVisibility(View.GONE);
                linearLayoutV2.setVisibility(View.GONE);
                linearLayoutV3.setVisibility(View.GONE);
            }
        }
        else{
            txtHead.setVisibility(View.VISIBLE);
            high.setVisibility(View.GONE);
            low.setVisibility(View.GONE);
            medium.setVisibility(View.GONE);
            linearLayoutV1.setVisibility(View.GONE);
            linearLayoutV2.setVisibility(View.GONE);
            linearLayoutV3.setVisibility(View.GONE);
        }
        linearLayout = v.findViewById(R.id.linearRadiobutton);
        linearLayout1 = v.findViewById(R.id.linearRadiobutton1);
        linearLayout2 = v.findViewById(R.id.linearRadiobutton2);
        linearLayout3 = v.findViewById(R.id.linearRadiobutton3);


        Target target = new ViewTarget(switchCompat);

        prefManager=new PrefManager(getContext());
        //call this method with title,text,view of first element(where to start highlights)
        if (prefManager.isFirstTimeLaunch()) {
            prefManager.setFirstTimeLaunch(false);
            showcaseView = new ShowcaseView.Builder(getActivity())
                    .setTarget(new ViewTarget(linearLayout))
                    .setContentTitle("High security")
                    .setContentText("This option helps you configure better security, privacy and content filtering. The network speed may degrade")
                    .setOnClickListener(this)
                    .setStyle(R.style.CustomShowCaseTheme)
                    .build();
            showcaseView.setButtonText("next");
            /*new ShowcaseView.Builder(getActivity())
                    .setTarget(target)
                    .setContentTitle("ShowcaseView")
                    .setContentText("This is highlighting the Home button")
                    .hideOnTouchOutside()
                    .build();*/
            
        }
        SharedPreferences prefs = getActivity().getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE);
        switchCompat.setChecked(prefs.getBoolean("connect",false));
        radioButton.setChecked(prefs.getBoolean("radioButton",false));
        radioButton1.setChecked(prefs.getBoolean("radioButton1",false));
        radioButton2.setChecked(prefs.getBoolean("radioButton2",false));
        radioButton3.setChecked(prefs.getBoolean("radioButton3",false));


        selectedConfig =
                (radioButton.isChecked() ? "high" :
                        (radioButton1.isChecked() ? "medium" :
                                (radioButton2.isChecked() ? "low" :
                                        (radioButton3.isChecked() ? "advanced" : "default"))));
        vpnHost = prefs.getString("selVpnHost", null);
        boolean isVpnOn = selectedConfig.equalsIgnoreCase("high") || (prefs.getBoolean("switchEnableVpn", false) && (vpnHost != null));
        String info = (isVpnOn ? vpnHost : DNSCommunicator.getInstance().getLastDNSAddress());
        setServerInfoTxt(info);

        String advancedFilter = prefs.getString("advancedFilter", null);

        switchCompat.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (radioButton.isChecked() == false && radioButton1.isChecked() == false && radioButton2.isChecked() == false && radioButton3.isChecked() == false) {
                    Toast.makeText(getActivity(), "Please select any of the above configurations.", Toast.LENGTH_SHORT).show();
                    switchCompat.setChecked(false);
                } else {
                    if (switchCompat.isChecked()) {
                        if (selectedConfig.equalsIgnoreCase("advanced")) {
                            Intent intent = getActivity().getIntent();
                            Bundle extras = intent.getExtras();
                            JSONObject vpnServerInfo = acquireVpnServerInfo();
                            if (extras != null) {
                                String options = extras.getString("advancedOptions");
                                WebSocketConnector connector = WebSocketConnector.getInstance();
                                try {
                                    connector.modifyConfig(new JSONObject(options), vpnServerInfo);
                                } catch (JSONException e) {
                                    Log.e(TAG, "onCheckedChanged: Error parsing JSON");
                                }
                            } else if (advancedFilter != null) {
                                WebSocketConnector connector = WebSocketConnector.getInstance();
                                try {
                                    connector.modifyConfig(new JSONObject(advancedFilter), vpnServerInfo);
                                } catch (JSONException e) {
                                    Log.e(TAG, "onCheckedChanged: Error parsing JSON");
                                }
                            }
                        } else {
                            WebSocketConnector connector = WebSocketConnector.getInstance();
                            PhoneUtils.setGlobalContext(getActivity().getApplicationContext());
                            PhoneUtils phoneUtils = PhoneUtils.getPhoneUtils();
                            JSONObject payload = new JSONObject();
                            try {
                                payload.put("level", selectedConfig);
                                payload.put("networkType", phoneUtils.getNetworkClass());
                                payload.put("deviceId", connector.getDeviceId());
                            } catch (JSONException e) {
                                Log.e(TAG, "onViewCreated: Error while building JSON");
                            }
                            connector.sendMessage(Config.STOMP_SERVER_CONFIG_REQUEST_ENDPOINT, payload.toString());
                        }
                        Toast.makeText(getActivity(), "Connected", Toast.LENGTH_SHORT).show();
                        connected.setBackgroundColor(Color.parseColor("#43A047"));
                        SharedPreferences.Editor editor = getActivity().getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE).edit();
                        editor.putBoolean("connect", true);
                        editor.apply();
                    } else {
                        restoreDefaults();
                        Toast.makeText(getActivity(), "Disconnected", Toast.LENGTH_SHORT).show();
                        connected.setBackgroundColor(Color.parseColor("#292B2B"));
                        SharedPreferences.Editor editor = getActivity().getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE).edit();
                        editor.putBoolean("connect", false);
                        editor.apply();
                    }
                }
            }
        });

        radioButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (radioButton.isChecked() == true) {
                    /*if(visuals.isChecked()==true){
                        linearLayoutV1.setVisibility(View.VISIBLE);
                        linearLayoutV2.setVisibility(View.VISIBLE);
                        linearLayoutV3.setVisibility(View.VISIBLE);
                    }
                    else {
                        linearLayoutV1.setVisibility(View.GONE);
                        linearLayoutV2.setVisibility(View.GONE);
                        linearLayoutV3.setVisibility(View.GONE);
                    }*/
                    switchCompat.setChecked(false);
                    SharedPreferences.Editor editor = getActivity().getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE).edit();
                    editor.putBoolean("radioButton", true);
                    editor.apply();
                    //high.setVisibility(View.VISIBLE);
                    Toast.makeText(getActivity(), "High security selected", Toast.LENGTH_SHORT).show();
                    selectedConfig = "high";
                } else {
                    //high.setVisibility(View.GONE);
                    SharedPreferences.Editor editor = getActivity().getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE).edit();
                    editor.putBoolean("radioButton", false);
                    editor.apply();
                    //linearLayoutV1.setVisibility(View.GONE);
                    //linearLayoutV2.setVisibility(View.GONE);
                    //linearLayoutV3.setVisibility(View.GONE);
                }
            }
        });
        radioButton1.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {

                if (radioButton1.isChecked() == true) {
                    /*if(visuals.isChecked()==true){
                        linearLayoutV1.setVisibility(View.VISIBLE);
                        linearLayoutV2.setVisibility(View.VISIBLE);
                        linearLayoutV3.setVisibility(View.VISIBLE);
                    }
                    else {
                        linearLayoutV1.setVisibility(View.GONE);
                        linearLayoutV2.setVisibility(View.GONE);
                        linearLayoutV3.setVisibility(View.GONE);
                    }*/
                    switchCompat.setChecked(false);
                    //medium.setVisibility(View.VISIBLE);
                    SharedPreferences.Editor editor = getActivity().getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE).edit();
                    editor.putBoolean("radioButton1", true);
                    editor.apply();
                    Toast.makeText(getActivity(), "Medium security selected", Toast.LENGTH_SHORT).show();
                    selectedConfig = "medium";
                } else {
                    //medium.setVisibility(View.GONE);
                    SharedPreferences.Editor editor = getActivity().getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE).edit();
                    editor.putBoolean("radioButton1", false);
                    editor.apply();
                    //linearLayoutV1.setVisibility(View.GONE);
                    //linearLayoutV2.setVisibility(View.GONE);
                    //linearLayoutV3.setVisibility(View.GONE);
                }
            }
        });
        radioButton2.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {

                if (radioButton2.isChecked() == true) {
                    /*if(visuals.isChecked()==true){
                        linearLayoutV1.setVisibility(View.VISIBLE);
                        linearLayoutV2.setVisibility(View.VISIBLE);
                        linearLayoutV3.setVisibility(View.VISIBLE);
                    }
                    else {
                        linearLayoutV1.setVisibility(View.GONE);
                        linearLayoutV2.setVisibility(View.GONE);
                        linearLayoutV3.setVisibility(View.GONE);
                    }*/
                    //low.setVisibility(View.VISIBLE);
                    switchCompat.setChecked(false);
                    SharedPreferences.Editor editor = getActivity().getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE).edit();
                    editor.putBoolean("radioButton2", true);
                    editor.apply();
                    Toast.makeText(getActivity(), "Low security selected", Toast.LENGTH_SHORT).show();
                    selectedConfig = "low";
                } else {
                    //low.setVisibility(View.GONE);
                    SharedPreferences.Editor editor = getActivity().getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE).edit();
                    editor.putBoolean("radioButton2", false);
                    editor.apply();
                    //linearLayoutV1.setVisibility(View.GONE);
                    //linearLayoutV2.setVisibility(View.GONE);
                    //linearLayoutV3.setVisibility(View.GONE);

                }
            }
        });
        radioButton3.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {

                if (radioButton3.isChecked() == true) {
                    switchCompat.setChecked(false);
                    SharedPreferences.Editor editor = getActivity().getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE).edit();
                    editor.putBoolean("radioButton3", true);
                    editor.apply();
                    Toast.makeText(getActivity(), "Advanced security options selected", Toast.LENGTH_SHORT).show();
                    selectedConfig = "advanced";
                    Intent intent = new Intent(getContext(), AdvancedActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                    startActivity(intent);
                } else {
                    SharedPreferences.Editor editor = getActivity().getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE).edit();
                    editor.putBoolean("radioButton3", false);
                    editor.apply();
                }
            }
        });


       /*  click.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("ResourceAsColor")
            @Override
            public void onClick(View view) {
                if (imageViewOff.getVisibility()== View.GONE && gifOff.getVisibility() == View.GONE){
                    imageViewOff.setVisibility(View.VISIBLE);
                    imageViewOn.setVisibility(View.GONE);
                    gifOff.setVisibility(View.VISIBLE);
                    gifOn.setVisibility(View.GONE);
                    connected.setVisibility(View.GONE);
                    Toast.makeText(getActivity(), "Disconnected", Toast.LENGTH_SHORT).show();

                }
                else if (imageViewOn.getVisibility() == View.GONE && gifOn.getVisibility() == View.GONE){
                    imageViewOn.setVisibility(View.VISIBLE);
                    imageViewOff.setVisibility(View.GONE);
                    gifOff.setVisibility(View.GONE);
                    gifOn.setVisibility(View.VISIBLE);
                    connected.setVisibility(View.VISIBLE);
                    Toast.makeText(getActivity(), "Connected", Toast.LENGTH_SHORT).show();

                }
            }
        });
       String[] advancedOptions = getResources().getStringArray(R.array.advanced_options);
        ArrayAdapter adapter = new ArrayAdapter(getContext(),
                R.layout.spinner_item, advancedOptions);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerAdvancedOptions.setAdapter(adapter);*/
        return v;
    }
    public void onClick(View v) {
        switch (counter) {
            case 0:
                showcaseView.setShowcase(new ViewTarget(linearLayout1), true);
                showcaseView.setContentTitle("Medium security");
                showcaseView.setContentText("This option helps you configure moderate security and privacy. The network speed may moderately degrade");
                break;

            case 1:
                showcaseView.setShowcase(new ViewTarget(linearLayout2), true);
                showcaseView.setContentTitle("Low security");
                showcaseView.setContentText("Using this option, your browsing activities may be seen by attackers. However, it lets you block malware, ads and adult content. You have better network speed.");
                break;

            case 2:
                showcaseView.setShowcase(new ViewTarget(linearLayout3), true);
                showcaseView.setContentTitle("Advanced security");
                showcaseView.setContentText("This option gives you flexibility to configure your desired level of security, privacy and content filtering ");
                break;

            case 3:
                showcaseView.setShowcase(new ViewTarget(switchCompat), true);
                showcaseView.setContentTitle("Switch button");
                showcaseView.setContentText("After choosing your desired level of security above, tap this button to effect the configuration on your phone.");
                showcaseView.setButtonText("close");
                break;

            case 4:
                showcaseView.hide();
                setAlpha(1.0f, radioButton, radioButton1, radioButton2, radioButton3, switchCompat);
                break;
        }
        counter++;
    }
    private void setAlpha(float alpha, View... views) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            for (View view : views) {
                view.setAlpha(alpha);
            }
        }
    }
    private void restoreDefaults() {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        String ln;
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(new ByteArrayInputStream(CONFIG.readConfig())));
            while ((ln = reader.readLine()) != null) {
                if (ln.trim().startsWith("detectDNS"))
                    ln = "detectDNS = true";

                else if (ln.trim().startsWith("fallbackDNS"))
                    ln = "fallbackDNS = ";

                else if (ln.trim().startsWith("cipher"))
                    ln = "cipher = ";

                else if (ln.trim().startsWith("secLevel"))
                    ln = "secLevel = default";

                else if (ln.trim().startsWith("filterProvider"))
                    ln = "filterProvider = no_filter";

                out.write((ln + "\r\n").getBytes());
            }

            reader.close();
            out.flush();
            out.close();

            CONFIG.updateConfig(out.toByteArray());
        } catch (Exception e) {
            Log.e(TAG, "restoreDefaults: " + e.getMessage());
        }
    }

    public static void setServerInfoTxt(String info) {
        boolean isVpnOn = selectedConfig.equalsIgnoreCase("high") || (prefs.getBoolean("switchEnableVpn", false) && (vpnHost != null));
        if (isVpnOn)
            serverInfoTxt.setText(String.format("VPN mode ON\nConnected to : %s", vpnHost));
        else {
            try {
                if (!info.isEmpty()) {
                    String[] components = info.split("::");
                    String protocol = components[2];
                    String filterInfo = CONFIG.getConfig().getProperty("filterProvider", "no_filter");
                    String provider = "System default";
                    String webFilter = "None";
                    if (filterInfo.equalsIgnoreCase("google"))
                        provider = Util.capitalize(filterInfo);
                    else if (!filterInfo.equalsIgnoreCase("no_filter")) {
                        String[] fInfo = filterInfo.split("_");
                        provider = Util.capitalize(fInfo[0]);
                        webFilter = Util.capitalize(fInfo[1]) + " filter";
                    }
                    serverInfoTxt.setText(String.format("DNS provider: %s\nWeb Filter: %s\nProtocol: %s", provider, webFilter, protocol));
                }
            } catch (IOException e) {
                Log.e(TAG, "setServerInfo: Unable to read local config\n" + e);
            }
        }
    }
    @Override
    public void onResume() {
        super.onResume();

        if (prefs.getBoolean("firstrun", true)) {
            // Do first run stuff here then set 'firstrun' as false
            // using the following line to edit/commit prefs
            prefs.edit().putBoolean("firstrun", false).commit();
        }
    }
    public static String getServerInfoTxt() {
        return serverInfoTxt.getText().toString();
    }

}
