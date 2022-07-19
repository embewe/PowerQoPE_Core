package za.ac.uct.cs.powerqope.fragment;


import static java.lang.Integer.parseInt;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ClipData;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import za.ac.uct.cs.powerqope.R;

import android.view.KeyEvent;
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

/**
 * A simple {@link Fragment} subclass.
 */
public class HTTPTestFragment extends Fragment {

    private static final String TAG = "HTTPTestFragment";

    public HTTPTestFragment() {

    }

    Spinner dropdown;
    private ProgressDialog progDailog;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_httptest, container, false);
        final LinearLayout buttonView= v.findViewById(R.id.button_start);
        final WebView browser =  v.findViewById(R.id.webview);
        final EditText editText = v.findViewById(R.id.editText2);
        dropdown = v.findViewById(R.id.sites);
        String[] advancedOptions = getResources().getStringArray(R.array.webSites);
        //ArrayAdapter adapter = new ArrayAdapter(getActivity(),
        //        R.layout.spinner_item2, advancedOptions);
        //adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        /*editText.setVisibility(View.GONE);
        dropdown.setAdapter(adapter);
        dropdown.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (dropdown.getSelectedItemPosition()==16){
                    editText.setVisibility(View.VISIBLE);
                }
                else if (dropdown.getSelectedItemPosition()==0){
                    editText.setVisibility(View.GONE);
                    browser.setVisibility(View.GONE);
                    buttonView.setVisibility(View.VISIBLE);
                }
                else if (dropdown.getSelectedItemPosition()!=16 && dropdown.getSelectedItemPosition()!=0){
                    String defaultLink = dropdown.getSelectedItem().toString();
                    editText.setVisibility(View.GONE);
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
                            buttonView.setVisibility(View.GONE);
                            browser.setVisibility(View.VISIBLE);
                            progDailog.dismiss();
                        }
                    });
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


}
}
