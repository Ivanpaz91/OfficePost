
package com.officewall.customs;

import com.officewall.activities.R;

import android.app.ActionBar;
import android.content.Context;
import android.graphics.Color;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.widget.DrawerLayout;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ScrollView;

public class NavigationDrawer extends DrawerLayout {

    // views
    private ScrollView svNavDrawer;
    private ActionBarDrawerToggle mNavDrawerToggle;

    /**
     * constructor
     */
    public NavigationDrawer(Context context) {
        super(context);
        // TODO Auto-generated constructor stub
    }

    public NavigationDrawer(Context context, AttributeSet attrs) {
        super(context, attrs);
        // TODO Auto-generated constructor stub
    }

    public NavigationDrawer(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        // TODO Auto-generated constructor stub
    }

    /**
     * setup drawer configuration
     * 
     * @param scrollview
     */
    public void setupDrawerConfiguration(ScrollView scrollview) {
        // TODO Auto-generated method stub
        svNavDrawer = scrollview;

        // ActionBarDrawerToggle ties together the proper interactions between
        // the sliding drawer and the action bar home button
        mNavDrawerToggle = setupDrawerToggle();

        // set a custom shadow that overlays the main content when the drawer
        // setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);
        setScrimColor(Color.TRANSPARENT);
        setDrawerListener(mNavDrawerToggle);

        // setup action buttons
        getActionBar().setHomeButtonEnabled(true);
        getActionBar().setDisplayHomeAsUpEnabled(true);
    }

    /**
     * @return ActionBarDrawerToggle instance
     */
    public ActionBarDrawerToggle getDrawerToggle() {
        return mNavDrawerToggle;
    }

    /**
     * @return FragmentActivity
     */
    private FragmentActivity getActivity() {
        return (FragmentActivity)getContext();
    }

    /**
     * @return ActionBar
     */
    private ActionBar getActionBar() {
        return getActivity().getActionBar();
    }

    /**
     * hide actionbar
     */
    public void hideActionBar() {
        if (getActionBar().isShowing()) {
            getActionBar().hide();
        }
    }

    /**
     * show actionbar
     */
    public void showActionBar() {
        if (!getActionBar().isShowing()) {
            getActionBar().show();
        }
    }

    /**
     * show actionbar
     * 
     * @return boolean
     */
    public boolean isActionBarShown() {
        return getActionBar().isShowing();
    }

    /**
     * setup drawer toggle
     * 
     * @return ActionBarDrawerToggle instance
     */
    private ActionBarDrawerToggle setupDrawerToggle() {
        return new ActionBarDrawerToggle(getActivity(), this, R.drawable.ic_drawer_icon,
                R.string.strDrawerOpen, R.string.strDrawerClose) {
            public void onDrawerClosed(View drawerView) {
                // drawer closed
            }

            public void onDrawerOpened(View drawerView) {
                // drawer opened
            }
        };
    }

    /**
     * @return true if drawer open, false otherwise
     */
    public boolean isDrawerOpen() {
        return isDrawerOpen(svNavDrawer);
    }

    /**
     * @return true if drawer indicator enabled, false otherwise
     */
    public boolean isDrawerIndicatorEnabled() {
        // TODO Auto-generated method stub
        return mNavDrawerToggle.isDrawerIndicatorEnabled();
    }

    /**
     * disable drawer indicator
     */
    public void disableDrawerIndicator() {
        // TODO Auto-generated method stub
        // remove control of drawer from home button
        mNavDrawerToggle.setDrawerIndicatorEnabled(false);
    }

    /**
     * enable drawer indicator
     */
    public void enableDrawerIndicator() {
        // TODO Auto-generated method stub
        // set home button as a drawer
        mNavDrawerToggle.setDrawerIndicatorEnabled(true);
    }

}
