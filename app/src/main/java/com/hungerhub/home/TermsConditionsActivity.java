package com.hungerhub.home;


import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.net.http.SslError;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.webkit.SslErrorHandler;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.TextView;

import com.chillarcards.cookery.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import com.hungerhub.application.Constants;
import com.hungerhub.application.CustomConnectionBuddyActivity;
import com.hungerhub.application.PrefManager;

public class TermsConditionsActivity extends CustomConnectionBuddyActivity {

    @BindView(R.id.webView)
    WebView webView;
    @BindView(R.id.HeaderTV)
    TextView HeaderText;
    @BindView(R.id.BackIV)
    ImageView BackButton;

    PrefManager prefManager;
    String url = "";

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().requestFeature(Window.FEATURE_PROGRESS);
        setContentView(R.layout.activity_payment_xpay_webview);
        getWindow().setFeatureInt( Window.FEATURE_PROGRESS, Window.PROGRESS_VISIBILITY_ON);
        ButterKnife.bind(this);

        HeaderText.setText("Terms and Conditions");
        BackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        prefManager = new PrefManager(this);
        String phoneNo = prefManager.getUserPhone();
        String studentID = prefManager.getStudentId();

//        Bundle b= getIntent().getExtras();
//        String type = b.getString("trans_category");

        url = Constants.BASE_URL+"r_terms_and_conditions.php";
        //System.out.println("URL Webview: "+url);

        webView.setWebChromeClient(new WebChromeClient() {
            public void onProgressChanged(WebView view, int progress)
            {
                //Make the bar disappear after URL is loaded, and changes string to Loading...
                setTitle("Loading...");
                setProgress(progress * 100); //Make the bar disappear after URL is loaded

                // Return the app name after finish loading
                if(progress == 100)
                    setTitle(R.string.app_name);
            }
        });
        webView.setWebViewClient(new TermsConditionsActivity.myWebClient());
        webView.getSettings().setJavaScriptEnabled(true);
        webView.loadUrl(url);



    }


    public class myWebClient extends WebViewClient
    {
        public void onReceivedSslError(WebView view, final SslErrorHandler handler, SslError error) {

            AlertDialog.Builder builder = new AlertDialog.Builder(TermsConditionsActivity.this);
            AlertDialog alertDialog = builder.create();
            String message = getResources().getString(R.string.ssl_certificate_error);
            switch (error.getPrimaryError()) {
                case SslError.SSL_UNTRUSTED:
                    message = getResources().getString(R.string.certificate_authority_not_trusted);
                    break;
                case SslError.SSL_EXPIRED:
                    message = getResources().getString(R.string.certificate_expired);
                    break;
                case SslError.SSL_IDMISMATCH:
                    message =getResources().getString(R.string.certificate_hostname_mismatch);

                    break;
                case SslError.SSL_NOTYETVALID:
                    message =getResources().getString(R.string.certificate_not_valid);
                    break;
            }

            message += getResources().getString(R.string.do_you_want_to_continue);
            alertDialog.setTitle(getResources().getString(R.string.ssl_certificate_error));
            alertDialog.setMessage(message);
            alertDialog.setButton(DialogInterface.BUTTON_POSITIVE, getResources().getString(R.string.ok), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    // Ignore SSL certificate errors
                    handler.proceed();
                }
            });

            alertDialog.setButton(DialogInterface.BUTTON_NEGATIVE, getResources().getString(R.string.cancel), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                    handler.cancel();
                }
            });
            try {
                alertDialog.show();
            }catch (Exception e){

            }

        }

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            // TODO Auto-generated method stub
            super.onPageStarted(view, url, favicon);
        }

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            // TODO Auto-generated method stub

            view.loadUrl(url);
            return true;

        }
    }

    // To handle &quot;Back&quot; key press event for WebView to go back to previous screen.
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event)
    {
        if ((keyCode == KeyEvent.KEYCODE_BACK) && webView.canGoBack()) {
            webView.goBack();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}
