package com.hungerhub.NewPreOrder;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import butterknife.BindView;
import butterknife.ButterKnife;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.StringRequest;
import com.chillarcards.cookery.R;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.wdullaer.materialdatetimepicker.time.TimePickerDialog;
import com.wdullaer.materialdatetimepicker.time.Timepoint;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.hungerhub.NewPreOrder.AllItems.CartCallback;
import com.hungerhub.NewPreOrder.Swipe.SwipeController;
import com.hungerhub.application.CampusWallet;
import com.hungerhub.application.Constants;
import com.hungerhub.application.CustomConnectionBuddyActivity;
import com.hungerhub.application.PrefManager;
import com.hungerhub.networkmodels.PlaceOrder.UploadCart;
import com.hungerhub.networkmodels.PlaceOrderDetails.Datum;
import com.hungerhub.networkmodels.PlaceOrderDetails.PlaceOrderData;
import com.hungerhub.networkmodels.PlaceOrderDetails.Status;

import com.hungerhub.payments.PaymentsActivity;
import com.hungerhub.utils.CommonSSLConnection;

public class CartListActivity extends CustomConnectionBuddyActivity implements CartCallback, View.OnClickListener, com.wdullaer.materialdatetimepicker.time.TimePickerDialog.OnTimeSetListener {

    PrefManager prefManager;
    @BindView(R.id.HeaderTV)
    TextView HeaderText;
    @BindView(R.id.BackIV)
    ImageView BackButton;
    @BindView(R.id.PlaceOrderLL)
    LinearLayout PlaceOrderLL;
    @BindView(R.id.CartTotalAmountTV)
    TextView CartTotalAmountTV;
    @BindView(R.id.CartItemsRV)
    RecyclerView CartItemsRV;
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
    @BindView(R.id.Time)
    RelativeLayout TimeLayout;

    private CartListAdapter mAdapter;
    private SwipeController swipeController;
    private Dialog dialogWhstapp;
    private String tag_json_object = "Upload_Cart_data";
    private int mHour, mMinute;
    private TextView ClockTV;
    private Calendar c;
    private RelativeLayout pickTime;
    private com.wdullaer.materialdatetimepicker.time.TimePickerDialog tpd;

    int maxCookTime = 0;
    boolean liveOrder = false;
    boolean timeSet = false;
    String pickTimeStr = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart_list);
        ClockTV = findViewById(R.id.ClockTV);
        pickTime = findViewById(R.id.ClockView);
        ButterKnife.bind(this);
        prefManager = new PrefManager(CartListActivity.this);
//        generateRandomNo(); //random Number TOKEN
        HeaderText.setText(getResources().getString(R.string.your_basket_header));
        setProgressBarGone();
        BackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

