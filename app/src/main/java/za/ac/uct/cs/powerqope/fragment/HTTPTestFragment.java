package za.ac.uct.cs.powerqope.fragment;


import android.app.ProgressDialog;
import android.content.ClipData;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import za.ac.uct.cs.powerqope.R;

import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.EditText;

/**
 * A simple {@link Fragment} subclass.
 */
public class HTTPTestFragment extends Fragment {


    public HTTPTestFragment() {
        // Required empty public constructor
    }

    private ProgressDialog progDailog;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_httptest, container, false);
        final Button button = v.findViewById(R.id.buttonStart);
        final WebView browser =  v.findViewById(R.id.webview);
        final EditText editText = v.findViewById(R.id.editText2);
        WebSettings webSettings = browser.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setDomStorageEnabled(true);
        browser.getSettings().setLoadWithOverviewMode(true);
        browser.getSettings().setUseWideViewPort(true);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String link = editText.getText().toString();
                String defaultLink = "https://www.google.com";
                if ("https://".equals(link)){

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
                });
            }
            }

        });







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
