package com.hungerhub.Diary.LeaveRequest;

import androidx.fragment.app.FragmentTransaction;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.view.WindowManager;
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
import butterknife.OnClick;
import com.hungerhub.application.CampusWallet;
import com.hungerhub.application.Constants;
import com.hungerhub.application.CustomConnectionBuddyActivity;
import com.hungerhub.application.PrefManager;
import com.hungerhub.networkmodels.leaverequest.Code;
import com.hungerhub.networkmodels.leaverequest.Leaverequest;
import com.hungerhub.networkmodels.leaverequest.Status;
import com.hungerhub.utils.CommonSSLConnection;


public class LeaveRequestActivity extends CustomConnectionBuddyActivity implements CallBack{

    PrefManager prefManager;
    final String tag_json_object = "r_leave_request";
    @BindView(R.id.progressBar) ProgressBar mProgressBar;
    @BindView(R.id.recyclerView4) RecyclerView mRecyclerView;
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
    @BindView(R.id.CodeErrorTV) TextView CodeErrorTV;
    @BindView(R.id.ErrorMessageTV) TextView ErrorMessageTV;
    @BindView(R.id.LeaveRequesrSRL)
    SwipeRefreshLayout LeaveRequesrSRL;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_leave_req_activity);
        ButterKnife.bind(this);
        prefManager = new PrefManager(this);

        HeaderText.setText(getResources().getString(R.string.leaverequest_header));
        BackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        LeaveRequesrSRL.setColorSchemeResources(R.color.colorAccent);

    }

    @Override
    protected void onResume() {
        super.onResume();

        loadLeaveRequest();
        LeaveRequesrSRL.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                loadLeaveRequest();
                LeaveRequesrSRL.setRefreshing(false);
            }
        });
    }

    @OnClick(R.id.floatingActionButton)
    public void createLeave(){
        mFirebaseAnalytics.logEvent(getResources().getString(R.string.New_Leave_Request_Add_Button_Clicked),new Bundle());
        CreateLeaveRequestDialog dialog = new CreateLeaveRequestDialog(LeaveRequestActivity.this);
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        dialog.show(ft, CreateLeaveRequestDialog.TAG);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        CampusWallet.getInstance().cancelPendingRequests(tag_json_object);
    }


    private void loadLeaveRequest() {


        String userPhone = prefManager.getUserPhone();
        String studentId = prefManager.getStudentId();
        setProgressBarVisible();
        String URL, parameters;
        parameters = "phoneNo=" +userPhone+"&studentID="+studentId;
        URL = Constants.BASE_URL + "r_leave_request.php?" + parameters.replaceAll(" ", "%20");
        //System.out.println("CHECK---> URL " + URL);
        CommonSSLConnection commonSSLConnection=new CommonSSLConnection();
        StringRequest jsonObjectRequestLogin = new StringRequest(Request.Method.GET, URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String jsonObject) {
                //System.out.println("CHECK---> Response " + jsonObject);
                setProgressBarGone();

                CampusWallet.getInstance().cancelPendingRequests(tag_json_object);

                Gson gson = new Gson();
                Leaverequest leaverequest =gson.fromJson(jsonObject,Leaverequest.class);
                Status status = leaverequest.getStatus();
                String code = status.getCode();


                if(code.equals("200")){

                    List<Code> myDataset= leaverequest.getCode();
                    if (myDataset.size()>0) {

                        mRecyclerView.setHasFixedSize(true);
                        mRecyclerView.setLayoutManager(new LinearLayoutManager(LeaveRequestActivity.this, LinearLayoutManager.VERTICAL, false));
                        mRecyclerView.setNestedScrollingEnabled(false);
                        LeaveRequestAdapter mAdapter = new LeaveRequestAdapter(myDataset, LeaveRequestActivity.this,
                                LeaveRequestActivity.this,mFirebaseAnalytics);
                        mAdapter.notifyDataSetChanged();
                        mRecyclerView.setAdapter(mAdapter);
                    }
                    else {
                        NodataTV.setText(getResources().getString(R.string.no_data_found));
                        GoBackBTN.setText(getResources().getString(R.string.go_back));
//                        ErrorImage.setBackgroundResource(R.drawable.nodata);
                        findViewById(R.id.NodataLL).setVisibility(View.VISIBLE);
                        GoBackBTN.setVisibility(View.GONE);
                    }

                }
                else if (code.equals("204"))
                {
                    NodataTV.setText(getResources().getString(R.string.no_data_found));
                    GoBackBTN.setText(getResources().getString(R.string.go_back));
//                        ErrorImage.setBackgroundResource(R.drawable.nodata);
                    findViewById(R.id.NodataLL).setVisibility(View.VISIBLE);
                    GoBackBTN.setVisibility(View.GONE);
                }

                else{
                    setProgressBarGone();
                    String message = status.getMessage();
                    Toast.makeText(LeaveRequestActivity.this, message, Toast.LENGTH_SHORT).show();
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
                Toast.makeText(LeaveRequestActivity.this, getResources().getString(R.string.network_error_try), Toast.LENGTH_SHORT).show();
            }
        });

        jsonObjectRequestLogin.setRetryPolicy(new DefaultRetryPolicy(50000, 3, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
//        RequestQueue requestQueue = Volley.newRequestQueue(this, commonSSLConnection.getHulkstack(LeaveRequestActivity.this));
        CampusWallet.getInstance().addToRequestQueue(jsonObjectRequestLogin, tag_json_object);
//        requestQueue.add(jsonObjectRequestLogin);

    }

    public void setProgressBarVisible(){
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        mProgressBar.setVisibility(View.VISIBLE);

    }

    public void setProgressBarGone(){

        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        mProgressBar.setVisibility(View.GONE);

    }


    @Override
    public void message(String message) {
        //System.out.println(message);

        if(message.equals("200")){

            loadLeaveRequest();

        }
    }
}
