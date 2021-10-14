package za.ac.uct.cs.powerqope;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
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

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class AdvancedActivity extends AppCompatActivity {

    private static final String TAG = "AdvancedActivity";

    LinearLayout back, container, container1, container2;
    Switch switchEnableVpn, switchAutoConnect;
    RadioButton radioButton4, radioButton5, radioButton6;
    EditText editText, editText2, editText3;
    Spinner dropdown, dropdown1, dropdown2, dropdown3;
    String[] advancedOptions;

    Map<String, List<String>> filterOptionsMap = new HashMap<>();

    List<String> filterOptions;
    Button configure;
    Context mContext;
    String MY_PREFS_NAME = "preferences";

    enum DNS_TYPES {Do53, DoT, DoH}

    DNS_TYPES selDNSType;
    String selDNSProvider;
    String selDNSFilter;

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
                filterOptionsMap.get(option).add("Unfiltered");
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

        switchEnableVpn = findViewById(R.id.switchEnableVpn);
        editText = findViewById(R.id.editText);
        editText2 = findViewById(R.id.editText2);
        editText3 = findViewById(R.id.editText3);
        configure = findViewById(R.id.configure);
        back = findViewById(R.id.back);
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
        switchAutoConnect = findViewById(R.id.switchAutoConnect);
        SharedPreferences prefs = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE);
        switchEnableVpn.setChecked(prefs.getBoolean("switchEnableVpn", false));
        radioButton4.setChecked(prefs.getBoolean("radio", false));
        radioButton5.setChecked(prefs.getBoolean("radio1", false));
        radioButton6.setChecked(prefs.getBoolean("radio2", false));
        switchAutoConnect.setChecked(prefs.getBoolean("switchAutoConnect", false));

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
                                }
                            });
                    builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            switchEnableVpn.setChecked(false);

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
                        filter.put("dnsType", "do53");
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
                                case "unfiltered":
                                    filter.put("url", "https://dns-unfiltered.adguard.com/dns-query");
                                    filter.put("ipAddress", "94.140.14.140");
                                    break;
                                case "ads":
                                    filter.put("url", "https://dns.adguard.com/dns-query");
                                    filter.put("ipAddress", "94.140.14.14");
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
                            break;
                    }
                } catch (JSONException e){
                    Log.e(TAG, "onClick: Invalid JSON operation");
                }
                if(validationsDone) {
                    SharedPreferences.Editor editor = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE).edit();
                    editor.putString("advancedFilter", filter.toString());
                    editor.apply();
                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                    intent.putExtra("advancedOptions", filter.toString());
                    startActivity(intent);
                }
            }
        });
        selDNSProvider = prefs.getString("selDNSProvider", "System recommended");
        Log.i(TAG, "onCreate: selDNSProvider = "+selDNSProvider);
        selDNSFilter = prefs.getString("selDNSFilter", "System Filter");
        Log.i(TAG, "onCreate: selDNSFilter = "+selDNSFilter);
        String customAddress = prefs.getString("editText", null);
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
    }

    private class DNSItemChangeListener implements AdapterView.OnItemSelectedListener {

        private EditText et;

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

}
