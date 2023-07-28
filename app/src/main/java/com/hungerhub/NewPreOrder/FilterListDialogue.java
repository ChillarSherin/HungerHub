package com.hungerhub.NewPreOrder;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.view.Window;
import android.widget.Button;

import com.chillarcards.cookery.R;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import com.hungerhub.networkmodels.OrderItemsDetails.Outlet;

public class FilterListDialogue extends Dialog {

    @BindView(R.id.FilterRV)
    RecyclerView FilterRV;
    @BindView(R.id.FilteCloseBTN)
    Button FilteCloseBTN;

    PreorderDesignActivity preorderDesignActivity;
    List<Outlet> categories;
    FilterCallBack filterCallBack;
    private FilterAdapter mAdapter;

    public FilterListDialogue(@NonNull Context context) {
        super(context);
    }

    public FilterListDialogue(@NonNull Context context, PreorderDesignActivity preorderDesignActivity, List<Outlet> categories, FilterCallBack filterCallBack) {
        super(context);
        this.preorderDesignActivity = preorderDesignActivity;
        this.categories = categories;
        this.filterCallBack = filterCallBack;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.filter_outlets);
        ButterKnife.bind(this);

        FilterRV.setLayoutManager(new LinearLayoutManager(preorderDesignActivity, LinearLayoutManager.VERTICAL, false));
        FilterRV.setItemAnimator(new DefaultItemAnimator());
        FilterRV.setNestedScrollingEnabled(false);
        mAdapter = new FilterAdapter(categories, preorderDesignActivity,filterCallBack);
        mAdapter.notifyDataSetChanged();
        FilterRV.setAdapter(mAdapter);

        FilteCloseBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
    }

}
