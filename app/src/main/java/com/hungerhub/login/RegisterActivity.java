package com.hungerhub.login;

import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.Nullable;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.chillarcards.cookery.R;
import com.google.gson.Gson;
//import com.moe.pushlibrary.MoEHelper;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import com.hungerhub.application.CampusWallet;
import com.hungerhub.application.Constants;
import com.hungerhub.application.CustomConnectionBuddyActivity;
import com.hungerhub.application.PrefManager;
import com.hungerhub.networkmodels.updateuser.Status;
import com.hungerhub.networkmodels.updateuser.UpdateUser;
import com.hungerhub.studentselection.StudentListActivity;
import com.hungerhub.utils.CommonSSLConnection;


/**
 * Created by Codmob on 17-07-18.
 */

public class RegisterActivity extends CustomConnectionBuddyActivity {


    @BindView(R.id.userPhone) EditText et_userPhone;
    @BindView(R.id.userEmail) EditText et_userEmail;
    @BindView(R.id.userName) EditText et_userName;
    @BindView(R.id.button3) Button btnContinue;
    @BindView(R.id.progressBar3) ProgressBar progressBar;
    final String tag_json_object = "json_obj_u_user_details";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_register_activity);
        ButterKnife.bind(this);

        Bundle b = getIntent().getExtras();
        String userPhone = b.getString("userPhone");
        String userName = b.getString("userName");
        String userEmail = b.getString("userEmail");

        et_userName.setText(userName);
        et_userEmail.setText(userEmail);
        et_userPhone.setText(userPhone);
        et_userPhone.setEnabled(false);



    }


    @OnClick(R.id.button3)
    public void setBtnContinue(Button btnContinue) {
        this.btnContinue = btnContinue;
        btnContinue.setText(getResources().getString(R.string.u_p_d_a_t_i_n_g));

        String phoneNum = et_userPhone.getText().toString();
        String userEmail = et_userEmail.getText().toString();
        String userName = et_userName.getText().toString();




        if(userEmail.isEmpty()||userName.isEmpty()){
            Toast.makeText(this, "Please fill all details", Toast.LENGTH_SHORT).show();
        }else{

            setProgressBarVisible();

            Bundle Firebase_bundle = new Bundle();
            Firebase_bundle.putString("MobileNumber",phoneNum);
            Firebase_bundle.putString("Email",userEmail);
            Firebase_bundle.putString("UserName",userName);
            mFirebaseAnalytics.logEvent(getResources().getString(R.string.Register_Button_Clicked),Firebase_bundle);
            mFirebaseAnalytics.setUserProperty(getResources().getString(R.string.UP_UserName),userName);
            mFirebaseAnalytics.setUserProperty(getResources().getString(R.string.UP_UserEmail),userEmail);
            mFirebaseAnalytics.setUserProperty(getResources().getString(R.string.UP_UserPhone),phoneNum);

            UpdateUser(phoneNum,userEmail,userName);
        }


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

    private void UpdateUser(final String phoneNum, final String userEmail, final String userName) {


        String URL, parameters;
        CommonSSLConnection commonSSLConnection=new CommonSSLConnection();
        parameters = "phoneNo=" + phoneNum +"&userEmail="+userEmail+"&userName="+userName+"&companyId="+1;
        URL = Constants.BASE_URL + "u_user_details.php?" + parameters.replaceAll(" ", "%20");
        System.out.println("CHECK---> URL " + URL);
        StringRequest jsonObjectRequestLogin = new StringRequest(Request.Method.GET, URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String jsonObject) {
                System.out.println("CHECK---Response-> "+jsonObject);
                setProgressBarGone();
                CampusWallet.getInstance().cancelPendingRequests(tag_json_object);
                Gson gson = new Gson();
                UpdateUser updateUser = gson.fromJson(jsonObject, UpdateUser.class);

                Status status = updateUser.getStatus();
                String code = status.getCode();
                if (code.equals("200")) {

                    btnContinue.setText(getResources().getString(R.string.S_u_c_c_e_s_s));

                    PrefManager prefManager = new PrefManager(RegisterActivity.this);
                    prefManager.setIsNumberVerified(true);
                    prefManager.setFirstTimeLaunch(false);
                    prefManager.setUserEmail(userEmail);
                    prefManager.setUserName(userName);
                    prefManager.setUserPhone(phoneNum);

                    //UNIQUE_ID is used to uniquely identify a user.
                    String UNIQUE_ID=phoneNum;
//                    MoEHelper.getInstance(getApplicationContext()).setUniqueId(UNIQUE_ID);


                    Intent intent=new Intent(RegisterActivity.this,StudentListActivity.class);
                    startActivity(intent);
                    finish();


                }else{
                    setProgressBarGone();
                    btnContinue.setText(getResources().getString(R.string.c_o_n_t_i_n_u_e));
                    String message = status.getMessage();
                    Toast.makeText(RegisterActivity.this, message, Toast.LENGTH_SHORT).show();
                }


            }
        }, new Response.ErrorListener()

        {
            @Override
            public void onErrorResponse (VolleyError volleyError){
                setProgressBarGone();
                btnContinue.setText(getResources().getString(R.string.c_o_n_t_i_n_u_e));
                CampusWallet.getInstance().cancelPendingRequests(tag_json_object);

            }


        });
        jsonObjectRequestLogin.setRetryPolicy(new DefaultRetryPolicy(50000, 3, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
//        RequestQueue requestQueue = Volley.newRequestQueue(this, commonSSLConnection.getHulkstack(RegisterActivity.this));
        CampusWallet.getInstance().addToRequestQueue(jsonObjectRequestLogin, tag_json_object);
//        requestQueue.add(jsonObjectRequestLogin);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        CampusWallet.getInstance().cancelPendingRequests(tag_json_object);
    }
}


