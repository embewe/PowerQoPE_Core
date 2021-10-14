package za.ac.uct.cs.powerqope.fragment;


import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;

import androidx.appcompat.widget.SwitchCompat;
import androidx.fragment.app.Fragment;
import za.ac.uct.cs.powerqope.AdvancedActivity;
import za.ac.uct.cs.powerqope.Config;
import za.ac.uct.cs.powerqope.R;
import za.ac.uct.cs.powerqope.util.PhoneUtils;
import za.ac.uct.cs.powerqope.util.WebSocketConnector;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import static android.content.Context.MODE_PRIVATE;

import org.json.JSONException;
import org.json.JSONObject;


/**
 * A simple {@link Fragment} subclass.
 */
public class HomeFragment extends Fragment {


    private static final String TAG = "HomeFragment";

    public HomeFragment() {
        // Required empty public constructor
    }

    LinearLayout connected;
    SwitchCompat switchCompat;
    RadioButton radioButton,radioButton1,radioButton2,radioButton3;

    String MY_PREFS_NAME="preferences";

    String selectedConfig = "default";

    @SuppressLint("ResourceAsColor")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_home, container, false);

        switchCompat = v.findViewById(R.id.switch1);
        connected = v.findViewById(R.id.connected);
        radioButton = v.findViewById(R.id.radioButton);
        radioButton1 = v.findViewById(R.id.radioButton1);
        radioButton2 = v.findViewById(R.id.radioButton2);
        radioButton3 = v.findViewById(R.id.radioButton3);

        SharedPreferences prefs = getActivity().getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE);
        switchCompat.setChecked(prefs.getBoolean("connect",false));
        radioButton.setChecked(prefs.getBoolean("radioButton",false));
        radioButton1.setChecked(prefs.getBoolean("radioButton1",false));
        radioButton2.setChecked(prefs.getBoolean("radioButton2",false));
        radioButton3.setChecked(prefs.getBoolean("radioButton3",false));

        switchCompat.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (radioButton.isChecked()==false && radioButton1.isChecked() == false && radioButton2.isChecked() == false && radioButton3.isChecked()== false){
                    Toast.makeText(getActivity(), "Please select any of the above configurations.", Toast.LENGTH_SHORT).show();
                    switchCompat.setChecked(false);

                }
                else {
                    if (switchCompat.isChecked()) {
                        if(!selectedConfig.equals("advanced")){
                            WebSocketConnector connector = WebSocketConnector.getInstance();
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
                        editor.putBoolean("connect",true);
                        editor.apply();
                    } else {
                        Toast.makeText(getActivity(), "Disconnected", Toast.LENGTH_SHORT).show();
                        connected.setBackgroundColor(Color.parseColor("#292B2B"));
                        SharedPreferences.Editor editor = getActivity().getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE).edit();
                        editor.putBoolean("connect",false);
                        editor.apply();
                    }
                }
            }
        });

        radioButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
        if (radioButton.isChecked()==true){
                    SharedPreferences.Editor editor = getActivity().getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE).edit();
                    editor.putBoolean("radioButton",true);
                    editor.apply();
                Toast.makeText(getActivity(), "High security selected", Toast.LENGTH_SHORT).show();
                selectedConfig = "high";
            }else {
                    SharedPreferences.Editor editor = getActivity().getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE).edit();
                    editor.putBoolean("radioButton",false);
                    editor.apply();
                }}
        });
        radioButton1.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {

                if (radioButton1.isChecked()==true) {
                    SharedPreferences.Editor editor = getActivity().getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE).edit();
                    editor.putBoolean("radioButton1",true);
                    editor.apply();
                    Toast.makeText(getActivity(), "Medium security selected", Toast.LENGTH_SHORT).show();
                    selectedConfig = "medium";
                }else {
                    SharedPreferences.Editor editor = getActivity().getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE).edit();
                    editor.putBoolean("radioButton1",false);
                    editor.apply();
                }
                }
        });
        radioButton2.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {

                if (radioButton2.isChecked()==true) {
                    SharedPreferences.Editor editor = getActivity().getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE).edit();
                    editor.putBoolean("radioButton2", true);
                    editor.apply();
                    Toast.makeText(getActivity(), "Low security selected", Toast.LENGTH_SHORT).show();
                    selectedConfig = "low";
                }else {
                    SharedPreferences.Editor editor = getActivity().getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE).edit();
                    editor.putBoolean("radioButton2", false);
                    editor.apply();
                }
                }
        });
        radioButton3.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {

                if (radioButton3.isChecked()==true) {
                    SharedPreferences.Editor editor = getActivity().getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE).edit();
                    editor.putBoolean("radioButton3", true);
                    editor.apply();
                    Intent intent = new Intent(getContext(), AdvancedActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                    startActivity(intent);
                    Toast.makeText(getActivity(), "Advanced security options selected", Toast.LENGTH_SHORT).show();
                    selectedConfig = "advanced";
                }else {
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

}
