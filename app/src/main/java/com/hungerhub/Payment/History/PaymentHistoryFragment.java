package com.hungerhub.Payment.History;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.chillarcards.cookery.R;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import com.hungerhub.networkmodels.FeePaymentReport.PaymentDetail;
import com.hungerhub.payments.cardtransaction.RefreshStatement;

public class PaymentHistoryFragment extends Fragment {
    private static final String ARG_TRANS_LIST = "transactionArrList";


    private String jsonTransactionArray;
    List<PaymentDetail> transactionList;
    Context cntxt;
    HistoryFragmentAdapterdummy mAdapter;
    RefreshStatement refreshStatement;

    @BindView(R.id.TransactionListRV)  RecyclerView TransactionListRV;
    @BindView(R.id.WalletTransactionSRL)
    SwipeRefreshLayout FeePaymentTransactionSRL;
    @BindView(R.id.GoBackBTN)
    Button GoBackBTN;
    @BindView(R.id.NodataTV)
    TextView NodataTV;
    @BindView(R.id.ReloadBTN)
    Button ReloadBTN;
    View view;
    int tabpos;
    FirebaseAnalytics mFirebaseAnalytics;

    public PaymentHistoryFragment() {
        // Required empty public constructor
    }

    @SuppressLint("ValidFragment")
    public PaymentHistoryFragment(RefreshStatement refreshStatement) {
        this.refreshStatement = refreshStatement;
    }


    @Override
    public void onStart() {
        super.onStart();
        if (getArguments() != null) {
            jsonTransactionArray = getArguments().getString(ARG_TRANS_LIST);
            tabpos=getArguments().getInt("tabpos");
            Gson gson=new Gson();
            TypeToken<List<PaymentDetail>> token = new TypeToken<List<PaymentDetail>>() {};
            transactionList= gson.fromJson(jsonTransactionArray,token.getType());
//            Iterator itr = transactionList.iterator();
//            while (itr.hasNext())
//            {
//                PaymentDetail x = (PaymentDetail)itr.next();
//                if (x.getTransactionCategoryID() .equalsIgnoreCase("2"))
//                    itr.remove();
//            }

            if (transactionList.size()!=0) {
                view.findViewById(R.id.NodataLL).setVisibility(View.GONE);
                mAdapter = new HistoryFragmentAdapterdummy(transactionList, getActivity(),tabpos,mFirebaseAnalytics);
                TransactionListRV.setAdapter(mAdapter);
            }
            else
            {
                NodataTV.setText(getActivity().getResources().getString(R.string.no_data_found));
                GoBackBTN.setText(getActivity().getResources().getString(R.string.go_back));
//                        ErrorImage.setBackgroundResource(R.drawable.nodata);
                view.findViewById(R.id.NodataLL).setVisibility(View.VISIBLE);
                GoBackBTN.setVisibility(View.GONE);
            }
            FeePaymentTransactionSRL.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                @Override
                public void onRefresh() {
                    refreshStatement.onStatementRefresh();
                    FeePaymentTransactionSRL.setRefreshing(false);
                }
            });
        }
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_payment_history, container, false);
        ButterKnife.bind(this,view);

        TransactionListRV.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
        TransactionListRV.setItemAnimator(new DefaultItemAnimator());
        TransactionListRV.setNestedScrollingEnabled(false);
        mFirebaseAnalytics=FirebaseAnalytics.getInstance(getActivity());
        FeePaymentTransactionSRL.setColorSchemeResources(R.color.colorAccent);

        return view;
    }


}
