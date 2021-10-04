package za.ac.uct.cs.powerqope;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Spinner;

public class PrivateDNSActivity extends AppCompatActivity {
    EditText editText;
    RadioButton radioButton;
    Spinner dropdown;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_private_dns2);
        editText = findViewById(R.id.editText);
        radioButton = findViewById(R.id.radio2);
        dropdown = findViewById(R.id.spinner1);
        String[] advancedOptions = getResources().getStringArray(R.array.advanced_options);
        ArrayAdapter adapter = new ArrayAdapter(this,
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
    }
}
