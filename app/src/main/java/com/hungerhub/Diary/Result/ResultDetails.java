package com.hungerhub.Diary.Result;

import android.app.Activity;
import android.os.Bundle;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
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
import com.hungerhub.networkmodels.ExamResults.Code;
import com.hungerhub.networkmodels.ExamResults.ExamResult;
import com.hungerhub.networkmodels.ExamResults.Result;
import com.hungerhub.networkmodels.ExamResults.Status;
import com.hungerhub.utils.CommonSSLConnection;

public class ResultDetails extends CustomConnectionBuddyActivity {
    @BindView(R.id.HeaderTV)
    TextView HeaderText;
    @BindView(R.id.s_name) TextView s_name;
    @BindView(R.id.exm_subj) TextView exm_subj;
    @BindView(R.id.exm_name) TextView exm_name;
    @BindView(R.id.teacher_name) TextView teacher_name;
    @BindView(R.id.tot_marks) TextView tot_marks;
    @BindView(R.id.percent) TextView percent;
    @BindView(R.id.classavg) TextView classavg;
    @BindView(R.id.recyclerView) RecyclerView recyclerView;
    @BindView(R.id.progressBar) ProgressBar progressBar;
    @BindView(R.id.BackIV)
    ImageView BackButton;
    Activity activity;
    PrefManager prefManager;
    String phoneNum,studentId,Studentname;
    private ResultDetailsAdapter mAdapter;
    String ExamID,ClassAvgString;
    @BindView(R.id.ReloadBTN)
    Button ReloadBTN;
    @BindView(R.id.GoBackBTN)
    Button GoBackBTN;
    @BindView(R.id.NodataTV)
    TextView NodataTV;
    @BindView(R.id.CodeErrorTV) TextView CodeErrorTV;
    @BindView(R.id.ErrorMessageTV) TextView ErrorMessageTV;
    final String tag_json_object = "r_exam_result";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result_details);
        ButterKnife.bind(this);
        activity=this;
        prefManager = new PrefManager(this);

        Bundle bundle = getIntent().getExtras();
        if (bundle!=null) {
            ExamID = bundle.getString("ExamID");
            ClassAvgString = bundle.getString("ClassAvg");
        }

        HeaderText.setText(getResources().getString(R.string.results_header));
        BackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        progressBar.setVisibility(View.GONE);
    }

    @Override
    protected void onResume() {
        super.onResume();
        phpGetExamResults(ExamID);
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        CampusWallet.getInstance().cancelPendingRequests(tag_json_object);
    }
    public void phpGetExamResults(String examID)
    {
        progressBar.setVisibility(View.VISIBLE);
        phoneNum = prefManager.getUserPhone();  //getting phone number from shared preference
        studentId = prefManager.getStudentId();
        Studentname = prefManager.getStudentName();

        String URL, parameters;
        parameters = "phoneNo=" + phoneNum + "&studentID=" + studentId+"&examID="+examID;
        URL = Constants.BASE_URL  + "r_exam_result.php?" + parameters.replaceAll(" ", "%20");
        //System.out.println("CHECK---> URL " + URL);
        CommonSSLConnection commonSSLConnection=new CommonSSLConnection();
        StringRequest jsonObjectRequestLogin = new StringRequest(Request.Method.GET, URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String jsonObject) {
                progressBar.setVisibility(View.GONE);
                //System.out.println("CHECK---> Response " + jsonObject);
                //findViewById(R.id.progressLayout).setVisibility(View.GONE);
                Gson gson = new Gson();

                ExamResult model = gson.fromJson(jsonObject, ExamResult.class);
                Status status = model.getStatus();
                String code = status.getCode();
                String message=status.getMessage();
                if (code.equals("200")){

                    Code data=model.getCode();
                    String examdate=data.getExamDate();
                    String examid=data.getExamID();
                    String examName=data.getExamName();
                    String subjectname=data.getSubjectname();
                    String teacherName=data.getTeacherName();
                    String TotalMark=data.getTotalMark();

                    Log.i("shanil ",examid);

                    exm_name.setText(examName+"");
                    exm_subj.setText(subjectname+"");
                    teacher_name.setText(teacherName+"");
                    tot_marks.setText(TotalMark+"");
                    percent.setText(examdate+"");
                    s_name.setText(Studentname);





                    List<Result> leavdate=data.getResult();
                    if (leavdate.size()>0) {
//
                        findViewById(R.id.NodataLL).setVisibility(View.GONE);
                        recyclerView.setVisibility(View.VISIBLE);
                        recyclerView.setLayoutManager(new LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false));
                        recyclerView.setItemAnimator(new DefaultItemAnimator());
                        recyclerView.setNestedScrollingEnabled(false);
                        mAdapter = new ResultDetailsAdapter(leavdate,activity, getApplicationContext());
                        recyclerView.setAdapter(mAdapter);


                    }else {
                        recyclerView.setVisibility(View.GONE);

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
                Toast.makeText(ResultDetails.this, getResources().getString(R.string.network_error_try), Toast.LENGTH_SHORT).show();
            }
        });
        jsonObjectRequestLogin.setRetryPolicy(new DefaultRetryPolicy(50000, 3, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
//        RequestQueue requestQueue = Volley.newRequestQueue(this, commonSSLConnection.getHulkstack(ResultDetails.this));
        CampusWallet.getInstance().addToRequestQueue(jsonObjectRequestLogin, tag_json_object);
//        requestQueue.add(jsonObjectRequestLogin);
    }
}
