package com.hungerhub.Diary.Gallary;

import android.app.Activity;
import android.app.Dialog;
import android.app.DownloadManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import androidx.core.app.NotificationCompat;
import androidx.viewpager.widget.ViewPager;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.chillarcards.cookery.R;
import com.google.firebase.analytics.FirebaseAnalytics;

import java.net.URLDecoder;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import com.hungerhub.application.Constants;
import com.hungerhub.application.PrefManager;
import com.hungerhub.networkmodels.Gallery.Code;
import com.hungerhub.networkmodels.Gallery.Image;
import com.hungerhub.utils.CommonDialogues_Utils;

/**
 * Created by user on 5/24/2018.
 */

public class GalleryAdapter extends RecyclerView.Adapter<GalleryAdapter.ViewHolder> {

    private List<Code> TeacherCodedList = new ArrayList<>();
    private List<String> ImageID = new ArrayList<>();
    private ArrayList<String> ImageUrl = new ArrayList<>();

    private final int rowLayout;
    private final Context mContext;
    private final Activity activity;
    public String parentPh,s_Id,TranactionTypeId,SelectedTransId;
    String PINN;
    int pos;
    private static ViewPager mPager;
    private static int currentPage = 0;
    Dialog dialogpopup;
    String parameters;
    Dialog Dlg;
    DownloadManager downloadManager;
    Uri Download_Uri;
    long enqueue;
    private ActivityImageHorizontalAdapetr horizontalAdapter;
    private ProgressDialog pDialog;
    private ProgressDialog progressDialog;
    ArrayList<Long> DownloadRefList;
    PrefManager prefManager;
    FirebaseAnalytics mFirebaseAnalytics;


    public GalleryAdapter(List<Code> teacherdata, Activity activity, Context context,FirebaseAnalytics mFirebaseAnalytics) {

        this.TeacherCodedList=teacherdata;
        this.activity = activity;
        this.rowLayout = R.layout.row_common_activity;
        this.mContext = context;
        this.mFirebaseAnalytics = mFirebaseAnalytics;
        ProgressDialog progressDialog = new ProgressDialog(activity);
        downloadManager = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
        DownloadRefList = new ArrayList<>();
        //DownloadRefList.clear();
        context.registerReceiver(onComplete,
                new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));
        prefManager=new PrefManager(context);


    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
//        View v = LayoutInflater.from(parent.getContext()).inflate(rowLayout, parent, false);
//        return new TeacherTimelineAdapter.ViewHolder(v);

