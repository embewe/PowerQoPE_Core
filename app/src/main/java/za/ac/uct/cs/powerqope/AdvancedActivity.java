package za.ac.uct.cs.powerqope;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;


public class AdvancedActivity extends AppCompatActivity {

    LinearLayout back;
    Switch aSwitch;
    RadioButton radioButton, radioButton2, radioButton3;
    TextView textView,textView2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_advanced);
        aSwitch = findViewById(R.id.switch30);
        back = findViewById(R.id.back);
        textView = findViewById(R.id.advancedVpnSettings);
        textView2 = findViewById(R.id.advancedDnsSettings);
        radioButton = findViewById(R.id.radioSecurity);
        radioButton2 = findViewById(R.id.radioFamily);
        radioButton3 = findViewById(R.id.radioAds);
        textView2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), PrivateDNSActivity.class));
            }
        });
        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), VPNSettingsActivity.class));
            }
        });
        aSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (aSwitch.isChecked() == true){
                    Toast.makeText(getApplicationContext(), "Filtering settings enabled", Toast.LENGTH_SHORT).show();
                    radioButton.setEnabled(true);
                    radioButton2.setEnabled(true);
                    radioButton3.setEnabled(true);
                    radioButton.setTextColor(Color.parseColor("#ffffff"));
                    radioButton2.setTextColor(Color.parseColor("#ffffff"));
                    radioButton3.setTextColor(Color.parseColor("#ffffff"));}
                else {
                    Toast.makeText(getApplicationContext(), "Filtering settings disabled", Toast.LENGTH_SHORT).show();

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
