package com.hungerhub.payments.cardtransaction;

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
import com.hungerhub.networkmodels.OutletMenu.Code;
import com.hungerhub.networkmodels.OutletMenu.OutletMenu;
import com.hungerhub.networkmodels.OutletMenu.Status;
import com.hungerhub.utils.CommonSSLConnection;

public class OutletMenuActivity extends CustomConnectionBuddyActivity {

    final String tag_json_object = "r_outlets";
    @BindView(R.id.HeaderTV)
    TextView HeaderText;
    @BindView(R.id.BackIV)
    ImageView BackButton;
    @BindView(R.id.OutletRV)
    RecyclerView OutletRV;
    @BindView(R.id.progressBar)
    ProgressBar progressBar;

    @BindView(R.id.CodeErrorTV) TextView CodeErrorTV;
    @BindView(R.id.ErrorMessageTV) TextView ErrorMessageTV;
    @BindView(R.id.ReloadBTN)
    Button ReloadBTN;
    @BindView(R.id.GoBackBTN)
    Button GoBackBTN;
    @BindView(R.id.NodataTV)
    TextView NodataTV;

    PrefManager prefManager;
    String phoneNum,studentId,StudentNasme;
    private OutletMenuAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_outlet_menu);
        ButterKnife.bind(this);
        progressBar.setVisibility(View.GONE);
        prefManager=new PrefManager(OutletMenuActivity.this);
        HeaderText.setText(getResources().getString(R.string.outlet_title));
        BackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        callOutletsPHP();
    }

    private void callOutletsPHP() {

        progressBar.setVisibility(View.VISIBLE);
        phoneNum = prefManager.getUserPhone();  //getting phone number from shared preference
        studentId = prefManager.getStudentId();
        StudentNasme = prefManager.getStudentName();


        String URL, parameters;
        parameters = "phoneNo=" + phoneNum + "&studentID=" + studentId;
        URL = Constants.BASE_URL  + "r_outlets.php?" + parameters.replaceAll(" ", "%20");
        //System.out.println("CHECK---> URL " + URL);
        final CommonSSLConnection commonSSLConnection=new CommonSSLConnection();
        StringRequest jsonObjectRequestLogin = new StringRequest(Request.Method.GET, URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String jsonObject) {
                progressBar.setVisibility(View.GONE);
                //System.out.println("CHECK---> Response " + jsonObject);
                Gson gson = new Gson();

                OutletMenu model = gson.fromJson(jsonObject, OutletMenu.class);
                Status status = model.getStatus();
                String code = status.getCode();

                if (code.equals("200")) {

                    List<Code> code1= new ArrayList<>();
                    code1= model.getCode();
                    if (code1.size()>0)
                    {
                        findViewById(R.id.NodataLL).setVisibility(View.GONE);
                        findViewById(R.id.ErrorLL).setVisibility(View.GONE);
                        OutletRV.setVisibility(View.VISIBLE);
                        OutletRV.setLayoutManager(new LinearLayoutManager(OutletMenuActivity.this, LinearLayoutManager.VERTICAL, false));
                        OutletRV.setItemAnimator(new DefaultItemAnimator());
                        OutletRV.setNestedScrollingEnabled(false);
                        mAdapter = new OutletMenuAdapter(code1,OutletMenuActivity.this,OutletMenuActivity.this,mFirebaseAnalytics);
                        OutletRV.setAdapter(mAdapter);

                    }
                    else {
                        NodataTV.setText(getResources().getString(R.string.no_data_found));
                        GoBackBTN.setText(getResources().getString(R.string.go_back));
//                        ErrorImage.setBackgroundResource(R.drawable.nodata);
                        findViewById(R.id.NodataLL).setVisibility(View.VISIBLE);
                        GoBackBTN.setVisibility(View.GONE);
                    }

                }
                else  if (code.equals("204")){
                    NodataTV.setText(getResources().getString(R.string.no_data_found));
                    GoBackBTN.setText(getResources().getString(R.string.go_back));
//                        ErrorImage.setBackgroundResource(R.drawable.nodata);
                    findViewById(R.id.NodataLL).setVisibility(View.VISIBLE);
                    GoBackBTN.setVisibility(View.GONE);
                }

                else {

                    String message = status.getMessage();
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
                    Toast.makeText(OutletMenuActivity.this, message, Toast.LENGTH_SHORT).show();
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
                Toast.makeText(OutletMenuActivity.this, getResources().getString(R.string.network_error_try), Toast.LENGTH_SHORT).show();
            }
        });
        jsonObjectRequestLogin.setRetryPolicy(new DefaultRetryPolicy(50000, 3, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
//        RequestQueue requestQueue = Volley.newRequestQueue(this, commonSSLConnection.getHulkstack(CardTransactionActivity.this));
        CampusWallet.getInstance().addToRequestQueue(jsonObjectRequestLogin, tag_json_object);
//        requestQueue.add(jsonObjectRequestLogin);

    }
}
