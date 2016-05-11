
package com.officewall.fragments;

import retrofit.RetrofitError;
import retrofit.client.Response;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.gson.JsonObject;
import com.officewall.activities.OfficeWallApp;
import com.officewall.activities.R;
import com.officewall.activities.SignupActivity;
import com.officewall.activities.UserWallsActivity;
import com.officewall.constants.DefaultConstants;
import com.officewall.constants.HttpConstants;
import com.officewall.pojo.wrappers.LoginRs;
import com.officewall.onscreenmessages.OnScreenMsg;
import com.officewall.retrofit.callback.CustomCallback;
import com.officewall.retrofit.service.OfficewallService;
import com.officewall.retrofit.service.OfficewallServiceProvider;
import com.officewall.utils.Utils;

public class LoginFragment extends Fragment implements OnClickListener {

    // views
    private EditText edtEmail, edtPassword;
    private Button btnLogin;
    private TextView tvForgotPassword, tvNewHere;

    /**
     * called on fragment created
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    /**
     * called on fragments view created
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        View mainView = inflater.inflate(R.layout.fragment_login, container, false);

        // setup layout controls
        initializeControls(mainView);
        initializeActions();

        return mainView;
    }

    /**
     * initialize controls
     * 
     * @param mainView
     */
    private void initializeControls(View mainView) {
        // TODO Auto-generated method stub
        // edittexts
        edtEmail = (EditText)mainView.findViewById(R.id.edt_login_email);
        edtPassword = (EditText)mainView.findViewById(R.id.edt_login_password);

        // buttons
        btnLogin = (Button)mainView.findViewById(R.id.btn_login_login);

        // textviews
        tvForgotPassword = (TextView)mainView.findViewById(R.id.tv_login_forgot_password);
        tvNewHere = (TextView)mainView.findViewById(R.id.tv_login_new_here_signup);
    }

