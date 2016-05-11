
package com.officewall.fragments;

import retrofit.RetrofitError;
import retrofit.client.Response;
import android.graphics.Paint;
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
import com.officewall.activities.R;
import com.officewall.activities.SignupActivity;
import com.officewall.constants.DefaultConstants;
import com.officewall.constants.HttpConstants;
import com.officewall.pojo.wrappers.SignupRs;
import com.officewall.onscreenmessages.OnScreenMsg;
import com.officewall.retrofit.callback.CustomCallback;
import com.officewall.retrofit.service.OfficewallService;
import com.officewall.retrofit.service.OfficewallServiceProvider;
import com.officewall.utils.Utils;

public class SignupFragment extends Fragment implements OnClickListener {

    // views
    private EditText edtEmail;
    private Button btnSignup;
    private TextView tvAlreadyHaveAccount;

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
        View mainView = inflater.inflate(R.layout.fragment_signup, container, false);

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
        edtEmail = (EditText)mainView.findViewById(R.id.edt_signup_email);

        // buttons
        btnSignup = (Button)mainView.findViewById(R.id.btn_signup_signup);

        // textviews
        tvAlreadyHaveAccount = (TextView)mainView
                .findViewById(R.id.tv_signup_already_have_account_login);
    }

    /**
     * initialize actions
     */
    private void initializeActions() {
        // TODO Auto-generated method stub
        // buttons
        btnSignup.setOnClickListener(this);

        // textviews
        tvAlreadyHaveAccount.setOnClickListener(this);

        // set Underline flag to textview
        tvAlreadyHaveAccount.setPaintFlags(tvAlreadyHaveAccount.getPaintFlags()
                | Paint.UNDERLINE_TEXT_FLAG);
    }

    /**
     * handles click event
     */
    @Override
    public void onClick(View v) {
        // TODO Auto-generated method stub
        switch (v.getId()) {
            case R.id.btn_signup_signup:
                // check validation
                if (isInputValid()) {
                    // request to get sign up
                    getSignup();
                }
                break;
            case R.id.tv_signup_already_have_account_login:
                // goto login screen
                gotoLoginScreen();
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
        }
        return !status;
    }

    /**
     * request to get sign up
     */
    private void getSignup() {
        // TODO Auto-generated method stub
        /* show progressbar */
        ((SignupActivity)getActivity()).showProgressbar();
        /* call api */
        OfficewallService service = OfficewallServiceProvider.getService();
        service.signup(getSignupRequestJson(), mCallback);
    }

    /**
     * creates http params json for sign up request.
     */
    private JsonObject getSignupRequestJson() {
        // TODO Auto-generated method stub
        // get data to pass with http request
        String request = HttpConstants.RQ_SIGNUP;
        String email = edtEmail.getText().toString();
        String uid = "";
        String app = getResources().getString(R.string.strAppLabel);
        String appVersion = getResources().getString(R.string.strAppVersion).replace("1.0",
                Utils.getAppVersion(getActivity()));
        String deviceInfo = Build.MODEL;

        // create json from data
        JsonObject objJson = new JsonObject();
        objJson.addProperty(HttpConstants.HTTP_RQ_TYPE, request);
        objJson.addProperty(HttpConstants.PARAM_SIGNUP_EMAIL, email);
        objJson.addProperty(HttpConstants.PARAM_SIGNUP_UID, uid);
        objJson.addProperty(HttpConstants.PARAM_SIGNUP_APP, app);
        objJson.addProperty(HttpConstants.PARAM_SIGNUP_APP_VERSION, appVersion);
        objJson.addProperty(HttpConstants.PARAM_SIGNUP_DEVICE_INFO, deviceInfo);

        return objJson;
    }

    /**
     * callback to handle sign up response
     */
    private CustomCallback<SignupRs> mCallback = new CustomCallback<SignupRs>() {

        @Override
        public void success(SignupRs signupRs, Response response) {
            // TODO Auto-generated method stub
            /* return if task is canceled */
            if (isCanceled()) {
                return;
            }
            /* hide progressbar */
            ((SignupActivity)getActivity()).hideProgressbar();
            /* handle result */
            if (signupRs.getResponseCode() == HttpConstants.RESULT_OK) {
                /* take action on success */
                // show status
                String message = signupRs.getUserMessage();
                OnScreenMsg.showToast(getActivity(), message);
                // ((SignupActivity)getActivity()).showStatus(HttpConstants.RESULT_OK,
                // message);
                // goto login screen
                gotoLoginScreen();
            } else {
                // show error
                String message = signupRs.getUserMessage();
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
     * goto login screen
     */
    private void gotoLoginScreen() {
        // TODO Auto-generated method stub
        Utils.loadFragmentInRoot(getActivity(), DefaultConstants.FRAGMENT_CONTAINER_ID,
                LoginFragment.class, null, DefaultConstants.FRAGMENT_TAG_LOGIN);
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
