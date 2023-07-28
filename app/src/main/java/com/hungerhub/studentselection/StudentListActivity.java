package com.hungerhub.studentselection;

import android.app.Activity;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
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
import com.onesignal.OneSignal;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import com.hungerhub.application.CampusWallet;
import com.hungerhub.application.Constants;
import com.hungerhub.application.CustomConnectionBuddyActivity;
import com.hungerhub.application.PrefManager;
import com.hungerhub.networkmodels.studentlist.Code;
import com.hungerhub.networkmodels.studentlist.OnesignalKey;
import com.hungerhub.networkmodels.studentlist.Status;
import com.hungerhub.networkmodels.studentlist.StudentList;
import com.hungerhub.utils.CommonSSLConnection;

public class StudentListActivity extends CustomConnectionBuddyActivity {

    @BindView(R.id.recyclerView) RecyclerView mRecyclerView;
    @BindView(R.id.progressBar4) ProgressBar progressBar;
    @BindView(R.id.ReloadBTN)
    Button ReloadBTN;
    @BindView(R.id.GoBackBTN)
    Button GoBackBTN;
    @BindView(R.id.NodataTV)
    TextView NodataTV;
    @BindView(R.id.CodeErrorTV) TextView CodeErrorTV;
    @BindView(R.id.ErrorMessageTV) TextView ErrorMessageTV;
    final String tag_json_object = "r_student_list";
    PrefManager prefManager;
    Activity activity;
    private String AppVersion;
    String phoneNum;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_student_list_activity);
        ButterKnife.bind(this);
        activity=this;
        prefManager = new PrefManager(StudentListActivity.this);
        phoneNum = prefManager.getUserPhone();
        prefManager.setCheckNewSwitch("Done");


    }

    @Override
    protected void onResume() {
        super.onResume();
        PackageManager manager = getApplicationContext().getPackageManager();
        PackageInfo info = null;
        try {
            info = manager.getPackageInfo(
                    getPackageName(), 0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        AppVersion = info.versionName;
        Constants.BASE_URL=Constants.BASE_URL_ORIGINAL;
        setProgressBarVisible();

        LoadStudentList(phoneNum);
    }

    private void LoadStudentList(final String phoneNum) {


        String URL, parameters;
        parameters = "phoneNo=" + phoneNum;
        URL = Constants.BASE_URL + "r_student_list.php?" + parameters.replaceAll(" ", "%20");
        System.out.println("CHECK--->                                                  " + URL);
        CommonSSLConnection commonSSLConnection=new CommonSSLConnection();
        StringRequest jsonObjectRequestLogin = new StringRequest(Request.Method.GET, URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String jsonObject) {
                //System.out.println("CHECK---> Response " + jsonObject);
                setProgressBarGone();

                CampusWallet.getInstance().cancelPendingRequests(tag_json_object);

                Gson gson = new Gson();

                StudentList studentList = gson.fromJson(jsonObject,StudentList.class);

                Status status = studentList.getStatus();
                String code= status.getCode();

                if(code.equals("200")){

                    List<Code> myDataset= studentList.getCode();
                    if (myDataset.size()!=0)
                    {
                        for (int i=0;i<myDataset.size();i++)
                        {
                            List<OnesignalKey> onesignalKeys= myDataset.get(i).getOnesignalKeys();
                            for (int j=0;j<onesignalKeys.size();j++){
                                Log.i("Abhinand : ","onesignalkey");
                                OnesignalKey keyvalue=new OnesignalKey();
                                keyvalue =onesignalKeys.get(j);
                                if (keyvalue!=null) {
                                    Log.i("Abhinand : ","keyvalue");

                                    String KEY = keyvalue.getKey();
                                    String VALUE = String.valueOf(keyvalue.getValue());
                                    //onesignalKeys.add(KEY + " : " + VALUE);
                                    //System.out.println("CHILLAR TEST NEW : "+KEY+"   "+VALUE);

                                    OneSignal.sendTag(KEY, VALUE);
                                }
                            }
                            OneSignal.sendTag("App version", AppVersion);
                            OneSignal.sendTag("user_mobile", prefManager.getUserPhone());
                        }
                        mRecyclerView.setHasFixedSize(true);
                        mRecyclerView.setLayoutManager(new LinearLayoutManager(StudentListActivity.this, LinearLayoutManager.VERTICAL, false));
                        mRecyclerView.setNestedScrollingEnabled(false);
                        StudentListAdapter mAdapter = new StudentListAdapter(myDataset,activity,StudentListActivity.this,mFirebaseAnalytics);
                        mAdapter.notifyDataSetChanged();
                        mRecyclerView.setAdapter(mAdapter);
                    }
                    else
                    {
                        NodataTV.setText(getResources().getString(R.string.no_data_found));
                        GoBackBTN.setText(getResources().getString(R.string.go_back));
//                        ErrorImage.setBackgroundResource(R.drawable.nodata);
                        findViewById(R.id.NodataLL).setVisibility(View.VISIBLE);
                        GoBackBTN.setVisibility(View.GONE);
                    }


                }else{
                    setProgressBarGone();
                    String message = status.getMessage();
                    Toast.makeText(StudentListActivity.this, message, Toast.LENGTH_SHORT).show();
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
                setProgressBarGone();
                CampusWallet.getInstance().cancelPendingRequests(tag_json_object);
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
                VolleyLog.d("Object Error : ", volleyError.getMessage());
                Toast.makeText(StudentListActivity.this, getResources().getString(R.string.network_error_try), Toast.LENGTH_SHORT).show();
            }
        });

        jsonObjectRequestLogin.setRetryPolicy(new DefaultRetryPolicy(50000, 3, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
//        RequestQueue requestQueue = Volley.newRequestQueue(this, commonSSLConnection.getHulkstack(StudentListActivity.this));
        CampusWallet.getInstance().addToRequestQueue(jsonObjectRequestLogin, tag_json_object);
//        requestQueue.add(jsonObjectRequestLogin);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        CampusWallet.getInstance().cancelPendingRequests(tag_json_object);
    }

    public void setProgressBarVisible(){
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        progressBar.setVisibility(View.VISIBLE);

    }

    public void setProgressBarGone(){

        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        progressBar.setVisibility(View.GONE);

    }

}
