
package com.officewall.activities;

import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import com.officewall.constants.DefaultConstants;
import com.officewall.constants.HttpConstants;
import com.officewall.fragments.LoginFragment;
import com.officewall.fragments.SignupFragment;
import com.officewall.utils.Utils;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

public class SignupActivity extends FragmentActivity {

    // progress and status view
    private ProgressBar pbProgress;
    private LinearLayout llStatus;
    private ImageView ivStatusIcon;
    private TextView tvStatusMsg;

    // timer to handle progressbar
    private Timer timerProgress;

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
        setContentView(R.layout.activity_signup);

        // setup layout controls
        setupProgressAndStatusView();

        // get data from intent
        if (getIntent().getStringExtra(DefaultConstants.EXTRA_TAG).equals(
                DefaultConstants.TAG_SIGNUP)) {
            // goto signup screen
            gotoSignupScreen();
        } else {
            // goto login screen
            gotoLoginScreen();
        }
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
     * goto signup screen
     */
    private void gotoSignupScreen() {
        // TODO Auto-generated method stub
        Utils.loadFragmentInRoot(getActivityContext(), DefaultConstants.FRAGMENT_CONTAINER_ID,
                SignupFragment.class, null, DefaultConstants.FRAGMENT_TAG_SIGNUP);
    }

    /**
     * goto signup screen
     */
    private void gotoLoginScreen() {
        // TODO Auto-generated method stub
        Utils.loadFragmentInRoot(getActivityContext(), DefaultConstants.FRAGMENT_CONTAINER_ID,
                LoginFragment.class, null, DefaultConstants.FRAGMENT_TAG_LOGIN);
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
            }, 10, 100);
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
     * handles back event
     */
    @Override
    public void onBackPressed() {
        // TODO Auto-generated method stub
        super.onBackPressed();
    }

}
