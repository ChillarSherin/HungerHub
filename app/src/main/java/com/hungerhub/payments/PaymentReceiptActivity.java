package com.hungerhub.payments;

import android.Manifest;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import androidx.annotation.Nullable;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.StringRequest;
import com.chillarcards.cookery.R;
import com.google.gson.Gson;

import butterknife.BindView;
import butterknife.ButterKnife;
import com.hungerhub.NewPreOrder.CartListActivity;
import com.hungerhub.Payment.History.PaymentHistoryActivity;
import com.hungerhub.application.CampusWallet;
import com.hungerhub.application.Constants;
import com.hungerhub.application.CustomConnectionBuddyActivity;
import com.hungerhub.application.PrefManager;
import com.hungerhub.networkmodels.TransactionReceipts.Code;
import com.hungerhub.networkmodels.TransactionReceipts.Status;
import com.hungerhub.networkmodels.TransactionReceipts.TransactionReceipt;
import com.hungerhub.payments.Preorder.PreOrderCartList_Activity;
import com.hungerhub.payments.Transactions.OnlineTransactionStatusActivity;
import com.hungerhub.utils.CommonSSLConnection;
//import okhttp3.internal.framed.ErrorCode;

public class PaymentReceiptActivity extends CustomConnectionBuddyActivity {

    @BindView(R.id.imageView) ImageView imageView;
    @BindView(R.id.textView18) TextView statusTitle;
    @BindView(R.id.textView24) TextView statusSubTitle;
    @BindView(R.id.textView25) TextView onlineTransId;
    @BindView(R.id.textView26) TextView amountTV;
    @BindView(R.id.textView27) TextView CategoryTV;
    @BindView(R.id.textView29) TextView CallUsTV;
    @BindView(R.id.textView28) TextView DateTV;
    @BindView(R.id.textView)
    TextView HeaderText;
    @BindView(R.id.imageView5)
    ImageView BackButton;
    @BindView(R.id.ReloadBTN)
    Button ReloadBTN;
    @BindView(R.id.DownloadLL)
    LinearLayout DownloadLL;
    @BindView(R.id.linearLayout11)
    LinearLayout HistoryLL;
    final String tag_json_object = "r_student_online_transaction_data";
    @BindView(R.id.CodeErrorTV) TextView CodeErrorTV;
    @BindView(R.id.ErrorMessageTV) TextView ErrorMessageTV;

    PrefManager prefManager;
    String phoneNum,studentId,Studentname,onlineTransactionID,TransactionID;
    private static final int PERMISSIONS_REQUEST_PHONE_CALL = 100;
    private static String[] PERMISSIONS_PHONECALL = {Manifest.permission.CALL_PHONE};
    String From;
    int tabpos;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_transaction_receipt_activity);

        ButterKnife.bind(this);
        HeaderText.setText(getResources().getString(R.string.payment_receipt_header));

        prefManager=new PrefManager(PaymentReceiptActivity.this);


        Bundle b = getIntent().getExtras();
        String status = b.getString("status");
        //System.out.println("Checkout: "+ status);
        onlineTransactionID = b.getString("onlineTransactionID");
        String transaction_category = b.getString("transaction_category");
        String amount = b.getString("amount");
        From = b.getString("from");
        tabpos=b.getInt("tabpos");

        BackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (From.equals("1"))
                {
                    onBackPressed();
                }
                else if (Constants.from_Low_balance)
                {
                    if (Constants.from_old_preorder==0)
                    {
                        Intent i = new Intent(PaymentReceiptActivity.this, CartListActivity.class);
                        i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        i.addFlags(Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT);
                        Bundle bundle1 = new Bundle();
                        i.putExtras(bundle1);
                        startActivity(i);
                    }
                    else {
                        mFirebaseAnalytics.logEvent(getResources().getString(R.string.Pre_Order_Cart_Button_clicked),new Bundle());
                        Intent i = new Intent(PaymentReceiptActivity.this, PreOrderCartList_Activity.class);
                        i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        i.addFlags(Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT);
                        Bundle bundle1 = new Bundle();
                        i.putExtras(bundle1);
                        startActivity(i);
                    }

                }
                else {
                    Intent i = new Intent(PaymentReceiptActivity.this, PaymentHistoryActivity.class);
                    i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    i.addFlags(Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT);
                    Bundle bundle1 = new Bundle();
                    bundle1.putInt("tabpos", tabpos);
                    i.putExtras(bundle1);
                    startActivity(i);
                }
            }
        });

