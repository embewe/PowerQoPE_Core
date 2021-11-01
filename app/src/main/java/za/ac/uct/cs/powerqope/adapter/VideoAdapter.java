package za.ac.uct.cs.powerqope.adapter;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import za.ac.uct.cs.powerqope.utils.Employee;
import za.ac.uct.cs.powerqope.utils.Video;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

public class VideoAdapter extends ArrayAdapter<Video> {

    Context mCtx;
    int listLayoutRes;
    List<Video> videoList;
    SQLiteDatabase mDatabase;

    public VideoAdapter(Context mCtx, int listLayoutRes, List<Video> videoList, SQLiteDatabase mDatabase) {
        super(mCtx, listLayoutRes, videoList);
        this.mCtx = mCtx;
        this.listLayoutRes = listLayoutRes;
        this.videoList = videoList;
        this.mDatabase = mDatabase;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater inflater = LayoutInflater.from(mCtx);
        View view = inflater.inflate(listLayoutRes, null);

        final Video video = videoList.get(position);
        TextView buffer = view.findViewById(za.ac.uct.cs.powerqope.R.id.buffer);
        //TextView slNo = view.findViewById(R.id.slNo);
        TextView dateSpeedmeter = view.findViewById(za.ac.uct.cs.powerqope.R.id.dateSpeedmeter);
        TextView loadtime = view.findViewById(za.ac.uct.cs.powerqope.R.id.loadtime);
        TextView bandwidth = view.findViewById(za.ac.uct.cs.powerqope.R.id.bandwidth);
        String date = video.getDate();

        //slNo.setText(String.valueOf(employee.getId()));
        dateSpeedmeter.setText(date);
        buffer.setText(video.getBuffer());
        loadtime.setText(video.getLoadtime());
        bandwidth.setText(video.getBandwidth());
        return view;
    }

}