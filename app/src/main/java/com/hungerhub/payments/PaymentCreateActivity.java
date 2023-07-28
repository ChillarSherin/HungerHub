package com.hungerhub.payments;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.chillarcards.cookery.R;
import com.google.gson.Gson;

import butterknife.BindView;
import butterknife.ButterKnife;
import com.hungerhub.application.CampusWallet;
import com.hungerhub.application.Constants;
import com.hungerhub.application.CustomConnectionBuddyActivity;
import com.hungerhub.application.PrefManager;
import com.hungerhub.networkmodels.createpayment.Code;
import com.hungerhub.networkmodels.createpayment.Createpayment;
import com.hungerhub.networkmodels.createpayment.Status;
import com.hungerhub.utils.CommonSSLConnection;


public class PaymentCreateActivity extends CustomConnectionBuddyActivity {

    PrefManager prefManager;
    Activity activity;
    final String tag_json_object = "c_online_transaction";
    @BindView(R.id.progressBar) ProgressBar progressBar;
    @BindView(R.id.CodeErrorTV)
    TextView CodeErrorTV;
    @BindView(R.id.ErrorMessageTV) TextView ErrorMessageTV;
    @BindView(R.id.ReloadBTN)
    Button ReloadBTN;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_create_payment_activity);
        ButterKnife.bind(this);
        activity=this;
        prefManager = new PrefManager(PaymentCreateActivity.this);
        String phoneNum = prefManager.getUserPhone();
        String studentId = prefManager.getStudentId();

        Bundle b = getIntent().getExtras();
        String paymentMode = b.getString("paymentMode");
        String amount = b.getString("amount");
        String trans_category = b.getString("trans_category");
        String paymentGateway = b.getString("paymentGateway");

        if(!trans_category.equals("2")){
            String paymentItemId = b.getString("paymentItemId");
            createTransaction(phoneNum,studentId,amount,trans_category,paymentGateway,paymentMode,paymentItemId);
        }else {
            createTransaction(phoneNum,studentId,amount,trans_category,paymentGateway,paymentMode,"");
        }



    }

    private void createTransaction(String phoneNum, String s, final String amount, final String trans_category, String paymentGateway, final String paymentMode, String paymentItemId) {


        final String URL;
        String parameters;
        parameters = "phoneNo=" + phoneNum+"&studentID="+s+"&amount="+amount+"&transaction_category="+trans_category+"&paymentGateway="+paymentGateway;

        if(!trans_category.equals("2")&&!paymentItemId.equals("")){
            parameters = parameters+ "&itemID="+paymentItemId;
        }
        URL = Constants.BASE_URL + "c_online_transaction.php?" + parameters.replaceAll(" ", "%20");
        System.out.println("CHECK---> URL " + URL);
        CommonSSLConnection commonSSLConnection=new CommonSSLConnection();
        final StringRequest jsonObjectRequestLogin = new StringRequest(Request.Method.GET, URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String jsonObject) {

                Log.i("CreatePayment",jsonObject);

                CampusWallet.getInstance().cancelPendingRequests(tag_json_object);
                Gson gson = new Gson();

                Createpayment createpayment = gson.fromJson(jsonObject,Createpayment.class);
                Status status = createpayment.getStatus();
                String code = status.getCode();

                if(code.equals("200")){
                    findViewById(R.id.ErrorLL).setVisibility(View.GONE);
                    Code code1 = createpayment.getCode();
                    String onlineTransactionID = code1.getOnlineTransactionID();
                    String razorpayKey = code1.getRazorpayKey();
                    String razorpaySecretKey = code1.getRazorpaySecretKey();
                    String parentName = code1.getParentName();
                    String parentEmail = code1.getParentEmail();


                    if(trans_category.equals("2")){

                        Intent i = new Intent(getApplicationContext(), PaymentRazorpay.class);
                        Bundle b = new Bundle();
                        b.putString("onlineTransactionID",onlineTransactionID);
                        b.putString("razorpayKey",razorpayKey);
                        b.putString("razorpaySecretKey",razorpaySecretKey);
                        b.putString("parentName",parentName);
                        b.putString("parentEmail",parentEmail);
                        b.putString("paymentFor","card_recharge");
                        b.putString("paymentMode",paymentMode);
                        b.putString("trans_category","card_recharge");
                        b.putString("razor_order_id",code1.getRazorpayOrderID());
                        b.putString("amount",amount);
                        i.putExtras(b);
                        startActivity(i);
                        activity.finish();

                    }else if(trans_category.equals("3")){

                        Intent i = new Intent(getApplicationContext(), PaymentRazorpay.class);
                        Bundle b = new Bundle();
                        b.putString("onlineTransactionID",onlineTransactionID);
                        b.putString("razorpayKey",razorpayKey);
                        b.putString("razorpaySecretKey",razorpaySecretKey);
                        b.putString("parentName",parentName);
                        b.putString("parentEmail",parentEmail);
                        b.putString("paymentFor","fee_payment");
                        b.putString("paymentMode",paymentMode);
                        b.putString("trans_category","fee_payment");
                        b.putString("amount",amount);
                        b.putString("razor_order_id",code1.getRazorpayOrderID());
                        i.putExtras(b);
                        startActivity(i);
                        activity.finish();

                    }else if(trans_category.equals("4")){

                        Intent i = new Intent(getApplicationContext(), PaymentRazorpay.class);
                        Bundle b = new Bundle();
                        b.putString("onlineTransactionID",onlineTransactionID);
                        b.putString("razorpayKey",razorpayKey);
                        b.putString("razorpaySecretKey",razorpaySecretKey);
                        b.putString("parentName",parentName);
                        b.putString("parentEmail",parentEmail);
                        b.putString("paymentFor","miscellaneous_payments");
                        b.putString("paymentMode",paymentMode);
                        b.putString("trans_category","miscellaneous_payments");
                        b.putString("razor_order_id",code1.getRazorpayOrderID());
                        b.putString("amount",amount);
                        i.putExtras(b);
                        startActivity(i);
                        activity.finish();

                    }else if(trans_category.equals("5")){

                        Intent i = new Intent(getApplicationContext(), PaymentRazorpay.class);
                        Bundle b = new Bundle();
                        b.putString("onlineTransactionID",onlineTransactionID);
                        b.putString("razorpayKey",razorpayKey);
                        b.putString("razorpaySecretKey",razorpaySecretKey);
                        b.putString("parentName",parentName);
                        b.putString("parentEmail",parentEmail);
                        b.putString("paymentFor","trust_payments");
                        b.putString("paymentMode",paymentMode);
                        b.putString("trans_category","trust_payments");
                        b.putString("razor_order_id",code1.getRazorpayOrderID());
                        b.putString("amount",amount);
                        i.putExtras(b);
                        startActivity(i);
                        activity.finish();
                    }



                }else if(code.equals("501")) {
                    progressBar.setVisibility(View.GONE);
                    ReloadBTN.setText(getResources().getString(R.string.go_back));
                    findViewById(R.id.ErrorLL).setVisibility(View.VISIBLE);
                    ErrorMessageTV.setText(status.getMessage());
                    CodeErrorTV.setText(getResources().getString(R.string.code_attendance)+code);
                    ReloadBTN.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {


                            onBackPressed();

                        }
                    });

                }else
                {
                    progressBar.setVisibility(View.GONE);
                    ReloadBTN.setText(getResources().getString(R.string.go_back));
                    findViewById(R.id.ErrorLL).setVisibility(View.VISIBLE);
                    ErrorMessageTV.setText(status.getMessage());
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
                volleyError.printStackTrace();
                progressBar.setVisibility(View.GONE);
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
            }
        });
        jsonObjectRequestLogin.setRetryPolicy(new DefaultRetryPolicy(50000, 0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
//        RequestQueue requestQueue = Volley.newRequestQueue(this, commonSSLConnection.getHulkstack(PaymentCreateActivity.this));
        CampusWallet.getInstance().addToRequestQueue(jsonObjectRequestLogin, tag_json_object);
//        requestQueue.add(jsonObjectRequestLogin);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
//        //System.out.println("OnDestroy ");
        CampusWallet.getInstance().cancelPendingRequests(tag_json_object);
    }
}
