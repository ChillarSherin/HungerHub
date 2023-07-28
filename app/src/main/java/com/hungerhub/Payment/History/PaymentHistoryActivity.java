package com.hungerhub.Payment.History;

import android.os.Bundle;
import com.google.android.material.tabs.TabLayout;
import androidx.viewpager.widget.ViewPager;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
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

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import com.hungerhub.NotificationCenter.DatabaseHandler;
import com.hungerhub.application.CampusWallet;
import com.hungerhub.application.Constants;
import com.hungerhub.application.CustomConnectionBuddyActivity;
import com.hungerhub.application.PrefManager;
import com.hungerhub.networkmodels.FeePaymentReport.Code;
import com.hungerhub.networkmodels.FeePaymentReport.FeePaymentReport;
import com.hungerhub.networkmodels.FeePaymentReport.Payment;
import com.hungerhub.networkmodels.FeePaymentReport.PaymentDetail;
import com.hungerhub.networkmodels.FeePaymentReport.Status;
import com.hungerhub.payments.cardtransaction.RefreshStatement;
import com.hungerhub.utils.CommonSSLConnection;

/*
Created By : Abhinand
Created Date : 09-10-2018
*/

public class PaymentHistoryActivity extends CustomConnectionBuddyActivity implements RefreshStatement {

