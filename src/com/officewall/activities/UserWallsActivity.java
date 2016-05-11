
package com.officewall.activities;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import retrofit.RetrofitError;
import retrofit.client.Response;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Paint;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.widget.DrawerLayout;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.google.gson.JsonObject;
import com.officewall.constants.DefaultConstants;
import com.officewall.constants.HttpConstants;
import com.officewall.customs.NavigationDrawer;
import com.officewall.database.DBHandler;
import com.officewall.fragments.AddPostFragment;
import com.officewall.fragments.PostsFragment;
import com.officewall.pojo.wrappers.GetUserWallsRs;
import com.officewall.pojo.core.UserWall;
import com.officewall.retrofit.callback.CustomCallback;
import com.officewall.retrofit.service.OfficewallService;
import com.officewall.retrofit.service.OfficewallServiceProvider;
import com.officewall.utils.Utils;

public class UserWallsActivity extends FragmentActivity implements OnClickListener {

    // views
    private NavigationDrawer mNavigationDrawer;
    private ScrollView svNavDrawer;

    // actionbar view
    private TextView tvActionbarWallName, tvActionbarPostCount;
    private ImageView ivAddPost;

    // drawer reload walls view
    private LinearLayout llWallsLoader;
    private TextView tvWallsLoading, tvWallsRetry;

    // drawer items views
    private LinearLayout llWallsContainer, llSignup, llSignout;
    private RelativeLayout rlNotifications, rlHelpCenter, rlPrivacyShortcuts;

    // wall selection and count list
    private List<ImageView> listIvSelect;
    private List<TextView> listTvCount;

    // progress and status view
    private ProgressBar pbProgress;
    private LinearLayout llStatus;
    private ImageView ivStatusIcon;
    private TextView tvStatusMsg;

    // timer to handle progressbar
    private Timer timerProgress;

    // currently selected wall
    public static String SELECTED_WALL_ID;
    public static String SELECTED_WALL_NAME;
    public static String SELECTED_WALL_COUNT;

    // flag to indicate config changed
    private boolean isConfigChanged = false;
    private boolean isMenuCreated = false;
    private int SELECTED_WALL_POS = 0;

    // database handler
    public static DBHandler dbHandler;

    /**
     * @return activity context
     */
    private FragmentActivity getActivityContext() {
        // TODO Auto-generated method stub
        return this;
    }

