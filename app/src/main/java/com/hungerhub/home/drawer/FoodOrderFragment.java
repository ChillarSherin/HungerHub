package com.hungerhub.home.drawer;

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

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import com.hungerhub.home.FoodOrderAdapter;

public class FoodOrderFragment extends Fragment {
    ArrayList<String> diaryModules= new ArrayList<>();
    String studentName,studentClass,schoolName;
    FoodOrderAdapter mAdapter;
    FirebaseAnalytics mFirebaseAnalytics;

    @BindView(R.id.recyclerView3) RecyclerView mRecyclerView;
    @BindView(R.id.textView20)
    TextView txt_stud_name;
    @BindView(R.id.textView21) TextView txt_stud_class;
    @BindView(R.id.textView22) TextView txt_stud_school;
    public FoodOrderFragment(){
        //Required empty public constructor
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.layout_diary_fragment, container, false);
        ButterKnife.bind(this,view);

        LinearLayoutManager mLayoutManager = new GridLayoutManager(this.getActivity().getBaseContext(),3);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mFirebaseAnalytics=FirebaseAnalytics.getInstance(getActivity());
        return view;
    }
    public List<String> dummyPreorderArray()
    {
        List<String> foorOrderList=new ArrayList<>();
        foorOrderList.clear();
//        foorOrderList.add("pre-order");
        foorOrderList.add("order");
        foorOrderList.add("my-order");
        return foorOrderList;
    }

    @Override
    public void onStart() {
        super.onStart();
        Bundle arguments = getArguments();
        diaryModules = arguments.getStringArrayList("modulefood");
        studentName = arguments.getString("studentName");
        studentClass = arguments.getString("studentClass");
        schoolName = arguments.getString("schoolName");

        txt_stud_name.setText(studentName);
//        txt_stud_class.setText(getActivity().getResources().getString(R.string.classtext));
        txt_stud_school.setText(schoolName);
//        txt_stud_school.setText("Chillar Payments");


//        //System.out.println("moduleDiaryNew : "+diaryModules.size());

        mAdapter = new FoodOrderAdapter(dummyPreorderArray(),this.getActivity(),this.getActivity(),mFirebaseAnalytics);
        mRecyclerView.setAdapter(mAdapter);
    }

}