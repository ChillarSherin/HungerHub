package com.hungerhub.Diary.TimeTable;

import android.app.Activity;
import android.os.Bundle;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
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
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import com.hungerhub.application.CampusWallet;
import com.hungerhub.application.Constants;
import com.hungerhub.application.CustomConnectionBuddyActivity;
import com.hungerhub.application.PrefManager;
import com.hungerhub.networkmodels.TimeTable.Code;
import com.hungerhub.networkmodels.TimeTable.Status;
import com.hungerhub.networkmodels.TimeTable.TimeTable;
import com.hungerhub.utils.CommonSSLConnection;

public class TimeTableActivity extends CustomConnectionBuddyActivity {

    @BindView(R.id.TimeTablePB)
    ProgressBar progressBar;
    @BindView(R.id.HeaderTV)
    TextView HeaderText;
    @BindView(R.id.BackIV)
    ImageView BackButton;
    @BindView(R.id.StudentnameTV)
    TextView StudentnameTV;
    @BindView(R.id.StudentSTDTV)
    TextView StudentSTDTV;
    @BindView(R.id.DayTimetableBTN)
    Spinner DayTimetableBTN;
    @BindView(R.id.TimetablemainRV)
    RecyclerView TimetablemainRV;
    List<String> list;
    private int todayposString;
    private int spinnerposition;
    private final ArrayList<HashMap<String, String>> timetablelist = new ArrayList<>();
    private String spinneritemno;
    private String parentPh,s_Id;
    private int datetoday;
    Activity activity;
    private final List<String> SubId = new ArrayList<>();
    private final List<String> SubName = new ArrayList<>();
    private final List<String> Teacher = new ArrayList<>();
    private final List<String> Day = new ArrayList<>();
    private final List<String> Order = new ArrayList<>();
    PrefManager prefManager;
    String phoneNum,studentId;
    private TimeTableAdapter mAdapter;
    @BindView(R.id.ReloadBTN)
    Button ReloadBTN;
    @BindView(R.id.GoBackBTN)
    Button GoBackBTN;
    @BindView(R.id.NodataTV)
    TextView NodataTV;

