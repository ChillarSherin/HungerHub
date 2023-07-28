package com.hungerhub.NewPreOrder.MyOrders;

import android.annotation.SuppressLint;
import android.os.Bundle;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
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
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import com.hungerhub.application.CampusWallet;
import com.hungerhub.application.Constants;
import com.hungerhub.application.CustomConnectionBuddyActivity;
import com.hungerhub.application.PrefManager;
import com.hungerhub.networkmodels.MyOrder.Datum;
import com.hungerhub.networkmodels.MyOrder.MyOrderData;
import com.hungerhub.networkmodels.MyOrder.Status;
import com.hungerhub.utils.CommonSSLConnection;
import com.hungerhub.utils.GetFragmentDatepickerDate;
import com.hungerhub.utils.ToDatePickerTVFragment;

public class MyOrdersActivity extends CustomConnectionBuddyActivity implements GetFragmentDatepickerDate {

    PrefManager prefManager;
    @BindView(R.id.HeaderTV)
    TextView HeaderText;
    @BindView(R.id.BackIV)
    ImageView BackButton;
    @BindView(R.id.MyorderRv)
    RecyclerView MyorderRv;
    @BindView(R.id.mProgressBar)
    ProgressBar mProgressBar;
    @BindView(R.id.ReloadBTN)
    Button ReloadBTN;
    @BindView(R.id.GoBackBTN)
    Button GoBackBTN;
    @BindView(R.id.NodataTV)
    TextView NodataTV;
    @BindView(R.id.CodeErrorTV)
    TextView CodeErrorTV;
    @BindView(R.id.ErrorMessageTV)
    TextView ErrorMessageTV;
    @BindView(R.id.MyOrderDateTV)
    TextView MyOrderDateTV;
    @BindView(R.id.HidMyOrderDateTV)
    TextView HidMyOrderDateTV;
    @BindView(R.id.MyOrderDateIV)
    ImageView MyOrderDateIV;
    private String tag_json_object = "my_order_data";
    private MyOrderAdapter mAdapter;
    private ToDatePickerTVFragment dateFragment;
    String thisDate;
    private Calendar mCalendar;
    Date date1, date2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_orders);
        ButterKnife.bind(this);
        prefManager = new PrefManager(MyOrdersActivity.this);
        mCalendar = Calendar.getInstance();
