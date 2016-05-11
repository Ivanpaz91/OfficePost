
package com.officewall.constants;

import java.io.File;

import com.officewall.activities.R;

import android.os.Environment;

/**
 * The class DefaultConstants contains all the Default CONSTANTS used by the app
 * Office Wall.
 */
public class DefaultConstants {
    /**
     * App cache directory. All the app data will be saved to this sdcard
     * directory.
     */
    public static final String APP_CACHE_DIR = Environment.getExternalStorageDirectory().toString()
            + File.separator + "OfficeWall";

    /**
     * Project id from Google developer Console. Will be used for GCM (Push
     * notification) setup.
     */
    public static final String GOOGLE_CONSOLE_PROJECT_ID = "";
    public static final long REGISTRATION_EXPIRY_TIME_MS = 1000 * 3600 * 24 * 7;

    /**
     * Default preference Constants. All the preference keys used in the App to
     * save data in default preference.
     */
    public static final String PREF_LOGIN_UID = "pref_login_user_id";
    public static final String PREF_LOGIN_OAUTH_KEY = "pref_login_oauth_key";
    public static final String PREF_LOGIN_STATUS = "pref_login_status";
    //
    public static final String PREF_ = "";

    /**
     * Fragment tags
     */
    public static final String FRAGMENT_TAG_SIGNUP = "signup";
    public static final String FRAGMENT_TAG_LOGIN = "login";
    //
    public static final String FRAGMENT_TAG_POSTS = "posts";
    public static final String FRAGMENT_TAG_ADD_POST = "add_post";
    //
    public static final String FRAGMENT_TAG_COMMENTS = "comments";
    public static final String FRAGMENT_TAG_ADD_COMMENT = "add_comment";
    //
    public static final String FRAGMENT_TAG_ = "";

    /**
     * App flags
     */
    public static final int MAX_TO_LOAD = 10;
    //
    public static final int VOTE_UP = 1;
    public static final int VOTE_DOWN = 2;
    //
    public static final int REPORT_SPAM = 1;
    public static final int REPORT_PERSONAL_HATE = 2;
    public static final int REPORT_SEXUALLY_EXPLICIT = 3;
    public static final int REPORT_ILLEGAL = 4;
    public static final int REPORT_OTHER = 99;
    //
    public static final int SET_STATUS_NONE = 0;
    public static final int SET_STATUS_SUBSCRIBE = 1;
    public static final int SET_STATUS_HIDE = 2;
    //
    public static final String NOTIFICATION_GCM = "GCM";
    public static final String NOTIFICATION_APNS = "APNS";

    /**
     * Default Integer Constants.
     */
    public static final int FRAGMENT_CONTAINER_ID = R.id.fl_content_screen;
    //
    public static final int IMAGE_TYPE_THUMB_1X = 100;
    public static final int IMAGE_TYPE_THUMB_2X = 200;
    public static final int IMAGE_TYPE_THUMB_GRID_2X = 201;
    public static final int IMAGE_TYPE_FULL = 0;
    //
    public static final int REQUEST_CAMERA = 1001;
    public static final int REQUEST_GALLERY = 1002;
    //
    public static final int NULL_INTEGER = -1;
    public static final int DEFAULT_INTEGER = 0;

    /**
     * Default String Constants.
     */
    public static final String STATUS_LOGIN = "status_login";
    public static final String STATUS_LOGOUT = "status_logout";
    //
    public static final String SYMBOL_USD = "$";
    //
    public static final String SEPARATOR_WHITESPACE = " ";
    public static final String SEPARATOR_DESH = "-";
    public static final String SEPARATOR_SLASH = "/";
    public static final String SEPARATOR_COMMA = ",";
    //
    public static final String FILE_TYPE_IMAGE = "image";
    public static final String FILE_TYPE_TEXT = "text";
    //
    public static final String IMAGE_TYPE_base64string = "image_type_base64string";
    public static final String IMAGE_TYPE_COLOR = "image_type_color";
    //
    public static final String EXTRA_TAG = "extra_tag";
    public static final String EXTRA_WALL_ID = "extra_wall_id";
    public static final String EXTRA_POST_ID = "extra_post_id";
    public static final String EXTRA_POST_BG_COLOR = "extra_post_bg_color";
    public static final String EXTRA_POST_BG_IMAGE = "extra_post_bg_image";
    //
    public static final String TAG_SIGNUP = "tag_signup";
    public static final String TAG_LOGIN = "tag_login";

}