    /**
     * initialize actions
     */
    private void initializeActions() {
        // TODO Auto-generated method stub
        // buttons
        btnLogin.setOnClickListener(this);

        // textviews
        tvForgotPassword.setOnClickListener(this);
        tvNewHere.setOnClickListener(this);

        // set Underline flag to textview
        tvForgotPassword
                .setPaintFlags(tvForgotPassword.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        tvNewHere.setPaintFlags(tvNewHere.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
    }

    /**
     * handles click event
     */
    @Override
    public void onClick(View v) {
        // TODO Auto-generated method stub
        switch (v.getId()) {
            case R.id.btn_login_login:
                // check validation
                if (isInputValid()) {
                    // request for login
                    getLogin();
                }
                break;
            case R.id.tv_login_forgot_password:
                // launch browser activity to load forgot password link
                launchBrowser();
                break;
            case R.id.tv_login_new_here_signup:
                // goto signup screen
                gotoSignupScreen();
                break;
        }
    }

    /**
     * check validation
     */
    private boolean isInputValid() {
        // TODO Auto-generated method stub
        boolean status = false;
        if (edtEmail.getText().toString().length() < 1) {
            // show toast
            String message = getResources().getString(R.string.strMsgEnterEmail);
            OnScreenMsg.showToast(getActivity(), message);
            return status;
        } else if (!Utils.isEmailValid(edtEmail.getText().toString())) {
            // show toast
            String message = getResources().getString(R.string.strMsgInvalidEmail);
            OnScreenMsg.showToast(getActivity(), message);
            return status;
        } else if (edtPassword.getText().toString().length() < 1) {
            // show toast
            String message = getResources().getString(R.string.strMsgEnterPassword);
            OnScreenMsg.showToast(getActivity(), message);
            return status;
        }
        return !status;
    }

    /**
     * request to get login
     */
    private void getLogin() {
        // TODO Auto-generated method stub
        /* show progressbar */
        ((SignupActivity)getActivity()).showProgressbar();
        /* call api */
        OfficewallService service = OfficewallServiceProvider.getService();
        service.login(getLoginRequestJson(), mCallback);
    }

    /**
     * creates http params json for login request.
     */
    private JsonObject getLoginRequestJson() {
        // TODO Auto-generated method stub
        // get data to pass with http request
        String request = HttpConstants.RQ_LOGIN;
        String email = edtEmail.getText().toString();
        String password = edtPassword.getText().toString();
        String app = getResources().getString(R.string.strAppLabel);
        String appVersion = getResources().getString(R.string.strAppVersion).replace("1.0",
                Utils.getAppVersion(getActivity()));
        String deviceInfo = Build.MODEL;

        // create json from data
        JsonObject objJson = new JsonObject();
        objJson.addProperty(HttpConstants.HTTP_RQ_TYPE, request);
        objJson.addProperty(HttpConstants.PARAM_LOGIN_EMAIL, email);
        objJson.addProperty(HttpConstants.PARAM_LOGIN_PASSWORD, password);
        objJson.addProperty(HttpConstants.PARAM_LOGIN_APP, app);
        objJson.addProperty(HttpConstants.PARAM_LOGIN_APP_VERSION, appVersion);
        objJson.addProperty(HttpConstants.PARAM_LOGIN_DEVICE_INFO, deviceInfo);

        return objJson;
    }

    /**
     * callback to handle login response
     */
    private CustomCallback<LoginRs> mCallback = new CustomCallback<LoginRs>() {

        @Override
        public void success(LoginRs loginRs, Response response) {
            // TODO Auto-generated method stub
            /* return if task is canceled */
            if (isCanceled()) {
                return;
            }
            /* hide progressbar */
            ((SignupActivity)getActivity()).hideProgressbar();
            /* handle result */
            if (loginRs.getResponseCode() == HttpConstants.RESULT_OK) {
                /* take action on success */
                // save data in prefrence
                SharedPreferences.Editor editor = OfficeWallApp.DefaultPref.edit();
                editor.putString(DefaultConstants.PREF_LOGIN_UID, loginRs.getUserId());
                editor.putString(DefaultConstants.PREF_LOGIN_OAUTH_KEY, loginRs.getOAuthKey());
                editor.putString(DefaultConstants.PREF_LOGIN_STATUS, DefaultConstants.STATUS_LOGIN);
                editor.commit();
                // goto wall screen
                gotoWallScreen();
            } else {
                // show error
                String message = loginRs.getUserMessage();
                ((SignupActivity)getActivity()).showStatus(HttpConstants.RESULT_ERROR, message);
            }
        }

        @Override
        public void failure(RetrofitError error) {
            // TODO Auto-generated method stub
            /* return if task is canceled */
            if (isCanceled()) {
                return;
            }
            /* hide progressbar */
            ((SignupActivity)getActivity()).hideProgressbar();
            // show error
            String message = error.getCause().getMessage();
            ((SignupActivity)getActivity()).showStatus(HttpConstants.RESULT_ERROR, message);
        }
    };

    /**
     * goto wall screen
     */
    private void gotoWallScreen() {
        // TODO Auto-generated method stub
        startActivity(new Intent(getActivity(), UserWallsActivity.class));
        getActivity().finish();
    }

    /**
     * launch browser activity to load forgot password link
     */
    private void launchBrowser() {
        // TODO Auto-generated method stub
        Intent browser = new Intent(Intent.ACTION_VIEW,
                Uri.parse(HttpConstants.LINK_FORGOT_PASSWORD));
        startActivity(browser);
    }

    /**
     * goto signup screen
     */
    private void gotoSignupScreen() {
        // TODO Auto-generated method stub
        Utils.loadFragmentInRoot(getActivity(), DefaultConstants.FRAGMENT_CONTAINER_ID,
                SignupFragment.class, null, DefaultConstants.FRAGMENT_TAG_SIGNUP);
    }

    /**
     * called on fragments view destroyed
     */
    @Override
    public void onDestroyView() {
        // TODO Auto-generated method stub
        super.onDestroyView();
        /* cancel request */
        mCallback.cancel();
        /* hide progressbar */
        ((SignupActivity)getActivity()).hideProgressbar();
    }

}
