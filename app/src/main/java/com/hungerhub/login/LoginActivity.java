package com.hungerhub.login;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import androidx.annotation.Nullable;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.StringRequest;
import com.hungerhub.R;
import com.google.gson.Gson;

import org.json.JSONObject;

import java.util.Iterator;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import com.hungerhub.application.CampusWallet;
import com.hungerhub.application.Constants;
import com.hungerhub.application.CustomConnectionBuddyActivity;
import com.hungerhub.networkmodels.checkuser.CheckUser;
import com.hungerhub.networkmodels.checkuser.Code;
import com.hungerhub.networkmodels.checkuser.Status;
import com.hungerhub.utils.CommonSSLConnection;
import it.sephiroth.android.library.tooltip.Tooltip;


public class LoginActivity extends CustomConnectionBuddyActivity {

    final String tag_json_object = "json_obj_req_r_login";
    @BindView(R.id.phoneNumber) EditText etPhonenum;
    @BindView(R.id.button3) Button btnLogin;
    @BindView(R.id.progressBar) ProgressBar progressBar;
    @BindView(R.id.ContactUsTV)
    TextView ContactUsTV;
    private static final int PERMISSIONS_REQUEST_PHONE_CALL = 100;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_login_activity);
        ButterKnife.bind(this);


//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//            etPhonenum.setTooltipText(getResources().getString(R.string.please_enter_reg_mobile));
//        }
//        else {
//            Tooltip.make(LoginActivity.this,
//                    new Tooltip.Builder(101)
//                            .anchor(etPhonenum, Tooltip.Gravity.TOP)
//                            .closePolicy(new Tooltip.ClosePolicy()
//                                    .insidePolicy(true, false)
//                                    .outsidePolicy(true, true), 10000)
//                            .activateDelay(1000)
//                            .showDelay(900)
//                            .text(getResources().getString(R.string.please_enter_reg_mobile))
//                            .maxWidth(500)
//                            .withArrow(true)

