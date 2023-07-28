package com.hungerhub.payments;


import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
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
import com.hungerhub.networkmodels.otherpaymentmodes.PaymentItem;
import com.hungerhub.networkmodels.paymentmodes.PaymentMode;
import com.hungerhub.utils.ClickListener;
import com.hungerhub.utils.RecyclerTouchListener;

public class PaymentsOtherFragment extends Fragment {


    PaymentModesAdapter mAdapter;
    String amount= "";
    String itemId= "";
    String category = "";
    FirebaseAnalytics mFirebaseAnalytics;

    @BindView(R.id.recyclerView2) RecyclerView mRecyclerView;
    @BindView(R.id.editText2) TextView txt_amount;
    @BindView(R.id.btn_recharge) Button btn_recharge;
    @BindView(R.id.spinner) Spinner spn_Items;
    @BindView(R.id.textView15) TextView txt_modes;

    public PaymentsOtherFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.layout_other_payments_fragment, container, false);

        ButterKnife.bind(this,view);

        LinearLayoutManager mLayoutManager = new LinearLayoutManager(this.getActivity().getBaseContext());
        mRecyclerView.setLayoutManager(mLayoutManager);

        callothers();
        mFirebaseAnalytics=FirebaseAnalytics.getInstance(getActivity());
        return view;
    }


    @OnClick(R.id.btn_recharge)
    public void payment(){

        mFirebaseAnalytics.logEvent(getActivity().getResources().getString(R.string.Card_Recharge_Credit_Debit_Card_Clicked),new Bundle());
        if (!amount.equals("")||!category.equals("")) {
            setFirebasePayButtonClickIdentifier(category);
            Intent i4 = new Intent(getActivity(), PaymentXPayWebview.class);
            i4.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            i4.addFlags(Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT);
            Bundle b4 = new Bundle();
            b4.putString("trans_category", category);
            b4.putString("amount", String.valueOf(amount));
            b4.putString("paymentItemId", itemId);
            i4.putExtras(b4);
            startActivity(i4);
        }else{
            Toast.makeText(getActivity(), getActivity().getResources().getString(R.string.select_item_to_pay), Toast.LENGTH_SHORT).show();
        }
    }

    @OnClick(R.id.textView5)
    public void btnCharges(){

        Intent i4 = new Intent(getActivity(), PaymentChargesWebview.class);
        Bundle b4 = new Bundle();
        b4.putString("trans_category", category);
        i4.putExtras(b4);
        startActivity(i4);

    }