        View v = LayoutInflater.from(parent.getContext()).inflate(rowLayout, parent, false);
        return new ViewHolder(v);

    }

    public static String replacer(String outBuffer) {

        String data = outBuffer;
        try {
            StringBuffer tempBuffer = new StringBuffer();
            int incrementor = 0;
            int dataLength = data.length();
            while (incrementor < dataLength) {
                char charecterAt = data.charAt(incrementor);
                if (charecterAt == '%') {
                    tempBuffer.append("<percentage>");
                } else if (charecterAt == '+') {
                    tempBuffer.append("<plus>");
                } else {
                    tempBuffer.append(charecterAt);
                }
                incrementor++;
            }
            data = tempBuffer.toString();
            data = URLDecoder.decode(data, "utf-8");
            data = data.replaceAll("<percentage>", "%");
            data = data.replaceAll("<plus>", "+");
        } catch(Exception e) {
            e.printStackTrace();
        }
        return data;
    }

    public static void addImageToGallery(final String filePath, final Context context) {

        ContentValues values = new ContentValues();

        values.put(MediaStore.Images.Media.DATE_TAKEN, System.currentTimeMillis());
        values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg");
        values.put(MediaStore.MediaColumns.DATA, filePath);

        context.getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
        //System.out.println("Image added to gallery : "+ filePath);
    }
    @Override
    public int getItemCount() {
        return TeacherCodedList.size();
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {


        parentPh = prefManager.getUserPhone();
        s_Id= prefManager.getStudentId();

        List<Image> ImagesArraylist=new ArrayList<>();
        ImagesArraylist.clear();

        ImagesArraylist=TeacherCodedList.get(position).getImages();
        //System.out.println("ImagesArraylist Size : "+ImagesArraylist.size());

        if (ImagesArraylist.size()>0){
            holder.DownloadImagesBTN.setVisibility(View.VISIBLE);
            holder.ActivityImageHoriListRV.setVisibility(View.VISIBLE);
            holder.ActivityImageHoriListRV.setLayoutManager(new GridLayoutManager(activity, 3));
            holder.ActivityImageHoriListRV.setItemAnimator(new DefaultItemAnimator());
            holder.ActivityImageHoriListRV.setNestedScrollingEnabled(false);
            horizontalAdapter = new ActivityImageHorizontalAdapetr(ImagesArraylist,activity, activity, R.layout.row_common_images,mFirebaseAnalytics);
            holder.ActivityImageHoriListRV.setAdapter(horizontalAdapter);


        }
        else {
            holder.ActivityImageHoriListRV.setVisibility(View.GONE);
            holder.DownloadImagesBTN.setVisibility(View.GONE);

        }

        holder.ContentTxt.setText(TeacherCodedList.get(position).getTeacherTimelineData());
        holder.DateTxt.setText(TeacherCodedList.get(position).getTeacherTimelineDate());
        final List<Image> finalImagesArraylist = ImagesArraylist;
        holder.DownloadImagesBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mFirebaseAnalytics.logEvent(mContext.getResources().getString(R.string.Gallery_Download_All_Item_Button_Clicked),new Bundle());
                if (finalImagesArraylist.size()!=0)
                {
                    int i=0;
                    for (Image image:finalImagesArraylist
                         ) {
                        DownloadImage(i,Constants.BASE_IMAGE_URL+image.getImageURL());
                        i++;

                    }
                }
                else {
                    Toast.makeText(mContext,mContext.getResources().getString(R.string.no_image_to_download),Toast.LENGTH_SHORT).show();
                }
            }
        });


    }


    private TrustManager[] getWrappedTrustManagers(TrustManager[] trustManagers) {
        final X509TrustManager originalTrustManager = (X509TrustManager) trustManagers[0];
        return new TrustManager[]{
                new X509TrustManager() {
                    public X509Certificate[] getAcceptedIssuers() {
                        return originalTrustManager.getAcceptedIssuers();
                    }

                    public void checkClientTrusted(X509Certificate[] certs, String authType) {
                        try {
                            if (certs != null && certs.length > 0){
                                certs[0].checkValidity();
                            } else {
                                originalTrustManager.checkClientTrusted(certs, authType);
                            }
                        } catch (CertificateException e) {
                            Log.w("checkClientTrusted", e.toString());
                        }
                    }

                    public void checkServerTrusted(X509Certificate[] certs, String authType) {
                        try {
                            if (certs != null && certs.length > 0){
                                certs[0].checkValidity();
                            } else {
                                originalTrustManager.checkServerTrusted(certs, authType);
                            }
                        } catch (CertificateException e) {
                            Log.w("checkServerTrusted", e.toString());
                        }
                    }
                }
        };
    }



    public static class ViewHolder extends RecyclerView.ViewHolder {

       TextView ContentTxt,DateTxt,MultippleImageTV;
       RecyclerView ActivityImageHoriListRV;
       LinearLayout LayoutMain;
       Button DownloadImagesBTN;

        public ViewHolder(View itemView) {
            super(itemView);

            ContentTxt= itemView.findViewById(R.id.EventMessageTV);
            DateTxt= itemView.findViewById(R.id.EventDateTV);
            ActivityImageHoriListRV= itemView.findViewById(R.id.ActivityImageHoriListRV);
            LayoutMain= itemView.findViewById(R.id.ActivitylayoutRowLL);
            DownloadImagesBTN= itemView.findViewById(R.id.DownloadImagesBTN);


        }
    }

    public void DownloadImage(int index,String Imageurl)
    {
        try{
        Uri Download_Uri= Uri.parse(Imageurl);

        DownloadManager.Request request = new DownloadManager.Request(Download_Uri);
        request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI | DownloadManager.Request.NETWORK_MOBILE);
        request.setAllowedOverRoaming(false);
        request.setTitle(mContext.getResources().getString(R.string.campus_wallet));
        request.setDescription(mContext.getResources().getString(R.string.downloading) );
        request.setVisibleInDownloadsUi(true);
//        File imageFolder = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/CWDownloads");
//        if (! imageFolder.exists())
//        {
//            imageFolder.mkdir();
//        }

        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE);// Visibility of the download Notification
        //request.setDestinationUri(Uri.fromFile(file));// Uri of the destination file
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            request.setRequiresCharging(false);// Set if charging is required to begin the download
        }
        request.setAllowedOverMetered(true);// Set if download is allowed on Mobile network
        request.setAllowedOverRoaming(true);// Set if download is allowed on roaming network

