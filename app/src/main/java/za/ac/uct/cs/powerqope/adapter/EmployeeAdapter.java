package za.ac.uct.cs.powerqope.adapter;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;


import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import za.ac.uct.cs.powerqope.R;
import za.ac.uct.cs.powerqope.utils.Employee;

public class EmployeeAdapter extends ArrayAdapter<Employee> {

    Context mCtx;
    int listLayoutRes;
    List<Employee> employeeList;
    SQLiteDatabase mDatabase;

    public EmployeeAdapter(Context mCtx, int listLayoutRes, List<Employee> employeeList, SQLiteDatabase mDatabase) {
        super(mCtx, listLayoutRes, employeeList);
        this.mCtx = mCtx;
        this.listLayoutRes = listLayoutRes;
        this.employeeList = employeeList;
        this.mDatabase = mDatabase;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater inflater = LayoutInflater.from(mCtx);
        View view = inflater.inflate(listLayoutRes, null);

        final Employee employee = employeeList.get(position);
        TextView downloadSpeedmeter = view.findViewById(R.id.downloadSpeedmeter);
        //TextView slNo = view.findViewById(R.id.slNo);
        TextView dateSpeedmeter = view.findViewById(R.id.dateSpeedmeter);
        TextView uploadSpeedmeter = view.findViewById(R.id.uploadSpeedmeter);
        String date = employee.getDate();
        downloadSpeedmeter.setText(employee.getDownload());
        //slNo.setText(String.valueOf(employee.getId()));
        dateSpeedmeter.setText(date);
        uploadSpeedmeter.setText(employee.getUpload());
        return view;
    }

}
