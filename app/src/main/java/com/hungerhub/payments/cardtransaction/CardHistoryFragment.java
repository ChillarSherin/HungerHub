package com.hungerhub.payments.cardtransaction;

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
import com.hungerhub.networkmodels.cardtransactionhistory.Transaction;


public class CardHistoryFragment extends Fragment {
    private static final String ARG_TRANS_LIST = "transactionArrList";


    private String jsonTransactionArray;
    List<Transaction> transactionList;
    Context cntxt;
    CardHistoryFragmentAdapterdummy mAdapter;

    @BindView(R.id.TransactionListRV)  RecyclerView TransactionListRV;
    @BindView(R.id.WalletTransactionSRL)
    SwipeRefreshLayout WalletTransactionSRL;
    @BindView(R.id.ReloadBTN)
    Button ReloadBTN;
    @BindView(R.id.GoBackBTN)
    Button GoBackBTN;
    @BindView(R.id.NodataTV)
    TextView NodataTV;
    View view;
    int posSpec;
    FirebaseAnalytics mFirebaseAnalytics;
    RefreshStatement refreshStatement;

    public CardHistoryFragment() {
        // Required empty public constructor
    }
    @SuppressLint("ValidFragment")
    public CardHistoryFragment(RefreshStatement refreshStatement) {
        // Required empty public constructor
        this.refreshStatement=refreshStatement;
    }

    @Override
    public void onStart() {
        super.onStart();
        if (getArguments() != null) {
            jsonTransactionArray = getArguments().getString(ARG_TRANS_LIST);
            posSpec=getArguments().getInt("posSpec");
            Gson gson=new Gson();
            TypeToken<List<Transaction>> token = new TypeToken<List<Transaction>>() {};
            transactionList= gson.fromJson(jsonTransactionArray,token.getType());

            if (transactionList.size()!=0) {
                view.findViewById(R.id.NodataLL).setVisibility(View.GONE);
                TransactionListRV.setVisibility(View.VISIBLE);
                mAdapter = new CardHistoryFragmentAdapterdummy(transactionList, getActivity(),posSpec,mFirebaseAnalytics);
                TransactionListRV.setAdapter(mAdapter);
            }
            else
            {
                TransactionListRV.setVisibility(View.GONE);
                //System.out.println("Abhinand Empty : ");
                NodataTV.setText(getActivity().getResources().getString(R.string.no_data_found));
                GoBackBTN.setText(getActivity().getResources().getString(R.string.go_back));
//                        ErrorImage.setBackgroundResource(R.drawable.nodata);
                view.findViewById(R.id.NodataLL).setVisibility(View.VISIBLE);
                GoBackBTN.setVisibility(View.GONE);
            }
            WalletTransactionSRL.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                @Override
                public void onRefresh() {
                    refreshStatement.onStatementRefresh();
                    WalletTransactionSRL.setRefreshing(false);
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
        WalletTransactionSRL.setColorSchemeResources(R.color.colorAccent);

        mFirebaseAnalytics=FirebaseAnalytics.getInstance(getActivity());
        return view;
    }


}
