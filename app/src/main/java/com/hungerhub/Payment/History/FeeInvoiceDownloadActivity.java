package com.hungerhub.Payment.History;

import android.Manifest;
import android.app.Dialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.http.SslError;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import androidx.core.app.ActivityCompat;
import androidx.appcompat.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.webkit.SslErrorHandler;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.chillarcards.cookery.R;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import butterknife.BindView;
import butterknife.ButterKnife;
import com.hungerhub.application.Constants;
import com.hungerhub.application.CustomConnectionBuddyActivity;
import com.hungerhub.application.PrefManager;

public class FeeInvoiceDownloadActivity extends CustomConnectionBuddyActivity {

    private static final String TAG = "FeeReceiptActivity";
    WebView ReceiptWV;
    String TransID,TransKey;
    int TabPos;
    String parentPh,s_Id;
    FloatingActionButton downloadBTN;
    Dialog OtpDialog;
    PrefManager prefManager;
    @BindView(R.id.HeaderTV) TextView HeaderText;
    @BindView(R.id.BackIV)
    ImageView BackButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().requestFeature(Window.FEATURE_PROGRESS);
        setContentView(R.layout.activity_invoice_download);
        ButterKnife.bind(this);

        HeaderText.setText(getResources().getString(R.string.invoice_header));
        BackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i=new Intent(FeeInvoiceDownloadActivity.this,PaymentHistoryActivity.class);
                i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                i.addFlags(Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT);
                Bundle bundle1=new Bundle();
                bundle1.putInt("tabpos",TabPos);
                i.putExtras(bundle1);
                startActivity(i);
                finish();
            }
        });

        prefManager=new PrefManager(FeeInvoiceDownloadActivity.this);
        ReceiptWV=findViewById(R.id.receiptWV);
        downloadBTN=findViewById(R.id.downloadBTN);

        Bundle bundle=getIntent().getExtras();
        if (bundle!=null) {
            TransID = bundle.getString("TransID");
            TransKey = bundle.getString("key");
            TabPos = bundle.getInt("tabpos");
            //System.out.println("AA InvoiceDownloadActivity ::TransID : " + TransID+ " : Key : "+TransKey);
        }
        parentPh = prefManager.getUserPhone();
        s_Id= prefManager.getStudentId();

        String url=Constants.BASE_URL+"/r_fee_payment_reciept.php?" +
                "phoneNo=" +parentPh+
                "&studentID=" +s_Id+
                "&payment_type="+TransKey+
                "&payment_id="+TransID;
        //System.out.println("Receipt URL : " +Constants.BASE_URL+"/r_fee_payment_reciept.php?" +
//                "phoneNo=" +parentPh+
//                "&studentID=" +s_Id+
//                "&payment_type="+TransKey+
//                "&payment_id="+TransID);
        ReceiptWV.setWebViewClient(new myWebClient());
        ReceiptWV.loadUrl(url);
        ReceiptWV.getSettings().setJavaScriptEnabled(true);


//        ReceiptWV.getSettings().setJavaScriptEnabled(true);

