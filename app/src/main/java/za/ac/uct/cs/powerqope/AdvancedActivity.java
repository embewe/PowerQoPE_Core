package za.ac.uct.cs.powerqope;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
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
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;


public class AdvancedActivity extends AppCompatActivity {

    private static final String TAG = "AdvancedActivity";

    LinearLayout back, container, container1, container2;
    Switch switchEnableVpn, switchAutoConnect;
    RadioButton radioButton4, radioButton5, radioButton6;
    EditText editText, editText2, editText3;
    Spinner dropdown, dropdown1, dropdown2, dropdown3;
    String[] advancedOptions;
    List<String> filterOptions = new ArrayList<String>() {{
        add("No Filter");
    }};
    Button configure;
    Context mContext;
    String MY_PREFS_NAME = "preferences";
    DNS_TYPES selDNSType = null;

    enum DNS_TYPES {Do53, DoT, DoH}

    String selDNSProvider = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_advanced);
        advancedOptions = getResources().getStringArray(R.array.dns_providers);
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

        configure.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);
            }
        });
        ArrayAdapter adapter = new ArrayAdapter(this,
                R.layout.spinner_item2, advancedOptions);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        dropdown.setAdapter(adapter);
        dropdown1.setAdapter(adapter);
        dropdown2.setAdapter(adapter);

        ArrayAdapter adapter1 = new ArrayAdapter(AdvancedActivity.this,
                R.layout.spinner_item2, filterOptions);
        adapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        dropdown3.setAdapter(adapter1);

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
    }

    private class DNSItemChangeListener implements AdapterView.OnItemSelectedListener {

        private EditText et;

        public DNSItemChangeListener(EditText et){
            this.et = et;
        }

        @Override
        public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
            selDNSProvider = advancedOptions[i];
            switch (selDNSProvider) {
                case "CloudFlare":
                    filterOptions.clear();
                    filterOptions.add("Standard");
                    filterOptions.add("Security");
                    filterOptions.add("Family");
                    et.setEnabled(false);
                    break;
                case "CleanBrowsing":
                    filterOptions.clear();
                    filterOptions.add("Standard");
                    filterOptions.add("Adult");
                    filterOptions.add("Family");
                    et.setEnabled(false);
                    break;
                case "AdGuard":
                    filterOptions.clear();
                    filterOptions.add("Ads");
                    filterOptions.add("Family");
                    et.setEnabled(false);
                    break;
                case "Google":
                    filterOptions.clear();
                    filterOptions.add("No filter available");
                    et.setEnabled(false);
                    break;
                case "Quad9":
                    filterOptions.clear();
                    filterOptions.add("Standard");
                    filterOptions.add("Security");
                    et.setEnabled(false);
                    break;
                case "Custom":
                    filterOptions.clear();
                    filterOptions.add("Custom Filter");
                    et.setEnabled(true);
                    SharedPreferences.Editor editor = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE).edit();
                    editor.putString("editText", et.getText().toString());
                    editor.apply();
                    break;
                default:
                    filterOptions.clear();
                    filterOptions.add("System Filter");
                    et.setEnabled(false);
                    break;
            }
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