//        generateRandomNo(); //random Number TOKEN
        HeaderText.setText(getResources().getString(R.string.your_basket_header));
        setProgressBarGone();
        BackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        MyOrderDateTV.setText(getTodaysDate());
        MyOrderDateIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDatePickerDialog(view);
            }
        });
        MyOrderDateTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDatePickerDialog(view);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        MyOrderPHP(MyOrderDateTV.getText().toString());
    }

    public String getTodaysDate() {
        Date today = new Date();
        SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy");
        String dateToStr = format.format(today);
        return dateToStr;
    }
 public String getParseDate(String phpDate) {
        String parsedDate="";
       try {
           String pattern = "dd-MM-yyyy";
           SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);

           Date date = simpleDateFormat.parse(phpDate);
           SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
           String dateToStr = format.format(date);
           parsedDate= dateToStr;
       }catch (Exception e)
       {

       }
        return  parsedDate;
    }

    public void setProgressBarVisible() {
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        mProgressBar.setVisibility(View.VISIBLE);

    }

    public void setProgressBarGone() {

        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        mProgressBar.setVisibility(View.GONE);

    }

    private void showDatePickerDialog(View v) {
//        dateFragment = new DatePickerFragment();
//        dateFragment.show(getFragmentManager(), "datePicker");
        @SuppressLint("SimpleDateFormat") SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
        @SuppressLint("SimpleDateFormat") SimpleDateFormat dateFormat2 = new SimpleDateFormat("dd-MM-yyyy");
        String dateForButton = dateFormat.format(mCalendar.getTime());
        String dateForButton2 = dateFormat2.format(mCalendar.getTime());
        try {
            date2 = dateFormat.parse(getTodaysDate());
            date1 = dateFormat.parse(dateForButton2);
            dateFragment = new ToDatePickerTVFragment(mCalendar, date1, date2, MyOrderDateTV, MyOrdersActivity.this, MyOrdersActivity.this);
            dateFragment.show(getFragmentManager(), "datePicker");
        } catch (ParseException e) {
            e.printStackTrace();
        }


    }

    private void MyOrderPHP(final String purchaseDate) {

        setProgressBarVisible();
        String URL;
        URL = Constants.BASE_ORDER_URL + "user_orders";
        //System.out.println("CHECK---> URL " + URL);
        CommonSSLConnection commonSSLConnection = new CommonSSLConnection();
        StringRequest jsonObjectRequestLogin = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String jsonObject) {
                System.out.println("CHECK---> Response " + jsonObject);
                setProgressBarGone();

                CampusWallet.getInstance().cancelPendingRequests(tag_json_object);

                Gson gson = new Gson();
                MyOrderData noticeboard = gson.fromJson(jsonObject, MyOrderData.class);
                Status status = noticeboard.getStatus();
                String code = status.getCode();
                if (code.equals("200")) {
                    findViewById(R.id.NodataLL).setVisibility(View.GONE);
                    findViewById(R.id.ErrorLL).setVisibility(View.GONE);
                    List<Datum> myDataset = noticeboard.getData();
                    if (myDataset.size() > 0) {
                        boolean isExpired=false;
                        for (int i = 0; i < myDataset.size(); i++) {
                            if (getTodaysDate().equalsIgnoreCase(purchaseDate))
                            {
                                isExpired=false;
                            }
                            else
                            {
                                isExpired=true;
                            }
                            MyorderRv.setLayoutManager(new LinearLayoutManager(MyOrdersActivity.this, LinearLayoutManager.VERTICAL, false));
                            MyorderRv.setItemAnimator(new DefaultItemAnimator());
                            MyorderRv.setNestedScrollingEnabled(false);
                            mAdapter = new MyOrderAdapter(myDataset, MyOrdersActivity.this,isExpired);
                            mAdapter.notifyDataSetChanged();
                            MyorderRv.setAdapter(mAdapter);
                        }


                    } else {
                        NodataTV.setText(getResources().getString(R.string.no_data_found));
                        GoBackBTN.setText(getResources().getString(R.string.go_back));
//                        ErrorImage.setBackgroundResource(R.drawable.nodata);
                        findViewById(R.id.NodataLL).setVisibility(View.VISIBLE);
                        GoBackBTN.setVisibility(View.GONE);
                    }

                }
                else if (code.equals("204"))
                {
                    findViewById(R.id.ErrorLL).setVisibility(View.GONE);
                    NodataTV.setText(getResources().getString(R.string.no_data_found));
                    GoBackBTN.setText(getResources().getString(R.string.go_back));
//                        ErrorImage.setBackgroundResource(R.drawable.nodata);
                    findViewById(R.id.NodataLL).setVisibility(View.VISIBLE);
                    GoBackBTN.setVisibility(View.GONE);
                }
                else {
                    setProgressBarGone();
                    String message = status.getMessage();
                    ReloadBTN.setText(getResources().getString(R.string.go_back));
                    findViewById(R.id.NodataLL).setVisibility(View.GONE);
                    findViewById(R.id.ErrorLL).setVisibility(View.VISIBLE);
                    ErrorMessageTV.setText(getResources().getString(R.string.error_message_errorlayout));
                    CodeErrorTV.setText(getResources().getString(R.string.code_attendance) + code);
                    ReloadBTN.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {


                            onBackPressed();

                        }
                    });
                    Toast.makeText(MyOrdersActivity.this, message, Toast.LENGTH_SHORT).show();
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
                Toast.makeText(MyOrdersActivity.this, getResources().getString(R.string.network_error_try), Toast.LENGTH_SHORT).show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("targetUserID", prefManager.getStudentUserId());
                params.put("schoolID", prefManager.getSChoolID());
                params.put("purchaseDate", getParseDate(purchaseDate));

                //System.out.println("CHECK---> " + prefManager.getStudentUserId() + " , " +
//                        prefManager.getSChoolID() + " ,\n " + getParseDate(purchaseDate));

                return params;
            }
        };

        jsonObjectRequestLogin.setRetryPolicy(new DefaultRetryPolicy(50000, 3, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
//        RequestQueue requestQueue = Volley.newRequestQueue(this, commonSSLConnection.getHulkstack(MyOrdersActivity.this));
        CampusWallet.getInstance().addToRequestQueue(jsonObjectRequestLogin, tag_json_object);
//        requestQueue.add(jsonObjectRequestLogin);

    }

    @Override
    public void onDateselected(String DateStr) {
        MyOrderDateTV.setText(DateStr);
        MyOrderPHP(DateStr);
    }
}
