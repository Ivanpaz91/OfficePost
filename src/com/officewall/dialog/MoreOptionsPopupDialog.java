
package com.officewall.dialog;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.officewall.activities.R;
import com.officewall.constants.DefaultConstants;
import com.officewall.onscreenmessages.OnScreenMsg;
import com.officewall.utils.Utils;

public class MoreOptionsPopupDialog implements OnClickListener {

    private Context mContext;
    private Dialog mDialog;
    private MoreOptionsPopupItemClickListener mListener;

    // Default options view
    private View viewDefaultOptions;
    private LinearLayout llSubscribe, llReportAbuse, llHide;
    private TextView tvSubscribe, tvReportAbuse, tvHide;
    private ImageView ivSubscribeSelect, ivReportAbuseSelect, ivHideSelect;

    // Report abuse reasons view
    private View viewReportAbuseReasons;
    private LinearLayout llSpam, llPersonalHate, llSexuallyExplicit, llIllegal, llOther;
    private ImageView ivSpamSelect, ivPersonalHateSelect, ivSexuallyExplicitSelect,
            ivIllegalSelect;

    // Other reasons view
    private View viewOtherReason;
    private EditText edtReason;
    private Button btnReport;

    // Row position, from where Popup is initiated
    private int mPosition;

    // status data
    private Integer mSubscribe, mReport;

    /**
     * constructor
     * 
     * @param context
     */
    public MoreOptionsPopupDialog(Context context) {
        // TODO Auto-generated constructor stub
        mContext = context;
    }

    /**
     * create dialog
     */
    public void createDialog() {
        // TODO Auto-generated method stub
        // instantiate dialog
        mDialog = new Dialog(mContext);
        mDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        mDialog.setContentView(R.layout.popup_more_options);
        mDialog.getWindow().setBackgroundDrawable(
                new ColorDrawable(android.graphics.Color.TRANSPARENT));
        mDialog.setCancelable(true);

        // setup layout controls
        initializeControls();
        initializeActions();
    }

    /**
     * set data
     * 
     * @param position
     * @param subscribe
     * @param report
     */
    public void setData(int position, Integer subscribe, Integer report) {
        // TODO Auto-generated method stub
        /* Row position */
        mPosition = position;

        /* status data */
        mSubscribe = subscribe;
        mReport = report;

        /* Default options */
        // Subscribe
        if (subscribe != null) {
            if (subscribe != DefaultConstants.DEFAULT_INTEGER) {
                tvSubscribe.setText(mContext.getResources().getString(R.string.strSubscribed));
                ivSubscribeSelect.setVisibility(View.VISIBLE);
            } else {
                tvSubscribe.setText(mContext.getResources().getString(R.string.strSubscribe));
                ivSubscribeSelect.setVisibility(View.GONE);
            }
        }
        // Report abuse
        if (report != DefaultConstants.DEFAULT_INTEGER) {
            tvReportAbuse.setText(mContext.getResources().getString(R.string.strReportedAbuse));
            ivReportAbuseSelect.setVisibility(View.VISIBLE);
        } else {
            tvReportAbuse.setText(mContext.getResources().getString(R.string.strReportAbuse));
            ivReportAbuseSelect.setVisibility(View.GONE);
        }

        /* reset popup */
        // set empty to edittext
        edtReason.setText("");
        // hide view
        viewReportAbuseReasons.setVisibility(View.GONE);
        viewOtherReason.setVisibility(View.GONE);
        // show view
        viewDefaultOptions.setVisibility(View.VISIBLE);

        /*
         * subscribe == null i.e. popup for Comments hence go directly to the
         * Report abuse view
         */
        if (subscribe == null) {
            llReportAbuse.performClick();
        }
    }

    /**
     * show dialog at specific position on the screen
     * 
     * @param location
     */
    public void showDialogAtLocation(Rect location) {
        // TODO Auto-generated method stub
        WindowManager.LayoutParams wmlp = mDialog.getWindow().getAttributes();
        DisplayMetrics screen = Utils.getScreenSize(mContext);
        wmlp.gravity = Gravity.BOTTOM | Gravity.RIGHT;
        wmlp.x = screen.widthPixels - location.left
                - mContext.getResources().getInteger(R.integer.arrow_from_right_px);
        wmlp.y = screen.heightPixels - location.top;

        // show dialog
        mDialog.show();
    }

