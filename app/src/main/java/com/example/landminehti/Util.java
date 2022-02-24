package com.example.landminehti;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class Util {

    public static final String USERS="Users";
    public static final String USER_ID="User ID";
    public static final String FULL_NAME="Full Name";
    public static final String EMAIL="Email";
    public static final String JOB_TITLE="Job Title";
    public static final String COMPANY_NAME="Company Name";
    public static final String STUDY_GROUP="Study Group";
    public static final String NATIONAL_ID="National ID";
    public static final String PHONE_NUMBER="Phone Number";
    public static final String IS_ACTIVE="isActive";
    public static final String USER_PIC="user_picture";

    public static boolean checkConnection(Context context) {
        final ConnectivityManager connMgr = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        if (connMgr != null) {
            NetworkInfo activeNetworkInfo = connMgr.getActiveNetworkInfo();
            if (activeNetworkInfo != null) { // connected to the internet
                // connected to the mobile provider's data plan
                if (activeNetworkInfo.getType() == ConnectivityManager.TYPE_WIFI) {
                    // connected to wifi
                    return true;
                } else return activeNetworkInfo.getType() == ConnectivityManager.TYPE_MOBILE;
            }
        }
        return false;
    }
}
