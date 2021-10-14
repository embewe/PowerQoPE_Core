package za.ac.uct.cs.powerqope.fragment;


import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import za.ac.uct.cs.powerqope.R;
import za.ac.uct.cs.powerqope.activity.SpeedTestActivity;
import za.ac.uct.cs.powerqope.adapter.EmployeeAdapter;
import za.ac.uct.cs.powerqope.adapter.VideoAdapter;
import za.ac.uct.cs.powerqope.utils.Employee;
import za.ac.uct.cs.powerqope.utils.Video;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

import static android.content.Context.MODE_PRIVATE;
import static za.ac.uct.cs.powerqope.fragment.SpeedCheckerFragment.DATABASE_NAME;


/**
 * A simple {@link Fragment} subclass.
 */
public class VideoSpeedReportsFragment extends Fragment {
    List<Video> videoList;
    SQLiteDatabase mDatabase;
    ListView listViewVideo;
    VideoAdapter adapter;


    public VideoSpeedReportsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        ((AppCompatActivity)getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        View v = inflater.inflate(R.layout.fragment_video_speed_reports, container, false);
        listViewVideo= v.findViewById(R.id.listViewEmployees);
        videoList = new ArrayList<>();
        //opening the database
        mDatabase = getActivity().openOrCreateDatabase(SpeedTestActivity.DATABASE_NAME, MODE_PRIVATE, null);
        mDatabase = getActivity().openOrCreateDatabase(DATABASE_NAME, MODE_PRIVATE, null);
        mDatabase.execSQL(
                "CREATE TABLE IF NOT EXISTS video (\n" +
                        "    id INTEGER NOT NULL CONSTRAINT video_pk PRIMARY KEY AUTOINCREMENT,\n" +
                        "    buffer varchar(200) NOT NULL,\n" +
                        "    loadtime varchar(200) NOT NULL,\n" +
                        "    bandwidth varchar(200) NOT NULL, \n" +
                        "    date datetime NOT NULL\n" +
                        ");"
        );
        showVideoFromDatabase();
        return v;
    }

    private void showVideoFromDatabase() {
        //we used rawQuery(sql, selectionargs) for fetching all the employees
        Cursor cursorVideos = mDatabase.rawQuery("SELECT * FROM video", null);

        //if the cursor has some data
        if (cursorVideos.moveToFirst()) {
            //looping through all the records
            do {
                //pushing each record in the employee list
                videoList.add(new Video(
                        cursorVideos.getInt(0),
                        cursorVideos.getString(4),
                        cursorVideos.getString(3),
                        cursorVideos.getString(1),
                        cursorVideos.getString(2)
                ));
            } while (cursorVideos.moveToNext());
        }
        //closing the cursor
        cursorVideos.close();

        //creating the adapter object
        adapter = new VideoAdapter(getContext(), R.layout.list_layout_video, videoList, mDatabase);

        //adding the adapter to listview
        listViewVideo.setAdapter(adapter);
    }
}
