package za.ac.uct.cs.powerqope.fragment;


import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;

import androidx.appcompat.widget.SwitchCompat;
import androidx.fragment.app.Fragment;
import za.ac.uct.cs.powerqope.AdvancedActivity;
import za.ac.uct.cs.powerqope.R;

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


/**
 * A simple {@link Fragment} subclass.
 */
public class HomeFragment extends Fragment {


    public HomeFragment() {
        // Required empty public constructor
    }

    LinearLayout connected;
    SwitchCompat switchCompat;
    RadioButton radioButton,radioButton1,radioButton2,radioButton3;
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

        switchCompat.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (radioButton.isChecked()==false && radioButton1.isChecked() == false && radioButton2.isChecked() == false && radioButton3.isChecked()== false){
                    Toast.makeText(getActivity(), "Please select any of the above configurations.", Toast.LENGTH_SHORT).show();
                    switchCompat.setChecked(false);

                }
                else {
                    if (switchCompat.isChecked()) {
                        Toast.makeText(getActivity(), "Connected", Toast.LENGTH_SHORT).show();
                        connected.setBackgroundColor(Color.parseColor("#43A047"));
                    } else {
                        Toast.makeText(getActivity(), "Disconnected", Toast.LENGTH_SHORT).show();
                        connected.setBackgroundColor(Color.parseColor("#292B2B"));

                    }
                }
            }
        });

        radioButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getActivity(), "High security selected", Toast.LENGTH_SHORT).show();
            }
        });
        radioButton1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               Toast.makeText(getActivity(), "Medium security selected", Toast.LENGTH_SHORT).show();
            }
        });
        radioButton2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               Toast.makeText(getActivity(), "Low security selected", Toast.LENGTH_SHORT).show();
            }
        });
        radioButton3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getContext(), AdvancedActivity.class));
                Toast.makeText(getActivity(), "Advanced security options selected", Toast.LENGTH_SHORT).show();
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
