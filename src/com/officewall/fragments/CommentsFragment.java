
package com.officewall.fragments;

import java.util.Arrays;
import java.util.List;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView.ScaleType;
import android.widget.AbsListView;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.JsonObject;
import com.officewall.activities.OfficeWallApp;
import com.officewall.activities.R;
import com.officewall.activities.UserWallsActivity;
import com.officewall.adapters.CommentsAdapter;
import com.officewall.constants.DefaultConstants;
import com.officewall.constants.HttpConstants;
import com.officewall.customs.PullToRefreshListView;
import com.officewall.customs.PullToRefreshListView.OnRefreshListener;
import com.officewall.dialog.MoreOptionsPopupDialog;
import com.officewall.dialog.MoreOptionsPopupDialog.MoreOptionsPopupItemClickListener;
import com.officewall.pojo.core.Comment;
import com.officewall.pojo.wrappers.GetCommentsRs;
import com.officewall.pojo.wrappers.ReportPostRs;
import com.officewall.pojo.wrappers.VoteCommentRs;
import com.officewall.retrofit.callback.CustomCallback;
import com.officewall.retrofit.service.OfficewallService;
import com.officewall.retrofit.service.OfficewallServiceProvider;
import com.officewall.utils.Utils;

public class CommentsFragment extends Fragment implements OnClickListener, OnItemClickListener,
        OnRefreshListener, OnScrollListener, MoreOptionsPopupItemClickListener {

    // views
    private ImageView ivHeader, ivClose;
    private PullToRefreshListView lvComments;
    private TextView tvSaySomething;

    // comment list
    private List<Comment> listComments;

    // comments adapter
    private CommentsAdapter adapterComments;

    // popup dialog
    private MoreOptionsPopupDialog moreOptionsPopupDialog;

    // start index to load more data
    private int START_FROM = 1;
    private boolean isLoading = false;

    // currently selected post id
    private String POST_ID;

    // header bg
    private String postBgColor;
    private String postBgImage;

    /**
     * called on fragment created
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setRetainInstance(true);

        // hide actionbar and set progressbar from the top
        ((UserWallsActivity)getActivity()).hideActionbarAndDrawer();
        ((UserWallsActivity)getActivity()).setPaddingTopToProgressbar();

        // get bundled data
        Bundle bundle = getArguments();
        if (bundle != null) {
            POST_ID = bundle.getString(DefaultConstants.EXTRA_POST_ID);
            postBgColor = bundle.getString(DefaultConstants.EXTRA_POST_BG_COLOR);
            postBgImage = bundle.getString(DefaultConstants.EXTRA_POST_BG_IMAGE);
        }

        // get comments
        getComments();
    }

    /**
     * request to get comments
     */
    private void getComments() {
        // TODO Auto-generated method stub
        /* load data from database to quick display */
        // get posts for specified post
        List<Comment> list = UserWallsActivity.dbHandler.getComments(POST_ID, START_FROM,
                DefaultConstants.MAX_TO_LOAD);
        if (list != null && list.size() != 0) {
            // add to the list
            if (listComments == null) {
                listComments = list;
            } else {
                listComments.addAll(list);
            }
            // bind list to the adapter
            if (adapterComments == null) {
                adapterComments = new CommentsAdapter(getActivity(), listComments);
            } else {
                adapterComments.notifyDataSetChanged(listComments);
            }
        }

        /* show progressbar */
        ((UserWallsActivity)getActivity()).showProgressbar();
        /* call api */
        OfficewallService service = OfficewallServiceProvider.getService();
        service.getComments(getCommentsRequestJson(), mCallback);
    }

    /**
     * creates http params json for comments request.
     */
    private JsonObject getCommentsRequestJson() {
        // TODO Auto-generated method stub
        // get data to pass with http request
        String request = HttpConstants.RQ_GET_COMMENTS;
        String uid = OfficeWallApp.DefaultPref.getString(DefaultConstants.PREF_LOGIN_UID, "");
        String oAuthKey = OfficeWallApp.DefaultPref.getString(
                DefaultConstants.PREF_LOGIN_OAUTH_KEY, "");
        String postId = POST_ID;
        int maxComments = DefaultConstants.MAX_TO_LOAD;
        int startFrom = START_FROM;

        // create json from data
        JsonObject objJson = new JsonObject();
        objJson.addProperty(HttpConstants.HTTP_RQ_TYPE, request);
        objJson.addProperty(HttpConstants.PARAM_UID, uid);
        objJson.addProperty(HttpConstants.PARAM_OAUTH_KEY, oAuthKey);
        objJson.addProperty(HttpConstants.PARAM_GET_COMMENT_POST_ID, postId);
        objJson.addProperty(HttpConstants.PARAM_GET_COMMENT_MAX_COMMENTS, maxComments);
        objJson.addProperty(HttpConstants.PARAM_GET_COMMENT_START_FROM, startFrom);

        return objJson;
    }

    /**
     * callback to handle comments response
     */
    private CustomCallback<GetCommentsRs> mCallback = new CustomCallback<GetCommentsRs>() {

        @Override
        public void success(GetCommentsRs getCommentsRs, Response response) {
            // TODO Auto-generated method stub
            /* return if task is canceled */
            if (isCanceled()) {
                return;
            }
            /* hide progressbar */
            ((UserWallsActivity)getActivity()).hideProgressbar();
            /* handle result */
            if (getCommentsRs.getResponseCode() == HttpConstants.RESULT_OK) {
                /* take action on success */
                List<Comment> list = getCommentsRs.getComments();
                if (list != null && list.size() != 0) {
                    // insert post into database
                    for (int i = 0; i < list.size(); i++) {
                        // get data
                        Comment comment = list.get(i);
                        int postId = Integer.parseInt(POST_ID);
                        int commentId = comment.getCommentId();
                        String text = comment.getText() == null ? "" : comment.getText();
                        String image = comment.getImage() == null ? "" : comment.getImage();
                        int isNew = comment.getNew() == null ? DefaultConstants.DEFAULT_INTEGER
                                : comment.getNew();
                        int upVoteCount = comment.getUpvotes() == null ? DefaultConstants.DEFAULT_INTEGER
                                : comment.getUpvotes();
                        int downVoteCount = comment.getDownvotes() == null ? DefaultConstants.DEFAULT_INTEGER
                                : comment.getDownvotes();
                        int vote = comment.getVote() == null ? DefaultConstants.DEFAULT_INTEGER
                                : comment.getVote();
                        int report = comment.getReport() == null ? DefaultConstants.DEFAULT_INTEGER
                                : comment.getReport();
                        UserWallsActivity.dbHandler.insertIntoComment(postId, commentId, text,
                                image, isNew, upVoteCount, downVoteCount, vote, report);
                    }

                    // add to the list
                    if (listComments == null) {
                        listComments = list;
                    } else {
                        for (int i = START_FROM - 1; i < list.size(); i++) {
                            if (i >= listComments.size()) {
                                listComments.add(list.get(i));
                            } else {
                                listComments.set(i, list.get(i));
                            }
                        }
                    }
                    // bind list to the adapter
                    if (adapterComments == null) {
                        adapterComments = new CommentsAdapter(getActivity(), listComments);
                        lvComments.setAdapter(adapterComments);
                    } else {
                        adapterComments.notifyDataSetChanged(listComments);
                    }
                }
            } else {
                // show error
                String message = getCommentsRs.getUserMessage();
                ((UserWallsActivity)getActivity()).showStatus(HttpConstants.RESULT_ERROR, message);
            }
            // reset loading flag on API call completed
            isLoading = false;
        }

        @Override
        public void failure(RetrofitError error) {
            // TODO Auto-generated method stub
            /* return if task is canceled */
            if (isCanceled()) {
                return;
            }
            /* hide progressbar */
            ((UserWallsActivity)getActivity()).hideProgressbar();
            // show error
            String message = error.getCause().getMessage();
            ((UserWallsActivity)getActivity()).showStatus(HttpConstants.RESULT_ERROR, message);
        }
    };

    /**
     * called on fragments view created
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        View mainView = inflater.inflate(R.layout.fragment_comments, container, false);

        // setup layout controls
        initializeControls(mainView);
        initializeActions();
        setHeaderBg();
        setData();

        return mainView;
    }

    /**
     * initialize controls
     */
    private void initializeControls(View mainView) {
        // TODO Auto-generated method stub
        // imageview
        ivHeader = (ImageView)mainView.findViewById(R.id.iv_comments_header);
        ivClose = (ImageView)mainView.findViewById(R.id.iv_comments_close);

        // listview
        lvComments = (PullToRefreshListView)mainView.findViewById(R.id.lv_comments);

        // textview
        tvSaySomething = (TextView)mainView.findViewById(R.id.tv_comments_say_something);
    }

    /**
     * initialize actions
     */
    private void initializeActions() {
        // TODO Auto-generated method stub
        // imageview
        ivClose.setOnClickListener(this);

        // listview
        lvComments.setOnItemClickListener(this);
        lvComments.setOnRefreshListener(this);
        lvComments.setOnScrollListener(this);

        // textview
        tvSaySomething.setOnClickListener(this);
    }

    /**
     * set header bg
     */
    private void setHeaderBg() {
        // TODO Auto-generated method stub
        if (postBgColor != null && !postBgColor.equals("")) {
            int index = Arrays.asList(getResources().getStringArray(R.array.arrPostColorCodes))
                    .indexOf(postBgColor);
            if (index != -1) {
                ivHeader.setScaleType(ScaleType.FIT_XY);
                ivHeader.setImageResource(getResources().obtainTypedArray(
                        R.array.typedArrCommentHeaderDrawables).getResourceId(index, 0));
            }
        } else {
            try {
                // required size
                int reqWidth, reqHeight;
                // get screen size
                DisplayMetrics dm = Utils.getScreenSize(getActivity());
                int sw = dm.widthPixels;
                // get bitmap
                Bitmap bitmap = Utils.decodeFromBase64(postBgImage.substring(postBgImage
                        .indexOf(",")));
                int bw = bitmap.getWidth();
                int bh = bitmap.getHeight();
                // calculate ratio to scale bitmap according
                reqWidth = sw;
                reqHeight = (bh * sw) / bw;
                // scale bitmap
                Bitmap scaled = Bitmap.createScaledBitmap(bitmap, reqWidth, reqHeight, true);
                int headerHeight = (int)getResources().getDimension(R.dimen.height_comment_header);
                // crop to header size
                Bitmap bmp = Bitmap.createBitmap(scaled, 0, 0, scaled.getWidth(), headerHeight);
                ivHeader.setImageBitmap(bmp);
                // clear bitmap
                bitmap.recycle();
                scaled.recycle();
                bitmap = scaled = null;
            } catch (Exception e) {
                // TODO: handle exception
                e.printStackTrace();
            }
        }
    }

    /**
     * set data
     */
    private void setData() {
        // TODO Auto-generated method stub
        // set adaper
        if (adapterComments != null) {
            lvComments.setAdapter(adapterComments);
        }

        // reset popup dialog instance
        moreOptionsPopupDialog = null;
    }

    /**
     * handles click event
     */
    @Override
    public void onClick(View v) {
        // TODO Auto-generated method stub
        switch (v.getId()) {
            case R.id.iv_comments_close:
                // go back
                getActivity().onBackPressed();
                break;
            case R.id.tv_comments_say_something:
                // goto add comments screen
                gotoAddCommentsScreen();
                break;
        }
    }

    /**
     * goto add comments screen
     */
    private void gotoAddCommentsScreen() {
        // TODO Auto-generated method stub
        // create bundle to pass data with fragment
        Bundle bundle = new Bundle();
        bundle.putString(DefaultConstants.EXTRA_POST_ID, POST_ID);
        bundle.putString(DefaultConstants.EXTRA_POST_BG_COLOR, postBgColor);
        bundle.putString(DefaultConstants.EXTRA_POST_BG_IMAGE, postBgImage);
        // load screen
        Utils.loadFragmentInBackstack(getActivity(), DefaultConstants.FRAGMENT_CONTAINER_ID,
                AddCommentFragment.class, bundle, DefaultConstants.FRAGMENT_TAG_ADD_COMMENT);
    }

    /**
     * handles listview refresh event
     */
    @Override
    public void onRefresh() {
        // TODO Auto-generated method stub
        // reset refresh
        lvComments.onRefreshComplete();
        lvComments.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (!isLoading) {
                    // update start counter
                    START_FROM += DefaultConstants.MAX_TO_LOAD;
                    // get comments
                    getComments();
                }
            }
        }, 0);
    }

    /**
     * handles refresh on scrolling
     */
    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {
        // TODO Auto-generated method stub
        if (!isLoading) {
            if (lvComments.getLastVisiblePosition() >= lvComments.getCount() - 1) {
                // set loading flag to true
                isLoading = true;
                // update start counter
                START_FROM += DefaultConstants.MAX_TO_LOAD;
                // get comments
                getComments();
            }
        }
    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount,
            int totalItemCount) {
        // TODO Auto-generated method stub
    }

    /**
     * handles listview item click event
     */
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        // TODO Auto-generated method stub
        if (Utils.isNetworkAvailable(getActivity())) {
            /* perform action */
            if (id == CommentsAdapter.Row.ITEM_UP_VOTE.ordinal()) {
                // up vote comment
                upVoteComment(position);
            } else if (id == CommentsAdapter.Row.ITEM_DOWN_VOTE.ordinal()) {
                // down vote comment
                downVoteComment(position);
            } else if (id == CommentsAdapter.Row.ITEM_REPORT.ordinal()) {
                // show more options dialog
                showMoreOptionsDialog(position, view);
            }
        } else {
            // show error
            String message = getResources().getString(R.string.strMsgErrorNoNetwork);
            ((UserWallsActivity)getActivity()).showStatus(HttpConstants.RESULT_ERROR, message);
        }
    }

    /**
     * up vote comment
     * 
     * @param position
     */
    private void upVoteComment(final int position) {
        // TODO Auto-generated method stub
        final Comment comment = listComments.get(position);
        final int vote = (comment.getVote() == null) ? DefaultConstants.DEFAULT_INTEGER : comment
                .getVote();
        final int upVote = (comment.getUpvotes() == null) ? DefaultConstants.DEFAULT_INTEGER
                : comment.getUpvotes();
        final int downVote = (comment.getDownvotes() == null) ? DefaultConstants.DEFAULT_INTEGER
                : comment.getDownvotes();
        if (vote != DefaultConstants.VOTE_UP) {
            // update data
            if (vote == DefaultConstants.VOTE_DOWN) {
                comment.setDownvotes(downVote - 1);
            }
            comment.setUpvotes(upVote + 1);
            comment.setVote(DefaultConstants.VOTE_UP);
            listComments.set(position, comment);
            adapterComments.notifyDataSetChanged(listComments);
            // request api
            OfficewallService service = OfficewallServiceProvider.getService();
            service.voteComment(getVoteCommentRequestJson(position), new Callback<VoteCommentRs>() {

                @Override
                public void success(VoteCommentRs voteCommentRs, Response response) {
                    // TODO Auto-generated method stub
                    // do nothing
                }

                @Override
                public void failure(RetrofitError error) {
                    // TODO Auto-generated method stub
                    // reset data
                    if (vote == DefaultConstants.VOTE_DOWN) {
                        comment.setDownvotes(downVote + 1);
                    }
                    comment.setUpvotes(upVote - 1);
                    comment.setVote(vote);
                    listComments.set(position, comment);
                    adapterComments.notifyDataSetChanged(listComments);
                }
            });
        }
    }

    /**
     * down vote comment
     * 
     * @param position
     */
    private void downVoteComment(final int position) {
        // TODO Auto-generated method stub
        final Comment comment = listComments.get(position);
        final int vote = (comment.getVote() == null) ? DefaultConstants.DEFAULT_INTEGER : comment
                .getVote();
        final int upVote = (comment.getUpvotes() == null) ? DefaultConstants.DEFAULT_INTEGER
                : comment.getUpvotes();
        final int downVote = (comment.getDownvotes() == null) ? DefaultConstants.DEFAULT_INTEGER
                : comment.getDownvotes();
        if (vote != DefaultConstants.VOTE_DOWN) {
            // update data
            if (vote == DefaultConstants.VOTE_UP) {
                comment.setUpvotes(upVote - 1);
            }
            comment.setDownvotes(downVote + 1);
            comment.setVote(DefaultConstants.VOTE_DOWN);
            listComments.set(position, comment);
            adapterComments.notifyDataSetChanged(listComments);
            // request api
            OfficewallService service = OfficewallServiceProvider.getService();
            service.voteComment(getVoteCommentRequestJson(position), new Callback<VoteCommentRs>() {

                @Override
                public void success(VoteCommentRs voteCommentRs, Response response) {
                    // TODO Auto-generated method stub
                    // do nothing
                }

                @Override
                public void failure(RetrofitError error) {
                    // TODO Auto-generated method stub
                    // reset data
                    if (vote == DefaultConstants.VOTE_UP) {
                        comment.setUpvotes(upVote + 1);
                    }
                    comment.setDownvotes(downVote - 1);
                    comment.setVote(vote);
                    listComments.set(position, comment);
                    adapterComments.notifyDataSetChanged(listComments);
                }
            });
        }
    }

    /**
     * creates http params json for vote comment request.
     * 
     * @param position
     */
    private JsonObject getVoteCommentRequestJson(int position) {
        // TODO Auto-generated method stub
        // get data to pass with http request
        String request = HttpConstants.RQ_VOTE_COMMENT;
        String uid = OfficeWallApp.DefaultPref.getString(DefaultConstants.PREF_LOGIN_UID, "");
        String oAuthKey = OfficeWallApp.DefaultPref.getString(
                DefaultConstants.PREF_LOGIN_OAUTH_KEY, "");
        String commentId = String.valueOf(listComments.get(position).getCommentId());
        String vote = String.valueOf(listComments.get(position).getVote());

        // create json from data
        JsonObject objJson = new JsonObject();
        objJson.addProperty(HttpConstants.HTTP_RQ_TYPE, request);
        objJson.addProperty(HttpConstants.PARAM_UID, uid);
        objJson.addProperty(HttpConstants.PARAM_OAUTH_KEY, oAuthKey);
        objJson.addProperty(HttpConstants.PARAM_VOTE_COMMENT_COMMENT_ID, commentId);
        objJson.addProperty(HttpConstants.PARAM_VOTE_COMMENT_VOTE, vote);

        return objJson;
    }

    /**
     * show more options dialog
     * 
     * @param position
     * @param view
     */
    private void showMoreOptionsDialog(int position, View view) {
        // TODO Auto-generated method stub
        // instantiate dialog if null
        if (moreOptionsPopupDialog == null) {
            moreOptionsPopupDialog = new MoreOptionsPopupDialog(getActivity());
            moreOptionsPopupDialog.createDialog();
            moreOptionsPopupDialog.setListener((MoreOptionsPopupItemClickListener)this);
        }

        // set data
        Comment comment = listComments.get(position);
        Integer report = comment.getReport() == null ? DefaultConstants.DEFAULT_INTEGER : comment
                .getReport();
        moreOptionsPopupDialog.setData(position, null, report);

        // show dialog
        moreOptionsPopupDialog.showDialogAtLocation(Utils.locateView(view));
    }

    @Override
    public void onSetStatus(int position, int statusCode) {
        // TODO Auto-generated method stub
    }

    /**
     * called on user action report abuse
     * 
     * @param position
     * @param reason
     * @param text
     */
    @Override
    public void onReportAbuse(final int position, final int reason, final String text) {
        // TODO Auto-generated method stub
        // request api
        OfficewallService service = OfficewallServiceProvider.getService();
        service.reportPost(getReportPostRequestJson(position, reason, text),
                new Callback<ReportPostRs>() {

                    @Override
                    public void success(ReportPostRs reportPostRs, Response response) {
                        // TODO Auto-generated method stub
                        // update item status on the screen
                        Comment comment = listComments.get(position);
                        comment.setReport(reason);
                        listComments.set(position, comment);
                    }

                    @Override
                    public void failure(RetrofitError error) {
                        // TODO Auto-generated method stub
                        // do nothing
                    }
                });
    }

    /**
     * creates http params json for vote post request.
     * 
     * @param position
     * @param reasonCode
     * @param reasonText
     * @return jsonObject
     */
    private JsonObject getReportPostRequestJson(int position, int reasonCode, String reasonText) {
        // TODO Auto-generated method stub
        // get data to pass with http request
        String request = HttpConstants.RQ_REPORT_COMMENT;
        String uid = OfficeWallApp.DefaultPref.getString(DefaultConstants.PREF_LOGIN_UID, "");
        String oAuthKey = OfficeWallApp.DefaultPref.getString(
                DefaultConstants.PREF_LOGIN_OAUTH_KEY, "");
        String commentId = String.valueOf(listComments.get(position).getCommentId());
        String reason = String.valueOf(reasonCode);
        String text = reasonText;

        // create json from data
        JsonObject objJson = new JsonObject();
        objJson.addProperty(HttpConstants.HTTP_RQ_TYPE, request);
        objJson.addProperty(HttpConstants.PARAM_UID, uid);
        objJson.addProperty(HttpConstants.PARAM_OAUTH_KEY, oAuthKey);
        objJson.addProperty(HttpConstants.PARAM_REPORT_COMMENT_COMMENT_ID, commentId);
        objJson.addProperty(HttpConstants.PARAM_REPORT_COMMENT_REASON, reason);
        objJson.addProperty(HttpConstants.PARAM_REPORT_COMMENT_TEXT, text);

        return objJson;
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
        ((UserWallsActivity)getActivity()).hideProgressbar();
    }

}
