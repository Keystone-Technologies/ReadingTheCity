package keystone_technologies.com.readingthecity;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class InfoWebViewClient extends WebViewClient {
    private Context mContext;
    private ProgressDialog mDialog;

    public InfoWebViewClient(Context context){
        mContext = context;
    }

    @Override
    public void onPageStarted(WebView view, String url, Bitmap favicon) {
        super.onPageStarted(view, url, favicon);
        if (mDialog != null) {
            mDialog.dismiss();
        }
        mDialog = ProgressDialog.show(mContext, "", "Loading...", true);
    }

    @Override
    public void onPageFinished(WebView view, String url) {
        super.onPageFinished(view, url);
        mDialog.dismiss();
    }
}
