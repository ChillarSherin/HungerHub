package com.hungerhub.Diary.Result;

import android.app.Activity;
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

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import com.hungerhub.application.CampusWallet;
import com.hungerhub.application.Constants;
import com.hungerhub.application.CustomConnectionBuddyActivity;
import com.hungerhub.application.PrefManager;
import com.hungerhub.networkmodels.Results.Code;
import com.hungerhub.networkmodels.Results.ResultMain;
import com.hungerhub.networkmodels.Results.Status;
import com.hungerhub.utils.CommonSSLConnection;

public class ResultActivity extends CustomConnectionBuddyActivity {

    @BindView(R.id.ResultmainRV)
    RecyclerView ResultmainRV;
    @BindView(R.id.HeaderTV)
    TextView HeaderText;
    @BindView(R.id.BackIV)
    ImageView BackButton;
    @BindView(R.id.division)
    TextView division;
    @BindView(R.id.std)
    TextView std;
    @BindView(R.id.studname)
    TextView studname;
    @BindView(R.id.ProgressBar)
    ProgressBar progressBar;
    PrefManager prefManager;
    Activity activity;
    private ResultAllSubjectsAdapter mAdapter;
    String phoneNum,studentId,Studentname,std_div,Standard,Division;
    @BindView(R.id.ReloadBTN)
    Button ReloadBTN;
    @BindView(R.id.GoBackBTN)
    Button GoBackBTN;
    @BindView(R.id.NodataTV)
    TextView NodataTV;

    @BindView(R.id.CodeErrorTV) TextView CodeErrorTV;
    @BindView(R.id.ErrorMessageTV) TextView ErrorMessageTV;
    @BindView(R.id.ResultmainSRL)
    SwipeRefreshLayout ResultmainSRL;
    final String tag_json_object = "r_subjects_result";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);
        ButterKnife.bind(this);
        activity=this;
        prefManager=new PrefManager(ResultActivity.this);
        HeaderText.setText(getResources().getString(R.string.results_header));
        BackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        progressBar.setVisibility(View.GONE);
        Studentname = prefManager.getStudentName();
        std_div = prefManager.getStudentStandardDivision();
        Standard = prefManager.getStudentStandard();
        Division = prefManager.getStudentDivision();
        studname.setText(Studentname);
        std.setText(Standard);
        division.setText(Division);
        ResultmainSRL.setColorSchemeResources(R.color.colorAccent);
    }

    @Override
    protected void onResume() {
        super.onResume();
        phpGetAttendanceMain();
        ResultmainSRL.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                phpGetAttendanceMain();
                ResultmainSRL.setRefreshing(false);
            }
        });

    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        CampusWallet.getInstance().cancelPendingRequests(tag_json_object);
    }
    public void phpGetAttendanceMain()
    {
         progressBar.setVisibility(View.VISIBLE);
        phoneNum = prefManager.getUserPhone();  //getting phone number from shared preference
        studentId = prefManager.getStudentId();
//        Studentname = prefManager.getUserName();

        String URL, parameters;
        parameters = "phoneNo=" + phoneNum + "&studentID=" + studentId;
        URL = Constants.BASE_URL  + "r_subjects.php?" + parameters.replaceAll(" ", "%20");
        //System.out.println("CHECK---> URL " + URL);
        CommonSSLConnection commonSSLConnection=new CommonSSLConnection();
        StringRequest jsonObjectRequestLogin = new StringRequest(Request.Method.GET, URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String jsonObject) {
                  progressBar.setVisibility(View.GONE);
                //System.out.println("CHECK---> Response " + jsonObject);
                //findViewById(R.id.progressLayout).setVisibility(View.GONE);
                Gson gson = new Gson();

                ResultMain model = gson.fromJson(jsonObject, ResultMain.class);
                Status status = model.getStatus();
                String code = status.getCode();
                String message=status.getMessage();
                if (code.equals("200")){


                      String  DivisionId = model.getStandardDivisionID();

                        List<Code> subcanteen=model.getCode();

                        if (subcanteen.size()>0){


                                // err.setVisibility(View.GONE);
                            ResultmainRV.setVisibility(View.VISIBLE);
                            ResultmainRV.setLayoutManager(new LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false));
                            ResultmainRV.setItemAnimator(new DefaultItemAnimator());
                            mAdapter = new ResultAllSubjectsAdapter(subcanteen, activity, getApplicationContext(),mFirebaseAnalytics);
                            ResultmainRV.setAdapter(mAdapter);




                        }else {
//                            progressBar.setVisibility(View.GONE);
                            ResultmainRV.setVisibility(View.GONE);

                            NodataTV.setText(getResources().getString(R.string.no_data_found));
                            GoBackBTN.setText(getResources().getString(R.string.go_back));
//                        ErrorImage.setBackgroundResource(R.drawable.nodata);
                            findViewById(R.id.NodataLL).setVisibility(View.VISIBLE);
                            GoBackBTN.setVisibility(View.GONE);
                        }


                }
                else if(code.equals("400")){
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

                    NodataTV.setText(getResources().getString(R.string.no_data_found));
                    GoBackBTN.setText(getResources().getString(R.string.go_back));
//                        ErrorImage.setBackgroundResource(R.drawable.nodata);
                    findViewById(R.id.NodataLL).setVisibility(View.VISIBLE);
                    GoBackBTN.setOnClickListener(new View.OnClickListener() {
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
                Toast.makeText(ResultActivity.this, getResources().getString(R.string.network_error_try), Toast.LENGTH_SHORT).show();
            }
        });
        jsonObjectRequestLogin.setRetryPolicy(new DefaultRetryPolicy(50000, 3, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
//        RequestQueue requestQueue = Volley.newRequestQueue(this, commonSSLConnection.getHulkstack(ResultActivity.this));
        CampusWallet.getInstance().addToRequestQueue(jsonObjectRequestLogin, tag_json_object);
//        requestQueue.add(jsonObjectRequestLogin);
    }
}
