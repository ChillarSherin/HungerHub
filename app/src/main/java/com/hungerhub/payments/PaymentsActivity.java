package com.hungerhub.payments;

import android.app.Activity;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
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
import com.android.volley.toolbox.StringRequest;
import com.chillarcards.cookery.R;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import com.hungerhub.application.CampusWallet;
import com.hungerhub.application.Constants;
import com.hungerhub.application.CustomConnectionBuddyActivity;
import com.hungerhub.application.PrefManager;
import com.hungerhub.networkmodels.otherpaymentmodes.Code;
import com.hungerhub.networkmodels.otherpaymentmodes.OtherPayment;
import com.hungerhub.networkmodels.otherpaymentmodes.PaymentItem;
import com.hungerhub.networkmodels.otherpaymentmodes.PaymentMode;
import com.hungerhub.networkmodels.otherpaymentmodes.Status;
import com.hungerhub.utils.CommonSSLConnection;


public class PaymentsActivity extends CustomConnectionBuddyActivity {

    PrefManager prefManager;
    Activity activity;

    Fragment fragment1 = new PaymentsRechargeFragment();
    Fragment fragment2 = new PaymentsOtherFragment();
    Fragment fragment3 = null;
    Fragment fragment4 = null;
    final FragmentManager fm = getSupportFragmentManager();
    Fragment active = null;
    final String tag_json_object = "r_online_transaction_details";
    @BindView(R.id.progressBar5) ProgressBar progressBar;
   // @BindView(R.id.textView) TextView txt_title;
    @BindView(R.id.HeaderTV)
    TextView HeaderText;
    @BindView(R.id.BackIV)
    ImageView BackButton;
    @BindView(R.id.CodeErrorTV) TextView CodeErrorTV;
    @BindView(R.id.ErrorMessageTV) TextView ErrorMessageTV;
    @BindView(R.id.ReloadBTN)
    Button ReloadBTN;
    @BindView(R.id.GoBackBTN)
    Button GoBackBTN;
    @BindView(R.id.NodataTV)
    TextView NodataTV;
    String fromLowbalance,rechargeAmount;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_payments_activity);
        ButterKnife.bind(this);

        activity=this;
        prefManager = new PrefManager(PaymentsActivity.this);
        String phoneNum = prefManager.getUserPhone();
        String studentId = prefManager.getStudentId();

        Bundle b = getIntent().getExtras();
        String category = b.getString("categoryId");
        String categoryName = b.getString("categoryName");
        fromLowbalance = b.getString("fromLowbalance");
        rechargeAmount = b.getString("rechargeAmount");

        HeaderText.setText(setHeaderText(category));   //set header name

        BackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        setProgressBarVisible();
        LoadAllPaymentModes(phoneNum,studentId,category);

    }
    public String setHeaderText(String catName)
    {
        String headerName="";
        switch (catName)
        {
            case "2":
                headerName=getResources().getString(R.string.card_recharge_caps);
                break;
            case "3":
                headerName=getResources().getString(R.string.fee_payments_caps);
                break;
            case "4":
                headerName=getResources().getString(R.string.miscellanious_caps);
                break;
            case "5":
                headerName=getResources().getString(R.string.trust_payments_caps);
                break;
            default :
                headerName=getResources().getString(R.string.payement_title);
                break;

        }
        return headerName;
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        CampusWallet.getInstance().cancelPendingRequests(tag_json_object);
    }
    private void LoadAllPaymentModes(String phoneNum, String s, final String category) {



        String URL, parameters;
        parameters = "phoneNo=" + phoneNum+"&studentID="+s+"&transactionCategoryID="+category;
        URL = Constants.BASE_URL + "r_online_transaction_details.php?" + parameters.replaceAll(" ", "%20");
        System.out.println("CHECK---> URL " + URL);
        final CommonSSLConnection commonSSLConnection=new CommonSSLConnection();
        StringRequest jsonObjectRequestLogin = new StringRequest(Request.Method.GET, URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String jsonObject) {

                CampusWallet.getInstance().cancelPendingRequests(tag_json_object);
                setProgressBarGone();

                Gson gson = new Gson();

                OtherPayment modes =gson.fromJson(jsonObject,OtherPayment.class);
                Status status = modes.getStatus();
                String code = status.getCode();

                if(code.equals("200")){

                    Code code1 = modes.getCode();
                    List<PaymentMode> paymentModes = new ArrayList<>();
                    paymentModes = code1.getPaymentModes();



                    List<PaymentItem> paymentItems = new ArrayList<>();
                    paymentItems = code1.getPaymentItems();




                    if(category.equals("2")){   //Card Recharge -inflate card recharge fragment

                        ArrayList<PaymentMode> modesPaymentsNew = new ArrayList<PaymentMode>();
                        modesPaymentsNew.addAll(paymentModes);

                        Gson gson1 = new Gson();
                        String paymentModesStr = gson1.toJson(modesPaymentsNew);

                        Bundle bundle1 = new Bundle();
                        bundle1.putString("paymentModes", paymentModesStr);
                        bundle1.putString("category", category);
                        bundle1.putString("fromLowbalance", fromLowbalance);
                        bundle1.putString("rechargeAmount", rechargeAmount);
                        fragment1.setArguments(bundle1);
                        active = fragment1;
                        fm.beginTransaction().add(R.id.frame_layout,fragment1, "1").commitAllowingStateLoss();
//                        fm.beginTransaction().hide(active).show(fragment1).commit();
                        if (active!=null) {
                            fm.beginTransaction().hide(active).show(fragment1).commitAllowingStateLoss();
                        }
                        else {
                            fm.beginTransaction().show(fragment1).commitAllowingStateLoss();
                        }



                    }else {

                        ArrayList<PaymentMode> modesPaymentsNew = new ArrayList<PaymentMode>();
                        modesPaymentsNew.addAll(paymentModes);
                        ArrayList<PaymentItem> itemPaymentsNew = new ArrayList<PaymentItem>();
                        itemPaymentsNew.addAll(paymentItems);


                        Gson gson1 = new Gson();
                        String paymentModesStr = gson1.toJson(modesPaymentsNew);
                        String paymentItemsStr = gson1.toJson(itemPaymentsNew);

                        //System.out.println("paymentItemsStr :: "+paymentItemsStr);

                        Bundle bundle1 = new Bundle();
                        bundle1.putString("paymentModes", paymentModesStr);
                        bundle1.putString("paymentItems", paymentItemsStr);
                        bundle1.putString("category", category);
                        fragment2.setArguments(bundle1);
                        active = fragment2;
                        fm.beginTransaction().add(R.id.frame_layout,fragment2, "2").commitAllowingStateLoss();
                        if (active!=null) {
                            fm.beginTransaction().hide(active).show(fragment2).commitAllowingStateLoss();
                        }
                        else
                        {
                            fm.beginTransaction().show(fragment2).commitAllowingStateLoss();
                        }

                    }


                }else{
                    setProgressBarGone();
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
                    String message = status.getMessage();
                    Toast.makeText(PaymentsActivity.this, message, Toast.LENGTH_SHORT).show();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
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

                volleyError.printStackTrace();
                setProgressBarGone();

            }
        });
        jsonObjectRequestLogin.setRetryPolicy(new DefaultRetryPolicy(50000, 3, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
//        RequestQueue requestQueue = Volley.newRequestQueue(this, commonSSLConnection.getHulkstack(PaymentsActivity.this));
        CampusWallet.getInstance().addToRequestQueue(jsonObjectRequestLogin, tag_json_object);
//        requestQueue.add(jsonObjectRequestLogin);

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