//        c = Calendar.getInstance();
//        mHour = c.get(Calendar.HOUR_OF_DAY);
//        mMinute = c.get(Calendar.MINUTE);
//
//        if(mHour>11)
//        {
//            mHour = mHour - 12;
//        }
//        else
//        {
//            mHour = mHour;
//        }
//
//        ClockTV.setText(mHour +":"+ mMinute );

        ArrayList<Integer> cookTime = new ArrayList<>();
        for(int i=0;i<Constants.CartItems.size();++i){

            cookTime.add(Integer.valueOf(Constants.CartItems.get(i).getCookingTime()));

            if(Integer.valueOf(Constants.CartItems.get(i).getCookingTime())>-1){
                liveOrder = true;
                TimeLayout.setVisibility(View.VISIBLE);
            }

        }


        maxCookTime = Collections.max(cookTime);

        PlaceOrderLL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String mTotal = CartTotalAmountTV.getText().toString().trim();
                String[] split = mTotal.split(getResources().getString(R.string.indian_rupee_symbol));
                String myval = split[1];
                if (!myval.equalsIgnoreCase("0")) {

                    if (!prefManager.getIsTokenClear()) {
                        prefManager.setTokenCart(generateRandomNo());
                        prefManager.setIsTokenClear(true);
                    }
//                    showPayment(myval);  //low balance
                    List<UploadCart> uploadCarts = new ArrayList<>();
                    uploadCarts.clear();
                    if (Constants.CartItems.size()>0) {
                        for (DummyOrderItems mOrderItems : Constants.CartItems) {
                            uploadCarts.add(new UploadCart(mOrderItems.getId(), mOrderItems.getOutletID(), mOrderItems.getSessionID(), mOrderItems.getCategotyId(),
                                    mOrderItems.getUnitPrice(), mOrderItems.getQty(),mOrderItems.getItemPrice(),mOrderItems.getItemSGSTPrice(),mOrderItems.getItemCGSTPrice(),mOrderItems.getItemLiveStatus()));

                            //System.out.println("JSON UPLOAD ITEMs : " + mOrderItems.getId()+" , "+ mOrderItems.getOutletID()+" , "+mOrderItems.getSessionID()+" , "+mOrderItems.getCategotyId());
                        }

                        Gson gson = new GsonBuilder().create();
                        JsonArray myCustomArray = gson.toJsonTree(uploadCarts).getAsJsonArray();
                        //System.out.println("JSON UPLOAD Array : " + myCustomArray.toString());
                        Gson gson1 = new Gson();
                        String UploadJSON = gson1.toJson(uploadCarts);
                        //System.out.println("JSON UPLOAD : " + UploadJSON);
                        if(liveOrder){
                            if(timeSet){
                                if(!ClockTV.getText().toString().isEmpty()|| pickTimeStr.isEmpty()){
                                    PlaceOrderPHP(UploadJSON, prefManager.getTokenCart(),liveOrder);
                                }else{
                                    Toast.makeText(CartListActivity.this, "Please set an expected order pick time", Toast.LENGTH_SHORT).show();

                                }
                            }else{
                                Toast.makeText(CartListActivity.this, "Please set an expected order pick time", Toast.LENGTH_SHORT).show();
                            }
                        }else{
                            PlaceOrderPHP(UploadJSON, prefManager.getTokenCart(),liveOrder);
                        }

                    }
                    else
                    {
                        Toast.makeText(CartListActivity.this, "Please Select Atleast One Item", Toast.LENGTH_SHORT).show();
                    }

                } else {
                    Toast.makeText(CartListActivity.this, "Please Select Atleast One Item", Toast.LENGTH_SHORT).show();
                }
            }
        });

        pickTime.setOnClickListener(this);
    }



    public String generateRandomNo() {
        RandomString tickets = new RandomString(14);
        //System.out.println("Random No :"+tickets.nextString());
        return tickets.nextString();
    }

    @Override
    protected void onResume() {
        super.onResume();


        ChangeTotalText();
        CartItemsRV.setLayoutManager(new LinearLayoutManager(CartListActivity.this, LinearLayoutManager.VERTICAL, false));
        CartItemsRV.setItemAnimator(new DefaultItemAnimator());
        CartItemsRV.setNestedScrollingEnabled(false);
        mAdapter = new CartListAdapter(Constants.CartItems, CartListActivity.this, CartListActivity.this, CartListActivity.this);
        mAdapter.notifyDataSetChanged();
        CartItemsRV.setAdapter(mAdapter);

//        swipeController = new SwipeController(new SwipeControllerActions() {
//            @Override
//            public void onRightClicked(int position) {
//                mAdapter.orderItems.remove(position);
//                mAdapter.notifyItemRemoved(position);
//                mAdapter.notifyItemRangeChanged(position, mAdapter.getItemCount());
//            }
//
//            @Override
//            public void onLeftClicked(int position) {
//                super.onLeftClicked(position);
//
//            }
//        });
//
//        ItemTouchHelper itemTouchhelper = new ItemTouchHelper(swipeController);
//        itemTouchhelper.attachToRecyclerView(CartItemsRV);
//
//        CartItemsRV.addItemDecoration(new RecyclerView.ItemDecoration() {
//            @Override
//            public void onDraw(Canvas c, RecyclerView parent, RecyclerView.State state) {
//                swipeController.onDraw(c);
//            }
//        });

        TimePickerDialog tpd = (TimePickerDialog) getSupportFragmentManager().findFragmentByTag("Timepickerdialog");
        if(tpd != null) tpd.setOnTimeSetListener(this);

    }

    @Override
    public void onAddtocartCallback(boolean liveStatus) {
        if(!liveStatus){
            TimeLayout.setVisibility(View.GONE);
            liveOrder =false;
        }
        ChangeTotalText();
    }

    public void ChangeTotalText() {
        float Total_Amount = 0;
        for (int i = 0; i < Constants.CartItems.size(); i++) {
            Total_Amount = Total_Amount + (Float.parseFloat(Constants.CartItems.get(i).getPrice()));
        }
        CartTotalAmountTV.setText(getResources().getString(R.string.indian_rupee_symbol) + String.valueOf(Total_Amount));
    }

    private void showPayment(final String reqAmount,String CurrentBalance) {

        dialogWhstapp = new Dialog(CartListActivity.this, android.R.style.Theme_Dialog);
        dialogWhstapp.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialogWhstapp.setContentView(R.layout.payment_balance_popup_layout);
        dialogWhstapp.setCanceledOnTouchOutside(true);
        dialogWhstapp.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        dialogWhstapp.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        dialogWhstapp.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        RelativeLayout close = dialogWhstapp.findViewById(R.id.exit);
        Button CancelButton = dialogWhstapp.findViewById(R.id.CancelBtn);
        Button OKButton = dialogWhstapp.findViewById(R.id.OkBtn);
        TextView popupcontent = dialogWhstapp.findViewById(R.id.popupcontent);
        String text="<font color=#FFFFFF> "+getString(R.string.you_require)+"</font><font color=#FE2E64> "+getString(R.string.indian_rupee_symbol)+reqAmount+
                " </font><font color=#FFFFFF>"+getString(R.string.to_complete)+"\n "+
                getString(R.string.your_current)+"</font><font color=#FE2E64> "+getString(R.string.indian_rupee_symbol)+CurrentBalance+
                "\n </font><font color=#FFFFFF>"+getString(R.string.please_recharge)+"</font>";

        popupcontent.setText(Html.fromHtml(text));

        try {
            dialogWhstapp.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                dialogWhstapp.dismiss();

            }
        });

        CancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                dialogWhstapp.dismiss();
            }
        });
        OKButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mFirebaseAnalytics.logEvent(getResources().getString(R.string.Low_Balance_popup_Selected), new Bundle());
                Intent i = new Intent(CartListActivity.this, PaymentsActivity.class);
                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                Bundle b = new Bundle();
                b.putString("categoryId", "2");
                b.putString("categoryName", "card_recharge");
                b.putString("fromLowbalance", "1");
                b.putString("rechargeAmount", reqAmount);
                i.putExtras(b);
                startActivity(i);
                dialogWhstapp.dismiss();
            }
        });

    }
    private void showCommonDialogue(String message) {

        dialogWhstapp = new Dialog(CartListActivity.this, android.R.style.Theme_Dialog);
        dialogWhstapp.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialogWhstapp.setContentView(R.layout.common_popup_layout);
        dialogWhstapp.setCanceledOnTouchOutside(true);
        dialogWhstapp.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        dialogWhstapp.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        dialogWhstapp.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        RelativeLayout close = dialogWhstapp.findViewById(R.id.exit);
        Button OKButton = dialogWhstapp.findViewById(R.id.OkBtn);
        TextView popupcontent = dialogWhstapp.findViewById(R.id.popupcontent);
        TextView logouttext = dialogWhstapp.findViewById(R.id.logouttext);
//        logouttext.setVisibility(View.GONE);
        popupcontent.setText(message);

        try {
            dialogWhstapp.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                dialogWhstapp.dismiss();

            }
        });


        OKButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                dialogWhstapp.dismiss();
            }
        });

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

    private void PlaceOrderPHP(final String jsonData, final String token, boolean liveOrder) {

        setProgressBarVisible();
        String URL;
        URL = Constants.BASE_ORDER_URL + "outlet_place_order";
        //System.out.println("CHECK---> URL " + URL);
        CommonSSLConnection commonSSLConnection = new CommonSSLConnection();
        StringRequest jsonObjectRequestLogin = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String jsonObject) {
                //System.out.println("CHECK---> Response " + jsonObject);
                setProgressBarGone();

                CampusWallet.getInstance().cancelPendingRequests(tag_json_object);

                Gson gson = new Gson();
                PlaceOrderData noticeboard = gson.fromJson(jsonObject, PlaceOrderData.class);
                Status status = noticeboard.getStatus();
                String code = status.getCode();
                if (code.equals("200")) {

                    List<Datum> myDataset = noticeboard.getData();
                    if (myDataset.size() > 0) {
                        for (int i = 0; i < myDataset.size(); i++) {
                            String orderID = myDataset.get(i).getOrderPurchaseID();
                            String realOrderID = myDataset.get(i).getRealOrderPurchaseID();

                            prefManager.setIsTokenClear(false);
                            Constants.CartItems.clear();
//                            Intent intent = new Intent(CartListActivity.this, CartStatusActivity.class);
//                            Bundle bundle=new Bundle();
//                            bundle.putString("OrderID",orderID);
//                            bundle.putString("RealOrderID",realOrderID);
//                            intent.putExtras(bundle);
//                            startActivity(intent);

                            Gson mgson = new Gson();
                            String ItemsJSON = mgson.toJson(myDataset.get(i));

                            Intent intent=new Intent(CartListActivity.this, CartOrderDetailsActivity.class);
                            Bundle bundle=new Bundle();
                            bundle.putString("OrderRealID",realOrderID);
                            bundle.putBoolean("isExpired",false);
                            intent.putExtras(bundle);
                            startActivity(intent);

                        }


                    } else {
//                        NodataTV.setText(getResources().getString(R.string.no_data_found));
//                        GoBackBTN.setText(getResources().getString(R.string.go_back));
////                        ErrorImage.setBackgroundResource(R.drawable.nodata);
//                        findViewById(R.id.NodataLL).setVisibility(View.VISIBLE);
//                        GoBackBTN.setVisibility(View.GONE);
                        String message = getResources().getString(R.string.network_error_try);
                        showCommonDialogue(message);
                    }

                }
                else if (code.equals("410"))
                {
                    List<Datum> myDataset = noticeboard.getData();
                    if (myDataset.size() > 0) {
                        for (int i = 0; i < myDataset.size(); i++) {
                            String orderID = myDataset.get(i).getOrderPurchaseID();
                            String CurrentBalance = myDataset.get(i).getCurrentBalance();
                            String RequiredAmount = myDataset.get(i).getRequiredAmnt();

                            showPayment(RequiredAmount,CurrentBalance);
                        }


                    } else {
//                        NodataTV.setText(getResources().getString(R.string.no_data_found));
//                        GoBackBTN.setText(getResources().getString(R.string.go_back));
////                        ErrorImage.setBackgroundResource(R.drawable.nodata);
//                        findViewById(R.id.NodataLL).setVisibility(View.VISIBLE);
//                        GoBackBTN.setVisibility(View.GONE);
                        String message = getResources().getString(R.string.network_error_try);
                        showCommonDialogue(message);
                    }


                }
                else if (code.equals("411"))
                {
                    showCommonDialogue(status.getMessage());
                }

                else {
                    setProgressBarGone();
//                    String message = status.getMessage();
//                    ReloadBTN.setText(getResources().getString(R.string.go_back));
//                    findViewById(R.id.ErrorLL).setVisibility(View.VISIBLE);
//                    ErrorMessageTV.setText(getResources().getString(R.string.error_message_errorlayout));
//                    CodeErrorTV.setText(getResources().getString(R.string.code_attendance) + code);
//                    ReloadBTN.setOnClickListener(new View.OnClickListener() {
//                        @Override
//                        public void onClick(View v) {
//
//
//                            onBackPressed();
//
//                        }
//                    });
                    String message = status.getMessage();
                    showCommonDialogue(message);
//                    Toast.makeText(CartListActivity.this, message, Toast.LENGTH_SHORT).show();
                }


            }


        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                setProgressBarGone();
                CampusWallet.getInstance().cancelPendingRequests(tag_json_object);
                VolleyLog.d("Object Error : ", volleyError.getMessage());
