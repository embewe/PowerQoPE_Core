package za.ac.uct.cs.powerqope;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.Switch;
import android.widget.Toast;

public class VPNSettingsActivity extends AppCompatActivity {
        LinearLayout back;
    Switch aSwitch;
    RadioButton radioButton, radioButton2, radioButton3;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vpnsettings);
        back = findViewById(R.id.back);
        aSwitch = findViewById(R.id.switch1);
        radioButton = findViewById(R.id.radio1);
        radioButton2 = findViewById(R.id.radio2);
        radioButton3 = findViewById(R.id.radio3);

        aSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (aSwitch.isChecked() == true){
                    Toast.makeText(getApplicationContext(), "Auto Connect enabled", Toast.LENGTH_SHORT).show();
                    radioButton.setEnabled(true);
                    radioButton2.setEnabled(true);
                    radioButton3.setEnabled(true);
                    radioButton.setTextColor(Color.parseColor("#ffffff"));
                    radioButton2.setTextColor(Color.parseColor("#ffffff"));
                    radioButton3.setTextColor(Color.parseColor("#ffffff"));}
                else {
                    Toast.makeText(getApplicationContext(), "Auto Connect disabled", Toast.LENGTH_SHORT).show();

                    radioButton.setEnabled(false);
                    radioButton2.setEnabled(false);
                    radioButton3.setEnabled(false);
                    radioButton.setTextColor(Color.parseColor("#8B8B8B"));
                    radioButton2.setTextColor(Color.parseColor("#8B8B8B"));
                    radioButton3.setTextColor(Color.parseColor("#8B8B8B"));
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
