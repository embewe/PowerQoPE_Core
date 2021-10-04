package za.ac.uct.cs.powerqope.activity;

import android.app.ActionBar;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.preference.SwitchPreference;
import android.view.MenuItem;
import android.widget.Toast;


import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NavUtils;
import za.ac.uct.cs.powerqope.R;

public class SettingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActionBar actionBar = getActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        getFragmentManager().beginTransaction()
                .replace(android.R.id.content, new SettingsFragment())
                .commit();
    }

    public static class SettingsFragment extends PreferenceFragment {

        SharedPreferences dataPref;

        @Override
        public void onStart() {
            super.onStart();
            dataPref = PreferenceManager.getDefaultSharedPreferences(getActivity());
        }

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.preferences);
            SwitchPreference NotificationStatus = (SwitchPreference) findPreference("notification_state");
            NotificationStatus.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference, Object newValue) {
                    boolean isNotificationOn = (boolean) newValue;
                    SharedPreferences.Editor edit = dataPref.edit();
                    edit.putBoolean("notification_state",isNotificationOn);
                    edit.apply();
                    if(isNotificationOn){
                        preference = findPreference("notification_state");
                        preference.setSummary("Notification is Enabled");

                    }else{
                        preference = findPreference("notification_state");
                        preference.setSummary("Notification is Disabled");
                    }
                    return true;
                }
            });

            Preference resetData = findPreference("reset_data");
            resetData.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
     builder.setMessage("All data will be removed!")
                            .setCancelable(false)
                            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    SharedPreferences sp_today = getActivity().getSharedPreferences("todaydata", Context.MODE_PRIVATE);
                                    SharedPreferences sp_month = getActivity().getSharedPreferences("monthdata", Context.MODE_PRIVATE);
                                    SharedPreferences.Editor editor = sp_today.edit();
                                    SharedPreferences.Editor edito2 = sp_month.edit();
                                    editor.clear();
                                    edito2.clear();
                                    editor.apply();
                                    edito2.apply();
                                    Toast.makeText(getActivity(), "Data Removed", Toast.LENGTH_LONG).show();
                                }
                            })
                            .setNegativeButton("No", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    dialog.cancel();
                                }
                            });
                    AlertDialog alert = builder.create();
                    alert.setTitle("Do you want to reset data?");
                    alert.show();
                    return true;

                }
            });
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);

                return true;
        }
        return super.onOptionsItemSelected(item);
    }


}