//                ReloadBTN.setText(getResources().getString(R.string.go_back));
//                findViewById(R.id.ErrorLL).setVisibility(View.VISIBLE);
//                ErrorMessageTV.setText(getResources().getString(R.string.error_message_admin));
//                CodeErrorTV.setText(getResources().getString(R.string.error_message_errorlayout));
//                ReloadBTN.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//
//
//                        onBackPressed();
//
//                    }
//                });
                String message = getResources().getString(R.string.network_error_try);
                showCommonDialogue(message);
                Toast.makeText(CartListActivity.this, getResources().getString(R.string.network_error_try), Toast.LENGTH_SHORT).show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("targetUserID", prefManager.getStudentUserId());
                params.put("schoolID", prefManager.getSChoolID());
                params.put("itemListJSON", jsonData);
                params.put("transactionToken", token);
                if(liveOrder) {
                    params.put("expectedpicTime", pickTimeStr);
                }
                System.out.println("CHECK---> " + prefManager.getStudentUserId() + " , " +
                        prefManager.getSChoolID()+ " ," +jsonData+ " , " +token+" , "+pickTimeStr);

                return params;
            }
        };

        jsonObjectRequestLogin.setRetryPolicy(new DefaultRetryPolicy(50000, 3, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
//        RequestQueue requestQueue = Volley.newRequestQueue(this, commonSSLConnection.getHulkstack(CartListActivity.this));
        CampusWallet.getInstance().addToRequestQueue(jsonObjectRequestLogin, tag_json_object);
//        requestQueue.add(jsonObjectRequestLogin);

    }



    @Override
    public void onClick(View v) {


        Calendar now = Calendar.getInstance();
            /*
            It is recommended to always create a new instance whenever you need to show a Dialog.
            The sample app is reusing them because it is useful when looking for regressions
            during testing
             */

        int hours = maxCookTime / 60; //since both are ints, you get an int
        int minutes = maxCookTime % 60;
        System.out.printf("%d:%02d", hours, minutes);

        int totalHours = hours + now.get(Calendar.HOUR_OF_DAY);
        int totalMinutes = minutes + now.get(Calendar.MINUTE);

        if (totalMinutes >= 60) {
            totalHours ++;
            totalMinutes = totalMinutes % 60;
        }

        if (tpd == null) {
            tpd = com.wdullaer.materialdatetimepicker.time.TimePickerDialog.newInstance(
                    CartListActivity.this,
                    totalHours,
                    totalMinutes,
                    true
            );
        } else {
            tpd.initialize(
                    CartListActivity.this,
                    totalHours,
                    totalMinutes,
                    now.get(Calendar.SECOND),
                    true
            );
        }
        tpd.setThemeDark(false);
        tpd.vibrate(true);
        tpd.dismissOnPause(true);
        tpd.enableSeconds(false);
        tpd.setVersion(TimePickerDialog.Version.VERSION_1);
        tpd.setAccentColor(Color.parseColor("#9C27B0"));
        tpd.setTitle("Delivery Time");
        tpd.setMinTime(new Timepoint(totalHours,totalMinutes));



        tpd.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialogInterface) {
                Log.d("TimePicker", "Dialog was cancelled");
                tpd = null;
                if(ClockTV.getText().toString().isEmpty()){
                    timeSet = false;
                }
            }
        });
        tpd.show(getSupportFragmentManager(), "Timepickerdialog");