    @BindView(R.id.CodeErrorTV) TextView CodeErrorTV;
    @BindView(R.id.ErrorMessageTV) TextView ErrorMessageTV;
    final String tag_json_object = "r_time_table";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_time_table);
        ButterKnife.bind(this);
        activity=this;

        progressBar.setVisibility(View.GONE);

        HeaderText.setText(getResources().getString(R.string.timetable_header));
        BackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        prefManager = new PrefManager(TimeTableActivity.this);

        list = new ArrayList<>();
        list.add(getResources().getString(R.string.monday));
        list.add(getResources().getString(R.string.tuesday));
        list.add(getResources().getString(R.string.wednesday));
        list.add(getResources().getString(R.string.thursday));
        list.add(getResources().getString(R.string.friday));
        list.add(getResources().getString(R.string.saturday));

        Calendar c = Calendar.getInstance();
        datetoday = (c.get(Calendar.DAY_OF_WEEK));
        //System.out.println("dateReal : "+datetoday);
        if(datetoday>7){
            datetoday=1;
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
//        spinner Adapter

        StudentnameTV.setText(prefManager.getStudentName());
        StudentSTDTV.setText(prefManager.getStudentStandardDivision());


        ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<>(this, R.layout.item_spinner_white, list); //selected item will look like a spinner set from XML
        spinnerArrayAdapter.setDropDownViewResource(R.layout.item_spinner);
        DayTimetableBTN.setAdapter(spinnerArrayAdapter);
        getSpinner();


    }
    public void getSpinner()
    {
        phoneNum = prefManager.getUserPhone();  //getting phone number from shared preference
        studentId = prefManager.getStudentId();
        todayposString=(datetoday - 2);
        //System.out.println("Todays'S dtae " + datetoday);

        DayTimetableBTN.setSelection(todayposString);
        DayTimetableBTN.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                if(timetablelist.size()<0){
                    //System.out.println("NoDatTaTime " + "True");
                }
                //System.out.println("TimeTableListBeforeClear " + timetablelist);
                timetablelist.clear();
                //System.out.println("TimeTableListBeforeClear " + timetablelist);
                spinnerposition = DayTimetableBTN.getSelectedItemPosition();
                spinneritemno = String.valueOf(spinnerposition + 1);


                //System.out.println("position is " + spinneritemno);
                String url = Constants.BASE_URL+"r_time_table.php?phoneNo="+phoneNum+"&studentID="+studentId+"&day="+spinneritemno;
                //System.out.println("Parameters : "+url);
                mFirebaseAnalytics.logEvent(getResources().getString(R.string.Time_Table_Day_Selected),new Bundle());
                phpTimeTable(url);

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }

        });
    }
    public void phpTimeTable(String URL)
    {
        progressBar.setVisibility(View.VISIBLE);


        URL = URL.replaceAll(" ", "%20");
        //System.out.println("CHECK---> URL " + URL);
        CommonSSLConnection commonSSLConnection=new CommonSSLConnection();
        StringRequest jsonObjectRequestLogin = new StringRequest(Request.Method.GET, URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String jsonObject) {
                progressBar.setVisibility(View.GONE);
                //System.out.println("CHECK---> Response " + jsonObject);
                //findViewById(R.id.progressLayout).setVisibility(View.GONE);
                Gson gson = new Gson();

                TimeTable model = gson.fromJson(jsonObject, TimeTable.class);
                Status status = model.getStatus();
                String code = status.getCode();

                if (code.equals("200")) {

                    Day.clear();
                    SubId.clear();
                    SubName.clear();
                    Teacher.clear();
                    Order.clear();
                    findViewById(R.id.ErrorLL).setVisibility(View.GONE);
                    findViewById(R.id.NodataLL).setVisibility(View.GONE);
                    List<Code> relationship = model.getCode();
                    if (relationship.size() > 0) {
                        for (int i = 0; i < relationship.size(); i++) {

                            Code relncode = relationship.get(i);
                            Day.add(relncode.getDay());
                            SubId.add(relncode.getSubjectId());
                            SubName.add(relncode.getSubjectName());
                            Teacher.add(relncode.getTeacher());
                            Order.add(relncode.getOrder());

                            progressBar.setVisibility(View.GONE);
                        }
                        try {

                            if (Day.size() > 0) {

                                //System.out.println("daysize : " + Day.size());
                                TimetablemainRV.setVisibility(View.VISIBLE);
//                                TimetableListview.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
                                TimetablemainRV.setLayoutManager(new LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false));
                                TimetablemainRV.setItemAnimator(new DefaultItemAnimator());
                                TimetablemainRV.setNestedScrollingEnabled(false);
                                mAdapter = new TimeTableAdapter(SubId, SubName, Teacher, Day, Order, activity,
                                        R.layout.timetable_listitem, getApplicationContext(),mFirebaseAnalytics);

                                TimetablemainRV.setAdapter(mAdapter);
                            } else {
                                //System.out.println("daysize2 : " + Day.size());
                                TimetablemainRV.setVisibility(View.GONE);
                                NodataTV.setText(getResources().getString(R.string.no_data_found));
                                GoBackBTN.setText(getResources().getString(R.string.go_back));
//                        ErrorImage.setBackgroundResource(R.drawable.nodata);
                                findViewById(R.id.NodataLL).setVisibility(View.VISIBLE);
                                GoBackBTN.setVisibility(View.GONE);

                            }

                        } catch (Exception e) // no guruji
                        {
                            //System.out.println("ExceptiomHere " + e.toString());
                            e.printStackTrace();
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
                    } else {
                        TimetablemainRV.setVisibility(View.GONE);
//                        ErrorLayout.setVisibility(View.VISIBLE);
                        String Msg = status.getMessage();
//                        Toast.makeText(TimeTable_Activity.this, "  " + Msg, Toast.LENGTH_SHORT).show();
                        NodataTV.setText(getResources().getString(R.string.no_data_found));
                        GoBackBTN.setText(getResources().getString(R.string.go_back));
//                        ErrorImage.setBackgroundResource(R.drawable.nodata);
                        findViewById(R.id.NodataLL).setVisibility(View.VISIBLE);
                        GoBackBTN.setVisibility(View.GONE);
                    }

                } else if(code.equals("400")){

                    TimetablemainRV.setVisibility(View.GONE);
//                    ErrorLayout.setVisibility(View.VISIBLE);
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

                    TimetablemainRV.setVisibility(View.GONE);
                    NodataTV.setText(getResources().getString(R.string.no_data_found));
                    GoBackBTN.setText(getResources().getString(R.string.go_back));
//                        ErrorImage.setBackgroundResource(R.drawable.nodata);
                    findViewById(R.id.NodataLL).setVisibility(View.VISIBLE);
                    GoBackBTN.setVisibility(View.GONE);

                }else if (code.equals("401")){
                    TimetablemainRV.setVisibility(View.GONE);
//                    ErrorLayout.setVisibility(View.VISIBLE);
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

                    TimetablemainRV.setVisibility(View.GONE);
//                    ErrorLayout.setVisibility(View.VISIBLE);
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
                Toast.makeText(TimeTableActivity.this, getResources().getString(R.string.network_error_try), Toast.LENGTH_SHORT).show();
            }
        });
        jsonObjectRequestLogin.setRetryPolicy(new DefaultRetryPolicy(50000, 3, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
//        RequestQueue requestQueue = Volley.newRequestQueue(this, commonSSLConnection.getHulkstack(TimeTableActivity.this));
        CampusWallet.getInstance().addToRequestQueue(jsonObjectRequestLogin, tag_json_object);
//        requestQueue.add(jsonObjectRequestLogin);
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        CampusWallet.getInstance().cancelPendingRequests(tag_json_object);
    }
}
