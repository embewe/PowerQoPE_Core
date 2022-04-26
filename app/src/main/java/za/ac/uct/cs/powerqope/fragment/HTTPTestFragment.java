package za.ac.uct.cs.powerqope.fragment;


import static java.lang.Integer.parseInt;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import za.ac.uct.cs.powerqope.Config;
import za.ac.uct.cs.powerqope.MeasurementError;
import za.ac.uct.cs.powerqope.MeasurementResult;
import za.ac.uct.cs.powerqope.MeasurementTask;
import za.ac.uct.cs.powerqope.R;
import za.ac.uct.cs.powerqope.dns.ConfigurationAccess;
import za.ac.uct.cs.powerqope.dns.DNSCommunicator;
import za.ac.uct.cs.powerqope.measurements.HttpTask;
import za.ac.uct.cs.powerqope.util.MeasurementJsonConvertor;
import za.ac.uct.cs.powerqope.util.Util;
import za.ac.uct.cs.powerqope.util.WebSocketConnector;

/**
 * A simple {@link Fragment} subclass.
 */
public class HTTPTestFragment extends Fragment {

    private static final String TAG = "HTTPTestFragment";

    public HTTPTestFragment() {

    }

    Spinner dropdown;
    private ProgressDialog progress;
    TextView httpResults;
    Handler handler;
    LinearLayout buttonView;
    double serverRtt, resolverRtt;
    String cipherLevel;
    String vpnServer;
    Button start;
    //public String[] links = {"https://google.com","https://facebook.com"};