    /**
     * initialize controls
     */
    private void initializeControls() {
        // TODO Auto-generated method stub
        /* Default options */
        // container view
        viewDefaultOptions = mDialog.findViewById(R.id.view_default_options);
        // layouts
        llSubscribe = (LinearLayout)viewDefaultOptions
                .findViewById(R.id.ll_popup_more_options_subscribe);
        llReportAbuse = (LinearLayout)viewDefaultOptions
                .findViewById(R.id.ll_popup_more_options_report_abuse);
        llHide = (LinearLayout)viewDefaultOptions.findViewById(R.id.ll_popup_more_options_hide);
        // textviews
        tvSubscribe = (TextView)viewDefaultOptions
                .findViewById(R.id.tv_popup_more_options_subscribe);
        tvReportAbuse = (TextView)viewDefaultOptions
                .findViewById(R.id.tv_popup_more_options_report_abuse);
        tvHide = (TextView)viewDefaultOptions.findViewById(R.id.tv_popup_more_options_hide);
        // imageviews
        ivSubscribeSelect = (ImageView)viewDefaultOptions
                .findViewById(R.id.iv_popup_more_options_subscribe_select);
        ivReportAbuseSelect = (ImageView)viewDefaultOptions
                .findViewById(R.id.iv_popup_more_options_report_abuse_select);
        ivHideSelect = (ImageView)viewDefaultOptions
                .findViewById(R.id.iv_popup_more_options_hide_select);

        /* Report abuse reasons */
        // container view
        viewReportAbuseReasons = mDialog.findViewById(R.id.view_report_abuse_reason);
        // layouts
        llSpam = (LinearLayout)viewReportAbuseReasons
                .findViewById(R.id.ll_report_abuse_reason_spam);
        llPersonalHate = (LinearLayout)viewReportAbuseReasons
                .findViewById(R.id.ll_report_abuse_reason_personal_hate);
        llSexuallyExplicit = (LinearLayout)viewReportAbuseReasons
                .findViewById(R.id.ll_report_abuse_reason_sexually_explicit);
        llIllegal = (LinearLayout)viewReportAbuseReasons
                .findViewById(R.id.ll_report_abuse_reason_illegal);
        llOther = (LinearLayout)viewReportAbuseReasons
                .findViewById(R.id.ll_report_abuse_reason_other);
        // imageviews
        ivSpamSelect = (ImageView)viewReportAbuseReasons
                .findViewById(R.id.iv_report_abuse_reason_spam_select);
        ivPersonalHateSelect = (ImageView)viewReportAbuseReasons
                .findViewById(R.id.iv_report_abuse_reason_personal_hate_select);
        ivSexuallyExplicitSelect = (ImageView)viewReportAbuseReasons
                .findViewById(R.id.iv_report_abuse_reason_sexually_explicit_select);
        ivIllegalSelect = (ImageView)viewReportAbuseReasons
                .findViewById(R.id.iv_report_abuse_reason_illegal_select);

        /* Other reasons */
        // container view
        viewOtherReason = mDialog.findViewById(R.id.view_report_abuse_reason_other);
        // edittext
        edtReason = (EditText)viewOtherReason
                .findViewById(R.id.edt_report_abuse_reason_other_reason);
        // button
        btnReport = (Button)viewOtherReason.findViewById(R.id.btn_report_abuse_reason_other_report);
    }

    /**
     * initialize actions
     */
    private void initializeActions() {
        // TODO Auto-generated method stub
        /* Default options */
        // layouts
        llSubscribe.setOnClickListener(this);
        llReportAbuse.setOnClickListener(this);
        llHide.setOnClickListener(this);

        /* Report abuse reasons */
        // layouts
        llSpam.setOnClickListener(this);
        llPersonalHate.setOnClickListener(this);
        llSexuallyExplicit.setOnClickListener(this);
        llIllegal.setOnClickListener(this);
        llOther.setOnClickListener(this);

        /* Other reasons */
        // layouts
        btnReport.setOnClickListener(this);
    }

