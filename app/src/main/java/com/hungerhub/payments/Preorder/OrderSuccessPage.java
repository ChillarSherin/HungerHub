package com.hungerhub.payments.Preorder;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.chillarcards.cookery.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import com.hungerhub.application.CustomConnectionBuddyActivity;
import com.hungerhub.application.PrefManager;

@SuppressWarnings("ALL")
public class OrderSuccessPage extends CustomConnectionBuddyActivity {
    private float offset;
    private boolean flipped;
    Button GoBack;
    String OrderID;

    TextView tview;
    @BindView(R.id.HeaderTV)
    TextView HeaderText;
    @BindView(R.id.BackIV)
    ImageView BackButton;
//    @BindView(R.id.ReloadBTN)
//    Button ReloadBTN;
//    @BindView(R.id.GoBackBTN)
//    Button GoBackBTN;
//    @BindView(R.id.NodataTV)
    TextView NodataTV;
    PrefManager prefManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order);
        ButterKnife.bind(this);
//        Fabric.with(this, new Answers());
        prefManager=new PrefManager(OrderSuccessPage.this);
        HeaderText.setText(getResources().getString(R.string.order_status));
        BackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        tview=(TextView)findViewById(R.id.orderid);
        Bundle bundle = getIntent().getExtras();
        OrderID = bundle.getString("OrderID");

        tview.setText(getResources().getString(R.string.successful_order)+" "+OrderID);
        GoBack= (Button) findViewById(R.id.BtnGoBack);

        GoBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

    }


    @Override
    public void onBackPressed() {

        mFirebaseAnalytics.logEvent(getResources().getString(R.string.Pre_Order_Success_Button_clicked),new Bundle());
        Intent i = new Intent(getApplicationContext(), PreOrderTimmings.class);
        i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(i);
        finish();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

}

