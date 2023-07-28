package com.hungerhub.application;

import android.app.Application;

import android.content.Context;
import android.os.StrictMode;
import android.text.TextUtils;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.crashlytics.android.Crashlytics;
import com.crashlytics.android.core.CrashlyticsCore;
import com.zplesac.connectionbuddy.ConnectionBuddy;
import com.zplesac.connectionbuddy.ConnectionBuddyConfiguration;

import com.hungerhub.utils.CommonSSLConnection;
import io.fabric.sdk.android.Fabric;


public class CampusWallet extends Application /*implements PushManager.OnTokenReceivedListener*/{

    public static final String TAG = CampusWallet.class.getSimpleName();
    private static CampusWallet mInstance;
    private RequestQueue mRequestQueue;
    PrefManager prefManager;
    CommonSSLConnection commonSSLConnection;

    static Context mContext;

    public static Context getContext() {
        return mContext;
    }
    @Override
    public void onCreate() {
        super.onCreate();
        mContext=getApplicationContext();
        mInstance = this;
        prefManager=new PrefManager(this);
        commonSSLConnection=new CommonSSLConnection();
        Crashlytics crashlyticsKit = new Crashlytics.Builder()
                .core(new CrashlyticsCore.Builder().disabled(false).build())
                .build();

        Fabric.with(this, crashlyticsKit);


        ConnectionBuddyConfiguration networkInspectorConfiguration = new ConnectionBuddyConfiguration.Builder(this).build();
        ConnectionBuddy.getInstance().init(networkInspectorConfiguration);

        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());
//
//        StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder().detectAll().build());
//        StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder().detectAll().build());

//        MoEngage moEngage =
//                new MoEngage.Builder(this, "TZ0LK32K5WK5LAKVNZU7RMXD")
//                        .setNotificationSmallIcon(R.mipmap.ic_launcher)
//                        .setNotificationLargeIcon(R.drawable.ic_launcher)
//                        .optOutTokenRegistration()
//                        .build();
//        MoEngage.initialise(moEngage);
//        if(prefManager.getFirstRun())
//        {
//            prefManager.setFirstRun(false);
//            MoEHelper.getInstance(getApplicationContext()).setExistingUser(false);
//        }
//        PushManager.getInstance().setTokenObserver(this);

    }




    public static synchronized CampusWallet getInstance() {
        return mInstance;
    }

    public RequestQueue getRequestQueue() {
        if (mRequestQueue == null) {
//            mRequestQueue = Volley.newRequestQueue(getApplicationContext(),commonSSLConnection.getHulkstack(getApplicationContext()));
            mRequestQueue = Volley.newRequestQueue(getApplicationContext());
        }


        ///Eldhose

        return mRequestQueue;
    }

    public <T> void addToRequestQueue(Request<T> req, String tag) {
        // set the default tag if tag is empty
        req.setTag(TextUtils.isEmpty(tag) ? TAG : tag);
        getRequestQueue().add(req);
    }

    public <T> void addToRequestQueue(Request<T> req) {
        req.setTag(TAG);
        getRequestQueue().add(req);
    }

    public void cancelPendingRequests(Object tag) {
        if (mRequestQueue != null) {
            mRequestQueue.cancelAll(tag);
        }
    }

//    @Override
//    public void onTokenReceived(String token) {
//        PushManager.getInstance().refreshToken(getApplicationContext(), token);
//    }
}