    /**
     * handles click event
     */
    @Override
    public void onClick(View v) {
        // TODO Auto-generated method stub
        switch (v.getId()) {
            case R.id.ll_popup_more_options_subscribe:
                // user action Subscribe
                onSubscribe();
                break;
            case R.id.ll_popup_more_options_report_abuse:
                // show Report abuse view
                showReportAbuseView();
                break;
            case R.id.ll_popup_more_options_hide:
                // user action Hide
                onHide();
                break;
            case R.id.ll_report_abuse_reason_spam:
                // user action Report spam
                onReportAbuse(ivSpamSelect, DefaultConstants.REPORT_SPAM, "");
                break;
            case R.id.ll_report_abuse_reason_personal_hate:
                // user action Report hate
                onReportAbuse(ivPersonalHateSelect, DefaultConstants.REPORT_PERSONAL_HATE, "");
                break;
            case R.id.ll_report_abuse_reason_sexually_explicit:
                // user action Report sexually explicit
                onReportAbuse(ivSexuallyExplicitSelect, DefaultConstants.REPORT_SEXUALLY_EXPLICIT,
                        "");
                break;
            case R.id.ll_report_abuse_reason_illegal:
                // user action Report illegal
                onReportAbuse(ivIllegalSelect, DefaultConstants.REPORT_ILLEGAL, "");
                break;
            case R.id.ll_report_abuse_reason_other:
                // show other reason view
                showOtherReasonView();
                break;
            case R.id.btn_report_abuse_reason_other_report:
                // user action Report other reason
                onReportAbuse(null, DefaultConstants.REPORT_PERSONAL_HATE, edtReason.getText()
                        .toString());
                break;
        }
    }

    /**
     * user action Subscribe
     */
    private void onSubscribe() {
        // TODO Auto-generated method stub
        if (mSubscribe == DefaultConstants.DEFAULT_INTEGER) {
            tvSubscribe.setText(mContext.getResources().getString(R.string.strSubscribed));
            ivSubscribeSelect.setVisibility(View.VISIBLE);
            if (mListener != null) {
                mListener.onSetStatus(mPosition, DefaultConstants.SET_STATUS_SUBSCRIBE);
            }
            // hide dialog
            mDialog.cancel();
        } else {
            String message = "You already subscribed this Post!";
            OnScreenMsg.showToast(mContext, message);
        }
    }

    /**
     * show Report abuse view
     */
    private void showReportAbuseView() {
        // TODO Auto-generated method stub
        if (mReport == DefaultConstants.DEFAULT_INTEGER) {
            // hide view
            viewDefaultOptions.setVisibility(View.GONE);
            // show view
            viewReportAbuseReasons.setVisibility(View.VISIBLE);
        } else {
            String message = "You already reported Abuse this Post!";
            OnScreenMsg.showToast(mContext, message);
        }
    }

    /**
     * user action Hide
     */
    private void onHide() {
        // TODO Auto-generated method stub
        tvHide.setText(mContext.getResources().getString(R.string.strHiden));
        ivHideSelect.setVisibility(View.VISIBLE);
        if (mListener != null) {
            mListener.onSetStatus(mPosition, DefaultConstants.SET_STATUS_HIDE);
        }
        // hide dialog
        mDialog.cancel();
    }

    /**
     * user action Report abuse
     * 
     * @param ivSelect
     * @param reason
     * @param text
     */
    private void onReportAbuse(ImageView ivSelect, int reason, String text) {
        // TODO Auto-generated method stub
        if (ivSelect != null) {
            ivSelect.setVisibility(View.VISIBLE);
        }
        if (mListener != null) {
            mListener.onReportAbuse(mPosition, reason, text);
        }
        // hide dialog
        mDialog.cancel();
    }

    /**
     * show other reason view
     */
    private void showOtherReasonView() {
        // TODO Auto-generated method stub
        // hide view
        viewReportAbuseReasons.setVisibility(View.GONE);
        // show view
        viewOtherReason.setVisibility(View.VISIBLE);
    }

    /**
     * listener for sending result back to caller
     */
    public interface MoreOptionsPopupItemClickListener {
        public abstract void onSetStatus(int position, int status);

        public abstract void onReportAbuse(int position, int reason, String text);
    }

    /**
     * set listener
     * 
     * @param listener
     */
    public void setListener(MoreOptionsPopupItemClickListener listener) {
        // TODO Auto-generated method stub
        mListener = listener;
    }

}
