package za.ac.uct.cs.powerqope;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.github.amlcurran.showcaseview.ShowcaseView;
import com.github.amlcurran.showcaseview.targets.ViewTarget;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import za.ac.uct.cs.powerqope.activity.PrefManager;


public class AdvancedActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "AdvancedActivity";
    private ShowcaseView showcaseView;
    private int counter = 0;
    private ShowcaseView mGuideView;
    private ShowcaseView.Builder builder;
    private PrefManager prefManager;
    LinearLayout back, container, container1, container2, dns, web, cypher, vpn, help;
    Switch switchEnableVpn, switchAutoConnect;
    RadioButton radioButton4, radioButton5, radioButton6;
    EditText editText, editText2, editText3;
    Spinner dropdown, dropdown1, dropdown2, dropdown3, dropdown4, vpnHosts;
    String[] advancedOptions;
    Map<String, List<String>> filterOptionsMap = new HashMap<>();

    List<String> filterOptions;
    Button configure;
    Context mContext;
    String MY_PREFS_NAME = "preferences";

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {
        super.onPointerCaptureChanged(hasCapture);
    }

    enum DNS_TYPES {Do53, DoT, DoH}

    DNS_TYPES selDNSType;
    String selDNSProvider;
    String selDNSFilter;
    String selCipherLevel;
    String selVpnHost;
    List<String> vpnServerList;

    boolean validationsDone = false;

    private void populateFilterOptionsMap() {
        for(String option : advancedOptions){
            filterOptionsMap.put(option, new ArrayList<>());
            if(option.equalsIgnoreCase("system recommended"))
                filterOptionsMap.get(option).add("System Filter");
            else if(option.equalsIgnoreCase("cloudflare")){
                filterOptionsMap.get(option).add("Standard");
                filterOptionsMap.get(option).add("Security");
                filterOptionsMap.get(option).add("Family");
            }
            else if(option.equalsIgnoreCase("cleanbrowsing")){
                filterOptionsMap.get(option).add("Security");
                filterOptionsMap.get(option).add("Adult");
                filterOptionsMap.get(option).add("Family");
            }
            else if(option.equalsIgnoreCase("adguard")){
                filterOptionsMap.get(option).add("Standard");
                filterOptionsMap.get(option).add("Ads");
                filterOptionsMap.get(option).add("Family");
            }
            else if(option.equalsIgnoreCase("google")){
                filterOptionsMap.get(option).add("No filters available");
            }
            else if(option.equalsIgnoreCase("quad9")){
                filterOptionsMap.get(option).add("Standard");
                filterOptionsMap.get(option).add("Security");
            }
            else if(option.equalsIgnoreCase("custom")){
                filterOptionsMap.get(option).add("Custom Filter");
            }
        }
    }

    private void showEmptyTextAlert(EditText et){
        AlertDialog.Builder builder = new AlertDialog.Builder(AdvancedActivity.this);
        builder.setCancelable(true);
        builder.setTitle("Alert");
        builder.setMessage("Please enter a URI or an IP address.");
        builder.setPositiveButton("Ok",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        et.setText("");
                    }
                });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_advanced);

        advancedOptions = getResources().getStringArray(R.array.dns_providers);
        populateFilterOptionsMap();
        help = findViewById(R.id.help);
        switchEnableVpn = findViewById(R.id.switchEnableVpn);
        editText = findViewById(R.id.editText);
        editText2 = findViewById(R.id.editText2);
        editText3 = findViewById(R.id.editText3);
        configure = findViewById(R.id.configure);
        back = findViewById(R.id.back);
        dns = findViewById(R.id.dnsSettings);
        web = findViewById(R.id.webFiltering);
        vpn = findViewById(R.id.vpnSettings);
        cypher = findViewById(R.id.cipherSuite);
        container = findViewById(R.id.container);
        container1 = findViewById(R.id.container1);
        container2 = findViewById(R.id.container2);
        radioButton4 = findViewById(R.id.regularDns);
        radioButton5 = findViewById(R.id.dnsOverHttps);
        radioButton6 = findViewById(R.id.dnsOverTls);
        dropdown = findViewById(R.id.spinner1);
        dropdown1 = findViewById(R.id.spinner2);
        dropdown2 = findViewById(R.id.spinner3);
        dropdown3 = findViewById(R.id.spinner4);
        dropdown4 = findViewById(R.id.spinner5);
        vpnHosts = findViewById(R.id.vpnHosts);
        switchAutoConnect = findViewById(R.id.switchAutoConnect);
        SharedPreferences prefs = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE);
        switchEnableVpn.setChecked(prefs.getBoolean("switchEnableVpn", false));
        radioButton4.setChecked(prefs.getBoolean("radio", false));
        radioButton5.setChecked(prefs.getBoolean("radio1", false));
        radioButton6.setChecked(prefs.getBoolean("radio2", false));
        switchAutoConnect.setChecked(prefs.getBoolean("switchAutoConnect", false));
        prefManager=new PrefManager(getApplicationContext());
       help.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
            showCaseView();
           }
       });




        if (radioButton4.isChecked() == true) {
            container.setVisibility(View.VISIBLE);
            selDNSType = DNS_TYPES.Do53;
        } else {
            container.setVisibility(View.GONE);
        }
        if (radioButton5.isChecked() == true) {
            container1.setVisibility(View.VISIBLE);
            selDNSType = DNS_TYPES.DoH;
        } else {
            container1.setVisibility(View.GONE);
        }
        if (radioButton6.isChecked() == true) {
            container2.setVisibility(View.VISIBLE);
            selDNSType = DNS_TYPES.DoT;
        } else {
            container2.setVisibility(View.GONE);
        }

        ArrayAdapter adapter = new ArrayAdapter(this,
                R.layout.spinner_item2, advancedOptions);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        dropdown.setAdapter(adapter);
        dropdown1.setAdapter(adapter);
        dropdown2.setAdapter(adapter);

        dropdown3.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                selDNSFilter = adapterView.getItemAtPosition(i).toString();
                SharedPreferences.Editor editor = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE).edit();
                editor.putString("selDNSFilter", selDNSFilter);
                editor.apply();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        dropdown4.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                selCipherLevel = adapterView.getItemAtPosition(i).toString();
                SharedPreferences.Editor editor = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE).edit();
                editor.putString("selCipherLevel", selCipherLevel);
                editor.apply();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        switchAutoConnect.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (switchAutoConnect.isChecked() == true) {
                    SharedPreferences.Editor editor = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE).edit();
                    editor.putBoolean("switchAutoConnect", true);
                    editor.apply();
                } else {
                    SharedPreferences.Editor editor = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE).edit();
                    editor.putBoolean("switchAutoConnect", false);
                    editor.apply();
                }
            }
        });
        radioButton4.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (radioButton4.isChecked() == true) {
                    selDNSType = DNS_TYPES.Do53;
                    SharedPreferences.Editor editor = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE).edit();
                    editor.putBoolean("radio", true);
                    editor.apply();
                    container.setVisibility(View.VISIBLE);
                    dropdown.setOnItemSelectedListener(new DNSItemChangeListener(editText));

                } else {
                    SharedPreferences.Editor editor = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE).edit();
                    editor.putBoolean("radio", false);
                    editor.apply();
                    container.setVisibility(View.GONE);
                    editText.setEnabled(false);
                }
            }
        });
        radioButton5.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (radioButton5.isChecked() == true) {
                    selDNSType = DNS_TYPES.DoH;
                    SharedPreferences.Editor editor = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE).edit();
                    editor.putBoolean("radio1", true);
                    editor.apply();
                    container1.setVisibility(View.VISIBLE);
                    dropdown1.setOnItemSelectedListener(new DNSItemChangeListener(editText2));


                } else {
                    SharedPreferences.Editor editor = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE).edit();
                    editor.putBoolean("radio1", false);
                    editor.apply();
                    container1.setVisibility(View.GONE);
                    editText2.setEnabled(false);
                }
            }
        });
        radioButton6.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (radioButton6.isChecked() == true) {
                    selDNSType = DNS_TYPES.DoT;
                    SharedPreferences.Editor editor = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE).edit();
                    editor.putBoolean("radio2", true);
                    editor.apply();
                    container2.setVisibility(View.VISIBLE);
                    dropdown2.setOnItemSelectedListener(new DNSItemChangeListener(editText3));
                } else {
                    SharedPreferences.Editor editor = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE).edit();
                    editor.putBoolean("radio2", false);
                    editor.apply();
                    container2.setVisibility(View.GONE);
                    editText3.setEnabled(false);
                }
            }
        });
        switchEnableVpn.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (switchEnableVpn.isChecked() == true) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(AdvancedActivity.this);
                    builder.setCancelable(true);
                    builder.setTitle("Alert");
                    builder.setMessage("This is a more private option. However, the internet may be slower.");
                    builder.setPositiveButton("Confirm",
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    switchEnableVpn.setChecked(true);
                                    Toast.makeText(getApplicationContext(), "VPN on", Toast.LENGTH_LONG).show();
                                    SharedPreferences.Editor editor = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE).edit();
                                    editor.putBoolean("switchEnableVpn", true);
                                    editor.apply();
                                    populateVpnList();
                                }
                            });
                    builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            switchEnableVpn.setChecked(false);
                            vpnServerList.clear();

                        }
                    });

                    AlertDialog dialog = builder.create();
                    dialog.show();

                } else {
                    Toast.makeText(getApplicationContext(), "VPN off", Toast.LENGTH_LONG).show();
                    SharedPreferences.Editor editor = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE).edit();
                    editor.putBoolean("switchEnableVpn", false);
                    editor.apply();
                }
            }
        });
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        configure.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                JSONObject filter = new JSONObject();
                try {
                    // Type
                    if (selDNSType.equals(DNS_TYPES.DoH))
                        filter.put("dnsType", "doh");
                    else if(selDNSType.equals(DNS_TYPES.DoT))
                        filter.put("dnsType", "dot");
                    else
                        filter.put("dnsType", "dns");
                    filter.put("recursive", selDNSProvider.toLowerCase()+"_"+selDNSFilter.toLowerCase());
                    // Provider
                    switch (selDNSProvider.toLowerCase()){
                        case "cloudflare":
                            switch (selDNSFilter.toLowerCase()){
                                case "standard":
                                    filter.put("url", "https://cloudflare-dns.com/dns-query");
                                    filter.put("ipAddress", "1.1.1.1");
                                    break;
                                case "security":
                                    filter.put("url", "https://security.cloudflare-dns.com/dns-query");
                                    filter.put("ipAddress", "1.1.1.2");
                                    break;
                                case "family":
                                    filter.put("url", "https://family.cloudflare-dns.com/dns-query");
                                    filter.put("ipAddress", "1.1.1.3");
                                    break;
                            }
                            validationsDone = true;
                            break;
                        case "cleanbrowsing":
                            switch (selDNSFilter.toLowerCase()){
                                case "family":
                                    filter.put("url", "https://doh.cleanbrowsing.org/doh/family-filter");
                                    filter.put("ipAddress", "185.228.168.9");
                                    break;
                                case "adult":
                                    filter.put("url", "https://doh.cleanbrowsing.org/doh/adult-filter");
                                    filter.put("ipAddress", "185.228.168.10");
                                    break;
                                case "security":
                                    filter.put("url", "https://doh.cleanbrowsing.org/doh/security-filter");
                                    filter.put("ipAddress", "185.228.168.168");
                                    break;
                            }
                            validationsDone = true;
                            break;
                        case "adguard":
                            switch (selDNSFilter.toLowerCase()){
                                case "standard":
                                    filter.put("url", "https://dns-unfiltered.adguard.com/dns-query");
                                    filter.put("ipAddress", "94.140.14.140");
                                    break;
                                case "ads":
                                    filter.put("url", "https://dns.adguard.com/dns-query");
                                    filter.put("ipAddress", "94.140.14.14");
                                    filter.put("recursive", "adguard_adblock");
                                    break;
                                case "family":
                                    filter.put("url", "https://dns-family.adguard.com/dns-query");
                                    filter.put("ipAddress", "94.140.14.15");
                                    break;
                            }
                            validationsDone = true;
                            break;
                        case "google":
                            filter.put("url", "https://dns.google/dns-query");
                            filter.put("ipAddress", "8.8.8.8");
                            filter.put("recursive", "google");
                            validationsDone = true;
                            break;
                        case "quad9":
                            switch (selDNSFilter.toLowerCase()) {
                                case "standard":
                                    filter.put("url", "https://dns10.quad9.net/dns-query");
                                    filter.put("ipAddress", "9.9.9.10");
                                    break;
                                case "security":
                                    filter.put("url", "https://dns9.quad9.net/dns-query");
                                    filter.put("ipAddress", "9.9.9.9");
                                    break;
                            }
                            validationsDone = true;
                            break;
                        case "custom":
                            if(selDNSType.equals(DNS_TYPES.Do53)){
                                if(editText.getText().toString().isEmpty())
                                    showEmptyTextAlert(editText);
                                else
                                    validationsDone = true;
                                filter.put("url", editText.getText());
                                filter.put("ipAddress", editText.getText());
                                SharedPreferences.Editor txtEditor = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE).edit();
                                txtEditor.putString("editText", editText.getText().toString());
                                txtEditor.apply();
                            } else if(selDNSType.equals(DNS_TYPES.DoH)){
                                if(editText2.getText().toString().isEmpty())
                                    showEmptyTextAlert(editText2);
                                else
                                    validationsDone = true;
                                filter.put("url", editText2.getText());
                                filter.put("ipAddress", editText2.getText());
                                SharedPreferences.Editor txtEditor = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE).edit();
                                txtEditor.putString("editText", editText2.getText().toString());
                                txtEditor.apply();
                            } else{
                                if(editText3.getText().toString().isEmpty())
                                    showEmptyTextAlert(editText3);
                                else
                                    validationsDone = true;
                                filter.put("url", editText3.getText());
                                filter.put("ipAddress", editText3.getText());
                                SharedPreferences.Editor txtEditor = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE).edit();
                                txtEditor.putString("editText", editText3.getText().toString());
                                txtEditor.apply();
                            }
                            filter.put("recursive", "custom_filter");
                            break;
                    }
                } catch (JSONException e){
                    Log.e(TAG, "onClick: Invalid JSON operation");
                }
                if(validationsDone) {
                    SharedPreferences.Editor editor = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE).edit();
                    editor.putString("advancedFilter", filter.toString());
                    editor.putString("advancedCipher", selCipherLevel);
                    editor.apply();
                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                    intent.putExtra("advancedOptions", filter.toString());
                    intent.putExtra("advancedCipher", selCipherLevel);
                    startActivity(intent);
                }
            }
        });
        selDNSProvider = prefs.getString("selDNSProvider", "System recommended");
        Log.i(TAG, "onCreate: selDNSProvider = "+selDNSProvider);
        selDNSFilter = prefs.getString("selDNSFilter", "System Filter");
        Log.i(TAG, "onCreate: selDNSFilter = "+selDNSFilter);
        selCipherLevel = prefs.getString("selCipherLevel", "low");
        String customAddress = prefs.getString("editText", null);
        selVpnHost = prefs.getString("selVpnHost", null);
        Log.i(TAG, "onCreate: customAddress = "+customAddress);
        List<String> providersList = new ArrayList<>(Arrays.asList(advancedOptions));

        if(selDNSType == DNS_TYPES.Do53){
            dropdown.setSelection(providersList.indexOf(selDNSProvider));
        } else if(selDNSType == DNS_TYPES.DoH){
            dropdown1.setSelection(providersList.indexOf(selDNSProvider));
        } else if(selDNSType == DNS_TYPES.DoT){
            dropdown2.setSelection(providersList.indexOf(selDNSProvider));
        }

        if(selDNSProvider.equalsIgnoreCase("custom")){
            if(selDNSType == DNS_TYPES.Do53){
                editText.setEnabled(true);
                editText.setText(customAddress);
            } else if(selDNSType == DNS_TYPES.DoH){
                editText2.setEnabled(true);
                editText2.setText(customAddress);
            } else if(selDNSType == DNS_TYPES.DoT){
                editText3.setEnabled(true);
                editText3.setText(customAddress);
            }
        }
        filterOptions = filterOptionsMap.get(selDNSProvider);
        ArrayAdapter adapter1 = new ArrayAdapter(this,
                R.layout.spinner_item2, filterOptions);
        adapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        dropdown3.setAdapter(adapter1);
        dropdown3.setSelection(filterOptions.indexOf(selDNSFilter));

        List<String> cipherLevels = new ArrayList<String>(){{
            add("low");
            add("medium");
            add("high");
        }};
        ArrayAdapter adapter2 = new ArrayAdapter(this, R.layout.spinner_item2, cipherLevels);
        adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        dropdown4.setAdapter(adapter2);
        dropdown4.setSelection(cipherLevels.indexOf(selCipherLevel));

        if(switchEnableVpn.isChecked()) populateVpnList();
    }

    private JSONArray loadVpnServers(){
        JSONArray obj = null;
        try {
            InputStream is = getAssets().open("vpn.json");
            byte[] buffer = new byte[is.available()];
            is.read(buffer);
            is.close();
            obj = new JSONArray(new String(buffer, StandardCharsets.UTF_8));
        } catch (IOException e) {
            Log.e(TAG, "loadVpnServers: IOException occurred "+e.getMessage());
        } catch (JSONException e) {
            Log.e(TAG, "loadVpnServers: JSONException occurred "+e.getMessage());
        }
        return obj;
    }

    private void populateVpnList(){
        vpnServerList = new ArrayList<>();
        JSONArray servers = loadVpnServers();
        try {
            for (int i = 0; i < servers.length(); i++) {
                JSONObject server = servers.getJSONObject(i);
                vpnServerList.add(server.getString("hostName"));
            }
            ArrayAdapter adapter3 = new ArrayAdapter(AdvancedActivity.this, R.layout.spinner_item2, vpnServerList);
            adapter3.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            vpnHosts.setAdapter(adapter3);
            vpnHosts.setOnItemSelectedListener(new VpnServerChangeListener());
            adapter3.notifyDataSetChanged();
            if(selVpnHost != null) vpnHosts.setSelection(vpnServerList.indexOf(selVpnHost));
        } catch (JSONException e){
            Log.e(TAG, "JSONException occurred"+e.getMessage());
        }
    }

    private class DNSItemChangeListener implements AdapterView.OnItemSelectedListener {

        private final EditText et;

        public DNSItemChangeListener(EditText et){
            this.et = et;
        }

        @Override
        public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
            selDNSProvider = advancedOptions[i];
            SharedPreferences.Editor editor = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE).edit();
            editor.putString("selDNSProvider", selDNSProvider);
            editor.apply();
            filterOptions = filterOptionsMap.get(selDNSProvider);
            et.setEnabled(selDNSProvider.equalsIgnoreCase("custom"));
            ArrayAdapter adapter = new ArrayAdapter(AdvancedActivity.this,
                    R.layout.spinner_item2, filterOptions);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            dropdown3.setAdapter(adapter);
            adapter.notifyDataSetChanged();
        }

        @Override
        public void onNothingSelected(AdapterView<?> adapterView) {

        }
    }

    private class VpnServerChangeListener implements AdapterView.OnItemSelectedListener {

        @Override
        public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
            selVpnHost = vpnServerList.get(i);
            SharedPreferences.Editor editor = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE).edit();
            editor.putString("selVpnHost", selVpnHost);
            editor.apply();
        }

        @Override
        public void onNothingSelected(AdapterView<?> adapterView) {

        }
    }
    void showCaseView (){
        showcaseView = new ShowcaseView.Builder(this)
                .setTarget(new ViewTarget(dns))
                .setContentTitle("DNS Settings")
                       .setContentText("This option lets you control the privacy of your browsing transactions, e.g. hide your browsing history and your device information. DoH is more private than DoT and Regular DNS.")
                       .setOnClickListener(this)
                       .setStyle(R.style.CustomShowCaseTheme)
                       .build();
               showcaseView.setButtonText("next");
    }
    public void onClick(View v) {
        switch (counter) {
            case 0:
                showcaseView.setShowcase(new ViewTarget(web), true);
                showcaseView.setContentTitle("Web Filtering Settings");
                showcaseView.setContentText("This option lets you block adverts (Ads filter), pornographic sites (Adult, Family), malware (Security), gambling (Family) etc. This is very important especially for children");
                break;

            case 1:
                showcaseView.setShowcase(new ViewTarget(cypher), true);
                showcaseView.setContentTitle("Cipher Suite level");
                showcaseView.setContentText("This option lets you specify the strength level of encryption algorithms. If you are not sure about this you may let the system decide the recommended setting.");
                break;

            case 2:
                showcaseView.setShowcase(new ViewTarget(vpn), true);
                showcaseView.setContentTitle("VPN Settings");
                showcaseView.setContentText("This is a more private security option. It lets you bypass pervasive monitoring and connects you to a remote private network. You remain anonymous and lets you access geo-restricted content. The server list in this setting are for academic purposes only. They are verified to be safe.");
                showcaseView.setButtonText("close");
                break;

            case 3:
                showcaseView.hide();
                setAlpha(1.0f, dns, web, cypher, vpn);
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
}
