package com.hungerhub.Diary.Attendance;

import android.app.Activity;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import android.os.Bundle;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
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
import com.hungerhub.networkmodels.Attendance.AttendanceDetailsList;
import com.hungerhub.networkmodels.Attendance.Code;
import com.hungerhub.networkmodels.Attendance.LeaveDate;
import com.hungerhub.networkmodels.Attendance.Status;
import com.hungerhub.utils.CommonSSLConnection;

public class AttendanceActivity extends CustomConnectionBuddyActivity {
    @BindView(R.id.HeaderTV)
    TextView HeaderText;
    @BindView(R.id.BackIV)
    ImageView BackButton;
    @BindView(R.id.AttendanceRV)
    RecyclerView AttendanceRV;
    @BindView(R.id.NoofLeavesTV)
    TextView NoofLeavesTV;
    @BindView(R.id.PercentageofAttTV)
    TextView PercentageofAttTV;

    @BindView(R.id.NoofWorkingTV)
    TextView NoofWorkingTV;
    @BindView(R.id.AttStudentnameTV)
    TextView StudentnameTV;
    @BindView(R.id.ReloadBTN)
    Button ReloadBTN;
    @BindView(R.id.GoBackBTN)
    Button GoBackBTN;
    @BindView(R.id.NodataTV)
    TextView NodataTV;
    @BindView(R.id.CodeErrorTV) TextView CodeErrorTV;
    @BindView(R.id.ErrorMessageTV) TextView ErrorMessageTV;
    @BindView(R.id.AttendanceSRL)
    SwipeRefreshLayout AttendanceSRL;

    AttendanceAdapter adapter;
    PrefManager prefManager;
    String phoneNum,studentId,Studentname;
    Activity activity;
    final String tag_json_object = "r_attendance";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_attendance);
        ButterKnife.bind(this);
//        Fabric.with(this, new Answers());
        activity=this;
        prefManager=new PrefManager(AttendanceActivity.this);
        HeaderText.setText(getResources().getString(R.string.attendance_header));
        BackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        AttendanceSRL.setColorSchemeResources(R.color.colorAccent);
        AttendanceSRL.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                phpGetAttendanceMain();
                AttendanceSRL.setRefreshing(false);
            }
        });
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        CampusWallet.getInstance().cancelPendingRequests(tag_json_object);
    }
    @Override
    protected void onResume() {
        super.onResume();
        phpGetAttendanceMain();
    }
    public void phpGetAttendanceMain()
    {
       // progressBar.setVisibility(View.VISIBLE);
        phoneNum = prefManager.getUserPhone();  //getting phone number from shared preference
        studentId = prefManager.getStudentId();
        Studentname = prefManager.getStudentName();

        String URL, parameters;
        parameters = "phoneNo=" + phoneNum + "&studentID=" + studentId;
        URL = Constants.BASE_URL  + "r_attendance.php?" + parameters.replaceAll(" ", "%20");
        System.out.println("CHECK---> URL11 " + URL);
        System.out.println("ELDHOSE:: "+prefManager.getType());

        CommonSSLConnection commonSSLConnection=new CommonSSLConnection();
        StringRequest jsonObjectRequestLogin = new StringRequest(Request.Method.GET, URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String jsonObject) {
              //  progressBar.setVisibility(View.GONE);
//                System.out.println("CHECK---> Response " + jsonObject);
                //findViewById(R.id.progressLayout).setVisibility(View.GONE);
                Gson gson = new Gson();

                AttendanceDetailsList model = gson.fromJson(jsonObject, AttendanceDetailsList.class);
                Status status = model.getStatus();
                String code = status.getCode();



                if (code.equals("200")){
                    findViewById(R.id.ErrorLL).setVisibility(View.GONE);
                    Code listcode=model.getCode();
                    String workingdays=listcode.getNoOfWorkingDays();
                    String leavedays=listcode.getNoOfLeaveDays();

                    NoofLeavesTV.setText(""+leavedays);
                    NoofWorkingTV.setText(""+workingdays);
                    StudentnameTV.setText(" "+Studentname);  //from Shared preferance
                    float leave= Float.parseFloat(leavedays.trim());
                    float woking= Float.parseFloat(workingdays.trim());
                    float presentdays=woking-leave;


                    double a =(((double)presentdays/(double)woking)*100);

                    int finalValue = (int) (Math.round( a * 100 ) / 100);
                    //System.out.println("SHANIL3 " +finalValue);

                    PercentageofAttTV.setText(""+finalValue+"%");

                    List<LeaveDate> leavdate=listcode.getLeaveDates();
//                    if (leavdate.size()>0) {

                        findViewById(R.id.NodataLL).setVisibility(View.GONE);
                        AttendanceRV.setVisibility(View.VISIBLE);
                        AttendanceRV.setLayoutManager(new GridLayoutManager(activity, 3));
                        AttendanceRV.setItemAnimator(new DefaultItemAnimator());
                        AttendanceRV.setNestedScrollingEnabled(false);
                        adapter = new AttendanceAdapter(getApplicationContext(), leavdate,prefManager.getType(),mFirebaseAnalytics);

                        AttendanceRV.setAdapter(adapter);



//                    }else {
//                        NodataTV.setText(getResources().getString(R.string.no_data_found));
//                        GoBackBTN.setText(getResources().getString(R.string.go_back));
////                        ErrorImage.setBackgroundResource(R.drawable.nodata);
//                        findViewById(R.id.NodataLL).setVisibility(View.VISIBLE);
//                        GoBackBTN.setVisibility(View.GONE);
//                    }

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

                    NodataTV.setText(getResources().getString(R.string.no_data_found));
                    GoBackBTN.setText(getResources().getString(R.string.go_back));
//                        ErrorImage.setBackgroundResource(R.drawable.nodata);
                    findViewById(R.id.NodataLL).setVisibility(View.VISIBLE);
                    GoBackBTN.setVisibility(View.GONE);

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

                Toast.makeText(AttendanceActivity.this, getResources().getString(R.string.network_error_try), Toast.LENGTH_SHORT).show();
            }
        });
        jsonObjectRequestLogin.setRetryPolicy(new DefaultRetryPolicy(50000, 3, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
//        RequestQueue requestQueue = Volley.newRequestQueue(this, commonSSLConnection.getHulkstack(AttendanceActivity.this));
        CampusWallet.getInstance().addToRequestQueue(jsonObjectRequestLogin, tag_json_object);
//        requestQueue.add(jsonObjectRequestLogin);
    }

}