//                            .withOverlay(true)
//
//                            .floatingAnimation(Tooltip.AnimationBuilder.SLOW)
//                            .build()
//            ).show();
//        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        OneSignal.getTags(new OneSignal.GetTagsHandler() {
            @Override
            public void tagsAvailable(JSONObject tags) {
                try {
                    //System.out.println("One Signal Tags : " + tags.toString());
                    Iterator<String> iter = tags.keys();
                    while (iter.hasNext()) {
                        String key = iter.next();
                        //System.out.println("One Signal Tags key : " + key);
                        OneSignal.deleteTag(key);

                    }
                }
                catch(Exception e)
                {
                    //System.out.println("One Signal Tags : ERROR : " + e);
                }


            }
        });
        OneSignal.sendTag("log_out", "Login_OnResume");
        ContactUsTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle Firebase_bundle = new Bundle();
                Firebase_bundle.putString("CustomerCare","Contacted");
                mFirebaseAnalytics.logEvent(getResources().getString(R.string.Login_Customer_Care_Clicked),Firebase_bundle);

                callPermission();
            }
        });
    }

    @OnClick(R.id.button3)
    public void login(Button btnLogin) {

        int length = etPhonenum.getText().toString().length();
        if(etPhonenum.getText().toString().isEmpty()||length>10||length<10){
            Toast.makeText(this, "Enter valid phone number", Toast.LENGTH_SHORT).show();
        }else{
            btnLogin.setText(getResources().getString(R.string.checking));
            setProgressBarVisible();
            Bundle Firebase_bundle = new Bundle();
            Firebase_bundle.putString("MobileNumber",etPhonenum.getText().toString());
            mFirebaseAnalytics.logEvent(getResources().getString(R.string.Login_Button_Clicked),Firebase_bundle);
            LoginCall(etPhonenum.getText().toString());

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
    private void callPermission() {
        // Check the SDK version and whether the permission is already granted or not.
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && checkSelfPermission(Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
//            requestPermissions(new String[]{Manifest.permission.CALL_PHONE}, PERMISSIONS_REQUEST_PHONE_CALL);
//        } else {
            //Open call function
            String number = getResources().getString(R.string.customercare_number);
            Intent intent = new Intent(Intent.ACTION_DIAL);
            intent.setData(Uri.parse("tel:" + number));
            startActivity(intent);
            overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
//        }
    }
//    @Override
//    public void onRequestPermissionsResult(int requestCode, String[] permissions,
//                                           int[] grantResults) {
//        if (requestCode == PERMISSIONS_REQUEST_PHONE_CALL) {
//            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//                callPermission();
//            } else {
//                callPermission();
//            }
//        }
//    }
    private void LoginCall(final String phoneNum) {

        try {

        String URL, parameters;
        parameters = "phoneNo=" + phoneNum ;
        URL = Constants.BASE_URL + "r_check_user.php?" + parameters.replaceAll(" ", "%20");
        System.out.println("CHECK---> URL " + URL);
            final CommonSSLConnection commonSSLConnection=new CommonSSLConnection();
        StringRequest jsonObjectRequestLogin = new StringRequest(Request.Method.GET, URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String jsonObject) {
                System.out.println("CHECK---> Response " + jsonObject);
                setProgressBarGone();

                CampusWallet.getInstance().cancelPendingRequests(tag_json_object);

                Gson gson = new Gson();

                CheckUser model = gson.fromJson(jsonObject, CheckUser.class);

                Status status = model.getStatus();
                String code = status.getCode();

                if (code.equals("200")) {
                    btnLogin.setText(getResources().getString(R.string.S_u_c_c_e_s_s));
                    Code code1 = model.getCode();

                    String userName = code1.getParentName();
                    String userEmail = code1.getParentEmail();
                    String bypass=code1.getBypass();
                    OneSignal.sendTag("user_mobile", phoneNum);
                    OneSignal.sendTag("log_out", "Login_Button_Click");
                    if (bypass.equalsIgnoreCase("0"))
                    {

                        Intent i = new Intent(getApplicationContext(), PhoneAuthActivity.class);
                        Bundle b = new Bundle();
                        b.putString("userPhoneNumber", phoneNum);
                        b.putString("parentName", userName);
                        b.putString("parentEmail",userEmail );
                        i.putExtras(b);
                        startActivity(i);
                        killActivity();
                    }
                    else if (bypass.equalsIgnoreCase("1"))
                    {

                        Intent i = new Intent(getApplicationContext(), RegisterActivity.class);
                        Bundle b = new Bundle();
                        b.putString("userPhone", phoneNum);
                        b.putString("userEmail", userEmail);
                        b.putString("userName", userName);
                        i.putExtras(b);
                        startActivity(i);
                        killActivity();
                    }




                } if (code.equals("500")) {

                    Intent i = new Intent(getApplicationContext(), PhoneAuthActivity.class);
                    Bundle b = new Bundle();
                    b.putString("userPhoneNumber", phoneNum);
                    b.putString("parentName", "");
                    b.putString("parentEmail","" );
                    i.putExtras(b);
                    startActivity(i);
                    killActivity();


                }else {
                    setProgressBarGone();
                    btnLogin.setText(getResources().getString(R.string.l_o_g_i_n));
                    btnLogin.setEnabled(true);
                    String message = status.getMessage();
                    Toast.makeText(LoginActivity.this, message, Toast.LENGTH_SHORT).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                setProgressBarGone();
                btnLogin.setText(getResources().getString(R.string.l_o_g_i_n));
                btnLogin.setEnabled(true);
                CampusWallet.getInstance().cancelPendingRequests(tag_json_object);
                VolleyLog.d("Object Error : ", volleyError.getMessage());
                Toast.makeText(LoginActivity.this, getResources().getString(R.string.network_error_try), Toast.LENGTH_SHORT).show();
            }
        });
        jsonObjectRequestLogin.setRetryPolicy(new DefaultRetryPolicy(50000, 3, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
//            RequestQueue requestQueue = Volley.newRequestQueue(this, commonSSLConnection.getHulkstack(LoginActivity.this));
        CampusWallet.getInstance().addToRequestQueue(jsonObjectRequestLogin, tag_json_object);
//            requestQueue.add(jsonObjectRequestLogin);

        }catch (Exception e)
        {
            setProgressBarGone();
            btnLogin.setText(getResources().getString(R.string.l_o_g_i_n));
            btnLogin.setEnabled(true);
            CampusWallet.getInstance().cancelPendingRequests(tag_json_object);
            Toast.makeText(LoginActivity.this, getResources().getString(R.string.network_error_try), Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        CampusWallet.getInstance().cancelPendingRequests(tag_json_object);
    }

    private void killActivity() {
        ((Activity) LoginActivity.this).finish();
    }

}
