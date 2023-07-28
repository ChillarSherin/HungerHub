package com.hungerhub.payments.Preorder;

import android.app.Activity;
import android.os.Bundle;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
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

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import com.hungerhub.application.CampusWallet;
import com.hungerhub.application.Constants;
import com.hungerhub.application.CustomConnectionBuddyActivity;
import com.hungerhub.application.PrefManager;
import com.hungerhub.networkmodels.PreOrderHistDetails.PreOrderHisDetail;
import com.hungerhub.networkmodels.PreOrderHistDetails.PreOrderHisDetailCode;
import com.hungerhub.networkmodels.PreOrderHistDetails.PreOrderHisDetailStatus;
import com.hungerhub.utils.CommonSSLConnection;


@SuppressWarnings("ALL")
public class PreorderHistoryDetailPopup extends CustomConnectionBuddyActivity {


    public String parentPh,s_Id,SelectedTransId,TypeName;
    Activity activity;
    TextView Txt_Content;

    LinearLayout ErrorLayout;

    Button Reload;
    ImageView ErrorImage;

//    Call<PreOrderHisDetail> call1;

    private List<String> preorderSalesItemID = new ArrayList<String>();
    private List<String> tansactionBillNo = new ArrayList<String>();
    private List<String> preorderItemTypeTimingItemID = new ArrayList<String>();
    private List<String> itemID = new ArrayList<String>();
    private List<String> itemCode = new ArrayList<String>();
    private List<String> itemName = new ArrayList<String>();
    private List<String> itemShortName = new ArrayList<String>();
    private List<String> preorderSalesItemQuantity = new ArrayList<String>();
    private List<String> preorderSalesItemAmount = new ArrayList<String>();
    private List<String> preorderItemSaleTransactionVendorID = new ArrayList<String>();
    private List<String> itemTypeID = new ArrayList<String>();
    private List<String> itemTypeName = new ArrayList<String>();



    ProgressBar ProgressBarnew;
    PrefManager prefManager;
    @BindView(R.id.HeaderTV)
    TextView HeaderText;
    @BindView(R.id.BackIV)
    ImageView BackButton;
    @BindView(R.id.CloseIV)
    ImageView CloseIV;
    @BindView(R.id.list)
    RecyclerView mRecyclerView;
    private PreOrderHistoryDetalsAdapter mAdapter;
    final String tag_json_object = "r_preorder_history_details";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.historydetails_popup);