//        final Activity activity = this;
//        ReceiptWV.setWebChromeClient(new WebChromeClient() {
//            public void onProgressChanged(WebView view, int progress) {
//                // Activities and WebViews measure progress with different scales.
//                // The progress meter will automatically disappear when we reach 100%
//                activity.setProgress(progress * 1000);
//            }
//        });
//        ReceiptWV.setWebViewClient(new WebViewClient() {
//            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
//                Toast.makeText(activity, "Oh no! " + description, Toast.LENGTH_SHORT).show();
//            }
//        });
//
//        ReceiptWV.loadUrl(Constants.BASE_URL+"/r_online_transaction_reciept.php?" +
//                "phoneNo=" +parentPh+
//                "&studentID=" +s_Id+
//                "&onlineTransactionID="+TransID);

        downloadBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isStoragePermissionGranted())
                {
                    mFirebaseAnalytics.logEvent(getResources().getString(R.string.Invoice_Download_Button_Clicked),new Bundle());
                    Bitmap bitmap = takeScreenshot();
                    saveBitmap(bitmap);
                }


            }
        });


       // shareIt();

    }
    public class myWebClient extends WebViewClient
    {
        public void onReceivedSslError(WebView view, final SslErrorHandler handler, SslError error) {

            AlertDialog.Builder builder = new AlertDialog.Builder(FeeInvoiceDownloadActivity.this);
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

    public Bitmap takeScreenshot() {
        View rootView = findViewById(R.id.receiptWV);
        rootView.setDrawingCacheEnabled(true);
        return rootView.getDrawingCache();
    }

    public void saveBitmap(Bitmap bitmap) {
        File imageFolder = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/Receipts");
        if (! imageFolder.exists())
        {
            imageFolder.mkdir();
        }

       File imagePath = new File(imageFolder.getAbsolutePath() +"/"+TransID+"_screenshot.jpg");
        FileOutputStream fos;
        try {
            fos = new FileOutputStream(imagePath);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
            fos.flush();
            fos.close();
            Custom_Dialogue();
            addImageToGallery(imagePath.getAbsolutePath(),getApplicationContext());
        } catch (FileNotFoundException e) {
            Log.e("GREC", e.getMessage(), e);
        } catch (IOException e) {
            Log.e("GREC", e.getMessage(), e);
        }
    }
    public void Custom_Dialogue()
    {
        OtpDialog = new Dialog(this);
        OtpDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        OtpDialog.setContentView(R.layout.download_receipt_layout);
        OtpDialog.setCanceledOnTouchOutside(false);
        OtpDialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        Button Ok = (Button) OtpDialog.findViewById(R.id.buttonChangeOK);

        TextView ContentText = (TextView) OtpDialog.findViewById(R.id.Tv);

        TextView title1 = (TextView) OtpDialog.findViewById(R.id.Title);

        try {
            OtpDialog.show();
        }catch (Exception e){

        }
       // ContentText.setText(Content);


        Ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Intent i=new Intent(HistoryReceipt.this,StudentList.class);
//                startActivity(i);
                Intent i=new Intent(FeeInvoiceDownloadActivity.this,PaymentHistoryActivity.class);
                i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                i.addFlags(Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT);
                Bundle bundle1=new Bundle();
                bundle1.putInt("tabpos",TabPos);
                i.putExtras(bundle1);
                startActivity(i);
                finish();
                OtpDialog.dismiss();

            }
        });


    }
    public  boolean isStoragePermissionGranted() {
        if (Build.VERSION.SDK_INT >= 23) {
            if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_GRANTED &&
                    checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                            == PackageManager.PERMISSION_GRANTED) {
                Log.v(TAG,"Permission is granted");
                return true;
            } else {

                Log.v(TAG,"Permission is revoked");
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
                return false;
            }
        }
        else { //permission is automatically granted on sdk<23 upon installation
            Log.v(TAG,"Permission is granted");
            return true;
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(grantResults.length > 0 && grantResults[0]== PackageManager.PERMISSION_GRANTED){
            Log.v(TAG,"Permission: "+permissions[0]+ "was "+grantResults[0]);
            //resume tasks needing this permission
            Bitmap bitmap = takeScreenshot();
            saveBitmap(bitmap);
        }
    }
    public static void addImageToGallery(final String filePath, final Context context) {

        ContentValues values = new ContentValues();

        values.put(MediaStore.Images.Media.DATE_TAKEN, System.currentTimeMillis());
        values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg");
        values.put(MediaStore.MediaColumns.DATA, filePath);

        context.getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
        //System.out.println("Image added to gallery : "+ filePath);
    }

    @Override
    public void onBackPressed() {
        Intent i=new Intent(FeeInvoiceDownloadActivity.this,PaymentHistoryActivity.class);
        i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        i.addFlags(Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT);
        Bundle bundle1=new Bundle();
        bundle1.putInt("tabpos",TabPos);
        i.putExtras(bundle1);
        startActivity(i);
        finish();
    }
}
