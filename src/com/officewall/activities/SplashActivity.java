
package com.officewall.activities;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import com.officewall.constants.DefaultConstants;
import com.officewall.database.DBAdapter;
import com.officewall.utils.Utils;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

public class SplashActivity extends Activity {

    // splash waiting time in milliseconds
    private static long SPLASH_TIME_OUT = 1000;

    /**
     * @return activity context
     */
    private Activity getActivityContext() {
        // TODO Auto-generated method stub
        return this;
    }

    /**
     * called on activity created
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        /*
         * Showing splash screen with a timer. This will be useful when you want
         * to show case your app logo / company
         */
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                // copy database if application launching first time
                copyDatabaseFromAsset();
                // start activity according to login status
                if (OfficeWallApp.DefaultPref.getString(DefaultConstants.PREF_LOGIN_STATUS,
                        DefaultConstants.STATUS_LOGOUT).equals(DefaultConstants.STATUS_LOGIN)) {
                    // goto wall screen
                    startActivity(new Intent(getActivityContext(), UserWallsActivity.class));
                } else {
                    // goto sign up screen
                    startActivity(new Intent(getActivityContext(), SignupActivity.class).putExtra(
                            DefaultConstants.EXTRA_TAG, DefaultConstants.TAG_SIGNUP));
                }
                // finish this activity
                finish();
            }
        }, SPLASH_TIME_OUT);
    }

    /**
     * copy database if app launches first time
     */
    private void copyDatabaseFromAsset() {
        // TODO Auto-generated method stub
        try {
            // create database adapter instance
            DBAdapter dbAdapter = new DBAdapter(getActivityContext());
            // get database path
            File dbFile = new File(DBAdapter.DB_PATH);
            if (!dbFile.exists()) {
                try {
                    // get database input stream
                    InputStream dbis = getAssets().open(DBAdapter.DB_NAME);
                    // open database
                    dbAdapter.openToWrite();
                    // close database
                    dbAdapter.close();
                    // copy database to given path
                    Utils.copyDataBase(dbis, DBAdapter.DB_PATH);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
