
package com.officewall.constants;

/**
 * The class HttpConstants contains all the Http CONSTANTS used by the app
 * Office Wall.
 */
public class HttpConstants {
    /**
     * Base url.
     */
    public static final String HTTP_URL = "http://expresys.com/officewall/test/";

    /**
     * Key to post data.
     */
    public static final String HTTP_POST_DATA = "PostData";

    /**
     * Request type.
     */
    public static final String HTTP_RQ_TYPE = "@request_type";

    /**
     * Operations.
     */
    public static final String RQ_SIGNUP = "signup";
    public static final String RQ_LOGIN = "login";
    public static final String RQ_GET_USER_WALLS = "get_user_walls";
    public static final String RQ_GET_POSTS = "get_posts";
    public static final String RQ_ADD_POST = "post";
    public static final String RQ_GET_COMMENTS = "get_comments";
    public static final String RQ_ADD_COMMENT = "comment";
    public static final String RQ_VOTE_POST = "vote_post";
    public static final String RQ_VOTE_COMMENT = "vote_comment";
    public static final String RQ_INVITE_FRIENDS = "invite_friends";
    public static final String RQ_SET_POST_STATUS = "set_post_status";
    public static final String RQ_REPORT_POST = "report_post";
    public static final String RQ_REPORT_COMMENT = "report_comment";
    public static final String RQ_ = "";

    /**
     * Links.
     */
    public static final String LINK_FORGOT_PASSWORD = "https://officewall.com/login/forgot";

    /**
     * Default params.
     */
    public static final String PARAM_UID = "user_id";
    public static final String PARAM_OAUTH_KEY = "oAuth_key";

    /**
     * Register request params.
     */
    public static final String PARAM_SIGNUP_EMAIL = "email";
    public static final String PARAM_SIGNUP_UID = "user_id";
    public static final String PARAM_SIGNUP_APP = "app";
    public static final String PARAM_SIGNUP_APP_VERSION = "app_version";
    public static final String PARAM_SIGNUP_DEVICE_INFO = "device_info";

    /**
     * Login request params.
     */
    public static final String PARAM_LOGIN_EMAIL = "email";
    public static final String PARAM_LOGIN_PASSWORD = "password";
    public static final String PARAM_LOGIN_APP = "app";
    public static final String PARAM_LOGIN_APP_VERSION = "app_version";
    public static final String PARAM_LOGIN_DEVICE_INFO = "device_info";

    /**
     * Get Post request params.
     */
    public static final String PARAM_GET_POST_WALL_ID = "wall_id";
    public static final String PARAM_GET_POST_MAX_POSTS = "max_posts";
    public static final String PARAM_GET_POST_START_FROM = "start_from";

    /**
     * Add Post request params.
     */
    public static final String PARAM_ADD_POST_WALL_ID = "wall_id";
    public static final String PARAM_ADD_POST_TEXT = "text";
    public static final String PARAM_ADD_POST_IMAGE = "image";
    public static final String PARAM_ADD_POST_BG_COLOR = "bg_color";

    /**
     * Get Comment request params.
     */
    public static final String PARAM_GET_COMMENT_POST_ID = "post_id";
    public static final String PARAM_GET_COMMENT_MAX_COMMENTS = "max_comments";
    public static final String PARAM_GET_COMMENT_START_FROM = "start_from";

    /**
     * Add Comment request params.
     */
    public static final String PARAM_ADD_COMMENT_POST_ID = "post_id";
    public static final String PARAM_ADD_COMMENT_TEXT = "text";
    public static final String PARAM_ADD_COMMENT_IMAGE = "image";
    public static final String PARAM_ADD_COMMENT_BG_COLOR = "bg_color";

    /**
     * Vote Post request params.
     */
    public static final String PARAM_VOTE_POST_POST_ID = "post_id";
    public static final String PARAM_VOTE_POST_VOTE = "vote";

    /**
     * Vote Comment request params.
     */
    public static final String PARAM_VOTE_COMMENT_COMMENT_ID = "comment_id";
    public static final String PARAM_VOTE_COMMENT_VOTE = "vote";

    /**
     * Invite friends request params.
     */
    public static final String PARAM_INVITE_FRIENDS_EMAILS = "emails";

    /**
     * Set post status request params.
     */
    public static final String PARAM_SET_POST_STATUS_POST_ID = "post_id";
    public static final String PARAM_SET_POST_STATUS_STATUS = "status";

    /**
     * Report post request params.
     */
    public static final String PARAM_REPORT_POST_POST_ID = "post_id";
    public static final String PARAM_REPORT_POST_REASON = "reason";
    public static final String PARAM_REPORT_POST_TEXT = "text";

    /**
     * Report comment request params.
     */
    public static final String PARAM_REPORT_COMMENT_COMMENT_ID = "comment_id";
    public static final String PARAM_REPORT_COMMENT_REASON = "reason";
    public static final String PARAM_REPORT_COMMENT_TEXT = "text";

    /**
     * Content type Constants.
     */
    public static final String HTTP_CONTENT_TYPE = "Content-type";
    public static final String CONTENT_TYPE_UrlEncoded = "application/x-www-form-urlencoded";

    /**
     * Connection timeout Constants.
     */
    public static final int HTTP_CONNECTION_TIMEOUT = 60000;
    public static final int HTTP_SO_TIMEOUT = 60000;

    /**
     * Result Constants.
     */
    public static final int RESULT_OK = 200;
    public static final int RESULT_ERROR = -1;

}
