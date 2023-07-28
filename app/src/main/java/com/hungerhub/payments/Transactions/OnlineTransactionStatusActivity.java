package com.hungerhub.payments.Transactions;

import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import android.os.Bundle;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
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
import com.hungerhub.application.CampusWallet;
import com.hungerhub.application.Constants;
import com.hungerhub.application.CustomConnectionBuddyActivity;
import com.hungerhub.application.PrefManager;
import com.hungerhub.networkmodels.OnlineTransactionHistory.Code;
import com.hungerhub.networkmodels.OnlineTransactionHistory.Status;
import com.hungerhub.networkmodels.OnlineTransactionHistory.Transaction;
import com.hungerhub.networkmodels.OnlineTransactionHistory.TransactionHistory;
import com.hungerhub.utils.CommonSSLConnection;

public class OnlineTransactionStatusActivity extends CustomConnectionBuddyActivity {

    @BindView(R.id.HistoryPB)
    ProgressBar progressBar;
    @BindView(R.id.HeaderTV)
    TextView HeaderText;
    @BindView(R.id.BackIV)
    ImageView BackButton;
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
    @BindView(R.id.OnlineTransactionStatusSRL)
    SwipeRefreshLayout OnlineTransactionStatusSRL;
    @BindView(R.id.OnlineTransactionStatusRV)
    RecyclerView OnlineTransactionStatusRV;
    String phoneNum,studentId;

    PrefManager prefManager;
    List<Transaction> transactionList;
    private OnlineTransactionStatusAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_online_transaction_status);
        ButterKnife.bind(this);
        prefManager = new PrefManager(OnlineTransactionStatusActivity.this);
        progressBar.setVisibility(View.GONE);
        transactionList=new ArrayList<>();

        HeaderText.setText(getResources().getString(R.string.online_trans_status_header));
        BackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        OnlineTransactionStatusSRL.setColorSchemeResources(R.color.colorAccent);
    }

    @Override
    protected void onResume() {
        super.onResume();
        StudentnameTV.setText(prefManager.getStudentName());
        balancewalletTV.setText(getResources().getString(R.string.wallet_balance_home)+ prefManager.getWalletBalance());

        OnlineTransactionHistoryPHP();
        OnlineTransactionStatusSRL.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                OnlineTransactionHistoryPHP();
                OnlineTransactionStatusSRL.setRefreshing(false);
            }
        });

    }
    private void OnlineTransactionHistoryPHP() {

        progressBar.setVisibility(View.VISIBLE);
        phoneNum = prefManager.getUserPhone();  //getting phone number from shared preference
        studentId = prefManager.getStudentId();


        String URL, parameters;
        parameters = "phoneNo=" + phoneNum + "&studentID=" + studentId;
        URL = Constants.BASE_URL  + "r_online_transaction_history.php?" + parameters.replaceAll(" ", "%20");
        //System.out.println("CHECK---> URL " + URL);
        final CommonSSLConnection commonSSLConnection=new CommonSSLConnection();
        StringRequest jsonObjectRequestLogin = new StringRequest(Request.Method.GET, URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String jsonObject) {
                progressBar.setVisibility(View.GONE);
                //System.out.println("CHECK---> Response " + jsonObject);
                //findViewById(R.id.progressLayout).setVisibility(View.GONE);
                Gson gson = new Gson();

                TransactionHistory model = gson.fromJson(jsonObject, TransactionHistory.class);
                Status status = model.getStatus();
                String code = status.getCode();

                if (code.equals("200")) {

                    Code code1 = model.getCode();

                    if (code1.getTransactions().size()>0) {

                        transactionList.clear();
                        transactionList = code1.getTransactions();
                        OnlineTransactionStatusRV.setLayoutManager(new LinearLayoutManager(OnlineTransactionStatusActivity.this, LinearLayoutManager.VERTICAL, false));
                        OnlineTransactionStatusRV.setItemAnimator(new DefaultItemAnimator());
                        OnlineTransactionStatusRV.setNestedScrollingEnabled(false);
                        mAdapter = new OnlineTransactionStatusAdapter(transactionList, OnlineTransactionStatusActivity.this,mFirebaseAnalytics);
                        OnlineTransactionStatusRV.setAdapter(mAdapter);

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
                else if(code.equals("204")){


                        NodataTV.setText(getResources().getString(R.string.no_data_found));
                        GoBackBTN.setText(getResources().getString(R.string.go_back));
//                        ErrorImage.setBackgroundResource(R.drawable.nodata);
                        findViewById(R.id.NodataLL).setVisibility(View.VISIBLE);
                        GoBackBTN.setVisibility(View.GONE);

                }

                else {

                    String message = status.getMessage();
                    Toast.makeText(OnlineTransactionStatusActivity.this, message, Toast.LENGTH_SHORT).show();
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

                Toast.makeText(OnlineTransactionStatusActivity.this, getResources().getString(R.string.network_error_try), Toast.LENGTH_SHORT).show();
            }
        });
        jsonObjectRequestLogin.setRetryPolicy(new DefaultRetryPolicy(50000, 3, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
//        RequestQueue requestQueue = Volley.newRequestQueue(this, commonSSLConnection.getHulkstack(PaymentHistoryActivity.this));
        CampusWallet.getInstance().addToRequestQueue(jsonObjectRequestLogin, tag_json_object);
//        requestQueue.add(jsonObjectRequestLogin);

    }
}
