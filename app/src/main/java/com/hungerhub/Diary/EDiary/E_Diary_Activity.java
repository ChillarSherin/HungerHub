package com.hungerhub.Diary.EDiary;

import android.os.Handler;
import com.google.android.material.tabs.TabLayout;
import androidx.viewpager.widget.ViewPager;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.chillarcards.cookery.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import com.hungerhub.Diary.LeaveRequest.CallBack;
import com.hungerhub.application.CustomConnectionBuddyActivity;

public class E_Diary_Activity extends CustomConnectionBuddyActivity implements CallBack {

    @BindView(R.id.EdiaryPB)
    ProgressBar progressBar;
    @BindView(R.id.HeaderTV)
    TextView HeaderText;
    @BindView(R.id.BackIV)
    ImageView BackButton;
    @BindView(R.id.EDiaryContainerVP)
    ViewPager EDiaryContainerVP;
    @BindView(R.id.EDiaryTL)
    TabLayout EDiaryTL;

    EDiaryAdapter eDiaryAdapter;
    final int[] ICONS = new int[]{
            R.drawable.ic_msg_from_teacher,
            R.drawable.ic_msg_to_teacher};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_e__diary);
        ButterKnife.bind(this);

        progressBar.setVisibility(View.GONE);

        HeaderText.setText(getResources().getString(R.string.ediary_header));
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
        eDiaryAdapter=new EDiaryAdapter(getSupportFragmentManager(),E_Diary_Activity.this,mFirebaseAnalytics);
        EDiaryContainerVP.setAdapter(eDiaryAdapter); // setting adapter to view pager

        EDiaryTL.setupWithViewPager(EDiaryContainerVP); //setting up tab with view pager
        EDiaryTL.setTabGravity(TabLayout.GRAVITY_FILL);
        EDiaryTL.getTabAt(0).setIcon(ICONS[0]);
        EDiaryTL.getTabAt(1).setIcon(ICONS[1]);

        Bundle bundle=getIntent().getExtras();
        if (bundle!=null)
        {
            String message=bundle.getString("MessageTo");
            if (message.equalsIgnoreCase("RequestFromParent"))
            {
                SelectTab();
            }
        }

    }
    public void SelectTab()
    {
        new Handler().postDelayed(
                new Runnable(){
                    @Override
                    public void run() {
                        EDiaryTL.getTabAt(1).select();
                    }
                }, 100);
    }

    @Override
    public void message(String message) {
        eDiaryAdapter=new EDiaryAdapter(getSupportFragmentManager(),E_Diary_Activity.this,mFirebaseAnalytics);
        EDiaryContainerVP.setAdapter(eDiaryAdapter); // setting adapter to view pager

        EDiaryTL.setupWithViewPager(EDiaryContainerVP); //setting up tab with view pager
        EDiaryTL.setTabGravity(TabLayout.GRAVITY_FILL);
        EDiaryTL.getTabAt(0).setIcon(ICONS[0]);
        EDiaryTL.getTabAt(1).setIcon(ICONS[1]);
        EDiaryContainerVP.setCurrentItem(1,true);


    }
}