//        Fabric.with(this, new Answers());
        prefManager=new PrefManager(PreorderHistoryDetailPopup.this);
        ButterKnife.bind(this);
        HeaderText.setText(getResources().getString(R.string.preorder_details));
        BackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        CloseIV.setVisibility(View.VISIBLE);
        BackButton.setVisibility(View.VISIBLE);
        activity=this;
        mRecyclerView = (RecyclerView) findViewById(R.id.list);
        ProgressBarnew = (ProgressBar) findViewById(R.id.ProgressBar2);
        ProgressBarnew.setVisibility(View.GONE);
        ErrorLayout=(LinearLayout) findViewById(R.id.LayoutErrorState);
        ErrorLayout.setVisibility(View.GONE);
        Reload= (Button) findViewById(R.id.reload);
        Txt_Content= (TextView) findViewById(R.id.txt_content);
        ErrorImage= (ImageView) findViewById(R.id.img_no_image);
        CloseIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                finish();

            }
        });
        //System.out.println("Details : "+parentPh+"  "+s_Id+" "+SelectedTransId);
    }

    @Override
    protected void onResume() {
        super.onResume();
        parentPh = prefManager.getUserPhone();
        s_Id= prefManager.getStudentId();
        Bundle b = getIntent().getExtras();
        SelectedTransId = b.getString("BillNumber");
        ErrorLayout.setVisibility(View.GONE);
        CloseIV.setVisibility(View.VISIBLE);
        BackButton.setVisibility(View.VISIBLE);
        phpGetPreOrderDetails(SelectedTransId);

    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        CampusWallet.getInstance().cancelPendingRequests(tag_json_object);
    }
    public void phpGetPreOrderDetails(String transID)
    {
        ProgressBarnew.setVisibility(View.VISIBLE);

        String URL, parameters;
        parameters = "tansactionBillNo=" + transID;
        URL = Constants.BASE_URL  + "r_preorder_history_details.php?" + parameters.replaceAll(" ", "%20");
        //System.out.println("CHECK---> URL " + URL);
        CommonSSLConnection commonSSLConnection=new CommonSSLConnection();
        StringRequest jsonObjectRequestLogin = new StringRequest(Request.Method.GET, URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String jsonObject) {
                ProgressBarnew.setVisibility(View.GONE);
                //System.out.println("CHECK---> Response " + jsonObject);
                //findViewById(R.id.progressLayout).setVisibility(View.GONE);
                Gson gson = new Gson();

                PreOrderHisDetail model = gson.fromJson(jsonObject, PreOrderHisDetail.class);
                PreOrderHisDetailStatus status = model.getStatus();
                String code = status.getCode();
                if (code.equals("200")){

                    //System.out.println("SHANIL 1: "+code.toString());
                    List<PreOrderHisDetailCode> transactionCode = model.getCode();
                    if (transactionCode.size() > 0) {

                        for (int i = 0; i < transactionCode.size(); i++) {

                            PreOrderHisDetailCode codeonline = transactionCode.get(i);
                            preorderSalesItemID.add(codeonline.getPreorderSalesItemID());
                            tansactionBillNo.add(codeonline.getTansactionBillNo());
                            preorderItemTypeTimingItemID.add(codeonline.getPreorderItemTypeTimingItemID());
                            itemID.add(codeonline.getItemID());
                            itemCode.add(codeonline.getItemCode());
                            itemName.add(codeonline.getItemName());
                            itemShortName.add(codeonline.getItemShortName());
                            preorderSalesItemQuantity.add(codeonline.getPreorderSalesItemQuantity());
                            preorderSalesItemAmount.add(codeonline.getPreorderSalesItemAmount());
                            itemTypeName.add(codeonline.getItemTypeName());
                            itemTypeID.add(codeonline.getItemTypeID());
                            preorderItemSaleTransactionVendorID.add(codeonline.getPreorderItemSaleTransactionVendorID());

                        }
                        mRecyclerView.setVisibility(View.VISIBLE);
                        mRecyclerView.setLayoutManager(new LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false));
                        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
                        mRecyclerView.setNestedScrollingEnabled(false);
                        mAdapter = new PreOrderHistoryDetalsAdapter(preorderSalesItemID, tansactionBillNo, preorderItemTypeTimingItemID,
                                itemID, itemCode, itemName,itemShortName,preorderSalesItemQuantity,preorderSalesItemAmount,itemTypeName,
                                itemTypeID,preorderItemSaleTransactionVendorID,
                                activity, R.layout.preorderhistorydetails, getApplicationContext());
                        mRecyclerView.setAdapter(mAdapter);
                        mRecyclerView.setNestedScrollingEnabled(false);
                    }

                }
                else if(code.equals("400")){

                    ErrorImage.setBackgroundResource(R.drawable.no_data);
                    Txt_Content.setText(getResources().getString(R.string.something_went_wrong));
                    Reload.setText(getResources().getString(R.string.goback));
                    ErrorLayout.setVisibility(View.VISIBLE);
                    Reload.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {


                            onBackPressed();

                        }
                    });
                }else if (code.equals("204")){


                    ErrorImage.setBackgroundResource(R.drawable.no_data);
                    Txt_Content.setText(getResources().getString(R.string.no_data_found));
                    Reload.setText(getResources().getString(R.string.goback));
                    ErrorLayout.setVisibility(View.VISIBLE);
                    Reload.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {


                            onBackPressed();

                        }
                    });
                }else if (code.equals("401")){

                    ErrorImage.setBackgroundResource(R.drawable.no_data);
                    Txt_Content.setText(getResources().getString(R.string.something_went_wrong));
                    Reload.setText(getResources().getString(R.string.goback));
                    ErrorLayout.setVisibility(View.VISIBLE);
                    Reload.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {


                            onBackPressed();

                        }
                    });
                }else if (code.equals("500")){
                    ErrorImage.setBackgroundResource(R.drawable.no_data);
                    Txt_Content.setText(getResources().getString(R.string.something_went_wrong));
                    Reload.setText(getResources().getString(R.string.goback));
                    ErrorLayout.setVisibility(View.VISIBLE);
                    Reload.setOnClickListener(new View.OnClickListener() {
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
                ProgressBarnew.setVisibility(View.GONE);
                VolleyLog.d("Object Error : ", volleyError.getMessage());
                //ADD SNACKBAR LAYOUT
                ErrorImage.setBackgroundResource(R.drawable.no_data);
                Txt_Content.setText(getResources().getString(R.string.something_went_wrong));
                Reload.setText(getResources().getString(R.string.goback));
                ErrorLayout.setVisibility(View.VISIBLE);
                Reload.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {


                        onBackPressed();

                    }
                });
                Toast.makeText(PreorderHistoryDetailPopup.this, getResources().getString(R.string.network_error_try), Toast.LENGTH_SHORT).show();
            }
        });
        jsonObjectRequestLogin.setRetryPolicy(new DefaultRetryPolicy(50000, 3, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
//        RequestQueue requestQueue = Volley.newRequestQueue(this, commonSSLConnection.getHulkstack(PreorderHistoryDetailPopup.this));
        CampusWallet.getInstance().addToRequestQueue(jsonObjectRequestLogin, tag_json_object);
//        requestQueue.add(jsonObjectRequestLogin);
    }


}