private void callothers()
{
    {

        Bundle arguments = getArguments();
        String paymentModesStr = arguments.getString("paymentModes");
        String paymentItemsStr = arguments.getString("paymentItems");
        //System.out.println("Items Payments : paymentItemsStr : "+paymentItemsStr); //[{"amount":"100","id":"54","item":"Term 1"}]
        category = arguments.getString("category");
        //Work around
        if(paymentItemsStr.equalsIgnoreCase("[]"))
        {
            paymentItemsStr="[{\"amount\":\"0\",\"id\":\"0\",\"item\":\""+getActivity().getResources().getString(R.string.select_term)+"\"}]";
        }
        else
        {
            paymentItemsStr="[{\"amount\":\"0\",\"id\":\"0\",\"item\":\""+getActivity().getResources().getString(R.string.select_term)+"\"},"+paymentItemsStr.substring(1);
        }

        ArrayList<PaymentMode> modePaymentsNew = new ArrayList<PaymentMode>();
        ArrayList<PaymentItem> itemPaymentsNew = new ArrayList<PaymentItem>();
        Gson gson = new Gson();
        modePaymentsNew = gson.fromJson(paymentModesStr, new TypeToken<List<PaymentMode>>(){}.getType());
        itemPaymentsNew.clear();
//        itemPaymentsNew.add(0,new PaymentItem("0","0",getActivity().getResources().getString(R.string.select_term)));
        itemPaymentsNew = gson.fromJson(paymentItemsStr, new TypeToken<List<PaymentItem>>(){}.getType());


        if(itemPaymentsNew.size()>1){
            spn_Items.setVisibility(View.VISIBLE);
//            for (int a=0; a<itemPaymentsNew.size(); a++) {
//                //System.out.println("Items Payments :" + itemPaymentsNew.get(a));
//            }

            ArrayAdapter<PaymentItem> adapter =
                    new ArrayAdapter<PaymentItem>(getActivity(),  R.layout.item_spinner_white, itemPaymentsNew);
            adapter.setDropDownViewResource( R.layout.item_spinner);

            spn_Items.setAdapter(adapter);

            final ArrayList<PaymentItem> finalItemPaymentsNew = itemPaymentsNew;
            spn_Items.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

                @Override
                public void onItemSelected(AdapterView<?> parent,
                                           View view, int pos, long id) {
                    String cardStatusString = parent.getItemAtPosition(pos).toString();
                    itemId = finalItemPaymentsNew.get(pos).getId();
                    amount = finalItemPaymentsNew.get(pos).getAmount();

                    txt_amount.setText(getActivity().getResources().getString(R.string.indian_rupee_symbol)+amount);
                }

                @Override
                public void onNothingSelected(AdapterView<?> arg0) {
                    // TODO Auto-generated method stub

                }
            });

        }
        else {
            spn_Items.setVisibility(View.GONE);
            txt_amount.setText(getActivity().getResources().getString(R.string.no_pending_payments));
            txt_amount.setTextSize(14);
//            Toast.makeText(getActivity(),"No pending payments",Toast.LENGTH_SHORT);
        }


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

                    if (!amount.equals("")) {
                        float amount1 = Float.parseFloat(amount);
                        if (amount1 > 0) {
                            setFirebaseClickIdentifier(category,finalModePaymentsNew.get(position).getName());
                            switch (finalModePaymentsNew.get(position).getName()) {

                                case "CC/DC":
                                    Intent i = new Intent(getActivity(), PaymentCreateActivity.class);
                                    Bundle b = new Bundle();
                                    b.putString("paymentMode", "card");
                                    b.putString("amount",  String.valueOf(amount1));
                                    b.putString("trans_category", category);
                                    b.putString("paymentGateway", "razorpay");
                                    b.putString("paymentItemId", itemId);
                                    i.putExtras(b);
                                    startActivity(i);
                                    break;

                                case "Xpay":
                                    Intent i4 = new Intent(getActivity(), PaymentXPayWebview.class);
                                    i4.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                    i4.addFlags(Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT);
                                    Bundle b4 = new Bundle();
                                    b4.putString("trans_category", category);
                                    b4.putString("amount", String.valueOf(amount1));
                                    b4.putString("paymentItemId", itemId);
                                    i4.putExtras(b4);
                                    startActivity(i4);
                                    break;
                                case "Netbanking":
                                    Intent i1 = new Intent(getActivity(), PaymentCreateActivity.class);
                                    Bundle b1 = new Bundle();
                                    b1.putString("paymentMode", "netbanking");
                                    b1.putString("amount",  String.valueOf(amount1));
                                    b1.putString("trans_category", category);
                                    b1.putString("paymentGateway", "razorpay");
                                    b1.putString("paymentItemId", itemId);
                                    i1.putExtras(b1);
                                    startActivity(i1);
                                    break;

                                case "UPI":
                                    Intent i2 = new Intent(getActivity(), PaymentCreateActivity.class);
                                    Bundle b2 = new Bundle();
                                    b2.putString("paymentMode", "upi");
                                    b2.putString("amount",  String.valueOf(amount1));
                                    b2.putString("trans_category", category);
                                    b2.putString("paymentItemId", itemId);
                                    b2.putString("paymentGateway", "razorpay");
                                    i2.putExtras(b2);
                                    startActivity(i2);
                                    break;

                            }
                        } else {
                            Toast.makeText(getActivity(), getActivity().getResources().getString(R.string.valid_payment), Toast.LENGTH_SHORT).show();
                        }

                    }else{
                        Toast.makeText(getActivity(), getActivity().getResources().getString(R.string.valid_payment), Toast.LENGTH_SHORT).show();
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
}
public void setFirebasePayButtonClickIdentifier(String category)
{
    switch (category)
    {
        case "2":
            mFirebaseAnalytics.logEvent(getActivity().getResources().getString(R.string.Card_Recharge_Pay_Clicked),new Bundle());
            break;
        case "3":
            mFirebaseAnalytics.logEvent(getActivity().getResources().getString(R.string.Fee_Payment_Pay_Clicked),new Bundle());
            break;
        case "4":
            mFirebaseAnalytics.logEvent(getActivity().getResources().getString(R.string.Miscellaneous_Payment_Pay_Clicked),new Bundle());
            break;
        case "5":
            mFirebaseAnalytics.logEvent(getActivity().getResources().getString(R.string.Trust_Payment_Pay_Clicked),new Bundle());
            break;
    }
}
    public void setFirebaseClickIdentifier(String catName,String modName)
    {
//        String headerName="";
        switch (catName)
        {
            case "2":
                switch (modName)
                {
                    case "CC/DC":
                        mFirebaseAnalytics.logEvent(getActivity().getResources().getString(R.string.Card_Recharge_Credit_Debit_Card_Clicked),new Bundle());
                        break;

                    case "Xpay":
                        mFirebaseAnalytics.logEvent(getActivity().getResources().getString(R.string.Card_Recharge_Xpay_Clicked),new Bundle());
                        break;
                    case "Netbanking":
                        mFirebaseAnalytics.logEvent(getActivity().getResources().getString(R.string.Card_Recharge_Netbanking_Clicked),new Bundle());
                        break;

                    case "UPI":
                        mFirebaseAnalytics.logEvent(getActivity().getResources().getString(R.string.Card_Recharge_Upi_Clicked),new Bundle());
                        break;
                }
                break;
            case "3":
                switch (modName)
                {
                    case "CC/DC":
                        mFirebaseAnalytics.logEvent(getActivity().getResources().getString(R.string.Fee_Payment_Credit_Debit_Card_Clicked),new Bundle());
                        break;

                    case "Xpay":
                        mFirebaseAnalytics.logEvent(getActivity().getResources().getString(R.string.Fee_Payment_Xpay_Clicked),new Bundle());
                        break;
                    case "Netbanking":
                        mFirebaseAnalytics.logEvent(getActivity().getResources().getString(R.string.Fee_Payment_Netbanking_Clicked),new Bundle());
                        break;

                    case "UPI":
                        mFirebaseAnalytics.logEvent(getActivity().getResources().getString(R.string.Fee_Payment_Upi_Clicked),new Bundle());
                        break;
                }
                break;
            case "4":
                switch (modName)
                {
                    case "CC/DC":
                        mFirebaseAnalytics.logEvent(getActivity().getResources().getString(R.string.Miscellaneous_Payment_Credit_Debit_Card_Clicked),new Bundle());
                        break;

                    case "Xpay":
                        mFirebaseAnalytics.logEvent(getActivity().getResources().getString(R.string.Miscellaneous_Payment_Xpay_Clicked),new Bundle());
                        break;
                    case "Netbanking":
                        mFirebaseAnalytics.logEvent(getActivity().getResources().getString(R.string.Miscellaneous_Payment_Netbanking_Clicked),new Bundle());
                        break;

                    case "UPI":
                        mFirebaseAnalytics.logEvent(getActivity().getResources().getString(R.string.Miscellaneous_Payment_Upi_Clicked),new Bundle());
                        break;
                }
                break;
            case "5":
                switch (modName)
                {
                    case "CC/DC":
                        mFirebaseAnalytics.logEvent(getActivity().getResources().getString(R.string.Trust_Payment_Credit_Debit_Card_Clicked),new Bundle());
                        break;

                    case "Xpay":
                        mFirebaseAnalytics.logEvent(getActivity().getResources().getString(R.string.Trust_Payment_Xpay_Clicked),new Bundle());
                        break;
                    case "Netbanking":
                        mFirebaseAnalytics.logEvent(getActivity().getResources().getString(R.string.Trust_Payment_Netbanking_Clicked),new Bundle());
                        break;

                    case "UPI":
                        mFirebaseAnalytics.logEvent(getActivity().getResources().getString(R.string.Trust_Payment_Upi_Clicked),new Bundle());
                        break;
                }
                break;


        }
//        return headerName;
    }

    @Override
    public void onStart()
    {
        super.onStart();
    }
}
