package za.ac.uct.cs.powerqope.fragment;


import static android.content.Context.MODE_PRIVATE;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.Switch;

import androidx.fragment.app.Fragment;

import za.ac.uct.cs.powerqope.R;

public class SettingsFragment extends Fragment {


    String MY_PREFS_NAME = "preferences";
    static SharedPreferences prefs;
    public SettingsFragment() {

    }
    public static Switch costInformation, visuals;



    @Override
public View onCreateView(LayoutInflater inflater, ViewGroup container,
        Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_settings, container, false);
        costInformation = v.findViewById(R.id.switch34);
        visuals = v.findViewById(R.id.switch50);
    SharedPreferences prefs = getActivity().getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE);
    costInformation.setChecked(prefs.getBoolean("cost", true));
    costInformation.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
            if(costInformation.isChecked()==true){
            SharedPreferences.Editor editor = getActivity().getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE).edit();
            editor.putBoolean("cost", true);
            editor.apply();}
            else {
                SharedPreferences.Editor editor = getActivity().getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE).edit();
                editor.putBoolean("cost", false);
                editor.apply();
            }
        }
        }
    );
        visuals.setChecked(prefs.getBoolean("visuals", true));
        visuals.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                                                       @Override
                                                       public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                                                           if(visuals.isChecked()==true){
                                                               SharedPreferences.Editor editor = getActivity().getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE).edit();
                                                               editor.putBoolean("visuals", true);
                                                               editor.apply();}
                                                           else {
                                                               SharedPreferences.Editor editor = getActivity().getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE).edit();
                                                               editor.putBoolean("visuals", false);
                                                               editor.apply();
                                                           }
                                                       }
                                                   }
        );

        return v;
        }
}