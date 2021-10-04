package za.ac.uct.cs.powerqope.fragment;


import android.os.Bundle;

import androidx.fragment.app.Fragment;
import za.ac.uct.cs.powerqope.R;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Spinner;


/**
 * A simple {@link Fragment} subclass.
 */
public class PrivateDNSFragment extends Fragment {
    EditText editText;
    RadioButton radioButton;
    Spinner dropdown;
    public PrivateDNSFragment() {
        // Required empty public constructor
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_private_dn, container, false);
        editText = v.findViewById(R.id.editText);
        radioButton = v.findViewById(R.id.radio2);
        dropdown = v.findViewById(R.id.spinner1);
        String[] advancedOptions = getResources().getStringArray(R.array.advanced_options);
        ArrayAdapter adapter = new ArrayAdapter(getContext(),
                R.layout.spinner_item, advancedOptions);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        dropdown.setEnabled(false);
        dropdown.setAdapter(adapter);
        radioButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (radioButton.isChecked()== true){
                    dropdown.setEnabled(true);
                    dropdown.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                            if (dropdown.getSelectedItemPosition()==11){
                                editText.setEnabled(true);
                            }
                            else if (dropdown.getSelectedItemPosition()!=11){
                                editText.setEnabled(false);
                            }
                        }

                        @Override
                        public void onNothingSelected(AdapterView<?> adapterView) {

                        }
                    });


                }
                else{
                    dropdown.setEnabled(false);
                    editText.setEnabled(false);
                }
            }
        });
        return v; }

}
