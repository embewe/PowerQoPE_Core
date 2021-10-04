package za.ac.uct.cs.powerqope.fragment;


import android.os.Bundle;

import androidx.fragment.app.Fragment;
import za.ac.uct.cs.powerqope.R;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


/**
 * A simple {@link Fragment} subclass.
 */
public class WebSpeedReportsFragment extends Fragment {


    public WebSpeedReportsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_web_speed_reports, container, false);
    }

}