/*



        // Get Current Time
//            c = Calendar.getInstance();
//            mHour = c.get(Calendar.HOUR_OF_DAY);
//            mMinute = c.get(Calendar.MINUTE);

        // Launch Time Picker Dialog
        Calendar c = Calendar.getInstance();

        TimePickerDialog timePickerDialog = new TimePickerDialog(           this,
                new TimePickerDialog.OnTimeSetListener() {

                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay,
                                          int minute) {

                        if(hourOfDay>11)
                        {
                            hourOfDay = hourOfDay - 12;
                        }
                        else
                        {
                            hourOfDay = hourOfDay;
                        }

                        ClockTV.setText(hourOfDay + ":" + minute);
                    }
                },  c.get(Calendar.HOUR_OF_DAY),c.get(Calendar.MINUTE)+5,false);
        timePickerDialog.show();*/



    }


    @Override
    public void onTimeSet(com.wdullaer.materialdatetimepicker.time.TimePickerDialog view, int hourOfDay, int minute, int second) {
//        String time = "You picked the following time: "+hourOfDay+"h"+minute+"m"+second;
        String minute1 = String.valueOf(minute);
        if(minute<10){

            minute1 = "0"+minute;
        }
        ClockTV.setText(hourOfDay + ":" + minute1);


        pickTimeStr = hourOfDay + ":" + minute;
        timeSet =true;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        tpd = null;
    }


}
