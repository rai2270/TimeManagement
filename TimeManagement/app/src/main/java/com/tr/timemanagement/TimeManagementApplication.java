package com.tr.timemanagement;

import com.firebase.client.Firebase;


/**
 * TimeManagementApplication
 */
public class TimeManagementApplication extends android.app.Application {

    @Override
    public void onCreate() {
        super.onCreate();
        Firebase.setAndroidContext(this);

        // TODO: enable Firebase debug if needed. In logcat select: 1) Debug , 2) filter with "Raising /"
        //Firebase.getDefaultConfig().setLogLevel(Logger.Level.DEBUG);
    }

}
