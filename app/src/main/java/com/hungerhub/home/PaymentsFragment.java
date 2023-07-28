package com.hungerhub.home;

import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.chillarcards.cookery.R;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import com.hungerhub.networkmodels.home.Payment;

public class PaymentsFragment extends Fragment {

    ArrayList<String> paymentModules= new ArrayList<>();
    String studentName,studentClass,schoolName;
    PaymentsAdapter mAdapter;
    FirebaseAnalytics mFirebaseAnalytics;

    @BindView(R.id.recyclerView3) RecyclerView mRecyclerView;
    @BindView(R.id.textView20) TextView txt_stud_name;
    @BindView(R.id.textView21) TextView txt_stud_class;
    @BindView(R.id.textView22) TextView txt_stud_school;

    public PaymentsFragment(){
        //Required empty public constructor
    }



    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.layout_payments_fragment, container, false);

        ButterKnife.bind(this,view);

        LinearLayoutManager mLayoutManager = new GridLayoutManager(this.getActivity().getBaseContext(),3);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mFirebaseAnalytics=FirebaseAnalytics.getInstance(getActivity());
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        Bundle arguments = getArguments();
        String paymentModulesStr = arguments.getString("modulePaymentsNew");
        studentName = arguments.getString("studentName");
        studentClass = arguments.getString("studentClass");
        schoolName = arguments.getString("schoolName");

        txt_stud_name.setText(studentName);
//        txt_stud_class.setText(studentClass);
        txt_stud_school.setText(schoolName);
//        txt_stud_class.setText(getResources().getString(R.string.classtext));
//        txt_stud_school.setText("");

        ArrayList<Payment> modulePaymentsNew = new ArrayList<Payment>();
        Gson gson = new Gson();
        modulePaymentsNew = gson.fromJson(paymentModulesStr, new TypeToken<List<Payment>>(){}.getType());

//        //System.out.println("modulePaymentsNew : "+modulePaymentsNew.size());

        mAdapter = new PaymentsAdapter(modulePaymentsNew,this.getActivity(),getActivity(),mFirebaseAnalytics);
        mRecyclerView.setAdapter(mAdapter);



    }
}
