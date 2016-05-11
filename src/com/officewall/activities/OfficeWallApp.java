
package com.officewall.activities;

import android.app.Application;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class OfficeWallApp extends Application {

    // default preference
    public static SharedPreferences DefaultPref;

    /**
     * called on app created
     */
    @Override
    public void onCreate() {
        // TODO Auto-generated method stub
        super.onCreate();
        // instantiate default preference
        DefaultPref = PreferenceManager.getDefaultSharedPreferences(this);
    }
}
