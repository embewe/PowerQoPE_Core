package za.ac.uct.cs.powerqope.fragment;


import android.app.ProgressDialog;
import android.content.ClipData;
import android.content.Context;
import android.content.SharedPreferences;
import android.icu.util.Measure;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

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

import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.util.Xml;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.net.URLEncoder;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * A simple {@link Fragment} subclass.
 */
public class HTTPTestFragment extends Fragment {


    public HTTPTestFragment() {
        // Required empty public constructor
    }
    Spinner dropdown;
    private ProgressDialog progDailog;
    TextView httpResults;
    Handler handler;
    LinearLayout buttonView;
    double serverRtt, resolverRtt;
    String cipherLevel;
    String vpnServer;
    private static ConfigurationAccess CONFIG = ConfigurationAccess.getLocal();
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_httptest, container, false);
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
        buttonView = v.findViewById(R.id.button_start);
        httpResults = v.findViewById(R.id.txtHttpResult);
        final EditText editText = v.findViewById(R.id.editText2);
        dropdown = v.findViewById(R.id.sites);
        String[] advancedOptions = getResources().getStringArray(R.array.webSites);
        ArrayAdapter adapter = new ArrayAdapter(getActivity(),
                R.layout.spinner_item2, advancedOptions);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        editText.setVisibility(View.GONE);
        dropdown.setAdapter(adapter);
        WebSettings webSettings = browser.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setDomStorageEnabled(true);
        browser.getSettings().setLoadWithOverviewMode(true);
        browser.getSettings().setUseWideViewPort(true);
        dropdown.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
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
                                buttonView.setVisibility(View.GONE);
                                browser.setVisibility(View.VISIBLE);
                                progDailog.dismiss();
                            }
                        });}
                    return true;
                }
                return false;
            }
        });
      /*  button.setOnClickListener(new View.OnClickListener() {
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

    class ExecutePageLoad extends AsyncTask<String, Void, MeasurementResult> {

        private String url;

        protected MeasurementResult doInBackground(String... urls) {
            try {
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
                progDailog.show();
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
            buttonView.setVisibility(View.GONE);
            progDailog.dismiss();
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
                Toast.makeText(getActivity().getApplicationContext(), "Could not connect!", Toast.LENGTH_LONG).show();
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
                        int curIcmpSeq = Integer.parseInt(extractedValues[0]);
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

}
