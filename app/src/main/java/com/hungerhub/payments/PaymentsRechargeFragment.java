package com.hungerhub.payments;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.text.InputFilter;
import android.text.Spanned;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.chillarcards.cookery.R;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import com.hungerhub.application.Constants;
import com.hungerhub.networkmodels.paymentmodes.PaymentMode;
import com.hungerhub.utils.ClickListener;
import com.hungerhub.utils.Clipboard_Utils;
import com.hungerhub.utils.RecyclerTouchListener;

public class PaymentsRechargeFragment extends Fragment {

    PaymentModesAdapter mAdapter;
    String category = "";
    String fromLowbalance,rechargeAmount;
    FirebaseAnalytics mFirebaseAnalytics;

    @BindView(R.id.recyclerView2) RecyclerView mRecyclerView;
    @BindView(R.id.editText2) EditText et_amount;
    @BindView(R.id.btn_recharge) Button btn_recharge;
    @BindView(R.id.textView15) TextView txt_modes;
    @BindView(R.id.textView5) TextView txnCharges;
    int i=0;
    private String blockCharacterSet = ".¿¡》《¤▪☆♧♢♡♤■□●○~#^|$%&*!&()€¥•_[]=£><@-+/`√π÷×¶∆¢°{}©®™✓:;?'\"\\";
    private InputFilter filter = new InputFilter() {

        @Override
        public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {

            if (source != null && blockCharacterSet.contains(("" + source))) {
                return "";
            }
            return null;
        }
    };