    /**
     * called on activity created
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_walls);

        // instantiate database handler
        dbHandler = DBHandler.getInstance(getActivityContext());

        // setup layout controls
        initializeControls();
        initializeActions();
        setupNavigationDrawerView();
        setupProgressAndStatusView();

        // prevent reload
        if (savedInstanceState != null) {
            isConfigChanged = true;
        }

        // get user walls
        getUserWalls();
    }

    /**
     * called on menu created
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_officewall, menu);

        // find view on Actionbar
        View view = menu.findItem(R.id.actionbar).getActionView();
        tvActionbarWallName = (TextView)view.findViewById(R.id.tv_actionbar_wall_name);
        tvActionbarPostCount = (TextView)view.findViewById(R.id.tv_actionbar_post_count);
        ivAddPost = (ImageView)view.findViewById(R.id.iv_actionbar_add_post);
        ivAddPost.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                /* handle click */
                // hide actionbar and drawer
                hideActionbarAndDrawer();
                // goto add post screen
                Utils.loadFragmentInBackstack(getActivityContext(),
                        DefaultConstants.FRAGMENT_CONTAINER_ID, AddPostFragment.class, null,
                        DefaultConstants.FRAGMENT_TAG_ADD_POST);
            }
        });

        // set flag
        isMenuCreated = true;
        // update actionbar info
        updateActionbarInfo();

        return super.onCreateOptionsMenu(menu);
    }

    /**
     * update actionbar info
     */
    private void updateActionbarInfo() {
        // TODO Auto-generated method stub
        /* set wall name */
        if (SELECTED_WALL_NAME == null) {
            SELECTED_WALL_NAME = "Office Wall";
        }
        tvActionbarWallName.setText(SELECTED_WALL_NAME);
        /* set post count in a selected wall */
        if (SELECTED_WALL_COUNT != null) {
            tvActionbarPostCount.setText(SELECTED_WALL_COUNT);
            tvActionbarPostCount.setVisibility(View.VISIBLE);
        } else {
            tvActionbarPostCount.setVisibility(View.GONE);
        }
    }

    /**
     * called on menu prepared
     */
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        return super.onPrepareOptionsMenu(menu);
    }

    /**
     * called on menu item selected
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                return mNavigationDrawer.getDrawerToggle().onOptionsItemSelected(item);
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /**
     * called after all creation completes
     */
    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        mNavigationDrawer.getDrawerToggle().syncState();
    }

    /**
     * save data on activity re-creation
     */
    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        // Save UI state changes to the savedInstanceState.
        // This bundle will be passed to onCreate if the process is
        // killed and restarted.
        savedInstanceState.putInt("selected_wall_pos", SELECTED_WALL_POS);
        savedInstanceState.putBoolean("nav_drawer_status", mNavigationDrawer.isActionBarShown());
    }

    /**
     * get saved data from bundle
     */
    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        // Restore UI state from the savedInstanceState.
        // This bundle has also been passed to onCreate.
        SELECTED_WALL_POS = savedInstanceState.getInt("selected_wall_pos");
        if (!savedInstanceState.getBoolean("nav_drawer_status")) {
            // hide actionbar and drawer
            hideActionbarAndDrawer();
        }
    }

    /**
     * request to get user walls
     */
    private void getUserWalls() {
        // TODO Auto-generated method stub
        // check if data available in database
        String userId = OfficeWallApp.DefaultPref.getString(DefaultConstants.PREF_LOGIN_UID, "");
        List<UserWall> list = dbHandler.getUserWalls(userId);
        if (list != null && list.size() != 0) {
            // inflate rows
            for (int i = 0; i < list.size(); i++) {
                // add user walls to navigation drawer
                addUserWallsToNavDrawer(i, list.get(i));
            }
            // hide error view on the drawer
            hideErrorViewOnDrawer();
        } else {
            // call api
            OfficewallService service = OfficewallServiceProvider.getService();
            service.getUserWalls(getUserWallsRequestJson(), mCallback);
        }
    }

    /**
     * creates http params json for user walls request.
     */
    private JsonObject getUserWallsRequestJson() {
        // TODO Auto-generated method stub
        // get data to pass with http request
        String request = HttpConstants.RQ_GET_USER_WALLS;
        String uid = OfficeWallApp.DefaultPref.getString(DefaultConstants.PREF_LOGIN_UID, "");
        String oAuthKey = OfficeWallApp.DefaultPref.getString(
                DefaultConstants.PREF_LOGIN_OAUTH_KEY, "");

        // create json from data
        JsonObject objJson = new JsonObject();
        objJson.addProperty(HttpConstants.HTTP_RQ_TYPE, request);
        objJson.addProperty(HttpConstants.PARAM_UID, uid);
        objJson.addProperty(HttpConstants.PARAM_OAUTH_KEY, oAuthKey);

        return objJson;
    }

    /**
     * callback to handle user walls response
     */
    private CustomCallback<GetUserWallsRs> mCallback = new CustomCallback<GetUserWallsRs>() {

        @Override
        public void success(GetUserWallsRs getUserWallsRs, Response response) {
            // TODO Auto-generated method stub
            /* return if task is canceled */
            if (isCanceled()) {
                return;
            }
            /* handle result */
            if (getUserWallsRs.getResponseCode() == HttpConstants.RESULT_OK) {
                /* take action on success */
                List<UserWall> list = getUserWallsRs.getWalls();
                if (list != null && list.size() != 0) {
                    // get user id
                    String userId = OfficeWallApp.DefaultPref.getString(
                            DefaultConstants.PREF_LOGIN_UID, "");
                    // inflate rows
                    for (int i = 0; i < list.size(); i++) {
                        // get data
                        UserWall wall = list.get(i);
                        String wallId = wall.getWallId();
                        String wallName = wall.getWallName();
                        String wallDomain = wall.getWallDomain();
                        String userEmail = wall.getUserEmail();
                        String newItems = wall.getNewItems();
                        // insert wall into database
                        dbHandler.insertIntoUserWall(userId, wallId, wallName, wallDomain,
                                userEmail, newItems);
                        // add wall to navigation drawer
                        addUserWallsToNavDrawer(i, wall);
                    }
                }
                // hide error view on the drawer
                hideErrorViewOnDrawer();
            } else {
                // set error on the drawer
                String message = getUserWallsRs.getUserMessage();
                setErrorOnDrawer(message);
            }
        }

        @Override
        public void failure(RetrofitError error) {
            // TODO Auto-generated method stub
            /* return if task is canceled */
            if (isCanceled()) {
                return;
            }
            // set error on the drawer
            String message = error.getCause().getMessage();
            setErrorOnDrawer(message);
        }
    };

    /**
     * add user walls to navigation drawer
     * 
     * @param position
     * @param userWall
     */
    private void addUserWallsToNavDrawer(final int position, final UserWall userWall) {
        // TODO Auto-generated method stub
        /* inflate row */
        final View view = getLayoutInflater().inflate(R.layout.row_nav_drawer_wall, null);

        /* initialize controls */
        final TextView tvName = (TextView)view.findViewById(R.id.tv_nav_drawer_wall_name);
        final TextView tvUserEmail = (TextView)view
                .findViewById(R.id.tv_nav_drawer_wall_user_email);
        final TextView tvPostCount = (TextView)view
                .findViewById(R.id.tv_nav_drawer_wall_post_count);
        final ImageView ivSelect = (ImageView)view.findViewById(R.id.iv_nav_drawer_wall_select);

        /* initialize actions */
        view.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                // set selection
                for (int pos = 0; pos < listIvSelect.size(); pos++) {
                    ImageView iv = listIvSelect.get(pos);
                    TextView tv = listTvCount.get(pos);
                    if (pos == position) {
                        tv.setVisibility(View.GONE);
                        iv.setVisibility(View.VISIBLE);
                    } else {
                        iv.setVisibility(View.GONE);
                        if (!tv.getText().toString().equals("")) {
                            tv.setVisibility(View.VISIBLE);
                        }
                    }
                }

                // close drawer
                mNavigationDrawer.closeDrawer(svNavDrawer);

                // get wall info
                SELECTED_WALL_POS = position;
                SELECTED_WALL_ID = userWall.getWallId();
                SELECTED_WALL_NAME = userWall.getWallName();
                SELECTED_WALL_COUNT = userWall.getNewItems();

                // update actionbar info if created
                if (isMenuCreated) {
                    updateActionbarInfo();
                }

                // if not from config changed
                if (!isConfigChanged) {
                    // goto posts screen
                    gotoPostsScreen();
                } else {
                    // reset flag
                    isConfigChanged = false;
                }
            }
        });

        /* add select imageview to the list */
        if (listIvSelect == null) {
            listIvSelect = new ArrayList<ImageView>();
        }
        listIvSelect.add(ivSelect);

        /* add count textview to the list */
        if (listTvCount == null) {
            listTvCount = new ArrayList<TextView>();
        }
        listTvCount.add(tvPostCount);

        /* get data */
        String wallName = userWall.getWallName();
        String userEmail = userWall.getUserEmail();
        String newItems = userWall.getNewItems();

        /* set data */
        tvName.setText(wallName);
        tvUserEmail.setText(userEmail);
        if (newItems != null) {
            tvPostCount.setText(newItems);
            tvPostCount.setVisibility(View.VISIBLE);
        }

        /* perform click at first */
        if (position == SELECTED_WALL_POS) {
            view.performClick();
        }

        // add row to walls container
        llWallsContainer.addView(view);
    }

    /**
     * goto posts screen
     */
    private void gotoPostsScreen() {
        // TODO Auto-generated method stub
        Utils.loadFragmentInRoot(getActivityContext(), DefaultConstants.FRAGMENT_CONTAINER_ID,
                PostsFragment.class, null, DefaultConstants.FRAGMENT_TAG_POSTS);
    }

    /**
     * initialize controls
     */
    private void initializeControls() {
        // TODO Auto-generated method stub
        // navigation drawer
        mNavigationDrawer = (NavigationDrawer)findViewById(R.id.navigation_drawer);

        // drawer scrollview
        svNavDrawer = (ScrollView)findViewById(R.id.sv_nav_drawer);
    }

    /**
     * initialize actions
     */
    private void initializeActions() {
        // TODO Auto-generated method stub
        // setup navigation drawer view
        mNavigationDrawer.setupDrawerConfiguration(svNavDrawer);
    }

    /**
     * setup navigation drawer
     */
    private void setupNavigationDrawerView() {
        // TODO Auto-generated method stub
        View view = findViewById(R.id.view_nav_drawer_items);

        /**/
        /* initialize controls */

        // walls loader
        llWallsLoader = (LinearLayout)view.findViewById(R.id.ll_nav_drawer_item_walls_loader);
        tvWallsLoading = (TextView)view.findViewById(R.id.tv_nav_drawer_item_walls_loading);
        tvWallsRetry = (TextView)view.findViewById(R.id.tv_nav_drawer_item_walls_retry);

        // drawer items
        llWallsContainer = (LinearLayout)view.findViewById(R.id.ll_nav_drawer_item_walls_container);
        llSignup = (LinearLayout)view.findViewById(R.id.ll_nav_drawer_item_sign_up);
        rlNotifications = (RelativeLayout)view.findViewById(R.id.rl_nav_drawer_item_notifications);
        rlHelpCenter = (RelativeLayout)view.findViewById(R.id.rl_nav_drawer_item_help_center);
        rlPrivacyShortcuts = (RelativeLayout)view
                .findViewById(R.id.rl_nav_drawer_item_privacy_shortcuts);
        llSignout = (LinearLayout)view.findViewById(R.id.ll_nav_drawer_item_sign_out);

        /**/
        /* initialize actions */

        // walls loader
        tvWallsRetry.setOnClickListener(this);
        tvWallsRetry.setPaintFlags(tvWallsRetry.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);

        // drawer items
        llSignup.setOnClickListener(this);
        rlNotifications.setOnClickListener(this);
        rlHelpCenter.setOnClickListener(this);
        rlPrivacyShortcuts.setOnClickListener(this);
        llSignout.setOnClickListener(this);
    }

    /**
     * setup progress and status view
     */
    private void setupProgressAndStatusView() {
        // TODO Auto-generated method stub
        View view = findViewById(R.id.view_progress_and_status);

        /* initialize controls */
        // progress
        pbProgress = (ProgressBar)view.findViewById(R.id.pb_progress);
        // status
        llStatus = (LinearLayout)findViewById(R.id.ll_status);
        ivStatusIcon = (ImageView)view.findViewById(R.id.iv_status_icon);
        tvStatusMsg = (TextView)view.findViewById(R.id.tv_status_message);
    }

    /**
     * handles click event
     */
    @Override
    public void onClick(View v) {
        // TODO Auto-generated method stub
        switch (v.getId()) {
            case R.id.tv_nav_drawer_item_walls_retry:
                // resend request to load user walls
                resendRequestToLoadUserWalls();
                break;
            case R.id.ll_nav_drawer_item_sign_up:
                // sign out current login and goto sign up screen
                signout(DefaultConstants.TAG_SIGNUP);
                break;
            case R.id.rl_nav_drawer_item_notifications:

                break;
            case R.id.rl_nav_drawer_item_help_center:

                break;
            case R.id.rl_nav_drawer_item_privacy_shortcuts:

                break;
            case R.id.ll_nav_drawer_item_sign_out:
                // sign out current login and goto login screen
                signout(DefaultConstants.TAG_LOGIN);
                break;
        }
    }

    /**
     * resend request to load user walls
     */
    private void resendRequestToLoadUserWalls() {
        // TODO Auto-generated method stub
        tvWallsLoading.setText(getResources().getString(R.string.strDrawerLoadingWalls));
        tvWallsRetry.setVisibility(View.GONE);

        // get user walls
        getUserWalls();
    }

    /**
     * show reload views on the drawer
     */
    private void setErrorOnDrawer(String message) {
        // TODO Auto-generated method stub
        tvWallsLoading.setText(message);
        tvWallsRetry.setVisibility(View.VISIBLE);
    }

    /**
     * hide reload views on the drawer
     */
    private void hideErrorViewOnDrawer() {
        // TODO Auto-generated method stub
        llWallsLoader.setVisibility(View.GONE);
    }

    /**
     * sign out current login
     */
    private void signout(String tag) {
        // TODO Auto-generated method stub
        // clear user data from the default prefrence
        SharedPreferences.Editor editor = OfficeWallApp.DefaultPref.edit();
        editor.putString(DefaultConstants.PREF_LOGIN_UID, "");
        editor.putString(DefaultConstants.PREF_LOGIN_OAUTH_KEY, "");
        editor.putString(DefaultConstants.PREF_LOGIN_STATUS, DefaultConstants.STATUS_LOGOUT);
        editor.commit();
        // goto signup screen
        Intent intent = new Intent(getActivityContext(), SignupActivity.class);
        intent.putExtra(DefaultConstants.EXTRA_TAG, tag);
        startActivity(intent);
        // finish this activity
        finish();
    }

    /**
     * show progressbar
     */
    public void showProgressbar() {
        // TODO Auto-generated method stub
        if (pbProgress != null) {
            // show progressbar
            if (!pbProgress.isShown()) {
                pbProgress.setVisibility(View.VISIBLE);
            }
            // schedule task
            timerProgress = new Timer();
            timerProgress.scheduleAtFixedRate(new TimerTask() {
                @Override
                public void run() {
                    // TODO Auto-generated method stub
                    int progress = pbProgress.getProgress() + new Random().nextInt(10);
                    if (progress < 100) {
                        pbProgress.setProgress(progress);
                    }
                }
            }, 10, 200);
        }
    }

    /**
     * hide progressbar
     */
    public void hideProgressbar() {
        // TODO Auto-generated method stub
        if (pbProgress != null) {
            // hide progressbar
            if (pbProgress.isShown()) {
                pbProgress.setProgress(0);
                pbProgress.setVisibility(View.GONE);
            }
            // cancel timer
            if (timerProgress != null) {
                timerProgress.cancel();
            }
        }
    }

    /**
     * show status
     * 
     * @param status
     * @param message
     */
    public void showStatus(int status, String message) {
        // TODO Auto-generated method stub
        // set status icon
        if (status == HttpConstants.RESULT_OK) {
            ivStatusIcon.setImageResource(R.drawable.ic_wall_success);
        } else {
            ivStatusIcon.setImageResource(R.drawable.ic_wall_error);
        }
        // set message to textview
        tvStatusMsg.setText(message);
        // fade in anim
        final Animation animFadeIn = new AlphaAnimation(0.0f, 1.0f);
        animFadeIn.setDuration(500);
        // fade out anim
        final Animation animFadeOut = new AlphaAnimation(1.0f, 0.0f);
        animFadeOut.setDuration(500);
        animFadeOut.setStartOffset(2000);
        // fade in anim listener
        animFadeIn.setAnimationListener(new AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                // TODO Auto-generated method stub
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
                // TODO Auto-generated method stub
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                // TODO Auto-generated method stub
                llStatus.startAnimation(animFadeOut);
            }
        });
        // start anim
        llStatus.startAnimation(animFadeIn);
    }

    /**
     * hide actionbar and drawer
     */
    public void setPaddingTopToProgressbar() {
        // TODO Auto-generated method stub
        ((LinearLayout)findViewById(R.id.ll_progress_and_status_container)).setPadding(0,
                (int)getResources().getDimension(R.dimen.height_comment_header), 0, 0);
    }

    /**
     * hide actionbar and drawer
     */
    public void hideActionbarAndDrawer() {
        // TODO Auto-generated method stub
        mNavigationDrawer.hideActionBar();
        mNavigationDrawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED, svNavDrawer);
    }

    /**
     * handles back event
     */
    @Override
    public void onBackPressed() {
        // TODO Auto-generated method stub
        super.onBackPressed();
        int backStackEntryCount = Utils.getNumberOfFragmentsInBackStack(getActivityContext());
        if (backStackEntryCount < 1) {
            // show actionbar and set navigation drawer enable if back stack
            // count zero
            mNavigationDrawer.showActionBar();
            mNavigationDrawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED, svNavDrawer);
            ((LinearLayout)findViewById(R.id.ll_progress_and_status_container)).setPadding(0, 0, 0,
                    0);
        }
    }

    /**
     * called on activity destroyed
     */
    @Override
    protected void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();
        /* cancel request */
        mCallback.cancel();
    }

}
