package com.hungerhub.NotificationCenter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.chillarcards.cookery.R;
import com.google.firebase.analytics.FirebaseAnalytics;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import com.hungerhub.BusTracking.ParentMapsActivity;
import com.hungerhub.Diary.Calendar.CalendarNew;
import com.hungerhub.Diary.EDiary.E_Diary_Activity;
import com.hungerhub.Diary.EDiary.TeachermessageDetailsActivity;
import com.hungerhub.Diary.Gallary.GalleryActivity;
import com.hungerhub.Diary.LeaveRequest.LeaveRequestActivity;
import com.hungerhub.Diary.NoticeBoard.NoticeBoardActivity;
import com.hungerhub.Payment.History.PaymentHistoryActivity;
import com.hungerhub.application.CampusWallet;
import com.hungerhub.application.Constants;
import com.hungerhub.application.PrefManager;
import com.hungerhub.home.HomeActivity;
import com.hungerhub.payments.cardtransaction.CardTransactionActivity;
import com.hungerhub.studentselection.StudentListActivity;

/**
 * Created by user on 12/2/2017.
 */

public class Notifications_list_adapterNW extends RecyclerView.Adapter<Notifications_list_adapterNW.ViewHolder> {
    
    List<Contact> contacts;
    private final int rowLayout;
    private final Context mContext;
    String SchoolID,SchoolName, StandardDivId,StandardName,StudID,SchoolidKey, StdDivIdKey,TrnsTypName,TransTypId,StudIDKEY;
    String PhoneNo, studentDiv,studentName, studentSchool,studentStandard,StudentCode,type;
    DatabaseHandler db;
    PrefManager prefManager;
    FirebaseAnalytics mFirebaseAnalytics;
    public Notifications_list_adapterNW(List<Contact> contacts, Activity activity, int rowLayout, Context context,FirebaseAnalytics mFirebaseAnalytics) {
        this.contacts=contacts;
        Activity activity1 = activity;
        this.rowLayout = R.layout.notificationitem_layout;
        this.mContext = context;
        this.mFirebaseAnalytics = mFirebaseAnalytics;
       db = new DatabaseHandler(activity1);
       prefManager=new PrefManager(context);
    }
    @Override
    public Notifications_list_adapterNW.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        View v = LayoutInflater.from(parent.getContext()).inflate(rowLayout, parent, false);
        return new Notifications_list_adapterNW.ViewHolder(v);
    }
    @Override
    public void onBindViewHolder(final Notifications_list_adapterNW.ViewHolder holder, final int position) {

        SchoolID = prefManager.getSChoolID();
        StandardDivId = prefManager.getStudentStandardDivisionID();
        StandardName = prefManager.getStudentStandard();
        StudID = prefManager.getStudentId();
        SchoolName = prefManager.getSChoolName();
        String JSDataString="",JSadditionalData="",JSbody="",JSnotificationID="",
        JSpayment="",JSTitle="",JSpushTypeLists = "",JSschoolID="",JSstudentID="";
        JSONObject json=null,jsonobjct = null;
        try {
            JSDataString=contacts.get(position).getDatas();
            json = new JSONObject(JSDataString);
            JSadditionalData = json.optString("additionalData");
            jsonobjct=new JSONObject(JSadditionalData);
            JSbody= json.getString("body");
            JSnotificationID=json.getString("notificationID");
            JSTitle= json.getString("title");
            JSpushTypeLists=jsonobjct.getString("pushType");
            JSpayment=jsonobjct.getString("payment");

            JSschoolID=jsonobjct.getString("schoolID");
            JSstudentID=jsonobjct.getString("studentID");

        } catch (JSONException e) {
            e.printStackTrace();
        }
        
            if (contacts.get(position).getClicked().equals("0")){
                holder.Groupnamecardview.setBackgroundResource(R.color.white);
                holder.Content.setTextColor(Color.parseColor("#1CA1DC"));
                holder.Content.setTypeface(null, Typeface.BOLD);
            }else {
                holder.Groupnamecardview.setBackgroundResource(R.color.white);
                holder.Content.setTextColor(Color.BLACK);
                holder.Content.setTypeface(null, Typeface.NORMAL);
                //System.out.println("SHANIL CHILLAR1234567  : ");
            }

        if (JSpushTypeLists.equals("home_status")){
            holder.iconimage.setImageDrawable(mContext.getResources().getDrawable(R.drawable.noticication_homestatus));
            holder.Content.setText(mContext.getResources().getString(R.string.general_notification));
            holder.MessageBody.setText(" "+JSbody);
        }else if (JSpushTypeLists.equals("notice board")){
            holder.iconimage.setImageDrawable(mContext.getResources().getDrawable(R.drawable.noticication_noticeboard));
            holder.Content.setText(mContext.getResources().getString(R.string.notice_board_title));
            holder.MessageBody.setText(" "+JSbody);
        }else if (JSpushTypeLists.equals("notice board std")){
            holder.iconimage.setImageDrawable(mContext.getResources().getDrawable(R.drawable.noticication_noticeboard));
            holder.Content.setText(mContext.getResources().getString(R.string.notice_board_title));
            holder.MessageBody.setText(" "+JSbody);
        }else if (JSpushTypeLists.equals("calender")){
            holder.iconimage.setImageDrawable(mContext.getResources().getDrawable(R.drawable.noticication_calendar));
            holder.Content.setText(mContext.getResources().getString(R.string.calendar));
            holder.MessageBody.setText(" "+JSbody);
        }else if (JSpushTypeLists.equals("calender std")){
            holder.iconimage.setImageDrawable(mContext.getResources().getDrawable(R.drawable.noticication_calendar));
            holder.Content.setText(mContext.getResources().getString(R.string.calendar));
            holder.MessageBody.setText(" "+JSbody);
        }else if (JSpushTypeLists.equals("Leave request Approved")){
            holder.Content.setText(mContext.getResources().getString(R.string.approved_leave_req));
            holder.MessageBody.setText(" "+JSbody);
            holder.iconimage.setImageDrawable(mContext.getResources().getDrawable(R.drawable.noticication_leave_requestapproved));
        }else if (JSpushTypeLists.equals("Inbox message")){
            holder.Content.setText(mContext.getResources().getString(R.string.inbox_message));
            holder.MessageBody.setText(" "+JSbody);
            holder.iconimage.setImageDrawable(mContext.getResources().getDrawable(R.drawable.noticication_inboxmessage));
        }else if (JSpushTypeLists.equals("Messages")){
            holder.Content.setText(mContext.getResources().getString(R.string.message_));
            holder.MessageBody.setText(" "+JSbody);
            holder.iconimage.setImageDrawable(mContext.getResources().getDrawable(R.drawable.noticication_message));
        }else if (JSpushTypeLists.equals("Transaction")){
            holder.Content.setText(mContext.getResources().getString(R.string.transactions_notification));
            holder.MessageBody.setText(" "+JSbody);
            holder.iconimage.setImageDrawable(mContext.getResources().getDrawable(R.drawable.noticication_transaction));
        }else if (JSpushTypeLists.equals("adds")){
            holder.Content.setText(mContext.getResources().getString(R.string.adds_notification));
            holder.MessageBody.setText(" "+JSbody);
            holder.iconimage.setImageDrawable(mContext.getResources().getDrawable(R.drawable.noticication_add));
        }else if (JSpushTypeLists.equals("success_recharge")){
            holder.Content.setText(mContext.getResources().getString(R.string.recharge_success));
            holder.MessageBody.setText(" "+JSbody);
            holder.iconimage.setImageDrawable(mContext.getResources().getDrawable(R.drawable.noticication_transaction));
        }else if (JSpushTypeLists.equals("Messages all")){
            holder.Content.setText(mContext.getResources().getString(R.string.message_));
            holder.MessageBody.setText(" "+JSbody);
            holder.iconimage.setImageDrawable(mContext.getResources().getDrawable(R.drawable.noticication_message));
        }else if (JSpushTypeLists.equals("Bus Tracking")){
            holder.Content.setText(mContext.getResources().getString(R.string.home_bustrack));
            holder.MessageBody.setText(" "+JSbody);
            holder.iconimage.setImageDrawable(mContext.getResources().getDrawable(R.drawable.bus_tracking_icon));
        }
        else if (JSpushTypeLists.equals("Activity")){
            holder.Content.setText(mContext.getResources().getString(R.string.gallery_notification));
            holder.MessageBody.setText(" "+JSbody);
            holder.iconimage.setImageDrawable(mContext.getResources().getDrawable(R.drawable.ic_highlights));
        }
        else if (JSpushTypeLists.equals("School Bus Notification")){
            holder.Content.setText(mContext.getResources().getString(R.string.home_bustrack));
            holder.MessageBody.setText(" "+JSbody);
            holder.iconimage.setImageDrawable(mContext.getResources().getDrawable(R.drawable.bus_tracking_icon));
        }
        else
        {
            holder.Content.setText(mContext.getResources().getString(R.string.notification_noti));
            holder.MessageBody.setText(" "+JSbody);
            holder.iconimage.setImageDrawable(mContext.getResources().getDrawable(R.drawable.ic_notification_grey));
        }
        final String finalJSTitle = JSTitle;
        final String finalJSnotificationID = JSnotificationID;
        final JSONObject finalJsonobjct = jsonobjct;
        final String finalJSpayment = JSpayment;
        final String finalJSpushTypeLists = JSpushTypeLists;
        final String finalJSbody = JSbody;
        holder.Groupnamecardview.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    mFirebaseAnalytics.logEvent(mContext.getResources().getString(R.string.Notification_Item_Clicked),new Bundle());
                    db.UpdateTable(finalJSnotificationID);
                    try {
                        if (finalJsonobjct != null) {
                            //System.out.println("SHANIL 3: ");
                            //System.out.println("SHANIL PUSH 1 : " + finalJSpushTypeLists + " "+ finalJSpayment);
                            type = finalJsonobjct.optString("type");
                            if (type.equals("school")) {
                                studentStandard = finalJsonobjct.optString("standardName");
                                studentDiv = finalJsonobjct.optString("standardDivisionName");
                                SchoolidKey = finalJsonobjct.optString("schoolID");
                            } else if (type.equals("college")) {
                                studentStandard = finalJsonobjct.optString("courseName");
                                studentDiv = finalJsonobjct.optString("semesterName");
                            }
                            //System.out.println("schoolid : stdID :  " + SchoolidKey + StdDivIdKey);
                            //System.out.println("schoolidold : stdID :  " + SchoolID + StandardDivId);
                            //System.out.println("datakey : " + finalJSpushTypeLists);
                            //System.out.println("PhoneNo : " + PhoneNo);
                            //System.out.println("studentDiv : " + studentDiv);
                            //System.out.println("studentName : " + studentName);
                            //System.out.println("studentSchool : " + studentSchool);
                            //System.out.println("studentStandard : " + studentStandard);
                            //System.out.println("StudentCode : " + StudentCode);
                            //System.out.println("type : " + type);
                            //System.out.println("StudIDKEY : " + StudIDKEY);
                            //System.out.println("keyvalue : " + finalJSpushTypeLists);
                            if (finalJSpushTypeLists.equalsIgnoreCase("home_status")) {
                                SchoolidKey = finalJsonobjct.optString("schoolID");
                                 PhoneNo = finalJsonobjct.optString("phoneNo");
                                studentName = finalJsonobjct.optString("studentName");
                                studentSchool = finalJsonobjct.optString("schoolName");
                                StudentCode = finalJsonobjct.optString("studentCode");
                                type = finalJsonobjct.optString("type");
                                StudIDKEY = finalJsonobjct.optString("studentID");
                                try {
                                    StdDivIdKey = finalJsonobjct.getString("standardDivisionID");
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                                //System.out.println("SHANIL 4: ");
                                //System.out.println("home_menu : stdIDnew_home :  " + SchoolidKey + StdDivIdKey);
                                //System.out.println("homeMessage:  " + SchoolidKey + StdDivIdKey);
                                //System.out.println("SHANIL 5: ");
                                //System.out.println("home1");
                                //System.out.println("background");
                                Intent mIntent = new Intent(CampusWallet.getContext(), HomeActivity.class);
                                //System.out.println("SHANIL 6: ");
                                Constants.HomePopup = finalJSbody;
                                Constants.HomePopupTitle = finalJSTitle;
                                Constants.HomeFlag = "true";
                                Bundle b = new Bundle();
                                //System.out.println("SHANIL 6: CONTENT " + Constants.HomePopup);
                                //System.out.println("SHANIL 6: TITLE " + Constants.HomePopupTitle);
                                //System.out.println("SHANIL 6: FLAG " + Constants.HomeFlag);
                                b.putString("CONTENT", Constants.HomePopup);
                                b.putString("TITLE", Constants.HomePopupTitle);
                                mIntent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT | Intent.FLAG_ACTIVITY_NEW_TASK);
                                mIntent.putExtras(b);
                                CampusWallet.getContext().startActivity(mIntent);
                            } else if (finalJSpushTypeLists.equalsIgnoreCase("notice board")) {
                                //System.out.println("SHANIL 9: ");
                                SchoolidKey = finalJsonobjct.optString("schoolID");
                                studentSchool = finalJsonobjct.optString("schoolName");
                                type = finalJsonobjct.optString("type");
                                if (SchoolidKey.equals(SchoolID) && (studentStandard.equalsIgnoreCase(StandardName))) {
                                    //System.out.println("SHANIL 10: ");
                                    Intent intent = new Intent(CampusWallet.getContext(), NoticeBoardActivity.class);
                                    intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT | Intent.FLAG_ACTIVITY_NEW_TASK);
                                    CampusWallet.getContext().startActivity(intent);
                                }
                                else {
                                    Intent intent = new Intent(CampusWallet.getContext(), StudentListActivity.class);
                                    intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT | Intent.FLAG_ACTIVITY_NEW_TASK);
                                    CampusWallet.getContext().startActivity(intent);
                                }
                            } else if (finalJSpushTypeLists.equalsIgnoreCase("notice board std")) {
                                //System.out.println("SHANIL 9: ");
                                SchoolidKey = finalJsonobjct.optString("schoolID");
                                studentSchool = finalJsonobjct.optString("schoolName");
                                type = finalJsonobjct.optString("type");
                                //System.out.println("SHANIL NEW : "+SchoolidKey+"   "+SchoolID+"  "+studentStandard+"  "+StandardName);
                                if (SchoolidKey.equals(SchoolID) && (studentStandard.equals(StandardName))) {
                                    //System.out.println("SHANIL 10: ");
                                    Intent intent = new Intent(CampusWallet.getContext(), NoticeBoardActivity.class);
                                    intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT | Intent.FLAG_ACTIVITY_NEW_TASK);
                                    CampusWallet.getContext().startActivity(intent);
                                }
                                else {
                                    Intent intent = new Intent(CampusWallet.getContext(), StudentListActivity.class);
                                    intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT | Intent.FLAG_ACTIVITY_NEW_TASK);
                                    CampusWallet.getContext().startActivity(intent);
                                }
                            }
                            else if (finalJSpushTypeLists.equalsIgnoreCase("calender")) {
                                //System.out.println("SHANIL 11: ");
                                SchoolidKey = finalJsonobjct.optString("schoolID");
                                studentSchool = finalJsonobjct.optString("schoolName");
                                type = finalJsonobjct.optString("type");
                                //System.out.println("schoolidnew : stdIDnew :  " + SchoolidKey + StdDivIdKey);
                                if (SchoolidKey.equals(SchoolID) && (studentStandard.equals(StandardName))) {
                                    //System.out.println("SHANIL 12: ");
                                    Intent intent = new Intent(CampusWallet.getContext(), CalendarNew.class);
                                    intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT | Intent.FLAG_ACTIVITY_NEW_TASK);
                                    CampusWallet.getContext().startActivity(intent);
                                } else {
                                    Intent intent = new Intent(CampusWallet.getContext(), StudentListActivity.class);
                                    intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT | Intent.FLAG_ACTIVITY_NEW_TASK);
                                    CampusWallet.getContext().startActivity(intent);
                                }
                            }
                            else if (finalJSpushTypeLists.equalsIgnoreCase("School Bus Notification")) {
                                //System.out.println("SHANIL 11: BusSystem");
                                SchoolidKey = finalJsonobjct.optString("schoolID");
                                if (SchoolidKey.equals(SchoolID))
                                {
                                    Intent intent = new Intent(CampusWallet.getContext(), ParentMapsActivity.class);
                                    intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT | Intent.FLAG_ACTIVITY_NEW_TASK);
                                    CampusWallet.getContext().startActivity(intent);
                                }
                                else
                                {
                                    Intent intent = new Intent(CampusWallet.getContext(), StudentListActivity.class);
                                    intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT | Intent.FLAG_ACTIVITY_NEW_TASK);
                                    CampusWallet.getContext().startActivity(intent);
                                }

                            }
                            else if (finalJSpushTypeLists.equalsIgnoreCase("calender std")) {
                                //System.out.println("SHANIL 11: ");
                                SchoolidKey = finalJsonobjct.optString("schoolID");
                                studentSchool = finalJsonobjct.optString("schoolName");
                                type = finalJsonobjct.optString("type");
                                //System.out.println("schoolidnew : stdIDnew :  " + SchoolidKey + StdDivIdKey);
                                if (SchoolidKey.equals(SchoolID) && (studentStandard.equals(StandardName))) {
                                    //System.out.println("SHANIL 12: ");
                                    Intent intent = new Intent(CampusWallet.getContext(), CalendarNew.class);
                                    intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT | Intent.FLAG_ACTIVITY_NEW_TASK);
                                    CampusWallet.getContext().startActivity(intent);
                                } else {
                                    Intent intent = new Intent(CampusWallet.getContext(), StudentListActivity.class);
                                    intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT | Intent.FLAG_ACTIVITY_NEW_TASK);
                                    CampusWallet.getContext().startActivity(intent);
                                }
                            }
                            else if (finalJSpushTypeLists.equalsIgnoreCase("Activity")) {
                                //System.out.println("Abhinand 11: ");
                                SchoolidKey = finalJsonobjct.optString("schoolID");
                                studentSchool = finalJsonobjct.optString("schoolName");
                                StudIDKEY = finalJsonobjct.optString("studentID");
                                type = finalJsonobjct.optString("type");
                                //System.out.println("schoolidnew : stdIDnew :  " + SchoolidKey + StdDivIdKey);
                                if (SchoolidKey.equals(SchoolID) && (studentStandard.equals(StandardName))&&
                                        (StudIDKEY.equals(StudID))) {
                                    //System.out.println("Abhinand 12: ");
                                    Intent intent = new Intent(CampusWallet.getContext(), GalleryActivity.class);
                                    intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT | Intent.FLAG_ACTIVITY_NEW_TASK);
                                    CampusWallet.getContext().startActivity(intent);

                                } else {
                                    Intent intent = new Intent(CampusWallet.getContext(), StudentListActivity.class);
                                    intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT | Intent.FLAG_ACTIVITY_NEW_TASK);
                                    CampusWallet.getContext().startActivity(intent);
                                }
                            }
                            else if (finalJSpushTypeLists.equalsIgnoreCase("Leave request Approved")) {
                                //System.out.println("SHANIL 11: ");
                                SchoolidKey = finalJsonobjct.optString("schoolID");
                                PhoneNo = finalJsonobjct.optString("phoneNo");
                                studentName = finalJsonobjct.optString("studentName");
                                studentSchool = finalJsonobjct.optString("schoolName");
                                StudentCode = finalJsonobjct.optString("studentCode");
                                type = finalJsonobjct.optString("type");
                                StudIDKEY = finalJsonobjct.optString("studentID");
                                StdDivIdKey = finalJsonobjct.getString("standardDivisionID");
                                //System.out.println("schoolidnew : stdIDnew :  " + SchoolidKey + StdDivIdKey);
                                if (SchoolID.equals(SchoolidKey) && (StandardDivId.equals(StdDivIdKey))) {
                                    //System.out.println("SHANIL xyz: ");
                                    Intent intent = new Intent(CampusWallet.getContext(), LeaveRequestActivity.class);
                                    intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT | Intent.FLAG_ACTIVITY_NEW_TASK);
                                    CampusWallet.getContext().startActivity(intent);

                                } else {
                                    //System.out.println("SHANIL zyx: ");
                                    Intent intent = new Intent(CampusWallet.getContext(), StudentListActivity.class);
                                    intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT | Intent.FLAG_ACTIVITY_NEW_TASK);
                                    CampusWallet.getContext().startActivity(intent);
                                }
                            }  else if (finalJSpushTypeLists.equalsIgnoreCase("Bus Tracking")) {
                                //System.out.println("SHANIL 9: ");
                                SchoolidKey = finalJsonobjct.optString("schoolID");
//                                    PhoneNo = data.optString("phoneNo");
//                                    studentName = data.optString("studentName");
                                studentSchool = finalJsonobjct.optString("schoolName");
//                                    StudentCode = data.optString("studentCode");
                                type = finalJsonobjct.optString("type");
//                                    StudIDKEY = data.optString("studentID");
//                                    StdDivIdKey = data.getString("standardDivisionID");
//                                    StdDivIdKey = data.getString("standardDivisionID");
                                if (SchoolidKey.equals(SchoolID) ) {
                                    //System.out.println("SHANIL 10: ");
                                    Intent intent = new Intent(CampusWallet.getContext(), ParentMapsActivity.class);
                                    intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT | Intent.FLAG_ACTIVITY_NEW_TASK);
                                    CampusWallet.getContext().startActivity(intent);
                                }
                                else {
                                    Intent intent = new Intent(CampusWallet.getContext(), StudentListActivity.class);
                                    intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT | Intent.FLAG_ACTIVITY_NEW_TASK);
                                    CampusWallet.getContext().startActivity(intent);
                                }}
                                else if (finalJSpushTypeLists.equalsIgnoreCase("Inbox message")) {
                                SchoolidKey = finalJsonobjct.optString("schoolID");
                                PhoneNo = finalJsonobjct.optString("phoneNo");
                                studentName = finalJsonobjct.optString("studentName");
                                studentSchool = finalJsonobjct.optString("schoolName");
                                StudentCode = finalJsonobjct.optString("studentCode");
                                type = finalJsonobjct.optString("type");
                                StudIDKEY = finalJsonobjct.optString("studentID");
                                StdDivIdKey = finalJsonobjct.getString("standardDivisionID");
                                //System.out.println("SHANIL 11: ");
                                String msg_type = " ";
                                msg_type = finalJsonobjct.getString("messageTypeID");
                                //System.out.println("schoolidnew : stdIDnew :  " + SchoolidKey + StdDivIdKey);
                                if (SchoolID.equals(SchoolidKey) && (StandardDivId.equals(StdDivIdKey))) {
                                    //System.out.println("SHANIL xyz1: ");
                                    Intent intent = new Intent(CampusWallet.getContext(), E_Diary_Activity.class);
                                    intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT | Intent.FLAG_ACTIVITY_NEW_TASK);
                                    Bundle bundle = new Bundle();
                                    bundle.putString("MessageTo", "RequestFromParent");
                                    intent.putExtras(bundle);
                                    CampusWallet.getContext().startActivity(intent);
                                } else {
                                    //System.out.println("SHANIL zyx2: ");
                                    Intent intent = new Intent(CampusWallet.getContext(), StudentListActivity.class);
                                    intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT | Intent.FLAG_ACTIVITY_NEW_TASK);
                                    CampusWallet.getContext().startActivity(intent);
                                }
                            } else if (finalJSpushTypeLists.equalsIgnoreCase("Messages")) {
                                //System.out.println("SHANIL 11: ");
                                SchoolidKey = finalJsonobjct.optString("schoolID");
                                PhoneNo = finalJsonobjct.optString("phoneNo");
                                studentName = finalJsonobjct.optString("studentName");
                                studentSchool = finalJsonobjct.optString("schoolName");
                                StudentCode = finalJsonobjct.optString("studentCode");
                                type = finalJsonobjct.optString("type");
                                StudIDKEY = finalJsonobjct.optString("studentID");
                                StdDivIdKey = finalJsonobjct.getString("standardDivisionID");
                                String SubjectID = finalJsonobjct.optString("subjectID");
                                String SubjectName = finalJsonobjct.optString("subjectName");

                                System.out.println("ABHINAND  :: SUBJECT ID: "+SubjectID);
                                System.out.println("ABHINAND  :: SUBJECT NAME: "+SubjectName);
                                //System.out.println("SubjectId " + SubjectID);
                                //System.out.println("StandardDivId1111 " + StandardDivId);
                                //System.out.println("SchoolID111 " + SchoolID);
                                //System.out.println("schoolidnew : stdIDnew :  " + SchoolidKey + StdDivIdKey);
                                if (SchoolID.equals(SchoolidKey) && (StandardDivId.equals(StdDivIdKey))) {
                                    //System.out.println("SHANIL 12testing: ");
                                    if (SubjectID.equals("0")) {
                                        Intent mIntent = new Intent(CampusWallet.getContext(), TeachermessageDetailsActivity.class);
                                        Bundle b = new Bundle();
                                        //System.out.println("SubjectId " + SubjectID);
                                        b.putString("SubjectId", SubjectID);
                                        b.putString("SubjectName", SubjectName);
                                        b.putBoolean("Flagteacher",true);
                                        mIntent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT | Intent.FLAG_ACTIVITY_NEW_TASK);
                                        mIntent.putExtras(b);
                                        CampusWallet.getContext().startActivity(mIntent);
                                    } else {
                                        Intent mIntent = new Intent(CampusWallet.getContext(), TeachermessageDetailsActivity.class);
                                        Bundle b = new Bundle();
                                        //System.out.println("SubjectId " + SubjectID);
                                        b.putString("SubjectId", SubjectID);
                                        b.putString("SubjectName", SubjectName);
                                        b.putBoolean("Flagteacher",false);
                                        mIntent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT | Intent.FLAG_ACTIVITY_NEW_TASK);
                                        mIntent.putExtras(b);
                                        CampusWallet.getContext().startActivity(mIntent);
                                    }
                                } else {
                                    //System.out.println("SHANIL 13: ");
                                    Intent intent = new Intent(CampusWallet.getContext(), StudentListActivity.class);
                                    intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT | Intent.FLAG_ACTIVITY_NEW_TASK);
                                    CampusWallet.getContext().startActivity(intent);
                                }
                            }
                            else if (finalJSpushTypeLists.equalsIgnoreCase("Messages all")) {
                                //System.out.println("SHANIL 11: ");
                                SchoolidKey = finalJsonobjct.optString("schoolID");
                                PhoneNo = finalJsonobjct.optString("phoneNo");
                                studentName = finalJsonobjct.optString("studentName");
                                studentSchool = finalJsonobjct.optString("schoolName");
                                StudentCode = finalJsonobjct.optString("studentCode");
                                type = finalJsonobjct.optString("type");
                                StudIDKEY = finalJsonobjct.optString("studentID");
                                try {
                                    StdDivIdKey = finalJsonobjct.getString("standardDivisionID");
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                                String SubjectID = finalJsonobjct.optString("subjectID");
                                String SubjectName = finalJsonobjct.optString("subjectName");
                                //System.out.println("SubjectId " + SubjectID);
                                //System.out.println("schoolidnew : stdIDnew :  " + SchoolidKey + StdDivIdKey);
                                if (SchoolID.equals(SchoolidKey) && (StandardDivId.equals(StdDivIdKey))) {
                                    //System.out.println("SHANIL 12: ");
                                    if (SubjectID.equals("0")) {
                                        Intent mIntent = new Intent(CampusWallet.getContext(), TeachermessageDetailsActivity.class);
                                        Bundle b = new Bundle();
                                        //System.out.println("SubjectId " + SubjectID);
                                        b.putString("SubjectId", SubjectID);
                                        b.putString("SubjectName", SubjectName);
                                        b.putBoolean("Flagteacher",true);
                                        mIntent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT | Intent.FLAG_ACTIVITY_NEW_TASK);
                                        mIntent.putExtras(b);
                                        CampusWallet.getContext().startActivity(mIntent);
                                    } else {
                                        Intent mIntent = new Intent(CampusWallet.getContext(), TeachermessageDetailsActivity.class);
                                        Bundle b = new Bundle();
                                        //System.out.println("SubjectId " + SubjectID);
                                        b.putString("SubjectId", SubjectID);
                                        b.putString("SubjectName", SubjectName);
                                        b.putBoolean("Flagteacher",false);
                                        mIntent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT | Intent.FLAG_ACTIVITY_NEW_TASK);
                                        mIntent.putExtras(b);
                                        CampusWallet.getContext().startActivity(mIntent);
                                    }

                                }
                                else {
                                    Intent intent = new Intent(CampusWallet.getContext(), StudentListActivity.class);
                                    intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT | Intent.FLAG_ACTIVITY_NEW_TASK);
                                    CampusWallet.getContext().startActivity(intent);
                                }
                            }
                            else if (finalJSpushTypeLists.equalsIgnoreCase("Transaction")) {
                                //System.out.println("SHANIL 14: ");
                                SchoolidKey = finalJsonobjct.optString("schoolID");
                                PhoneNo = finalJsonobjct.optString("phoneNo");
                                studentName = finalJsonobjct.optString("studentName");
                                studentSchool = finalJsonobjct.optString("schoolName");
                                StudentCode = finalJsonobjct.optString("studentCode");
                                type = finalJsonobjct.optString("type");
                                StudIDKEY = finalJsonobjct.optString("studentID");
                                StdDivIdKey = finalJsonobjct.getString("standardDivisionID");
                                TransTypId = finalJsonobjct.optString("transactionTypeID");
                                TrnsTypName = finalJsonobjct.optString("transactionTypeName");
                                StudIDKEY = finalJsonobjct.optString("studentID");
                                //System.out.println("Canteentrnsactionss : " + finalJSpushTypeLists);
                                //System.out.println("Stud Details  : " + StudID + "  " + StudIDKEY);
                                if (SchoolID.equals(SchoolidKey) && (StandardDivId.equals(StdDivIdKey)) && (StudID.equals(StudIDKEY))) {
                                    //System.out.println("SHANIL 15: ");
                                    Intent mIntent = new Intent(CampusWallet.getContext(), CardTransactionActivity.class);
                                    Bundle b = new Bundle();
                                    //System.out.println("transactionTypeID " + TransTypId);
                                    b.putString("transactionTypeID", TransTypId);
                                    mIntent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT | Intent.FLAG_ACTIVITY_NEW_TASK);
                                    mIntent.putExtras(b);
                                    CampusWallet.getContext().startActivity(mIntent);
                                } else {
                                    //System.out.println("SHANIL 22: ");
                                    Intent intent = new Intent(CampusWallet.getContext(), StudentListActivity.class);
                                    intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT | Intent.FLAG_ACTIVITY_NEW_TASK);
                                    CampusWallet.getContext().startActivity(intent);
                                }

                            }
                            else if (finalJSpushTypeLists.equalsIgnoreCase("adds"))
                            {
                                SchoolidKey = finalJsonobjct.optString("schoolID");
                                PhoneNo = finalJsonobjct.optString("phoneNo");
                                studentName = finalJsonobjct.optString("studentName");
                                studentSchool = finalJsonobjct.optString("schoolName");
                                StudentCode = finalJsonobjct.optString("studentCode");
                                type = finalJsonobjct.optString("type");
                                StudIDKEY = finalJsonobjct.optString("studentID");
                                StdDivIdKey = finalJsonobjct.getString("standardDivisionID");
                                //System.out.println("SHANIL 25: ");
                                //System.out.println("home_menu : stdIDnew_home :  " + SchoolidKey + StdDivIdKey);
                                //System.out.println("homeMessage:  " + SchoolidKey + StdDivIdKey);
                                //System.out.println("SHANIL 26: ");
                                //System.out.println("home1");
                                String weburl= finalJsonobjct.getString("weburl");
                                String imageurl= finalJsonobjct.getString("imageurl");
                                String chillarAddsID= finalJsonobjct.getString("chillarAddsID");
                                //System.out.println("background");
                                Intent mIntent = new Intent(CampusWallet.getContext(), HomeActivity.class);
                                //System.out.println("SHANIL 27: ");
                                Constants.HomePopup = finalJSbody;
                                Constants.HomePopupTitle = finalJSTitle;
                                Constants.HomeFlag = "true1";
                                Constants.onesignal_weburl=weburl;
                                Constants.onesignal_imageurl=imageurl;
                                Constants.onesignal_chillarAddsID=chillarAddsID;
                                Bundle b = new Bundle();
                                //System.out.println("SHANIL 27: CONTENT " + Constants.HomePopup);
                                //System.out.println("SHANIL 27: TITLE " + Constants.HomePopupTitle);
                                //System.out.println("SHANIL 27: FLAG " + Constants.HomeFlag);
                                b.putString("CONTENT", Constants.HomePopup);
                                b.putString("TITLE", Constants.HomePopupTitle);
                                b.putString("IMGURL", Constants.onesignal_weburl);
                                b.putString("WEBURL", Constants.onesignal_imageurl);
                                b.putString("chillarAddsID", Constants.onesignal_chillarAddsID);
                                mIntent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT | Intent.FLAG_ACTIVITY_NEW_TASK);
                                mIntent.putExtras(b);
                                CampusWallet.getContext().startActivity(mIntent);
                            }
                            else if (finalJSpushTypeLists.equalsIgnoreCase("success_recharge")) {
                                //System.out.println("SHANIL 29: ");
                                String trn_type = " ";
                                trn_type = finalJsonobjct.getString("transaction_type");
                                SchoolidKey = finalJsonobjct.optString("schoolID");
                                PhoneNo = finalJsonobjct.optString("phoneNo");
                                studentName = finalJsonobjct.optString("studentName");
                                studentSchool = finalJsonobjct.optString("schoolName");
                                StudentCode = finalJsonobjct.optString("studentCode");
                                type = finalJsonobjct.optString("type");
                                StudIDKEY = finalJsonobjct.optString("studentID");
                                StdDivIdKey = finalJsonobjct.getString("standardDivisionID");
                                //System.out.println("schoolidnew : stdIDnew :  " + SchoolidKey + StdDivIdKey);
                                if (SchoolID.equals(SchoolidKey) && (StandardDivId.equals(StdDivIdKey))) {
                                    //System.out.println("SHANIL xyz1: ");
                                    Intent intent = new Intent(CampusWallet.getContext(), PaymentHistoryActivity.class);
                                    intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT | Intent.FLAG_ACTIVITY_NEW_TASK);
                                    Bundle bundle=new Bundle();
                                    bundle.putString("pos","1");
                                    intent.putExtras(bundle);
                                    CampusWallet.getContext().startActivity(intent);

                                } else {Intent intent = new Intent(CampusWallet.getContext(), StudentListActivity.class);
                                    intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT | Intent.FLAG_ACTIVITY_NEW_TASK);
                                    CampusWallet.getContext().startActivity(intent);
                                }
                            }
                            else {
                                //System.out.println("SHANIL 23: ");
                            }
                        } else {
                            //System.out.println("SHANIL 24: ");
                            Intent intent = new Intent(CampusWallet.getContext(), HomeActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT | Intent.FLAG_ACTIVITY_NEW_TASK);
                            CampusWallet.getContext().startActivity(intent);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
    }
    @Override
    public int getItemCount() {
        return contacts.size();
    }
    public static class ViewHolder extends RecyclerView.ViewHolder {
        final TextView Content;
        final TextView MessageBody;
        final ImageView iconimage;
        final ConstraintLayout Groupnamecardview;
        final LinearLayout linearlayout;
        public ViewHolder(View itemView) {
            super(itemView);
            Content = itemView.findViewById(R.id.content);
            MessageBody = itemView.findViewById(R.id.message);
            Groupnamecardview = itemView.findViewById(R.id.namecardview);
            linearlayout = itemView.findViewById(R.id.linearlayout);
            iconimage = itemView.findViewById(R.id.iconimage);
        }
    }
}
