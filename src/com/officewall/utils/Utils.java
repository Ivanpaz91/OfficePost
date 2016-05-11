
package com.officewall.utils;

import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.regex.Pattern;

import com.google.gson.Gson;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.media.ExifInterface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.telephony.TelephonyManager;
import android.util.Base64;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.WindowManager;

/**
 * The class Utils contains all the static methods used by the app Office Wall.
 */
public class Utils {

    /**
     * Pattern to validate email address.
     */
    public final static Pattern EMAIL_ADDRESS_PATTERN = Pattern.compile("[a-zA-Z0-9+._%-+]{1,256}"
            + "@" + "[a-zA-Z0-9][a-zA-Z0-9-]{0,64}" + "(" + "." + "[a-zA-Z0-9][a-zA-Z0-9-]{0,25}"
            + ")+");

    /**
     * This method matches given Address with the Pattern and returns result.
     * 
     * @param email
     * @return true if email valid, false otherwise
     */
    public static boolean isEmailValid(String email) {
        return EMAIL_ADDRESS_PATTERN.matcher(email).matches();
    }

    /**
     * This method checks if the Network available on the device or not.
     * 
     * @param context
     * @return true if network available, false otherwise
     */
    public static Boolean isNetworkAvailable(Context context) {
        boolean connected = false;
        final ConnectivityManager cm = (ConnectivityManager)context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        final NetworkInfo netInfo = cm.getActiveNetworkInfo();
        if (netInfo != null && netInfo.isConnectedOrConnecting()) {
            connected = true;
        } else if (netInfo != null && netInfo.isConnected()
                && cm.getActiveNetworkInfo().isAvailable()) {
            connected = true;
        } else if (netInfo != null && netInfo.isConnected()) {
            try {
                URL url = new URL("http://www.google.com");
                HttpURLConnection urlc = (HttpURLConnection)url.openConnection();
                urlc.setConnectTimeout(3000);
                urlc.connect();
                if (urlc.getResponseCode() == 200) {
                    connected = true;
                }
            } catch (MalformedURLException e1) {
                e1.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else if (cm != null) {
            final NetworkInfo[] netInfoAll = cm.getAllNetworkInfo();
            for (NetworkInfo ni : netInfoAll) {
                System.out.println("get network type :::" + ni.getTypeName());
                if ((ni.getTypeName().equalsIgnoreCase("WIFI") || ni.getTypeName()
                        .equalsIgnoreCase("MOBILE")) && ni.isConnected() && ni.isAvailable()) {
                    connected = true;
                    if (connected) {
                        break;
                    }
                }
            }
        }
        return connected;
    }

    /**
     * This method clears backstack and loads fragment in a root.
     * 
     * @param fragmentActivity
     * @param fragmentContainerId
     * @param fragmentClass
     * @param bundle
     * @param tag
     * @return true if loaded successfully, false otherwise
     */
    public static boolean loadFragmentInRoot(FragmentActivity fragmentActivity,
            int fragmentContainerId, Class<? extends Fragment> fragmentClass, Bundle bundle,
            String tag) {
        // TODO Auto-generated method stub
        boolean status = false;

        FragmentManager fragmentManager = fragmentActivity.getSupportFragmentManager();
        // remove all fragments from back stack
        fragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
        // add new fragment
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out,
                android.R.anim.fade_in, android.R.anim.fade_out);
        Fragment fragment;
        try {
            fragment = fragmentClass.newInstance();
            fragment.setArguments(bundle);
            fragmentTransaction.replace(fragmentContainerId, fragment, tag).commit();
            // finish pending transactions
            fragmentManager.executePendingTransactions();
            status = true;
        } catch (InstantiationException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return status;
    }

    /**
     * This method loads fragment in a backstack.
     * 
     * @param fragmentActivity
     * @param fragmentContainerId
     * @param fragmentClass
     * @param bundle
     * @param tag
     * @return true if loaded successfully, false otherwise
     */
    public static boolean loadFragmentInBackstack(FragmentActivity fragmentActivity,
            int fragmentContainerId, Class<? extends Fragment> fragmentClass, Bundle bundle,
            String tag) {
        // TODO Auto-generated method stub
        boolean status = false;
        try {
            FragmentManager fragmentManager = fragmentActivity.getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.setCustomAnimations(android.R.anim.fade_in,
                    android.R.anim.fade_out, android.R.anim.fade_in, android.R.anim.fade_out);
            Fragment fragment = fragmentClass.newInstance();
            fragment.setArguments(bundle);
            fragmentTransaction.replace(fragmentContainerId, fragment, tag).addToBackStack(null)
                    .commit();
            // finish pending transactions
            fragmentManager.executePendingTransactions();
            status = true;
        } catch (InstantiationException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return status;
    }

    /**
     * This method counts no of fragments in a backstack.
     * 
     * @param fragmentActivity
     * @return total no of fragments in a backstack
     */
    public static int getNumberOfFragmentsInBackStack(FragmentActivity fragmentActivity) {
        // TODO Auto-generated method stub
        // make sure transactions are finished before reading backstack count
        FragmentManager fragmentManager = fragmentActivity.getSupportFragmentManager();
        fragmentManager.executePendingTransactions();

        return fragmentManager.getBackStackEntryCount();
    }

    /**
     * @param context
     * @return device token/id
     */
    public static String getDeviceID(Context context) {
        // TODO Auto-generated method stub
        TelephonyManager telephonyManager = (TelephonyManager)context
                .getSystemService(Context.TELEPHONY_SERVICE);
        return telephonyManager.getDeviceId();
    }

    /**
     * @param context
     * @return app version
     */
    public static String getAppVersion(Context context) {
        // TODO Auto-generated method stub
        try {
            PackageInfo packageInfo = context.getPackageManager().getPackageInfo(
                    context.getPackageName(), 0);
            return packageInfo.versionName;
        } catch (NameNotFoundException e) {
            // TODO: handle exception
            throw new RuntimeException(e);
        }
    }

    /**
     * This method copies data from input stream to output stream.
     * 
     * @param is
     * @param os
     */
    public static void CopyStream(InputStream is, OutputStream os) {
        final int buffer_size = 1024;
        try {
            byte[] bytes = new byte[buffer_size];
            for (;;) {
                int count = is.read(bytes, 0, buffer_size);
                if (count == -1)
                    break;
                os.write(bytes, 0, count);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    /**
     * This method copies database file from /assets folder to default directory
     * 
     * @param dbis
     * @param dbPath
     */
    public static void copyDataBase(InputStream dbis, String dbPath) {
        try {
            // open your local database as the input stream
            OutputStream dbos = new FileOutputStream(dbPath);
            // path to the just created empty database
            byte[] buffer = new byte[1024];
            while (dbis.read(buffer) > 0) {
                dbos.write(buffer);
            }
            dbis.close();
            dbos.flush();
            dbos.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * This method resets bitmap orientation to 0 if not.
     * 
     * @param path
     * @param bitmap
     * @return bitmap with 0 degree orientation
     */
    public static Bitmap resetBitmapOrientation(String path, Bitmap bitmap) {
        // TODO Auto-generated method stub
        int rotate = 0;
        try {
            ExifInterface ei = new ExifInterface(path);
            int orientation = ei.getAttributeInt(ExifInterface.TAG_ORIENTATION,
                    ExifInterface.ORIENTATION_NORMAL);
            switch (orientation) {
                case ExifInterface.ORIENTATION_ROTATE_90:
                    rotate = 90;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    rotate = 180;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_270:
                    rotate = 270;
                    break;
                default:
                    // do nothing...
                    break;
            }
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        if (rotate == 0) {
            return bitmap;
        } else {
            Matrix m = new Matrix();
            m.postRotate(rotate);
            return Bitmap
                    .createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), m, true);
        }
    }

    /**
     * This method to get real path from uri.
     * 
     * @param context
     * @param contentURI
     * @return imagepath
     */
    public static String getRealPathFromURI(Context context, Uri contentURI) {
        Cursor cursor = context.getContentResolver().query(contentURI, null, null, null, null);
        if (cursor == null) {
            return contentURI.getPath();
        } else {
            cursor.moveToFirst();
            int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
            return cursor.getString(idx);
        }
    }

    /**
     * This method converts bitmap to base64 string.
     * 
     * @param bitmap
     * @return base64EncodedImage
     */
    public static String encodeTobase64(Bitmap bitmap) {
        // TODO Auto-generated method stub
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] b = baos.toByteArray();
        return Base64.encodeToString(b, Base64.DEFAULT);
    }

    /**
     * This method converts base64 string to bitmap.
     * 
     * @param base64EncodedImage
     * @return bitmap
     */
    public static Bitmap decodeFromBase64(String base64EncodedImage) {
        try {
            byte[] b = Base64.decode(base64EncodedImage, 0);
            return BitmapFactory.decodeByteArray(b, 0, b.length);
        } catch (Exception e) {
            // TODO: handle exception
            e.printStackTrace();
            return null;
        }
    }

    /**
     * This method sets background of given view.
     * 
     * @param context
     * @param view
     * @param drawable
     */
    @SuppressWarnings("deprecation")
    public static void setViewBackground(Context context, View view, int drawable) {
        // TODO Auto-generated method stub
        if (Build.VERSION.SDK_INT >= 16) {
            view.setBackground(context.getResources().getDrawable(drawable));
        } else {
            view.setBackgroundDrawable(context.getResources().getDrawable(drawable));
        }
    }

    /**
     * This method sets background of given view.
     * 
     * @param context
     * @param view
     * @param drawable
     */
    @SuppressWarnings("deprecation")
    public static void setViewBackground(Context context, View view, BitmapDrawable drawable) {
        // TODO Auto-generated method stub
        if (Build.VERSION.SDK_INT >= 16) {
            view.setBackground(drawable);
        } else {
            view.setBackgroundDrawable(drawable);
        }
    }

    /**
     * This method to get screen size.
     * 
     * @param context
     * @return window manager instance which contains width and height of the
     *         screen
     */
    public static DisplayMetrics getScreenSize(Context context) {
        // TODO Auto-generated method stub
        DisplayMetrics dm = new DisplayMetrics();
        WindowManager wm = (WindowManager)context.getSystemService(Context.WINDOW_SERVICE);
        wm.getDefaultDisplay().getMetrics(dm);
        return dm;
    }

    /**
     * This method converts Pojo class into Json string.
     * 
     * @param pojo class object
     * @return json string
     */
    public static String getJsonFromObject(Object object) {
        // TODO Auto-generated method stub
        return new Gson().toJson(object);
    }

    /**
     * This method converts Json string into Pojo class.
     * 
     * @param json string
     * @param pojo class object
     * @return pojo class object with data
     */
    public static Object getObjectFromJson(String json, Class<? extends Object> Class) {
        // TODO Auto-generated method stub
        return new Gson().fromJson(json, Class);
    }

    /**
     * This method finds location of the view on the screen.
     * 
     * @param view
     * @return rect
     */
    public static Rect locateView(View view) {
        int[] loc_int = new int[2];
        if (view == null)
            return null;
        try {
            view.getLocationOnScreen(loc_int);
        } catch (NullPointerException npe) {
            // Happens when the view doesn't exist on screen anymore.
            return null;
        }
        Rect location = new Rect();
        location.left = loc_int[0];
        location.top = loc_int[1];
        location.right = location.left + view.getWidth();
        location.bottom = location.top + view.getHeight();
        return location;
    }

}
