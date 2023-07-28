package com.hungerhub.NewPreOrder;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.drawable.ColorDrawable;
import android.os.CountDownTimer;
import android.os.Environment;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
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
import com.google.zxing.WriterException;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import androidmads.library.qrgenearator.QRGContents;
import androidmads.library.qrgenearator.QRGEncoder;
import butterknife.BindView;
import butterknife.ButterKnife;
import com.hungerhub.application.CampusWallet;
import com.hungerhub.application.Constants;
import com.hungerhub.application.PrefManager;
import com.hungerhub.home.HomeActivity;
import com.hungerhub.networkmodels.RefreshOrderData.Data;
import com.hungerhub.networkmodels.RefreshOrderData.RefreshOrderData;
import com.hungerhub.networkmodels.RefreshOrderData.Status;
import com.hungerhub.utils.CommonSSLConnection;

public class CartStatusActivity extends AppCompatActivity {

    PrefManager prefManager;
    @BindView(R.id.HeaderTV)
    TextView HeaderText;
    @BindView(R.id.OrderRefreshTV)
    TextView OrderRefreshTV;
    @BindView(R.id.BackIV)
    ImageView BackButton;
    @BindView(R.id.OderQRIV)
    ImageView OderQRIV;
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

    private String TAG="QR_CODE";
    String savePath = Environment.getExternalStorageDirectory().getPath() + "/QRCode/";
    Bitmap bitmap;
    QRGEncoder qrgEncoder;
    String mOrderID="",mRealOrderID="";
    CountDownTimer countDownTimer=null;
    private String tag_json_object="Refresh_Order";
    private Dialog dialogWhstapp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart_status);
        ButterKnife.bind(this);

        Bundle bundle=new Bundle();
        bundle=getIntent().getExtras();
        if (bundle!=null)
        {
            mOrderID=bundle.getString("OrderID");
            mRealOrderID=bundle.getString("RealOrderID");
        }

        prefManager=new PrefManager(CartStatusActivity.this);
        setProgressBarGone();

        generateQR(convertOrderJSON(mOrderID));
        QRCountDownTimer();

        OrderRefreshTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (OrderRefreshTV.getText().toString().trim().contains("CLICK HERE"))
                {
                    if (mRealOrderID.trim().length()>0) {
                        RefreshQRPHP(mRealOrderID);
                    }
                }
            }
        });

        Constants.CartItems.clear();

        HeaderText.setText(getResources().getString(R.string.order_status));
        BackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent=new Intent(CartStatusActivity.this, HomeActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                startActivity(intent);
                finish();
            }
        });
    }
    public String convertOrderJSON(String orderID)
    {
        String jsonStr="";
        JSONObject orderList = new JSONObject();
        try {
            orderList.put("OrderIDTemp", orderID);
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        JSONObject orderObj = new JSONObject();
        try {
            orderObj.put("OrderDetails", orderList);
        } catch (JSONException e) {
            e.printStackTrace();

        }
        jsonStr = orderObj.toString();
        //System.out.println("ORDER JSON : "+jsonStr);
        return jsonStr;
    }
    public void QRCountDownTimer()
    {
        countDownTimer= new CountDownTimer(150000,1000){

            @Override
            public void onTick(long l) {
                OrderRefreshTV.setText(Html.fromHtml("<font color=#000000> " +"QR Code will expire in "+ "</font>  "+(l/1000)));
            }

            @Override
            public void onFinish() {
                OrderRefreshTV.setText(Html.fromHtml("<font color=#000000> " +"QR Code Expired ?"+ "</font>  "
                        + " <font color=#1abc9c>" + "CLICK HERE" + "</font> " ));
            }
        }.start();
    }

    @Override
    public void onBackPressed() {
        Intent intent=new Intent(CartStatusActivity.this, HomeActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        startActivity(intent);
        finish();
    }
    public  void generateQR(String orderData)
    {
        if (orderData.length() > 0) {
            WindowManager manager = (WindowManager) getSystemService(WINDOW_SERVICE);
            Display display = manager.getDefaultDisplay();
            Point point = new Point();
            display.getSize(point);
            int width = point.x;
            int height = point.y;
            int smallerDimension = width < height ? width : height;
            smallerDimension = smallerDimension * 3 / 4;

            qrgEncoder = new QRGEncoder(
                    orderData, null,
                    QRGContents.Type.TEXT,
                    smallerDimension);
            try {
                bitmap = qrgEncoder.encodeAsBitmap();
                OderQRIV.setImageBitmap(bitmap);
            } catch (WriterException e) {
                Log.v(TAG, e.toString());
            }
        } else {
            //System.out.println("QR Genarator ERROR");
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        countDownTimer.cancel();
    }

    @Override
    protected void onStop() {
        super.onStop();
        countDownTimer.cancel();
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
    private void RefreshQRPHP(final String realOrderID) {

        setProgressBarVisible();
        String URL;
        URL = Constants.BASE_ORDER_URL + "user_single_orders";
        //System.out.println("CHECK---> URL " + URL);
        CommonSSLConnection commonSSLConnection = new CommonSSLConnection();
        StringRequest jsonObjectRequestLogin = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String jsonObject) {
                //System.out.println("CHECK---> Response " + jsonObject);
                setProgressBarGone();

                CampusWallet.getInstance().cancelPendingRequests(tag_json_object);

                Gson gson = new Gson();
                RefreshOrderData noticeboard = gson.fromJson(jsonObject, RefreshOrderData.class);
                Status status = noticeboard.getStatus();
                String code = status.getCode();
                if (code.equals("200")) {

                    List<Data> myDataset = noticeboard.getData();
                    if (myDataset.size() > 0) {
                        for (int i = 0; i < myDataset.size(); i++) {
                            String orderID = myDataset.get(i).getOrderPurchaseID();
                            String realOrderID = myDataset.get(i).getRealOrderPurchaseID();
                            generateQR(convertOrderJSON(orderID));
                        }


                    } else {
                        NodataTV.setText(getResources().getString(R.string.no_data_found));
                        GoBackBTN.setText(getResources().getString(R.string.go_back));
//                        ErrorImage.setBackgroundResource(R.drawable.nodata);
                        findViewById(R.id.NodataLL).setVisibility(View.VISIBLE);
                        GoBackBTN.setVisibility(View.GONE);
                    }

                }

                else if (code.equals("411"))
                {
                    showCommonDialogue(status.getMessage());
                }

                else {
                    setProgressBarGone();
                    String message = status.getMessage();
                    ReloadBTN.setText(getResources().getString(R.string.go_back));
                    findViewById(R.id.ErrorLL).setVisibility(View.VISIBLE);
                    ErrorMessageTV.setText(getResources().getString(R.string.error_message_errorlayout));
                    CodeErrorTV.setText(getResources().getString(R.string.code_attendance) + code);
                    ReloadBTN.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {


                            onBackPressed();

                        }
                    });
                    Toast.makeText(CartStatusActivity.this, message, Toast.LENGTH_SHORT).show();
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
                Toast.makeText(CartStatusActivity.this, getResources().getString(R.string.network_error_try), Toast.LENGTH_SHORT).show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("targetUserID", prefManager.getStudentUserId());
                params.put("schoolID", prefManager.getSChoolID());
                params.put("realOrderPurchaseID", realOrderID);
                //System.out.println("CHECK---> " + prefManager.getStudentUserId() + " , " +
//                        prefManager.getSChoolID()+ " ,\n " +realOrderID);

                return params;
            }
        };

        jsonObjectRequestLogin.setRetryPolicy(new DefaultRetryPolicy(50000, 3, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
//        RequestQueue requestQueue = Volley.newRequestQueue(this, commonSSLConnection.getHulkstack(CartListActivity.this));
        CampusWallet.getInstance().addToRequestQueue(jsonObjectRequestLogin, tag_json_object);
//        requestQueue.add(jsonObjectRequestLogin);

    }
    private void showCommonDialogue(String message) {

        dialogWhstapp = new Dialog(CartStatusActivity.this, android.R.style.Theme_Dialog);
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
}
