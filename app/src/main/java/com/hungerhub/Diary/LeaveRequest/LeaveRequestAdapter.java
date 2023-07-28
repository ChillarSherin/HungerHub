package com.hungerhub.Diary.LeaveRequest;

import android.app.Activity;
import android.content.Context;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.chillarcards.cookery.R;
import com.google.firebase.analytics.FirebaseAnalytics;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import com.hungerhub.networkmodels.leaverequest.Code;


public class LeaveRequestAdapter extends RecyclerView.Adapter<LeaveRequestAdapter.MyViewHolder> {

    private List<Code> mData = new ArrayList<>();
    private Activity activity;
    Context mContext;
    FirebaseAnalytics mFirebaseAnalytics;

    public LeaveRequestAdapter(List<Code> myDataset, Activity activity, Context mContext,FirebaseAnalytics mFirebaseAnalytics) {
        this.mData = myDataset;
        this.activity=activity;
        this.mContext=mContext;
        this.mFirebaseAnalytics=mFirebaseAnalytics;
    }


    @NonNull
    @Override
    public LeaveRequestAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v= LayoutInflater.from(parent.getContext()).inflate(R.layout.item_leave_request, parent, false);
        LeaveRequestAdapter.MyViewHolder vh = new LeaveRequestAdapter.MyViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull LeaveRequestAdapter.MyViewHolder holder, final int position) {

        holder.txtFrom.setText(mData.get(position).getFromDate());
        holder.txtTo.setText(mData.get(position).getToDate());
        holder.txtMsg.setText(mData.get(position).getReason());
        holder.txtApplied.setText(mData.get(position).getCreatedDate());

        if(mData.get(position).getReadStatus().equals("1")){

            holder.topLayout.setBackgroundResource(R.drawable.bg_leave_item_top_two);
            holder.txtStatus.setText(mContext.getResources().getString(R.string.leave_granted_leave));

        }else{
            holder.topLayout.setBackgroundResource(R.drawable.bg_leave_item_top_one);
            holder.txtStatus.setText(mContext.getResources().getString(R.string.waiting_for_approval));
        }

    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.fromid) TextView txtFrom;
        @BindView(R.id.toid) TextView txtTo;
        @BindView(R.id.dataid) TextView txtMsg;
        @BindView(R.id.applied) TextView txtApplied;
        @BindView(R.id.statusid) TextView txtStatus;
        @BindView(R.id.toplayout) RelativeLayout topLayout;


        public MyViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this,itemView);
        }
    }
}
