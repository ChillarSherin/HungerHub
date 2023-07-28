package com.hungerhub.payments.Transactions;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.chillarcards.cookery.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import com.hungerhub.Payment.History.PaymentHistoryActivity;
import com.hungerhub.application.CustomConnectionBuddyActivity;
import com.hungerhub.payments.cardtransaction.CardTransactionActivity;

public class TransactionSelectionActivity extends CustomConnectionBuddyActivity {
    @BindView(R.id.HeaderTV)
    TextView HeaderText;
    @BindView(R.id.BackIV)
    ImageView BackButton;
    @BindView(R.id.WalletTransactionsLL)
    LinearLayout WalletTransactionsLL;
    @BindView(R.id.PamentTransactionsLL)
    LinearLayout PamentTransactionsLL;
    @BindView(R.id.onlinePaymentStatus)
    TextView onlinePaymentStatusTV;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transaction_selection);
        ButterKnife.bind(this);
        HeaderText.setText(getResources().getString(R.string.transaction));
        BackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        PamentTransactionsLL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mFirebaseAnalytics.logEvent(getResources().getString(R.string.Payment_Transactions_Menu_Selected),new Bundle());
                Intent i6 =new Intent(TransactionSelectionActivity.this,PaymentHistoryActivity.class);
                i6.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                i6.addFlags(Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT);
                startActivity(i6);
            }
        });
        WalletTransactionsLL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mFirebaseAnalytics.logEvent(getResources().getString(R.string.Wallet_Transactions_Menu_Selected),new Bundle());
                Intent i6 =new Intent(TransactionSelectionActivity.this,CardTransactionActivity.class);
                i6.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                i6.addFlags(Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT);
                startActivity(i6);
            }
        });
        onlinePaymentStatusTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mFirebaseAnalytics.logEvent(getResources().getString(R.string.Online_Transactions_Menu_Selected),new Bundle());
                Intent i6 =new Intent(TransactionSelectionActivity.this,OnlineTransactionStatusActivity.class);
                i6.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                i6.addFlags(Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT);
                startActivity(i6);
            }
        });
    }
}
