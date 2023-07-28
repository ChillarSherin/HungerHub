package com.hungerhub.Diary.EDiary;

import android.content.Context;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.StringRequest;
import com.chillarcards.cookery.R;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import com.hungerhub.application.CampusWallet;
import com.hungerhub.application.Constants;
import com.hungerhub.application.PrefManager;
import com.hungerhub.networkmodels.Results.Code;
import com.hungerhub.networkmodels.Results.ResultMain;
import com.hungerhub.networkmodels.Results.Status;
import com.hungerhub.utils.CommonSSLConnection;

public class FromTeacherFragment extends Fragment {

    @BindView(R.id.FromteacherRV)  RecyclerView FromteacherRV;
    @BindView(R.id.FromteacherSRL)
    SwipeRefreshLayout FromteacherSRL;
    @BindView(R.id.ProgressBar)
    ProgressBar progressBar;
    @BindView(R.id.ReloadBTN)
    Button ReloadBTN;
    @BindView(R.id.GoBackBTN)
    Button GoBackBTN;
    @BindView(R.id.NodataTV)
    TextView NodataTV;
    PrefManager prefManager;
    String phoneNum,studentId,Studentname;
    private FromteacherFragmentAdapter mAdapter;
    View view;
    @BindView(R.id.CodeErrorTV) TextView CodeErrorTV;
    @BindView(R.id.ErrorMessageTV) TextView ErrorMessageTV;
    final String tag_json_object = "r_subjects";
    FirebaseAnalytics mFirebaseAnalytics;
    public FromTeacherFragment() {
        // Required empty public constructor
    }



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Retain this fragment across configuration changes.
        setRetainInstance(true);
        if (getArguments() != null) {

        }
        prefManager=new PrefManager(getActivity());

    }

    @Override
    public void onStart() {
        super.onStart();

        FromteacherSRL.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                phpGetAttendanceMain();
                FromteacherSRL.setRefreshing(false);

            }
        });

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view=inflater.inflate(R.layout.fragment_from_teacher, container, false);
        ButterKnife.bind(this,view);
        FromteacherRV.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
        FromteacherRV.setItemAnimator(new DefaultItemAnimator());
        FromteacherRV.setNestedScrollingEnabled(false);
        view.findViewById(R.id.NodataLL).setVisibility(View.GONE);
        view.findViewById(R.id.ErrorLL).setVisibility(View.GONE);
        FromteacherSRL.setColorSchemeResources(R.color.colorAccent);
        phpGetAttendanceMain();
        mFirebaseAnalytics=FirebaseAnalytics.getInstance(getActivity());
        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

    }

    @Override
    public void onDetach() {
        super.onDetach();
    }
    public void phpGetAttendanceMain()
    {
        progressBar.setVisibility(View.VISIBLE);
        phoneNum = prefManager.getUserPhone();  //getting phone number from shared preference
        studentId = prefManager.getStudentId();
        Studentname = prefManager.getUserName();

        String URL, parameters;
        parameters = "phoneNo=" + phoneNum + "&studentID=" + studentId;
        URL = Constants.BASE_URL  + "r_subjects.php?" + parameters.replaceAll(" ", "%20");
        //System.out.println("CHECK---> URL " + URL);
        final CommonSSLConnection commonSSLConnection=new CommonSSLConnection();
        StringRequest jsonObjectRequestLogin = new StringRequest(Request.Method.GET, URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String jsonObject) {
                progressBar.setVisibility(View.GONE);
                //System.out.println("CHECK---> Response " + jsonObject);
                //findViewById(R.id.progressLayout).setVisibility(View.GONE);
                Gson gson = new Gson();

                ResultMain model = gson.fromJson(jsonObject, ResultMain.class);
                Status status = model.getStatus();
                String code = status.getCode();
                String teacherID=model.getTeacherID();
                String message=status.getMessage();
                if (code.equals("200")){

                    List<Code> subcanteen=new ArrayList<>();
                    subcanteen.clear();
                    boolean flagteacher;

//                    String  DivisionId = model.getStandardDivisionID();
                    if (teacherID.equalsIgnoreCase(""))
                    {
                        subcanteen=model.getCode();
                        flagteacher=false;
                    }
                    else
                    {
                        Code co=new Code();
                        co.setSubjectID(teacherID);   //for class teacher , teacherID set as SubjectID
                        co.setSubjectName("Class teacher");
                        subcanteen.add(co);
                        subcanteen.addAll(model.getCode());
                        flagteacher=true;
                    }


                    if (subcanteen.size()>0){

                        view.findViewById(R.id.NodataLL).setVisibility(View.GONE);
                        view.findViewById(R.id.ErrorLL).setVisibility(View.GONE);
                        FromteacherRV.setVisibility(View.VISIBLE);

                        // err.setVisibility(View.GONE);
                        mAdapter = new FromteacherFragmentAdapter(getActivity(),subcanteen,flagteacher,mFirebaseAnalytics);
                        FromteacherRV.setAdapter(mAdapter);




                    }else {
//                            progressBar.setVisibility(View.GONE);
                        FromteacherRV.setVisibility(View.GONE);


                        NodataTV.setText(getActivity().getResources().getString(R.string.no_data_found));
                        GoBackBTN.setText(getActivity().getResources().getString(R.string.go_back));
//                        ErrorImage.setBackgroundResource(R.drawable.nodata);
                        view.findViewById(R.id.NodataLL).setVisibility(View.VISIBLE);
                        GoBackBTN.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {


                            getActivity().onBackPressed();

                        }
                    });
                    }


                }
                else if(code.equals("400")){

                    FromteacherRV.setVisibility(View.GONE);
                    ReloadBTN.setText(getActivity().getResources().getString(R.string.go_back));
                    view.findViewById(R.id.ErrorLL).setVisibility(View.VISIBLE);
                    ErrorMessageTV.setText(getActivity().getResources().getString(R.string.error_message_errorlayout));
                    CodeErrorTV.setText(getActivity().getResources().getString(R.string.code_attendance)+code);
                    ReloadBTN.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {


                            getActivity().onBackPressed();

                        }
                    });
                }else if (code.equals("204")){
                    FromteacherRV.setVisibility(View.GONE);
                    NodataTV.setText(getActivity().getResources().getString(R.string.no_data_found));
                    GoBackBTN.setText(getActivity().getResources().getString(R.string.go_back));
//                        ErrorImage.setBackgroundResource(R.drawable.nodata);
                    view.findViewById(R.id.NodataLL).setVisibility(View.VISIBLE);
                    GoBackBTN.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {


                            getActivity().onBackPressed();

                        }
                    });

                }else if (code.equals("401")){
                    FromteacherRV.setVisibility(View.GONE);
                    ReloadBTN.setText(getActivity().getResources().getString(R.string.go_back));
                    view.findViewById(R.id.ErrorLL).setVisibility(View.VISIBLE);
                    ErrorMessageTV.setText(getActivity().getResources().getString(R.string.error_message_errorlayout));
                    CodeErrorTV.setText(getActivity().getResources().getString(R.string.code_attendance)+code);
                    ReloadBTN.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {


                            getActivity().onBackPressed();

                        }
                    });

                }else if (code.equals("500")){
                    FromteacherRV.setVisibility(View.GONE);
                    ReloadBTN.setText(getActivity().getResources().getString(R.string.go_back));
                    view.findViewById(R.id.ErrorLL).setVisibility(View.VISIBLE);
                    ErrorMessageTV.setText(getActivity().getResources().getString(R.string.error_message_errorlayout));
                    CodeErrorTV.setText(getActivity().getResources().getString(R.string.code_attendance)+code);
                    ReloadBTN.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {


                            getActivity().onBackPressed();

                        }
                    });
                }


            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                try{

                progressBar.setVisibility(View.GONE);
                VolleyLog.d("Object Error : ", volleyError.getMessage());
                FromteacherRV.setVisibility(View.GONE);
//                Toast.makeText(getActivity(), getResources().getString(R.string.network_error_try), Toast.LENGTH_SHORT).show();
                ReloadBTN.setText(getActivity().getResources().getString(R.string.go_back));
                view.findViewById(R.id.ErrorLL).setVisibility(View.VISIBLE);
                ErrorMessageTV.setText(getActivity().getResources().getString(R.string.error_message_admin));
                CodeErrorTV.setText(getActivity().getResources().getString(R.string.error_message_errorlayout));
                ReloadBTN.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        getActivity().onBackPressed();
                    }
                });
                }catch (Exception e)
                {

                }
            }
        });
        jsonObjectRequestLogin.setRetryPolicy(new DefaultRetryPolicy(50000, 3, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

//        RequestQueue requestQueue = Volley.newRequestQueue(getActivity(), commonSSLConnection.getHulkstack(getActivity()));
        CampusWallet.getInstance().addToRequestQueue(jsonObjectRequestLogin, tag_json_object);
//        requestQueue.add(jsonObjectRequestLogin);
    }
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        CampusWallet.getInstance().getRequestQueue().cancelAll(tag_json_object);
    }
}
