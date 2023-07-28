package com.hungerhub.Diary.Gallary;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Build;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
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
import com.hungerhub.networkmodels.Gallery.Code;
import com.hungerhub.networkmodels.Gallery.GalleryData;
import com.hungerhub.networkmodels.Gallery.Status;
import com.hungerhub.utils.CommonSSLConnection;

public class GalleryActivity extends CustomConnectionBuddyActivity {
    private static final int MY_PERMISSIONS_REQUEST_READ_CAMERA = 555;
    @BindView(R.id.ProgressBar)
    ProgressBar progressBar;
    @BindView(R.id.HeaderTV)
    TextView HeaderText;
    @BindView(R.id.StudentSTDStoriesTV)
    TextView StudentSTDStoriesTV;
    @BindView(R.id.studentname)
    TextView studentnameTV;
    @BindView(R.id.BackIV)
    ImageView BackButton;
    @BindView(R.id.timelinelist)
    RecyclerView timelinelistRV;
    PrefManager prefManager;
    String phoneNum,studentId,Studentname,StudentSTDDIV;
    Activity activity;
    private GalleryAdapter mAdapter;
    @BindView(R.id.ReloadBTN)
    Button ReloadBTN;
    @BindView(R.id.GoBackBTN)
    Button GoBackBTN;
    @BindView(R.id.NodataTV)
    TextView NodataTV;
    @BindView(R.id.CodeErrorTV) TextView CodeErrorTV;
    @BindView(R.id.ErrorMessageTV) TextView ErrorMessageTV;
    @BindView(R.id.timelinelistSRL)
    SwipeRefreshLayout timelinelistSRL;
    final String tag_json_object = "r_teacher_timeline";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gallery);
        ButterKnife.bind(this);
//        Fabric.with(this, new Answers());
        activity=this;
        prefManager=new PrefManager(GalleryActivity.this);
        progressBar.setVisibility(View.GONE);

        HeaderText.setText(getResources().getString(R.string.gallery_header));
        BackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        timelinelistSRL.setColorSchemeResources(R.color.colorAccent);

    }

    @Override
    protected void onResume() {
        super.onResume();
        phoneNum = prefManager.getUserPhone();  //getting phone number from shared preference
        studentId = prefManager.getStudentId();
        Studentname = prefManager.getStudentName();
        StudentSTDDIV = prefManager.getStudentStandardDivision();
        studentnameTV.setText(""+Studentname);
        StudentSTDStoriesTV.setText(StudentSTDDIV);
        if (Build.VERSION.SDK_INT >= 23) {
            CheckPermissionCAM();
        }
        else {

            MessagesFromTeacher();
        }
        timelinelistSRL.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (Build.VERSION.SDK_INT >= 23) {
                    CheckPermissionCAM();
                }
                else {

                    MessagesFromTeacher();
                }
                timelinelistSRL.setRefreshing(false);
            }
        });
    }
    public void CheckPermissionCAM() {
        // Here, thisActivity is the current activity
        if (ContextCompat.checkSelfPermission(getApplicationContext(),
                Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(getApplicationContext(),
                Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            //System.out.println("PERMISSION NOT GRANTED ");
            // Permission is not granted
            // Should we show an explanation?
            if (
                    ActivityCompat.shouldShowRequestPermissionRationale(GalleryActivity.this,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE) &&
                            ActivityCompat.shouldShowRequestPermissionRationale(GalleryActivity.this,
                                    Manifest.permission.READ_EXTERNAL_STORAGE)) {
                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
                //System.out.println("PERMISSION RETRY ");
                ActivityCompat.requestPermissions(GalleryActivity.this,
                        new String[]{ Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE},
                        MY_PERMISSIONS_REQUEST_READ_CAMERA);
            } else {
                // No explanation needed; request the permission
                ActivityCompat.requestPermissions(GalleryActivity.this,
                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE},
                        MY_PERMISSIONS_REQUEST_READ_CAMERA);

                // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                // app-defined int constant. The callback method gets the
                // result of the request.
                //System.out.println("PERMISSION REQUESTED ");
            }
        } else {
            // Permission has already been granted
            MessagesFromTeacher();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_READ_CAMERA: {
                // If request is cancelled, the result arrays are empty.
                //System.out.println("PERMISSION ARRAY SIZE : " + grantResults.length);
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED
                        && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
                    // dispatchTakePictureIntent();
                    //System.out.println("PERMISSION GRANTED ");

                    try {
                        MessagesFromTeacher();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    //galleryAddPic();
                    // setPic();
                } else {
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    //System.out.println("PERMISSION DENIED ");
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request.
        }
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        CampusWallet.getInstance().cancelPendingRequests(tag_json_object);
    }
    public void MessagesFromTeacher()
    {
         progressBar.setVisibility(View.VISIBLE);
        phoneNum = prefManager.getUserPhone();  //getting phone number from shared preference
        studentId = prefManager.getStudentId();
        Studentname = prefManager.getStudentName();

        String URL, parameters;
        parameters = "phoneNo=" + phoneNum + "&studentID=" + studentId;
        URL = Constants.BASE_URL  + "r_teacher_timeline.php?" + parameters.replaceAll(" ", "%20");
        System.out.println("CHECK---> URL " + URL);
        CommonSSLConnection commonSSLConnection=new CommonSSLConnection();
        StringRequest jsonObjectRequestLogin = new StringRequest(Request.Method.GET, URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String jsonObject) {
                  progressBar.setVisibility(View.GONE);
                //System.out.println("CHECK---> Response " + jsonObject);
                //findViewById(R.id.progressLayout).setVisibility(View.GONE);
                Gson gson = new Gson();

                GalleryData model = gson.fromJson(jsonObject, GalleryData.class);
                Status status = model.getStatus();
                String code = status.getCode();
                if (code.equals("200")){
                    List<Code> relationship=model.getCode();
                    if (relationship.size()>0) {
                        timelinelistRV.setVisibility(View.VISIBLE);
                        timelinelistRV.setLayoutManager(new LinearLayoutManager(GalleryActivity.this, LinearLayoutManager.VERTICAL, false));
                        timelinelistRV.setItemAnimator(new DefaultItemAnimator());
                        timelinelistRV.setNestedScrollingEnabled(false);
                        mAdapter = new GalleryAdapter(relationship, GalleryActivity.this, GalleryActivity.this,mFirebaseAnalytics);
                        timelinelistRV.setAdapter(mAdapter);
                       // findViewById(R.id.ErrorLayoutnew).setVisibility(View.GONE);




                    }else {
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
                //progressBar.setVisibility(View.GONE);
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
                Toast.makeText(GalleryActivity.this, getResources().getString(R.string.network_error_try), Toast.LENGTH_SHORT).show();
            }
        });
        jsonObjectRequestLogin.setRetryPolicy(new DefaultRetryPolicy(50000, 3, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
//        RequestQueue requestQueue = Volley.newRequestQueue(this, commonSSLConnection.getHulkstack(GalleryActivity.this));
        CampusWallet.getInstance().addToRequestQueue(jsonObjectRequestLogin, tag_json_object);
//        requestQueue.add(jsonObjectRequestLogin);
    }

}
