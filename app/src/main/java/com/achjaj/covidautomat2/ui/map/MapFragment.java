package com.achjaj.covidautomat2.ui.map;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.*;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import com.achjaj.covidautomat2.R;
import com.achjaj.covidautomat2.Utils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.IOException;

public class MapFragment extends Fragment implements AdapterView.OnItemSelectedListener {
    private final String[] mapIds = {"#aktualny-sk", "#buduci-sk"};

    private WebView mapView;
    private ProgressBar progressBar;
    private TextView noMap;
    private Client client;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_map, container, false);

        progressBar = root.findViewById(R.id.progressBar);
        noMap = root.findViewById(R.id.noMap);

        Spinner spinner = root.findViewById(R.id.map_spinner);
        spinner.setOnItemSelectedListener(this);

        client = new Client(progressBar, getActivity());
        mapView = root.findViewById(R.id.map_view);
        mapView.setWebViewClient(client);
        mapView.getSettings().setBuiltInZoomControls(true);
        mapView.getSettings().setJavaScriptEnabled(true);

        return root;
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        noMap.setVisibility(View.GONE);
        progressBar.setVisibility(View.VISIBLE);
        mapView.setVisibility(View.INVISIBLE);

        new Thread(() -> {
            try {
                Document doc = Jsoup.connect("https://korona.gov.sk/").get();
                Element iframe = doc.selectFirst(mapIds[position]).selectFirst("iframe");

                if (iframe == null) {
                    this.getActivity().runOnUiThread(() -> {
                        progressBar.setVisibility(View.GONE);
                        noMap.setVisibility(View.VISIBLE);
                    });
                } else {
                    String src = iframe.attr("src");

                    this.getActivity().runOnUiThread(() -> {
                        mapView.setVisibility(View.VISIBLE);
                        client.setNextWeek(position == 1);
                        mapView.loadUrl(src);
                    });
                }
            } catch (IOException e) {
                e.printStackTrace();
                getActivity().runOnUiThread(() ->
                    Utils.showExceptionDialog(getString(R.string.internet_question), e, getContext(), (dialog, which) -> {}));
            }
        }).start();
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    private static class Client extends WebViewClient {

        private final ProgressBar progressBar;
        private final Activity activity;
        private boolean nextWeek = false;

        public Client(ProgressBar progressBar, Activity activity) {
            this.progressBar = progressBar;
            this.activity = activity;
        }

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            super.onPageStarted(view, url, favicon);
            progressBar.setVisibility(View.VISIBLE);
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
            progressBar.setVisibility(View.GONE);
        }

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
            Uri url = request.getUrl();

            if (url.getHost().equals("automat.gov.sk")) {
                Utils.showRegionInfo("regionUrl", url.getLastPathSegment(), activity, nextWeek);
                return true;
            } else if (!url.getHost().contains("dwcdn.net")) {
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(url);
                activity.startActivity(i);

                return true;
            }

            return false;
        }

        public void setNextWeek(boolean nextWeek) {
            this.nextWeek = nextWeek;
        }
    }
}
