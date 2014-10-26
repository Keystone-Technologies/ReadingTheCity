package keystone_technologies.com.readingthecity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.webkit.WebView;


public class BeaconInfoActivity extends Activity {

    private WebView webView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_beacon_info);

        Intent intent = getIntent();
        String url = intent.getExtras().getString("url");

        webView = (WebView) findViewById(R.id.webView);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.setWebViewClient(new InfoWebViewClient(this));
        webView.loadUrl(url);
    }
}