    private static final ConfigurationAccess CONFIG = ConfigurationAccess.getLocal();
    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_httptest, container, false);

        List<String> listOfStrings = new ArrayList<String>();
        try {
            //FileReader fr = new FileReader("sites.txt");
            //listOfStrings = Files.readAllLines(Paths.get("sites"));
            BufferedReader bf = new BufferedReader(
                    new InputStreamReader(getActivity().getAssets().open("sites.txt")));
            String line = bf.readLine();

            // checking for end of file
            while (line != null) {
                listOfStrings.add(line);
                line = bf.readLine();
            }

            // closing bufferreader object
            bf.close();
        } catch (IOException e) {
            Toast toast = Toast.makeText(getActivity(), "File: not found!", Toast.LENGTH_LONG);
            toast.show();
        }


        String[] links = listOfStrings.toArray(new String[0]);


        start = v.findViewById(R.id.buttonStart);

        start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                for (int i = 0; i < links.length; i++) {

                        new ExecutePageLoad().execute(links[i]);

                    }


            }

        });
        SharedPreferences prefs = getActivity().getSharedPreferences("preferences", Context.MODE_PRIVATE);
        try {
            String secLevel = CONFIG.getConfig().getProperty("secLevel", "advanced");
            if(!secLevel.equalsIgnoreCase("advanced"))
                cipherLevel = secLevel;
            else{
                cipherLevel = prefs.getString("advancedCipher", "low");
            }
        } catch (IOException e){
            Log.e(TAG, "onCreateView: "+e);
        }






        vpnServer = prefs.getString("selVpnHost", null);
        handler = new Handler(Looper.getMainLooper());
        httpResults = v.findViewById(R.id.txtHttpResult);
        //final EditText editText = v.findViewById(R.id.editText2);
        //dropdown = v.findViewById(R.id.sites);
        String[] advancedOptions = getResources().getStringArray(R.array.webSites);
        //ArrayAdapter adapter = new ArrayAdapter(getActivity(),
        //        R.layout.spinner_item2, advancedOptions);
        //adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        /*editText.setVisibility(View.GONE);
        dropdown.setAdapter(adapter);
        dropdown.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                //for (i = 0; i < 3; i++) {

                    if (dropdown.getSelectedItemPosition() == 16) {
                        editText.setVisibility(View.VISIBLE);
                    } else if (dropdown.getSelectedItemPosition() == 0) {
                        editText.setVisibility(View.GONE);
                        httpResults.setVisibility(View.GONE);
                        buttonView.setVisibility(View.VISIBLE);
                    } else if (dropdown.getSelectedItemPosition() != 16 && dropdown.getSelectedItemPosition() != 0) {
                        String defaultLink = dropdown.getSelectedItem().toString();
                        editText.setVisibility(View.GONE);
                        progDailog = ProgressDialog.show(getContext(), "Loading", "Please wait...", true);
                        progDailog.setCancelable(false);
                        new ExecutePageLoad().execute(defaultLink);
//                    browser.setWebViewClient(new MyBrowser() {
//                        @Override
//                        public boolean shouldOverrideUrlLoading(WebView view, String url) {
//                            progDailog.show();
//                            view.loadUrl(url);
//
//                            return true;
//                        }
//
//                        @Override
//                        public void onPageFinished(WebView view, final String url) {
//                            buttonView.setVisibility(View.GONE);
//                            browser.setVisibility(View.VISIBLE);
//                            progDailog.dismiss();
//                        }
//                    });
                    }
                }


            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        editText.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (event.getAction() == KeyEvent.ACTION_UP && keyCode == KeyEvent.KEYCODE_ENTER) {
                    String link = editText.getText().toString();
                    if ("https://".equals(link)) {
                        Toast.makeText(getActivity(), "Please input link.", Toast.LENGTH_SHORT).show();
                    } else {
                        progDailog = ProgressDialog.show(getContext(), "Loading", "Please wait...", true);
                        progDailog.setCancelable(false);
                        new ExecutePageLoad().execute(link);
                    }
                    return true;
                }
                return false;
            }
        });
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String link = editText.getText().toString();
                String defaultLink = dropdown.getSelectedItem().toString();
                if ("Custom".equals(defaultLink)){
                    if("https://".equals(link)){
                        Toast.makeText(getActivity(), "Please input link.", Toast.LENGTH_SHORT).show();
                    }
                    else{
                    browser.loadUrl(link);
                    progDailog = ProgressDialog.show(getContext(), "Loading", "Please wait...", true);
                    progDailog.setCancelable(false);
                    browser.setWebViewClient(new MyBrowser() {
                        @Override
                        public boolean shouldOverrideUrlLoading(WebView view, String url) {
                            progDailog.show();
                            view.loadUrl(url);

                            return true;
                        }

                        @Override
                        public void onPageFinished(WebView view, final String url) {
                            button.setVisibility(View.GONE);
                            browser.setVisibility(View.VISIBLE);
                            progDailog.dismiss();
                        }
                    });}
                }

                else{
                browser.loadUrl(defaultLink);
                progDailog = ProgressDialog.show(getContext(), "Loading", "Please wait...", true);
                progDailog.setCancelable(false);
                browser.setWebViewClient(new MyBrowser() {
                    @Override
                    public boolean shouldOverrideUrlLoading(WebView view, String url) {
                        progDailog.show();
                        view.loadUrl(url);

                        return true;
                    }

                    @Override
                    public void onPageFinished(WebView view, final String url) {
                        button.setVisibility(View.GONE);
                        browser.setVisibility(View.VISIBLE);
                        progDailog.dismiss();
                    }
                });
            }
            }

        });*/


        return v;
    }

    private class MyBrowser extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            view.loadUrl(url);
            return true;
        }
    }

    class ExecutePageLoad extends AsyncTask<String, Void, MeasurementResult> {

        private String url;
        int position = 0;

        @RequiresApi(api = Build.VERSION_CODES.O)
        protected MeasurementResult doInBackground(String... urls ) {
            try {
                /*getActivity().runOnUiThread(new Runnable() {
                    public void run() {
                        progDailog = ProgressDialog.show(getContext(), "Testing " + url, "Please wait...", true);
                        progDailog.setCancelable(false);
                    }
                });*/
                List<String> listOfStrings = new ArrayList<String>();
                try {
                    //FileReader fr = new FileReader("sites.txt");
                    //listOfStrings = Files.readAllLines(Paths.get("sites"));
                    BufferedReader bf = new BufferedReader(
                            new InputStreamReader(getActivity().getAssets().open("sites.txt")));
                    String line = bf.readLine();

                    // checking for end of file
                    while (line != null) {
                        listOfStrings.add(line);
                        line = bf.readLine();
                    }

                    // closing bufferreader object
                    bf.close();
                } catch (IOException e) {
                    Toast toast = Toast.makeText(getActivity(), "File: not found!", Toast.LENGTH_LONG);
                    toast.show();
                }



                String[] link = listOfStrings.toArray(new String[0]);

                getActivity().runOnUiThread(new Runnable() {
                    public void run() {
                        position = Arrays.asList(link).indexOf(url);
                        progress = new ProgressDialog(getActivity());
                        progress.setMessage("Testing " + url);
                        progress.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
                        progress.setIndeterminate(false);
                        progress.setProgress(0);
                        progress.show();
                        progress.setProgress(((position) * 100)/(link.length-1));
                        progress.setOnCancelListener(new DialogInterface.OnCancelListener() {
                            @Override
                            public void onCancel(DialogInterface dialog) {

                                new AlertDialog.Builder(getContext())
                                        .setTitle("Really Exit?")
                                        .setMessage("Are you sure you want to exit?")
                                        .setNegativeButton(android.R.string.no, null)
                                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {

                                            public void onClick(DialogInterface arg0, int arg1) {
                                                progress.dismiss();
                                            }
                                        }).create().show();


                            }
                        });
                    }});

                PingTest pingTest = new PingTest();
                url = urls[0];
                try {
                    String hostname = new URL(url).getHost();
                    serverRtt = pingTest.execute(InetAddress.getByName(hostname).getHostAddress());
                    String dnsServer = DNSCommunicator.getInstance().
                            getLastDNSAddress().split("::")[0];
                    String ipAddress = dnsServer.substring(1, dnsServer.length() - 1);
                    resolverRtt = pingTest.execute(ipAddress);
                } catch (UnknownHostException e){
                    Log.e(TAG,"Unknown host : "+url);
                } catch (MalformedURLException e){
                    Log.e(TAG,"Invalid URL : "+url);
                }
                Map<String, String> params = new HashMap<>();
                params.put("url", url);
                params.put("method", "get");
                HttpTask.HttpDesc desc = new HttpTask.HttpDesc("USER_INITIATED",
                        Calendar.getInstance().getTime(),
                        null,
                        Config.DEFAULT_USER_MEASUREMENT_INTERVAL_SEC,
                        Config.DEFAULT_USER_MEASUREMENT_COUNT,
                        MeasurementTask.USER_PRIORITY,
                        params, 1, cipherLevel);
                HttpTask httpTask = new HttpTask(desc, getActivity().getApplicationContext());
                return httpTask.call();
            } catch (MeasurementError e) {


                Log.e(TAG, "doInBackground: "+e);
                return null;
            }
        }

        protected void onPostExecute(MeasurementResult result) {
            start.setVisibility(View.GONE);
            //progDailog.dismiss();
            if(result != null) {
                Map<String, String> values = result.getValues();
                double dnsTime, sslTime, tcpTime, pageLoadTime;
                dnsTime = Double.parseDouble(values.get("dnsEnd")) - Double.parseDouble(values.get("dnsStart"));
                sslTime = Double.parseDouble(values.get("secureConnectEnd")) - Double.parseDouble(values.get("secureConnectStart"));
                tcpTime = Double.parseDouble(values.get("connectEnd")) - Double.parseDouble(values.get("connectStart"));
                pageLoadTime = Double.parseDouble(values.get("callEnd")) - Double.parseDouble(values.get("callStart"));
                String resultTxt = "";
                resultTxt += "DNS time = " + String.format("%.3f", dnsTime) + " sec\n";
                resultTxt += "SSL time = " + String.format("%.3f", sslTime) + " sec\n";
                resultTxt += "TCP time = " + String.format("%.3f", tcpTime) + " sec\n";
                resultTxt += "Page load time = " + String.format("%.3f", pageLoadTime) + " sec\n";
                httpResults.setText(resultTxt);
                httpResults.setVisibility(View.VISIBLE);
                result.getValues().clear();
                try {
                    result.addResult("cipherLevel", cipherLevel);
                    result.addResult("secLevel", CONFIG.getConfig().getProperty("secLevel"));
                    result.addResult("filter", CONFIG.getConfig().getProperty("fallbackDNS"));
                    result.addResult("filterProvider", CONFIG.getConfig().getProperty("filterProvider"));
                    result.addResult("vpnServer", (vpnServer == null ? "VPN_OFF" : vpnServer));
                    result.addResult("dnsTime", dnsTime);
                    result.addResult("sslTime", sslTime);
                    result.addResult("tcpTime", tcpTime);
                    result.addResult("pageLoadTime", pageLoadTime);
                    result.addResult("rttPage", serverRtt);
                    result.addResult("rttResolver", resolverRtt);
                } catch (IOException e){
                    Log.e(TAG, e.getMessage());
                }
                String resultJsonString = MeasurementJsonConvertor.toJsonString(result);
                WebSocketConnector.getInstance().sendMessage(Config.STOMP_SERVER_JOB_RESULT_ENDPOINT, resultJsonString);
            } else{
                Toast.makeText(getActivity().getApplicationContext(), "Connection failed", Toast.LENGTH_LONG).show();
            }
        }
    }

    class PingTest {
        public double execute(String ip) {
            String pingCmd = "ping -c 5 "+ip;
            try {
                Runtime r = Runtime.getRuntime();
                Process p = r.exec(pingCmd);

                BufferedReader in = new BufferedReader(new
                        InputStreamReader(p.getInputStream()));
                String inputLine;
                ArrayList<Double> rtts = new ArrayList<Double>();
                ArrayList<Integer> receivedIcmpSeq = new ArrayList<Integer>();
                while ((inputLine = in.readLine()) != null) {
                    Log.i(TAG, inputLine);
                    String[] extractedValues = Util.extractInfoFromPingOutput(inputLine);
                    if (extractedValues != null) {
                        int curIcmpSeq = parseInt(extractedValues[0]);
                        double rrtVal = Double.parseDouble(extractedValues[1]);
                        // ICMP responses from the system ping command could be duplicate and out of order
                        if (!receivedIcmpSeq.contains(curIcmpSeq)) {
                            rtts.add(rrtVal);
                            receivedIcmpSeq.add(curIcmpSeq);
                        }
                    }
                }
                in.close();
                return Util.median(rtts);
            } catch (IOException e) {
                Log.e(TAG, "Error while running ping");
            }
            return -1.0;
        }
    }
    public void onBackPressed() {
        new AlertDialog.Builder(getContext())
                .setTitle("Really Exit?")
                .setMessage("Are you sure you want to exit?")
                .setNegativeButton(android.R.string.no, null)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface arg0, int arg1) {

                    }
                }).create().show();
    }
}
