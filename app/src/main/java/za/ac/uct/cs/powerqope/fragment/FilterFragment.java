package za.ac.uct.cs.powerqope.fragment;


import android.graphics.Color;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import za.ac.uct.cs.powerqope.R;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.RadioButton;
import android.widget.Switch;
import android.widget.Toast;

/**
 * A simple {@link Fragment} subclass.
 */
public class FilterFragment extends Fragment {


    public FilterFragment() {
        // Required empty public constructor
    }
    Switch aSwitch;
    RadioButton radioButton, radioButton2, radioButton3, radioButton4;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
       View v = inflater.inflate(R.layout.fragment_filter, container, false);
        aSwitch = v.findViewById(R.id.switch30);
        radioButton = v.findViewById(R.id.radioSecurity);
        radioButton2 = v.findViewById(R.id.radioFamily);
        radioButton3 = v.findViewById(R.id.radioAds);
        radioButton4 = v.findViewById(R.id.radioAdult);
       aSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (aSwitch.isChecked() == true) {
                    Toast.makeText(getContext(), "Filtering settings enabled", Toast.LENGTH_SHORT).show();
                    radioButton.setEnabled(true);
                    radioButton2.setEnabled(true);
                    radioButton3.setEnabled(true);
                    radioButton4.setEnabled(true);
                    radioButton.setTextColor(Color.parseColor("#ffffff"));
                    radioButton2.setTextColor(Color.parseColor("#ffffff"));
                    radioButton3.setTextColor(Color.parseColor("#ffffff"));
                    radioButton4.setTextColor(Color.parseColor("#ffffff"));
                } else {
                    Toast.makeText(getContext(), "Filtering settings disabled", Toast.LENGTH_SHORT).show();

                    radioButton.setEnabled(false);
                    radioButton2.setEnabled(false);
                    radioButton3.setEnabled(false);
                    radioButton4.setEnabled(false);
                    radioButton.setTextColor(Color.parseColor("#8B8B8B"));
                    radioButton2.setTextColor(Color.parseColor("#8B8B8B"));
                    radioButton3.setTextColor(Color.parseColor("#8B8B8B"));
                    radioButton4.setTextColor(Color.parseColor("#8B8B8B"));

                }
            }
        });
        return v;
    }

}
