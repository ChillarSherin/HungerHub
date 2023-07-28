package com.hungerhub.Diary.Attendance;

import android.app.Activity;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
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

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import com.hungerhub.application.CampusWallet;
import com.hungerhub.application.Constants;
import com.hungerhub.application.PrefManager;
import com.hungerhub.networkmodels.AttendancePopup.AttendancePopupClass;
import com.hungerhub.networkmodels.AttendancePopup.Code;
import com.hungerhub.networkmodels.AttendancePopup.Status;
import com.hungerhub.utils.CommonSSLConnection;

public class AttendancePopupActivity extends Activity {
    @BindView(R.id.HeaderTV)
    TextView HeaderText;
    @BindView(R.id.BackIV)
    ImageView BackButton;
    @BindView(R.id.attendancepopupRv)
    RecyclerView attendancepopupRv;

    @BindView(R.id.ReloadBTN)
    Button ReloadBTN;
    @BindView(R.id.GoBackBTN)
    Button GoBackBTN;
    @BindView(R.id.NodataTV)
    TextView NodataTV;
    @BindView(R.id.CodeErrorTV) TextView CodeErrorTV;
    @BindView(R.id.ErrorMessageTV) TextView ErrorMessageTV;

    PrefManager prefManager;
    String phoneNum,studentId,Studentname,date,datestr;
    Activity activity;
    final String tag_json_object = "r_attendance_detail";
    AttendancePopUpAdapter adapter;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        setTheme(R.style.AppDialogTheme); // can either use R.style.AppTheme_Dialog or R.style.AppTheme as defined in styles.xml

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_attendance_popup);
        getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);


        ButterKnife.bind(this);
        activity=this;
        prefManager=new PrefManager(AttendancePopupActivity.this);
        HeaderText.setText(getResources().getString(R.string.attendance_pop_header));
        BackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        Bundle b =getIntent().getExtras();
        date = b.getString("date");


        Date date1 = null;
        try {
            date1 = new SimpleDateFormat("dd-MM-yyyy").parse(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
// format the java.util.Date object to the desired format
        datestr = new SimpleDateFormat("yyyy-MM-dd").format(date1);

        phpGetAttendanceMain();
    }


    public void phpGetAttendanceMain()
    {
        // progressBar.setVisibility(View.VISIBLE);
        phoneNum = prefManager.getUserPhone();  //getting phone number from shared preference
        studentId = prefManager.getStudentId();
        Studentname = prefManager.getStudentName();

        String URL, parameters;
        parameters = "phoneNo=" + phoneNum + "&studentID=" + studentId+ "&date=" + datestr;
        URL = Constants.BASE_URL  + "r_absent_periods.php?" + parameters.replaceAll(" ", "%20");
        System.out.println("CHECK---> URL " + URL);
        CommonSSLConnection commonSSLConnection=new CommonSSLConnection();
        StringRequest jsonObjectRequestLogin = new StringRequest(Request.Method.GET, URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String jsonObject) {
                //  progressBar.setVisibility(View.GONE);
                System.out.println("CHECK---> Response " + jsonObject);
                //findViewById(R.id.progressLayout).setVisibility(View.GONE);
                Gson gson = new Gson();


                AttendancePopupClass model = gson.fromJson(jsonObject,AttendancePopupClass.class);
                Status status = model.getStatus();
                String code = status.getCode();
                if (code.equals("200")){
                    findViewById(R.id.ErrorLL).setVisibility(View.GONE);
                    List<Code> listcode=model.getCode();
                    if(listcode.size()>0){
                        findViewById(R.id.NodataLL).setVisibility(View.GONE);
                        attendancepopupRv.setVisibility(View.VISIBLE);
                        attendancepopupRv.setLayoutManager(new GridLayoutManager(activity, 3));
                        attendancepopupRv.setItemAnimator(new DefaultItemAnimator());
                        attendancepopupRv.setNestedScrollingEnabled(false);
                        adapter = new AttendancePopUpAdapter(getApplicationContext(), listcode);

                        attendancepopupRv.setAdapter(adapter);

                    }


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

                Toast.makeText(AttendancePopupActivity.this, getResources().getString(R.string.network_error_try), Toast.LENGTH_SHORT).show();
            }
        });
        jsonObjectRequestLogin.setRetryPolicy(new DefaultRetryPolicy(50000, 3, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
//        RequestQueue requestQueue = Volley.newRequestQueue(this, commonSSLConnection.getHulkstack(AttendanceActivity.this));
        CampusWallet.getInstance().addToRequestQueue(jsonObjectRequestLogin, tag_json_object);
//        requestQueue.add(jsonObjectRequestLogin);
    }
}