    ViewPager HistoryVP;
    TabLayout HistoryTL;
    PaymentHistoryAdapter historyAdapter;
    PrefManager prefManager;
    String phoneNum,studentId;
    List<Payment> catagotyList;
    List<PaymentDetail> transactionList;
    @BindView(R.id.HistoryPB) ProgressBar progressBar;
    @BindView(R.id.HeaderTV) TextView HeaderText;
    @BindView(R.id.BackIV) ImageView BackButton;
    @BindView(R.id.ReloadBTN)
    Button ReloadBTN;
    @BindView(R.id.GoBackBTN)
    Button GoBackBTN;
    @BindView(R.id.NodataTV)
    TextView NodataTV;
    @BindView(R.id.StudentnameTV)
    TextView StudentnameTV;
    @BindView(R.id.balancewalletTV)
    TextView balancewalletTV;
    final String tag_json_object = "r_online_transaction_history";
    @BindView(R.id.CodeErrorTV) TextView CodeErrorTV;
    @BindView(R.id.ErrorMessageTV) TextView ErrorMessageTV;
    @BindView(R.id.preorderMenuTV) TextView preorderMenuTV;
    @BindView(R.id.preorderMenuIV) ImageView preorderMenuIV;
    @BindView(R.id.preorderMenuLL)
    LinearLayout preorderMenuLL;
    int tabpos=0;
    DatabaseHandler db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment_history);
        ButterKnife.bind(this);

        getHandles(); //getting handles or initializing widgets

        prefManager = new PrefManager(PaymentHistoryActivity.this);
        catagotyList=new ArrayList<>();
        transactionList=new ArrayList<>();
        progressBar.setVisibility(View.GONE);

        HeaderText.setText(getResources().getString(R.string.history_header));
        BackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        preorderMenuLL.setVisibility(View.GONE);
        db = new DatabaseHandler(PaymentHistoryActivity.this);

    }
    public void getHandles()
    {
        HistoryVP=findViewById(R.id.PaymentHistoryContainerVP);
        HistoryTL=findViewById(R.id.PaymentHistoryTL);
    }

    @Override
    protected void onResume() {
        super.onResume();
        preorderMenuLL.setVisibility(View.GONE);
        StudentnameTV.setText(prefManager.getStudentName());
        balancewalletTV.setText(getResources().getString(R.string.wallet_balance_home)+ prefManager.getWalletBalance());

        Bundle bundle=getIntent().getExtras();
        if (bundle!=null) {
            tabpos=bundle.getInt("tabpos");
        }

        OnlineTransactionHistoryPHP();
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        CampusWallet.getInstance().cancelPendingRequests(tag_json_object);
    }
    private void OnlineTransactionHistoryPHP() {

        progressBar.setVisibility(View.VISIBLE);
        phoneNum = prefManager.getUserPhone();  //getting phone number from shared preference
        studentId = prefManager.getStudentId();


        String URL, parameters;
        parameters = "phoneNo=" + phoneNum + "&studentID=" + studentId;
        URL = Constants.BASE_URL  + "r_fee_payment_report.php?" + parameters.replaceAll(" ", "%20");
        //System.out.println("CHECK---> URL " + URL);
        final CommonSSLConnection commonSSLConnection=new CommonSSLConnection();
        StringRequest jsonObjectRequestLogin = new StringRequest(Request.Method.GET, URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String jsonObject) {
                progressBar.setVisibility(View.GONE);
                //System.out.println("CHECK---> Response " + jsonObject);
                //findViewById(R.id.progressLayout).setVisibility(View.GONE);
                Gson gson = new Gson();

                FeePaymentReport model = gson.fromJson(jsonObject, FeePaymentReport.class);
                Status status = model.getStatus();
                String code = status.getCode();

                if (code.equals("200")) {

                    Code code1 = model.getCode();
                    catagotyList.clear();
                    if (code1.getPayment().size()>0) {

                        // catagotyList=code1.getTrasactionCategories();
//                        TrasactionCategory all = new TrasactionCategory("0", "All");
//                        catagotyList.add(all);
                        catagotyList.addAll(code1.getPayment());
//                        for (TrasactionCategory category: catagotyList)
//                        {
//                            if (db.UpdateTransactionCategories(category)==0) {
//                                db.addTransactionCategory(category);
//                            }
//                        }

//                        catagotyList.remove(0);
//                    for (TrasactionCategory other : code1.getTrasactionCategories())
//                    {
//                        catagotyList.add(other);
//                    }

                        transactionList.clear();
                        transactionList = code1.getPaymentDetails();
//                        for (Transaction transaction: transactionList)
//                        {
//                            db.addTransactions(transaction);
//                        }
//                        transactionList.clear();
//                        transactionList=db.getAllTransactions();
//                        if (transactionList.size()>0) {
                            historyAdapter = new PaymentHistoryAdapter(getSupportFragmentManager(), catagotyList, transactionList,
                                    PaymentHistoryActivity.this, mFirebaseAnalytics,PaymentHistoryActivity.this);
                            HistoryVP.setAdapter(historyAdapter); // setting adapter to view pager

                            HistoryTL.setupWithViewPager(HistoryVP); //setting up tab with view pager
                            HistoryTL.setTabMode(TabLayout.MODE_SCROLLABLE);
                            HistoryTL.getTabAt(tabpos).select();
//                        }
//                        else
//                        {
//                            NodataTV.setText(getResources().getString(R.string.no_data_found));
//                            GoBackBTN.setText(getResources().getString(R.string.go_back));
////                        ErrorImage.setBackgroundResource(R.drawable.nodata);
//                            findViewById(R.id.NodataLL).setVisibility(View.VISIBLE);
//                            GoBackBTN.setVisibility(View.GONE);
//                        }
                    }
                    else
                    {
//                        catagotyList.addAll(code1.getTrasactionCategories());
//                        for (TrasactionCategory category: catagotyList)
//                        {
//                            if (db.UpdateTransactionCategories(category)==0) {
//                                db.addTransactionCategory(category);
//                            }
//                        }
//
//                        catagotyList.remove(0);
//                        transactionList.clear();
//                        transactionList = code1.getTransactions();
//                        for (Transaction transaction: transactionList)
//                        {
//                            db.addTransactions(transaction);
//                        }
//                        transactionList.clear();
//                        transactionList=db.getAllTransactions();
//                        if (transactionList.size()>0) {
//                            historyAdapter = new PaymentHistoryAdapter(getSupportFragmentManager(), catagotyList, transactionList, PaymentHistoryActivity.this, mFirebaseAnalytics);
//                            HistoryVP.setAdapter(historyAdapter); // setting adapter to view pager
//
//                            HistoryTL.setupWithViewPager(HistoryVP); //setting up tab with view pager
//                            HistoryTL.setTabMode(TabLayout.MODE_SCROLLABLE);
//                            HistoryTL.getTabAt(tabpos).select();
//                        }
//                        else {
                            NodataTV.setText(getResources().getString(R.string.no_data_found));
                            GoBackBTN.setText(getResources().getString(R.string.go_back));
//                        ErrorImage.setBackgroundResource(R.drawable.nodata);
                            findViewById(R.id.NodataLL).setVisibility(View.VISIBLE);
                            GoBackBTN.setVisibility(View.GONE);
//                        }
                    }

                }
                else if(code.equals("204")){

                    Code code1 = model.getCode();
                    catagotyList.clear();
                    if (code1.getPayment().size()>0) {

                        // catagotyList=code1.getTrasactionCategories();
//                        TrasactionCategory all = new TrasactionCategory("0", "All");
//                        catagotyList.add(all);
//                        catagotyList.addAll(code1.getTrasactionCategories());
                        catagotyList.addAll(code1.getPayment());
                        catagotyList.remove(0);
//                    for (TrasactionCategory other : code1.getTrasactionCategories())
//                    {
//                        catagotyList.add(other);
//                    }
                        transactionList = new ArrayList<>();
                        transactionList.clear();

                        historyAdapter = new PaymentHistoryAdapter(getSupportFragmentManager(), catagotyList, transactionList,
                                PaymentHistoryActivity.this,mFirebaseAnalytics,PaymentHistoryActivity.this);
                        HistoryVP.setAdapter(historyAdapter); // setting adapter to view pager

                        HistoryTL.setupWithViewPager(HistoryVP); //setting up tab with view pager
                        HistoryTL.setTabMode(TabLayout.MODE_SCROLLABLE);
                    }
                    else
                    {
                        NodataTV.setText(getResources().getString(R.string.no_data_found));
                        GoBackBTN.setText(getResources().getString(R.string.go_back));
//                        ErrorImage.setBackgroundResource(R.drawable.nodata);
                        findViewById(R.id.NodataLL).setVisibility(View.VISIBLE);
                        GoBackBTN.setVisibility(View.GONE);
                    }

                }

                else {

                    String message = status.getMessage();
                    Toast.makeText(PaymentHistoryActivity.this, message, Toast.LENGTH_SHORT).show();
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
                progressBar.setVisibility(View.GONE);
                VolleyLog.d("Object Error : ", volleyError.getMessage());
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

                Toast.makeText(PaymentHistoryActivity.this, getResources().getString(R.string.network_error_try), Toast.LENGTH_SHORT).show();
            }
        });
        jsonObjectRequestLogin.setRetryPolicy(new DefaultRetryPolicy(50000, 3, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
//        RequestQueue requestQueue = Volley.newRequestQueue(this, commonSSLConnection.getHulkstack(PaymentHistoryActivity.this));
        CampusWallet.getInstance().addToRequestQueue(jsonObjectRequestLogin, tag_json_object);
//        requestQueue.add(jsonObjectRequestLogin);

    }

    @Override
    public void onStatementRefresh() {
        OnlineTransactionHistoryPHP();
    }
}