//        if(status.equalsIgnoreCase("F")){
//
//            imageView.setImageResource(R.mipmap.ic_failed);
//            statusTitle.setTextColor(getResources().getColor(R.color.red));
//            statusTitle.setText(getResources().getString(R.string.failed));
//            statusSubTitle.setText(getResources().getString(R.string.failed_transaction));
//            DownloadLL.setVisibility(View.GONE);
//
//
//        }else if(status.equalsIgnoreCase("P")){
//            imageView.setImageResource(R.mipmap.ic_pending);
//            statusTitle.setTextColor(getResources().getColor(R.color.red));
//            statusTitle.setText(getResources().getString(R.string.pending_payment));
//            statusSubTitle.setText(getResources().getString(R.string.pending_transaction));
//            DownloadLL.setVisibility(View.GONE);
//        }
//        else if(status.equalsIgnoreCase("A")){
//            imageView.setImageResource(R.mipmap.ic_failed);
//            statusTitle.setTextColor(getResources().getColor(R.color.red));
//            statusTitle.setText(getResources().getString(R.string.failed));
//            statusSubTitle.setText(getResources().getString(R.string.failed_transaction));
//            DownloadLL.setVisibility(View.GONE);
//        }else{
//            imageView.setImageResource(R.mipmap.ic_success);
//            statusTitle.setTextColor(getResources().getColor(R.color.green_txt));
//            statusTitle.setText(getResources().getString(R.string.Success));
//            statusSubTitle.setText(getResources().getString(R.string.success_transaction));
//            DownloadLL.setVisibility(View.VISIBLE);
//        }
        amountTV.setText(getResources().getString(R.string.indian_rupee_symbol)+amount);
        onlineTransId.setText(onlineTransactionID);
        CategoryTV.setText(setHeaderText(transaction_category));
        HistoryLL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mFirebaseAnalytics.logEvent(getResources().getString(R.string.Receipt_Payment_History_Button_Clicked),new Bundle());
                Intent i=new Intent(PaymentReceiptActivity.this, OnlineTransactionStatusActivity.class);
                i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(i);
            }
        });
        DownloadLL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mFirebaseAnalytics.logEvent(getResources().getString(R.string.Receipt_Download_Button_Clicked),new Bundle());
                Intent i=new Intent(PaymentReceiptActivity.this, InvoiceDownloadActivity.class);
                i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_NEW_TASK);
                Bundle bundle=new Bundle();
                bundle.putString("TransID",onlineTransactionID);
                i.putExtras(bundle);
                //System.out.println("AA Payment Receipt :: TransID : "+onlineTransactionID +" TransID 1 : "+TransactionID);
                startActivity(i);
            }
        });
        CallUsTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                callPermission();
            }
        });

    }
    public String setHeaderText(String catName)
    {
        String headerName="";
        switch (catName)
        {
            case "card_recharge":
                headerName=getResources().getString(R.string.card_recharge_caps);
                break;
            case "fee_payment":
                headerName=getResources().getString(R.string.fee_payments_caps);
                break;
            case "miscellaneous_payments":
                headerName=getResources().getString(R.string.miscellanious_caps);
                break;
            case "trust_payments":
                headerName=getResources().getString(R.string.trust_payments_caps);
                break;
            default :
                headerName=catName;
                break;

        }
        return headerName;
    }