    public PaymentsRechargeFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.layout_recharge_fragment_razor, container, false);

        ButterKnife.bind(this,view);

        LinearLayoutManager mLayoutManager = new LinearLayoutManager(this.getActivity().getBaseContext());
        mRecyclerView.setLayoutManager(mLayoutManager);
        et_amount.setFilters(new InputFilter[]{filter/*, new InputFilter.LengthFilter(100)*/});
        et_amount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Clipboard_Utils.copyToClipboard(getActivity(), "");
            }
        });
        //Show keyboard and focuz on amount edittext
        et_amount.requestFocus();
        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);

        init();
        mFirebaseAnalytics=FirebaseAnalytics.getInstance(getActivity());
        return view;
    }

    private void init(){

        Bundle arguments = getArguments();
        String paymentModesStr = arguments.getString("paymentModes");
        category = arguments.getString("category");
        fromLowbalance = arguments.getString("fromLowbalance");
        rechargeAmount = arguments.getString("rechargeAmount");
        if (fromLowbalance.equalsIgnoreCase("1"))
        {
            et_amount.setText(rechargeAmount);
//            et_amount.setEnabled(false);
            Constants.from_Low_balance=true;
            Constants.from_old_preorder=0;
        }
        else if(fromLowbalance.equalsIgnoreCase("5"))
        {
            Constants.from_Low_balance=true;
            Constants.from_old_preorder=1;
        }
        else {
            et_amount.setEnabled(true);
            Constants.from_Low_balance=false;
        }

        ArrayList<PaymentMode> modePaymentsNew = new ArrayList<PaymentMode>();
        Gson gson = new Gson();
        modePaymentsNew = gson.fromJson(paymentModesStr, new TypeToken<List<PaymentMode>>(){}.getType());


        if(modePaymentsNew.size()>0) {

            mAdapter = new PaymentModesAdapter(modePaymentsNew, this.getActivity());
            mRecyclerView.setAdapter(mAdapter);

            final ArrayList<PaymentMode> finalModePaymentsNew = modePaymentsNew;
            mRecyclerView.addOnItemTouchListener(new RecyclerTouchListener(getActivity().getBaseContext(),
                    mRecyclerView, new ClickListener() {
                @Override
                public void onClick(View view, final int position) {
                    //Values are passing to activity & to fragment as well
//                    Toast.makeText(getActivity().getBaseContext(), "Single Click on position        :" + position,
//                            Toast.LENGTH_SHORT).show();

                    if (et_amount.getText().toString().length() > 0) {
                        float amount = Float.parseFloat(et_amount.getText().toString());
                        if (amount > 0) {

                            switch (finalModePaymentsNew.get(position).getName()) {

                                case "CC/DC":

                                    mFirebaseAnalytics.logEvent(getActivity().getResources().getString(R.string.Card_Recharge_Credit_Debit_Card_Clicked),new Bundle());
                                    Intent i = new Intent(getActivity(), PaymentCreateActivity.class);
                                    Bundle b = new Bundle();
                                    b.putString("paymentMode", "card");
                                    b.putString("amount", et_amount.getText().toString());
                                    b.putString("trans_category",category);
                                    b.putString("paymentGateway", "razorpay");
                                    i.putExtras(b);
                                    startActivity(i);
                                    break;

                                case "Xpay":
                                    mFirebaseAnalytics.logEvent(getActivity().getResources().getString(R.string.Card_Recharge_Xpay_Clicked),new Bundle());
                                    Intent i4 = new Intent(getActivity(), PaymentXPayWebview.class);
                                    Bundle b4 = new Bundle();
                                    b4.putString("trans_category",category);
                                    b4.putString("amount", String.valueOf(amount));
                                    i4.putExtras(b4);
                                    startActivity(i4);
                                    break;
                                case "Netbanking":
                                    mFirebaseAnalytics.logEvent(getActivity().getResources().getString(R.string.Card_Recharge_Netbanking_Clicked),new Bundle());
                                    Intent i1 = new Intent(getActivity(), PaymentCreateActivity.class);
                                    Bundle b1 = new Bundle();
                                    b1.putString("paymentMode", "netbanking");
                                    b1.putString("amount", et_amount.getText().toString());
                                    b1.putString("trans_category", category);
                                    b1.putString("paymentGateway", "razorpay");
                                    i1.putExtras(b1);
                                    startActivity(i1);
                                    break;
                                case "UPI":

                                    mFirebaseAnalytics.logEvent(getActivity().getResources().getString(R.string.Card_Recharge_Upi_Clicked),new Bundle());
                                    Intent i2 = new Intent(getActivity(), PaymentCreateActivity.class);
                                    Bundle b2 = new Bundle();
                                    b2.putString("paymentMode", "upi");
                                    b2.putString("amount", et_amount.getText().toString());
                                    b2.putString("trans_category", category);
                                    b2.putString("paymentGateway", "razorpay");
                                    i2.putExtras(b2);
                                    startActivity(i2);
                                    break;
                            }
                        } else {
                            Toast.makeText(getActivity(), getActivity().getResources().getString(R.string.valid_amount), Toast.LENGTH_SHORT).show();
                        }

                    }else{
                        Toast.makeText(getActivity(), getActivity().getResources().getString(R.string.valid_amount), Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onLongClick(View view, int position) {
//                    Toast.makeText(getActivity().getBaseContext(), "Long press on position :" + position,
//                            Toast.LENGTH_LONG).show();
                }
            }));

        }else{

            mRecyclerView.setVisibility(View.GONE);
            txt_modes.setVisibility(View.GONE);
            btn_recharge.setVisibility(View.VISIBLE);
        }
    }

    @OnClick(R.id.btn_recharge)
    public void btnClick(){
        mFirebaseAnalytics.logEvent(getActivity().getResources().getString(R.string.Card_Recharge_Pay_Clicked),new Bundle());
        if(et_amount.getText().toString().length()>0){

            int amount = Integer.parseInt(et_amount.getText().toString());
            if (!et_amount.getText().toString().contains(".")) {
                if (amount > 0) {

                    if (amount <= 10000) {
                        //XPay
                        Intent i4 = new Intent(getActivity(), PaymentXPayWebview.class);
                        Bundle b4 = new Bundle();
                        b4.putString("trans_category", category);
                        b4.putString("amount", String.valueOf(amount));
                        i4.putExtras(b4);
                        startActivity(i4);
                    } else {
                        et_amount.setError(getActivity().getResources().getString(R.string.valid_amount_lessthan));
                    }

                } else {
                    et_amount.setError(getActivity().getResources().getString(R.string.valid_amount));

                    //Toast.makeText(getActivity(), "Enter a valid amount!", Toast.LENGTH_SHORT).show();
                }
            }
            else
                {
                    et_amount.setError(getActivity().getResources().getString(R.string.valid_amount));
                }

        }else{
//            Toast.makeText(getActivity(), "Enter a valid amount!", Toast.LENGTH_SHORT).show();
            et_amount.setError(getActivity().getResources().getString(R.string.valid_amount));
        }


    }

    @OnClick(R.id.textView5)
    public void btnCharges(){
        mFirebaseAnalytics.logEvent(getActivity().getResources().getString(R.string.Card_Recharge_Transaction_Charges_Clicked),new Bundle());
        Intent i4 = new Intent(getActivity(), PaymentChargesWebview.class);
        Bundle b4 = new Bundle();
        b4.putString("trans_category", category);
        i4.putExtras(b4);
        startActivity(i4);

    }



    @Override
    public void onStart() {
        super.onStart();

        //System.out.println("PaymentsRechargeFragment OnSTART : "+i);
        i++;


    }
}