//                request.setDestinationInExternalFilesDir(ImageDownloadActivity.this,Environment.getExternalStorageDirectory().getAbsolutePath(),  "/CWDownloads/"
//                        +index+"_" +getcurrentdatetime()+".jpg");
        request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DCIM,  "/CWDownloads/"+
                index+"_" +getcurrentdatetime()+".jpg");

        long refid = downloadManager.enqueue(request);
        DownloadRefList.add(refid);
        Toast.makeText(mContext,mContext.getResources().getString(R.string.downloading),Toast.LENGTH_SHORT).show();

        }catch (Exception e)
        {
            CommonDialogues_Utils commonDialogues_utils=new CommonDialogues_Utils();
            commonDialogues_utils.showDialogPopup(mContext);
        }
    }
    public String getcurrentdatetime()
    {
        int mYear, mMonth, mDay,h,m,s;
        final Calendar c = Calendar.getInstance();
        mYear = c.get(Calendar.YEAR);
        mMonth = c.get(Calendar.MONTH);
        mDay = c.get(Calendar.DAY_OF_MONTH);
        h=c.get(Calendar.HOUR_OF_DAY);
        m=c.get(Calendar.MINUTE);
        s=c.get(Calendar.SECOND);
        return mYear+mMonth+mDay+h+m+s+"";
    }
    private String fileExt(String url) {
        if (url.indexOf("?") > -1) {
            url = url.substring(0, url.indexOf("?"));
        }
        if (url.lastIndexOf(".") == -1) {
            return null;
        } else {
            String ext = url.substring(url.lastIndexOf(".") + 1);
            if (ext.indexOf("%") > -1) {
                ext = ext.substring(0, ext.indexOf("%"));
            }
            if (ext.indexOf("/") > -1) {
                ext = ext.substring(0, ext.indexOf("/"));
            }
            return ext.toLowerCase();

        }
    }

    BroadcastReceiver onComplete = new BroadcastReceiver() {

        public void onReceive(Context ctxt, Intent intent) {

            // get the refid from the download manager
            // long referenceId = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1);


            String action = intent.getAction();
            if (DownloadManager.ACTION_DOWNLOAD_COMPLETE.equals(action)) {
                long downloadId = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1);
                DownloadManager.Query query = new DownloadManager.Query();
                query.setFilterById(downloadId);
                downloadManager = (DownloadManager)mContext.getSystemService(Context.DOWNLOAD_SERVICE);
                Cursor c = downloadManager.query(query);
                if (c.moveToFirst()) {
                    int columnIndex = c.getColumnIndex(DownloadManager.COLUMN_STATUS);
                    if (DownloadManager.STATUS_SUCCESSFUL == c.getInt(columnIndex)) {
                        String uriString = c.getString(c.getColumnIndex(DownloadManager.COLUMN_LOCAL_URI));
                        //TODO : Use this local uri and launch intent to open file
                        //System.out.println("local uri : "+uriString);
                        MimeTypeMap myMime = MimeTypeMap.getSingleton();
                        Intent newIntent = new Intent(Intent.ACTION_VIEW);
                        String mimeType = myMime.getMimeTypeFromExtension(fileExt(uriString));
                        newIntent.setDataAndType(Uri.parse(uriString),mimeType);
                        newIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        PendingIntent pendingIntent = PendingIntent.getActivity(mContext, 0, newIntent, 0);
// remove it from our list
                        DownloadRefList.remove(downloadId);

// if list is empty means all downloads completed
                        if (DownloadRefList.isEmpty())
                        {
                            NotificationChannel mChannel = null;
                            String CHANNEL_ID = "cw_channel_01";// The id of the channel.
                            CharSequence name = mContext.getResources().getString(R.string.notification_id_channel);// The user-visible name of the channel.
                            int importance = NotificationManager.IMPORTANCE_HIGH;
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                mChannel = new NotificationChannel(CHANNEL_ID, name, importance);
                            }
// show a notification
                            Log.e("INSIDE", "" + downloadId);
                            NotificationCompat.Builder mBuilder =
                                    new NotificationCompat.Builder(mContext,mContext.getResources().getString(R.string.notification_id_channel))
                                            .setSmallIcon(R.mipmap.ic_launcher)
                                            .setContentTitle(mContext.getResources().getString(R.string.campus_wallet))
                                            .setContentText(mContext.getResources().getString(R.string.downloading))
                                            .setChannelId(CHANNEL_ID)
                                            .setContentIntent(pendingIntent);
//                // Issue the initial notification with zero progress
//                int PROGRESS_MAX = 100;
//                int PROGRESS_CURRENT = 0;
//                mBuilder.setProgress(PROGRESS_MAX, PROGRESS_CURRENT, false);
//
//
//
//                NotificationManager notificationManager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
//                notificationManager.notify(455, mBuilder.build());
//
//// Do the job here that tracks the progress.
//// Usually, this should be in a
//// worker thread
//// To show progress, update PROGRESS_CURRENT and update the notification with:
//// mBuilder.setProgress(PROGRESS_MAX, PROGRESS_CURRENT, false);
//// notificationManager.notify(notificationId, mBuilder.build());
//
//// When done, update the notification one more time to remove the progress bar
                            mBuilder.setContentText(mContext.getResources().getString(R.string.download_complete))
                                    .setProgress(0,0,false);
                            NotificationManager notificationManager = (NotificationManager)mContext.getSystemService(Context.NOTIFICATION_SERVICE);
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                notificationManager.createNotificationChannel(mChannel);
                            }
                            notificationManager.notify(455, mBuilder.build());
                            Toast.makeText(mContext,mContext.getResources().getString(R.string.download_complete),Toast.LENGTH_SHORT).show();

                        }
                    }
                }
            }
        }
    };

}