//    @Override
//    public void onRequestPermissionsResult(int requestCode, String[] permissions,
//                                           int[] grantResults) {
//        if (requestCode == PERMISSIONS_REQUEST_PHONE_CALL) {
//            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//                callPermission();
//            } else {
//                callPermission();
//            }
//        }
//    }
    private void callPermission() {
        // Check the SDK version and whether the permission is already granted or not.
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && checkSelfPermission(Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
//            requestPermissions(new String[]{Manifest.permission.CALL_PHONE}, PERMISSIONS_REQUEST_PHONE_CALL);
//        } else {
            //Open call function
            String number = getResources().getString(R.string.customercare_number);
            Intent intent = new Intent(Intent.ACTION_DIAL);
            intent.setData(Uri.parse("tel:" + number));
            startActivity(intent);
            overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
//        }
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        CampusWallet.getInstance().cancelPendingRequests(tag_json_object);
    }
    @Override
    public void onBackPressed() {
        if (From.equals("1"))
        {
            super.onBackPressed();
        }
        else {
            Intent i = new Intent(PaymentReceiptActivity.this, PaymentHistoryActivity.class);
            i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            i.addFlags(Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT);
            Bundle bundle1 = new Bundle();
            bundle1.putInt("tabpos", tabpos);
            i.putExtras(bundle1);
            startActivity(i);
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        phpGetTransactionReceipt();
    }

    public void phpGetTransactionReceipt()
    {
        // progressBar.setVisibility(View.VISIBLE);
        phoneNum = prefManager.getUserPhone();  //getting phone number from shared preference
        studentId = prefManager.getStudentId();
        Studentname = prefManager.getStudentName();

        String URL, parameters;
        parameters = "phoneNo=" + phoneNum + "&studentID=" + studentId+"&onlineTransactionID="+onlineTransactionID;
        URL = Constants.BASE_URL  + "r_student_online_transaction_data.php?" + parameters.replaceAll(" ", "%20");
        //System.out.println("CHECK---> URL " + URL);
        final CommonSSLConnection commonSSLConnection=new CommonSSLConnection();
        StringRequest jsonObjectRequestLogin = new StringRequest(Request.Method.GET, URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String jsonObject) {
                //  progressBar.setVisibility(View.GONE);
                //System.out.println("CHECK---> Response " + jsonObject);
                //findViewById(R.id.progressLayout).setVisibility(View.GONE);
                Gson gson = new Gson();

                TransactionReceipt model = gson.fromJson(jsonObject, TransactionReceipt.class);
                Status status = model.getStatus();
                String code = status.getCode();
                if (code.equals("200")){

                    Code code1=model.getCode();
                    TransactionID=code1.getTransactionID();
                    String TransactionCategoryID=code1.getTransactionCategoryID();
                    String OnlineTransactionCreatedOn=code1.getOnlineTransactionCreatedOn();
                    String TransactionCategoryKey=code1.getTransactionCategoryKey();
                    String Amount=code1.getAmount();
                    String DownloadDate=code1.getDownloadDate();
                    String DownloadStatus=code1.getDownloadStatus();
                    String Status=code1.getStatus();
                    prefManager.setWalletBalance(code1.getCurrent_balance());
                    if (Status.equalsIgnoreCase("Success"))
                    {
                        imageView.setImageResource(R.mipmap.ic_success);
                        statusTitle.setTextColor(getResources().getColor(R.color.green_txt));
                        DownloadLL.setVisibility(View.VISIBLE);
                        statusSubTitle.setText(getResources().getString(R.string.success_transaction));

                    }else if(Status.equalsIgnoreCase("Pending")){
                        imageView.setImageResource(R.mipmap.ic_pending);
                        statusTitle.setTextColor(getResources().getColor(R.color.red));
                        statusSubTitle.setText(getResources().getString(R.string.pending_transaction));
                        DownloadLL.setVisibility(View.GONE);
                    }
                    else if(Status.equalsIgnoreCase("Aborted")){
                        imageView.setImageResource(R.mipmap.ic_failed);
                        statusTitle.setTextColor(getResources().getColor(R.color.red));
                        statusSubTitle.setText(getResources().getString(R.string.failed_transaction));
                        DownloadLL.setVisibility(View.GONE);
                    }
                    else
                    {
                        imageView.setImageResource(R.mipmap.ic_failed);
                        statusTitle.setTextColor(getResources().getColor(R.color.red));
                        DownloadLL.setVisibility(View.GONE);
                        statusSubTitle.setText(getResources().getString(R.string.failed_transaction));

                    }
                    statusTitle.setText(Status);
                    amountTV.setText(getResources().getString(R.string.indian_rupee_symbol)+Amount);
                    onlineTransId.setText(TransactionID);
                    CategoryTV.setText(setHeaderText(TransactionCategoryKey));
                    DateTV.setText(OnlineTransactionCreatedOn);

                }
                else if(code.equals("400")){

//                    ErrorImage.setBackgroundResource(R.drawable.server_issue);
                    // Txt_Content.setText("something went wrong - purely technical, give us a minute.");
                    ReloadBTN.setText(getResources().getString(R.string.go_back));
                    findViewById(R.id.ErrorLL).setVisibility(View.VISIBLE);
                    ErrorMessageTV.setText(getResources().getString(R.string.error_message_errorlayout));
                    CodeErrorTV.setText(getResources().getString(R.string.code_attendance)+code);
                    ReloadBTN.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {


                            onBackPressed();

                        }
                    });
                }else if (code.equals("204")){

                    ReloadBTN.setText(getResources().getString(R.string.go_back));
                    findViewById(R.id.ErrorLL).setVisibility(View.VISIBLE);
                    ErrorMessageTV.setText(getResources().getString(R.string.no_data_found));
                    CodeErrorTV.setText(getResources().getString(R.string.code_attendance)+code);
                    CodeErrorTV.setVisibility(View.GONE);
                    ReloadBTN.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {


                            onBackPressed();

                        }
                    });

                }else if (code.equals("401")){

                    ReloadBTN.setText(getResources().getString(R.string.go_back));
                    findViewById(R.id.ErrorLL).setVisibility(View.VISIBLE);
                    ErrorMessageTV.setText(getResources().getString(R.string.error_message_errorlayout));
                    CodeErrorTV.setText(getResources().getString(R.string.code_attendance)+code);
                    ReloadBTN.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {


                            onBackPressed();

                        }
                    });
                }else if (code.equals("500")){

                    //ADD SNACKBAR LAYOUT
                    ReloadBTN.setText(getResources().getString(R.string.go_back));
                    findViewById(R.id.ErrorLL).setVisibility(View.VISIBLE);
                    ErrorMessageTV.setText(getResources().getString(R.string.error_message_errorlayout));
                    CodeErrorTV.setText(getResources().getString(R.string.code_attendance)+code);
                    ReloadBTN.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {


                            onBackPressed();

                        }
                    });

                }


            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                //progressBar.setVisibility(View.GONE);
                VolleyLog.d("Object Error : ", volleyError.getMessage());
                //ADD SNACKBAR LAYOUT
                ReloadBTN.setText(getResources().getString(R.string.go_back));
                findViewById(R.id.ErrorLL).setVisibility(View.VISIBLE);
                ErrorMessageTV.setText(getResources().getString(R.string.error_message_admin));
                CodeErrorTV.setText(getResources().getString(R.string.error_message_errorlayout));
                ReloadBTN.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {


                        onBackPressed();

                    }
                });

                Toast.makeText(PaymentReceiptActivity.this, getResources().getString(R.string.network_error_try), Toast.LENGTH_SHORT).show();
            }
        });
        jsonObjectRequestLogin.setRetryPolicy(new DefaultRetryPolicy(50000, 3, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
//        RequestQueue requestQueue = Volley.newRequestQueue(this, commonSSLConnection.getHulkstack(PaymentReceiptActivity.this));
        CampusWallet.getInstance().addToRequestQueue(jsonObjectRequestLogin, tag_json_object);
//        requestQueue.add(jsonObjectRequestLogin);
    }
}
