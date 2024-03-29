package za.ac.uct.cs.powerqope.fragment;



import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


import org.json.JSONObject;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import za.ac.uct.cs.powerqope.R;
import za.ac.uct.cs.powerqope.adapter.GraphAdapter;
import za.ac.uct.cs.powerqope.service.DataService;
import za.ac.uct.cs.powerqope.utils.DataInfo;

public class MonthFragment extends Fragment {

    private final SimpleDateFormat SDF = new SimpleDateFormat("MMM dd, yyyy");
    private final DecimalFormat df = new DecimalFormat("#.##");
    final static String MEGABYTE = " MB", GIGABYTE = " GB";
    private Handler vHandler = new Handler();
    private Thread dataUpdate;
    private TextView wTotal, mTotal, tTotal;
    private double total_wifi;
    private double total_mobile;
    private double today_wifi = 0;
    private double today_mobile = 0;
    private GraphAdapter dataAdapter;
    private RecyclerView recList;
    private String today_date = null;
    List<DataInfo> monthData;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_month, container, false);


        wTotal = (TextView) rootView.findViewById(R.id.id_wifi);
        mTotal = (TextView) rootView.findViewById(R.id.id_mobile);
        tTotal = (TextView) rootView.findViewById(R.id.id_total);
        recList = (RecyclerView) rootView.findViewById(R.id.cardList);
        recList.setHasFixedSize(true);
        LinearLayoutManager llm = new LinearLayoutManager(getActivity());
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        recList.setLayoutManager(llm);
        recList.getItemAnimator().setChangeDuration(0);
        monthData = createList(30);
        dataAdapter = new GraphAdapter(monthData);
        recList.setAdapter(dataAdapter);
        totalData();
        clearExtraData();
        liveData();
        return rootView;

    }


    public void liveData() {
        dataUpdate = new Thread(new Runnable() {
            @Override
            public void run() {
                while (!dataUpdate.getName().equals("stopped")) {
                    Calendar ca = Calendar.getInstance();
                    final String temp_today = SDF.format(ca.getTime());// get today's date
                    vHandler.post(new Runnable() {

                        @Override
                        public void run() {
                            if (temp_today.equals(today_date)) {
                                monthData.set(0, todayData());
                                dataAdapter.notifyItemChanged(0);
                            } else {
                                today_wifi = 0;
                                today_mobile = 0;
                                monthData = createList(30);  // to update total month data
                                dataAdapter.dataList = monthData;  //update adapter list
                                dataAdapter.notifyDataSetChanged();
                                monthData.set(0, todayData());
                                dataAdapter.notifyItemChanged(0);
                            }
                            totalData();
                        }
                    });

                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        dataUpdate.setName("started");
        dataUpdate.start();
    }

    public List<DataInfo> createList(int size) {

        List<DataInfo> result = new ArrayList<>();
        total_wifi = 0;
        total_mobile = 0;
        double wTemp, mTemp, tTemp;
        String wifi = "0", mobile = "0", total = "0";
        SharedPreferences sp_month = getActivity().getSharedPreferences("monthdata", Context.MODE_PRIVATE);
        for (int i = 1; i <= size; i++) {
            if (i == 1) {
                result.add(todayData());
                continue;
            }
            Calendar calendar = Calendar.getInstance();
            calendar.add(Calendar.DATE, (1 - i));
            String mDate = SDF.format(calendar.getTime());
            List<String> allData = new ArrayList<>();
            if (sp_month.contains(mDate)) {
                String sData = sp_month.getString(mDate, null);
                try {
                    JSONObject jOb = new JSONObject(sData);
                    wifi = jOb.getString("WIFI_DATA");
                    mobile = jOb.getString("MOBILE_DATA");
                    wTemp = (Long.parseLong(wifi) / 1048576.0);
                    mTemp = (Long.parseLong(mobile) / 1048576.0);
                    tTemp = wTemp + mTemp;
                    allData = dataFormate(wTemp, mTemp, tTemp);
                    total_wifi += wTemp;
                    total_mobile += mTemp;
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                allData = dataFormate(0, 0, 0);
            }
            DataInfo dataInfo = new DataInfo();
            dataInfo.date = mDate;
            dataInfo.wifi = allData.get(0);
            dataInfo.mobile = allData.get(1);
            dataInfo.total = allData.get(2);
            result.add(dataInfo);
        }
        return result;
    }

    public DataInfo todayData() {

        List<DataInfo> listToday = new ArrayList<>();
        Calendar ca = Calendar.getInstance();
        today_date = SDF.format(ca.getTime());
        double wTemp = 0, mTemp = 0, tTemp = 0;
        try {
            SharedPreferences sp = getActivity().getSharedPreferences("todaydata", Context.MODE_PRIVATE);
            wTemp = sp.getLong("WIFI_DATA", 0) / 1048576.0;
            mTemp = sp.getLong("MOBILE_DATA", 0) / 1048576.0;
        } catch (Exception e) {
            e.printStackTrace();
        }
        tTemp = wTemp + mTemp;
        List<String> allData = dataFormate(wTemp, mTemp, tTemp);
        total_wifi = total_wifi + (wTemp - today_wifi);
        total_mobile = total_mobile + (mTemp - today_mobile);
        today_wifi = wTemp;
        today_mobile = mTemp;
        DataInfo dataInfo = new DataInfo();
        dataInfo.date = "Today";
        dataInfo.wifi = allData.get(0);
        dataInfo.mobile = allData.get(1);
        dataInfo.total = allData.get(2);
        listToday.add(dataInfo);
        return dataInfo;

    }

    public void totalData() {
        List<String> total = dataFormate(total_wifi, total_mobile, total_wifi + total_mobile);
        wTotal.setText(total.get(0));
        mTotal.setText(total.get(1));
        tTotal.setText(total.get(2));
    }

    public List<String> dataFormate(double wifi, double mobile, double total) {
        List<String> allData = new ArrayList<>();
        if (wifi < 1024) {
            allData.add(df.format(wifi) + MEGABYTE); // consider 2 value after decimal point
        } else {
            allData.add(df.format(wifi / 1024) + GIGABYTE);
        }

        if (mobile < 1024) {
            allData.add(df.format(mobile) + MEGABYTE); // consider 2 value after decimal point
        } else {
            allData.add(df.format(mobile / 1024) + GIGABYTE);
        }

        if (total < 1024) {
            allData.add(df.format(total) + MEGABYTE); // consider 2 value after decimal point
        } else {
            allData.add(df.format(total / 1024) + GIGABYTE);
        }
        return allData;
    }

    void clearExtraData() {
        SharedPreferences sp_month = getActivity().getSharedPreferences("monthdata", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp_month.edit();
        for (int i = 40; i <= 1000; i++) {
            Calendar calendar = Calendar.getInstance();
            calendar.add(Calendar.DATE, (1 - i));
            String mDate = SDF.format(calendar.getTime());// get  date

            if (sp_month.contains(mDate)) {
                editor.remove(mDate);

            }
        }
        editor.apply();
    }

    @Override
    public void onPause() {
        super.onPause();
        dataUpdate.setName("stopped");
    }


    @Override
    public void onResume() {
        super.onResume();
        DataService.notification_status = true;
        dataUpdate.setName("started");
        if (!dataUpdate.isAlive()) {
            liveData();

        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onStop() {
        super.onStop();
    }


}
