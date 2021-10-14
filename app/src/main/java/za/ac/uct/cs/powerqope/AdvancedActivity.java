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


public class AdvancedActivity extends AppCompatActivity {

    LinearLayout back, container,container1,container2;
    Switch switchEnableFilter, switchEnableVpn, switchAutoConnect;
    RadioButton radioSecurityFilter, radioFamilyFilter, radioAdsFilter, radioButton4,radioButton5,radioButton6;
    TextView textView,textView2;
    EditText editText, editText2, editText3;
    Spinner dropdown, dropdown1, dropdown2;
    Button configure;
    Context mContext;
    String MY_PREFS_NAME="preferences";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_advanced);
        switchEnableFilter = findViewById(R.id.switchEnableFilter);
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
        radioSecurityFilter = findViewById(R.id.radioSecurity);
        radioFamilyFilter = findViewById(R.id.radioFamily);
        radioAdsFilter = findViewById(R.id.radioAds);
        dropdown = findViewById(R.id.spinner1);
        dropdown1 = findViewById(R.id.spinner2);
        dropdown2 = findViewById(R.id.spinner3);
        switchAutoConnect = findViewById(R.id.switchAutoConnect);
        SharedPreferences prefs = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE);
        switchEnableFilter.setChecked(prefs.getBoolean("switchEnableFilter",false));
        switchEnableVpn.setChecked(prefs.getBoolean("switchEnableVpn",false));
        radioButton4.setChecked(prefs.getBoolean("radio",false));
        radioButton5.setChecked(prefs.getBoolean("radio1",false));
        radioButton6.setChecked(prefs.getBoolean("radio2",false));
        radioSecurityFilter.setChecked(prefs.getBoolean("radioSecurityFilter",false));
        radioFamilyFilter.setChecked(prefs.getBoolean("radioFamilyFilter",false));
        radioAdsFilter.setChecked(prefs.getBoolean("radioAdsFilter",false));
        switchAutoConnect.setChecked(prefs.getBoolean("switchAutoConnect",false));
        if (radioButton4.isChecked() == true) {
            container.setVisibility(View.VISIBLE);
        } else {
            container.setVisibility(View.GONE);
        }
        if (radioButton5.isChecked() == true) {
            container1.setVisibility(View.VISIBLE);
        } else {
            container1.setVisibility(View.GONE);
        }
        if (radioButton6.isChecked() == true) {
            container2.setVisibility(View.VISIBLE);
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
        String[] advancedOptions = getResources().getStringArray(R.array.advanced_options);
        ArrayAdapter adapter = new ArrayAdapter(this,
                R.layout.spinner_item2, advancedOptions);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        dropdown.setAdapter(adapter);
        dropdown1.setAdapter(adapter);
        dropdown2.setAdapter(adapter);

        switchAutoConnect.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (switchAutoConnect.isChecked() == true) {
                    SharedPreferences.Editor editor = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE).edit();
                    editor.putBoolean("switchAutoConnect",true);
                    editor.apply();
                }
                else {
                    SharedPreferences.Editor editor = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE).edit();
                    editor.putBoolean("switchAutoConnect",false);
                    editor.apply();
                }
            }
        });
        radioSecurityFilter.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (radioSecurityFilter.isChecked() == true) {
                    SharedPreferences.Editor editor = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE).edit();
                    editor.putBoolean("radioSecurityFilter",true);
                    editor.apply();
                }
                else {
                    SharedPreferences.Editor editor = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE).edit();
                    editor.putBoolean("radioSecurityFilter",false);
                    editor.apply();
                }}
                                                });

        radioFamilyFilter.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (radioFamilyFilter.isChecked() == true) {
                    SharedPreferences.Editor editor = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE).edit();
                    editor.putBoolean("radioFamilyFilter",true);
                    editor.apply();
                }
                else {
                    SharedPreferences.Editor editor = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE).edit();
                    editor.putBoolean("radioFamilyFilter",false);
                    editor.apply();
                }}
        });

        radioAdsFilter.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (radioAdsFilter.isChecked() == true) {
                    SharedPreferences.Editor editor = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE).edit();
                    editor.putBoolean("radioAdsFilter",true);
                    editor.apply();
                }
                else {
                    SharedPreferences.Editor editor = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE).edit();
                    editor.putBoolean("radioAdsFilter",false);
                    editor.apply();
                }}
        });
        radioButton4.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (radioButton4.isChecked() == true) {
                    SharedPreferences.Editor editor = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE).edit();
                    editor.putBoolean("radio",true);
                    editor.apply();
                    container.setVisibility(View.VISIBLE);
                    dropdown.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                            if (dropdown.getSelectedItemPosition() == 12) {
                                editText.setEnabled(true);
                                SharedPreferences.Editor editor = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE).edit();
                                editor.putString("editText",editText.getText().toString());
                                editor.apply();
                            } else if (dropdown.getSelectedItemPosition() != 12) {
                                editText.setEnabled(false);
                            }
                        }

                        @Override
                        public void onNothingSelected(AdapterView<?> adapterView) {

                        }
                    });


                } else {
                    SharedPreferences.Editor editor = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE).edit();
                    editor.putBoolean("radio",false);
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
                    editor.putBoolean("radio1",true);
                    editor.apply();
                    container1.setVisibility(View.VISIBLE);
                    dropdown1.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                            if (dropdown1.getSelectedItemPosition() == 12) {
                                editText2.setEnabled(true);
                            } else if (dropdown1.getSelectedItemPosition() != 12) {
                                editText2.setEnabled(false);
                            }
                        }

                        @Override
                        public void onNothingSelected(AdapterView<?> adapterView) {

                        }
                    });


                } else {
                    SharedPreferences.Editor editor = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE).edit();
                    editor.putBoolean("radio1",false);
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
                    editor.putBoolean("radio2",true);
                    editor.apply();
                    container2.setVisibility(View.VISIBLE);
                    dropdown2.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                            if (dropdown2.getSelectedItemPosition() == 12) {
                                editText3.setEnabled(true);
                            } else if (dropdown2.getSelectedItemPosition() != 12) {
                                editText3.setEnabled(false);
                            }
                        }

                        @Override
                        public void onNothingSelected(AdapterView<?> adapterView) {

                        }
                    });


                } else {
                    SharedPreferences.Editor editor = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE).edit();
                    editor.putBoolean("radio2",false);
                    editor.apply();
                    container2.setVisibility(View.GONE);
                    editText3.setEnabled(false);
                }
            }
        });
        if(switchEnableFilter.isChecked()==true){
            radioSecurityFilter.setEnabled(true);
            radioFamilyFilter.setEnabled(true);
            radioAdsFilter.setEnabled(true);
            radioSecurityFilter.setTextColor(Color.parseColor("#ffffff"));
            radioFamilyFilter.setTextColor(Color.parseColor("#ffffff"));
            radioAdsFilter.setTextColor(Color.parseColor("#ffffff"));

        }
        else {
            radioSecurityFilter.setEnabled(false);
            radioFamilyFilter.setEnabled(false);
            radioAdsFilter.setEnabled(false);
            radioSecurityFilter.setTextColor(Color.parseColor("#8B8B8B"));
            radioFamilyFilter.setTextColor(Color.parseColor("#8B8B8B"));
            radioAdsFilter.setTextColor(Color.parseColor("#8B8B8B"));

        }
        switchEnableFilter.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (switchEnableFilter.isChecked()==true) {
                            AlertDialog.Builder builder = new AlertDialog.Builder(AdvancedActivity.this);
                            builder.setCancelable(true);
                            builder.setTitle("Alert");
                            builder.setMessage("With this configuration some websites may break or may be unreachable.");
                            builder.setPositiveButton("Confirm",
                                    new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            switchEnableFilter.setChecked(true);
                                            SharedPreferences.Editor editor = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE).edit();
                                            editor.putBoolean("switchEnableFilter",true);
                                            editor.apply();
                                            Toast.makeText(getApplicationContext(), "Filtering settings enabled", Toast.LENGTH_LONG).show();
                                            radioSecurityFilter.setEnabled(true);
                                            radioFamilyFilter.setEnabled(true);
                                            radioAdsFilter.setEnabled(true);
                                            radioSecurityFilter.setTextColor(Color.parseColor("#ffffff"));
                                            radioFamilyFilter.setTextColor(Color.parseColor("#ffffff"));
                                            radioAdsFilter.setTextColor(Color.parseColor("#ffffff"));
                                        }
                                    });
                            builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    switchEnableFilter.setChecked(false);

                                }
                            });

                            AlertDialog dialog = builder.create();
                            dialog.show();

                }
                else{
                    Toast.makeText(getApplicationContext(), "Filtering settings disabled", Toast.LENGTH_SHORT).show();
                    SharedPreferences.Editor editor = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE).edit();
                    editor.putBoolean("switchEnableFilter",false);
                    editor.apply();
                    radioSecurityFilter.setEnabled(false);
                    radioFamilyFilter.setEnabled(false);
                    radioAdsFilter.setEnabled(false);
                    radioSecurityFilter.setTextColor(Color.parseColor("#8B8B8B"));
                    radioFamilyFilter.setTextColor(Color.parseColor("#8B8B8B"));
                    radioAdsFilter.setTextColor(Color.parseColor("#8B8B8B"));
                }
            }
        });
        switchEnableVpn.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (switchEnableVpn.isChecked()==true) {
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
                                editor.putBoolean("switchEnableVpn",true);
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
                    editor.putBoolean("switchEnableVpn",false);
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

}
