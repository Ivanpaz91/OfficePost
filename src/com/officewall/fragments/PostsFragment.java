
package com.officewall.fragments;

import java.util.ArrayList;
import java.util.List;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import android.content.ContentResolver;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.provider.ContactsContract;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AbsListView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.google.gson.JsonObject;
import com.officewall.activities.OfficeWallApp;
import com.officewall.activities.R;
import com.officewall.activities.UserWallsActivity;
import com.officewall.adapters.InviteFriendsContactsAdapter;
import com.officewall.adapters.PostsAdapter;
import com.officewall.constants.DefaultConstants;
import com.officewall.constants.HttpConstants;
import com.officewall.customs.PullToRefreshListView;
import com.officewall.customs.PullToRefreshListView.OnRefreshListener;
import com.officewall.dialog.MoreOptionsPopupDialog;
import com.officewall.dialog.MoreOptionsPopupDialog.MoreOptionsPopupItemClickListener;
import com.officewall.pojo.core.Contact;
import com.officewall.pojo.wrappers.GetPostsRs;
import com.officewall.pojo.wrappers.InviteFriendsRs;
import com.officewall.pojo.core.Post;
import com.officewall.pojo.wrappers.ReportPostRs;
import com.officewall.pojo.wrappers.SetPostStatusRs;
import com.officewall.pojo.wrappers.VotePostRs;
import com.officewall.retrofit.callback.CustomCallback;
import com.officewall.retrofit.service.OfficewallService;
import com.officewall.retrofit.service.OfficewallServiceProvider;
import com.officewall.utils.Utils;

public class PostsFragment extends Fragment implements OnItemClickListener, OnScrollListener,
        OnRefreshListener, OnClickListener, MoreOptionsPopupItemClickListener {

    /* Posts */
    // views
    private PullToRefreshListView lvPosts;

    // wall post list
    private List<Post> listPosts;

    // wall post adapter
    private PostsAdapter adapterPosts;

    // popup dialog
    private MoreOptionsPopupDialog moreOptionsPopupDialog;

    // start index to load more data
    private int START_FROM = 1;
    private boolean isLoading = false;

    /* Invite friends */
    // views
    private LinearLayout llInviteFriends;
    private TextView tvInactiveWallNote, tvSendInvitationNote, tvSelectAll, tvSelectNone,
            tvLoadingContacts;
    private ListView lvContacts;
    private Button btnInvite;

    // contact list
    private List<Contact> listContact;

    // contact adapter
    private InviteFriendsContactsAdapter adapterContacts;

    /**
     * called on fragment created
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setRetainInstance(true);

        // get posts
        getPosts();
    }

    /**
     * request to get posts
     */
    private void getPosts() {
        // TODO Auto-generated method stub
        /* load data from database to quick display */
        String wallId = UserWallsActivity.SELECTED_WALL_ID;
        // get posts for specified wall
        List<Post> list = UserWallsActivity.dbHandler.getPosts(wallId, START_FROM,
                DefaultConstants.MAX_TO_LOAD);
        if (list != null && list.size() != 0) {
            // add to the list
            if (listPosts == null) {
                listPosts = list;
            } else {
                listPosts.addAll(list);
            }
            // bind list to the adapter
            if (adapterPosts == null) {
                adapterPosts = new PostsAdapter(getActivity(), listPosts);
            } else {
                adapterPosts.notifyDataSetChanged(listPosts);
            }
        }

        /* show progressbar */
        ((UserWallsActivity)getActivity()).showProgressbar();
        /* call api */
        OfficewallService service = OfficewallServiceProvider.getService();
        service.getPosts(getPostsRequestJson(), mPostsCallback);
    }

    /**
     * creates http params json for posts request.
     */
    private JsonObject getPostsRequestJson() {
        // TODO Auto-generated method stub
        // get data to pass with http request
        String request = HttpConstants.RQ_GET_POSTS;
        String uid = OfficeWallApp.DefaultPref.getString(DefaultConstants.PREF_LOGIN_UID, "");
        String oAuthKey = OfficeWallApp.DefaultPref.getString(
                DefaultConstants.PREF_LOGIN_OAUTH_KEY, "");
        String wallId = UserWallsActivity.SELECTED_WALL_ID;
        int maxPosts = DefaultConstants.MAX_TO_LOAD;
        int startFrom = START_FROM;

        // create json from data
        JsonObject objJson = new JsonObject();
        objJson.addProperty(HttpConstants.HTTP_RQ_TYPE, request);
        objJson.addProperty(HttpConstants.PARAM_UID, uid);
        objJson.addProperty(HttpConstants.PARAM_OAUTH_KEY, oAuthKey);
        objJson.addProperty(HttpConstants.PARAM_GET_POST_WALL_ID, wallId);
        objJson.addProperty(HttpConstants.PARAM_GET_POST_MAX_POSTS, maxPosts);
        objJson.addProperty(HttpConstants.PARAM_GET_POST_START_FROM, startFrom);

        return objJson;
    }

    /**
     * callback to handle posts response
     */
    private CustomCallback<GetPostsRs> mPostsCallback = new CustomCallback<GetPostsRs>() {

        @Override
        public void success(GetPostsRs getPostsRs, Response response) {
            // TODO Auto-generated method stub
            /* return if task is canceled */
            if (isCanceled()) {
                return;
            }
            /* hide progressbar */
            ((UserWallsActivity)getActivity()).hideProgressbar();
            /* handle result */
            if (getPostsRs.getResponseCode() == HttpConstants.RESULT_OK) {
                /* take action on success */
                List<Post> list = getPostsRs.getPosts();
                if (list != null && list.size() != 0) {

                    /* insert post into database */
                    for (int i = 0; i < list.size(); i++) {
                        // get data
                        Post post = list.get(i);
                        String wallId = UserWallsActivity.SELECTED_WALL_ID;
                        int postId = post.getPostId();
                        String text = post.getText() == null ? "" : post.getText();
                        String image = post.getImage() == null ? "" : post.getImage();
                        String bgColor = post.getBgColor() == null ? "" : post.getBgColor();
                        int isNew = post.getNew() == null ? DefaultConstants.DEFAULT_INTEGER : post
                                .getNew();
                        int upVoteCount = post.getUpvotes() == null ? DefaultConstants.DEFAULT_INTEGER
                                : post.getUpvotes();
                        int downVoteCount = post.getDownvotes() == null ? DefaultConstants.DEFAULT_INTEGER
                                : post.getDownvotes();
                        int totalComment = post.getTotalComments() == null ? DefaultConstants.DEFAULT_INTEGER
                                : post.getTotalComments();
                        int newComment = post.getNewComments() == null ? DefaultConstants.DEFAULT_INTEGER
                                : post.getNewComments();
                        int vote = post.getVote() == null ? DefaultConstants.DEFAULT_INTEGER : post
                                .getVote();
                        int report = post.getReport() == null ? DefaultConstants.DEFAULT_INTEGER
                                : post.getReport();
                        int status = post.getStatus() == null ? DefaultConstants.DEFAULT_INTEGER
                                : post.getStatus();
                        UserWallsActivity.dbHandler.insertIntoPost(wallId, postId, text, image,
                                bgColor, isNew, upVoteCount, downVoteCount, totalComment,
                                newComment, vote, report, status);
                    }

                    // add to the list
                    if (listPosts == null) {
                        listPosts = list;
                    } else {
                        for (int i = START_FROM - 1; i < list.size(); i++) {
                            if (i >= listPosts.size()) {
                                listPosts.add(list.get(i));
                            } else {
                                listPosts.set(i, list.get(i));
                            }
                        }
                    }
                    // bind list to the adapter
                    if (adapterPosts == null) {
                        adapterPosts = new PostsAdapter(getActivity(), listPosts);
                        lvPosts.setAdapter(adapterPosts);
                    } else {
                        adapterPosts.notifyDataSetChanged(listPosts);
                    }

                    // show posts view
                    showPostsView();
                } else {
                    // show invite friends view
                    showInviteFriendsView();
                    // get contacts from device
                    getContacts();
                }
            } else {
                // show error
                String message = getPostsRs.getUserMessage();
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
        View mainView = inflater.inflate(R.layout.fragment_posts, container, false);

        // setup layout controls
        initializeControls(mainView);
        initializeActions();
        setData();

        return mainView;
    }

    /**
     * initialize controls
     */
    private void initializeControls(View mainView) {
        // TODO Auto-generated method stub
        /* posts view */
        // listview
        lvPosts = (PullToRefreshListView)mainView.findViewById(R.id.lv_posts);

        /* invite friends view */
        // layout
        llInviteFriends = (LinearLayout)mainView.findViewById(R.id.ll_invite_friends);

        // textview
        tvInactiveWallNote = (TextView)mainView
                .findViewById(R.id.tv_invite_friends_inactive_wall_note);
        tvSendInvitationNote = (TextView)mainView
                .findViewById(R.id.tv_invite_friends_send_invitation_note);
        tvSelectAll = (TextView)mainView.findViewById(R.id.tv_invite_friends_select_all);
        tvSelectNone = (TextView)mainView.findViewById(R.id.tv_invite_friends_select_none);
        tvLoadingContacts = (TextView)mainView
                .findViewById(R.id.tv_invite_friends_loading_contacts);

        // listview
        lvContacts = (ListView)mainView.findViewById(R.id.lv_invite_friends_contacts);

        // button
        btnInvite = (Button)mainView.findViewById(R.id.btn_invite_friends_invite);
    }

    /**
     * initialize actions
     */
    private void initializeActions() {
        // TODO Auto-generated method stub
        /* Posts */
        // listview
        lvPosts.setOnItemClickListener(this);
        lvPosts.setOnRefreshListener(this);
        lvPosts.setOnScrollListener(this);

        /* Invite friends */
        // textview
        tvSelectAll.setOnClickListener(this);
        tvSelectNone.setOnClickListener(this);

        // button
        btnInvite.setOnClickListener(this);
    }

    /**
     * set data
     */
    private void setData() {
        // TODO Auto-generated method stub
        // check if posts found
        if (adapterPosts != null) {
            // set adaper
            lvPosts.setAdapter(adapterPosts);
            // show posts view
            showPostsView();
        } else if (adapterContacts != null) {
            // set adaper
            lvContacts.setAdapter(adapterContacts);
            // show invite friends view
            showInviteFriendsView();
        }

        // reset popup dialog instance
        moreOptionsPopupDialog = null;
    }

    /**
     * show posts view
     */
    private void showPostsView() {
        // TODO Auto-generated method stub
        lvPosts.setVisibility(View.VISIBLE);
    }

    /**
     * show invite friends view
     */
    private void showInviteFriendsView() {
        // TODO Auto-generated method stub
        // set data
        tvInactiveWallNote.setText(Html.fromHtml(tvInactiveWallNote.getText().toString()
                .replace("_", UserWallsActivity.SELECTED_WALL_NAME)));
        tvSendInvitationNote.setText(Html.fromHtml(tvSendInvitationNote.getText().toString()
                .replace("_", UserWallsActivity.SELECTED_WALL_NAME)));
        // show view
        llInviteFriends.setVisibility(View.VISIBLE);
    }

    /**
     * get contacts from device
     */
    private void getContacts() {
        // TODO Auto-generated method stub
        (new AsyncTask<Void, Void, Void>() {

            Handler mHandler;
            List<Contact> list;

            @Override
            protected void onPreExecute() {
                // TODO Auto-generated method stub
                super.onPreExecute();
                // show loading textview
                tvLoadingContacts.setVisibility(View.VISIBLE);
                // instantiate contact list
                list = new ArrayList<Contact>();
                // instantiate handler
                mHandler = new Handler();
            }

            @Override
            protected Void doInBackground(Void... params) {
                // TODO Auto-generated method stub
                try {
                    // get content resolver
                    ContentResolver cr = getActivity().getBaseContext().getContentResolver();
                    // query to read contacts
                    Cursor cursor = cr.query(ContactsContract.Contacts.CONTENT_URI, null, null,
                            null, null);
                    if (cursor.getCount() > 0) {
                        while (cursor.moveToNext()) {
                            /* get contact data */
                            String contactId = cursor.getString(cursor
                                    .getColumnIndex(ContactsContract.Contacts._ID));
                            String contactName = cursor.getString(cursor
                                    .getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
                            String contactEmail = null;
                            // query to get emails
                            Cursor emails = cr.query(
                                    ContactsContract.CommonDataKinds.Email.CONTENT_URI, null,
                                    ContactsContract.CommonDataKinds.Email.CONTACT_ID + " = "
                                            + contactId, null, null);
                            if (emails.getCount() > 0) {
                                emails.moveToFirst();
                                // get email
                                contactEmail = emails
                                        .getString(emails
                                                .getColumnIndex(ContactsContract.CommonDataKinds.Email.DATA));
                                // check email and set if not empty
                                if (contactEmail != null && !contactEmail.equals("")) {
                                    /* set data */
                                    Contact contact = new Contact();
                                    contact.setName(contactName);
                                    contact.setEmail(contactEmail);
                                    list.add(contact);
                                    /* set adapter */
                                    setContactAdapter();
                                }
                            }
                            // close emails cursor
                            emails.close();
                        }
                    }
                    // close cursor
                    cursor.close();

                } catch (Exception e) {
                    e.printStackTrace();
                }
                return null;
            }

            private void setContactAdapter() {
                // TODO Auto-generated method stub
                mHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        // TODO Auto-generated method stub
                        // update list
                        listContact = list;
                        // set adapter
                        if (adapterContacts == null) {
                            // hide loading text
                            tvLoadingContacts.setVisibility(View.GONE);
                            // instantiate adapter
                            adapterContacts = new InviteFriendsContactsAdapter(getActivity(),
                                    listContact);
                            lvContacts.setAdapter(adapterContacts);
                        } else {
                            adapterContacts.notifyDataSetChanged(listContact);
                        }
                    }
                }, 500);
            }

        }).execute();
    }

    /**
     * handles click event
     */
    @Override
    public void onClick(View v) {
        // TODO Auto-generated method stub
        switch (v.getId()) {
            case R.id.tv_invite_friends_select_all:
                // select all
                if (adapterContacts != null) {
                    adapterContacts.selectAll();
                }
                break;
            case R.id.tv_invite_friends_select_none:
                // select none
                if (adapterContacts != null) {
                    adapterContacts.selectNone();
                }
                break;
            case R.id.btn_invite_friends_invite:
                // invite anonymously
                inviteFriends();
                break;
        }
    }

    /**
     * invite friends
     */
    private void inviteFriends() {
        // TODO Auto-generated method stub
        String selectedEmails = adapterContacts.getSelectedContacts();
        if (selectedEmails != null) {
            /* show progressbar */
            ((UserWallsActivity)getActivity()).showProgressbar();
            /* call api */
            OfficewallService service = OfficewallServiceProvider.getService();
            service.inviteFriends(getInviteFriendsRequestJson(selectedEmails),
                    mInviteFriendsCallback);
        } else {
            // show error
            String message = "Select any contact to send Invitation!";
            ((UserWallsActivity)getActivity()).showStatus(HttpConstants.RESULT_ERROR, message);
        }
    }

    /**
     * creates http params json for invite friends request.
     * 
     * @param selectedEmails
     */
    private JsonObject getInviteFriendsRequestJson(String selectedEmails) {
        // TODO Auto-generated method stub
        // get data to pass with http request
        String request = HttpConstants.RQ_INVITE_FRIENDS;
        String uid = OfficeWallApp.DefaultPref.getString(DefaultConstants.PREF_LOGIN_UID, "");
        String oAuthKey = OfficeWallApp.DefaultPref.getString(
                DefaultConstants.PREF_LOGIN_OAUTH_KEY, "");
        String emails = "[" + selectedEmails + "]";

        // create json from data
        JsonObject objJson = new JsonObject();
        objJson.addProperty(HttpConstants.HTTP_RQ_TYPE, request);
        objJson.addProperty(HttpConstants.PARAM_UID, uid);
        objJson.addProperty(HttpConstants.PARAM_OAUTH_KEY, oAuthKey);
        objJson.addProperty(HttpConstants.PARAM_INVITE_FRIENDS_EMAILS, emails);

        return objJson;
    }

    /**
     * callback to handle posts response
     */
    private CustomCallback<InviteFriendsRs> mInviteFriendsCallback = new CustomCallback<InviteFriendsRs>() {

        @Override
        public void success(InviteFriendsRs inviteFriendsRs, Response response) {
            // TODO Auto-generated method stub
            /* return if task is canceled */
            if (isCanceled()) {
                return;
            }
            /* hide progressbar */
            ((UserWallsActivity)getActivity()).hideProgressbar();
            /* handle result */
            if (inviteFriendsRs.getResponseCode() == HttpConstants.RESULT_OK) {
                /* take action on success */
                // show success status
                String message = "Invitation sent successfully.";
                ((UserWallsActivity)getActivity()).showStatus(HttpConstants.RESULT_OK, message);
            } else {
                // show error
                String message = inviteFriendsRs.getUserMessage();
                ((UserWallsActivity)getActivity()).showStatus(HttpConstants.RESULT_ERROR, message);
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
            ((UserWallsActivity)getActivity()).hideProgressbar();
            // show error
            String message = error.getCause().getMessage();
            ((UserWallsActivity)getActivity()).showStatus(HttpConstants.RESULT_ERROR, message);
        }
    };

    /**
     * handles listview refresh event
     */
    @Override
    public void onRefresh() {
        // TODO Auto-generated method stub
        // reset refresh
        lvPosts.onRefreshComplete();
        lvPosts.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (!isLoading) {
                    // set loading flag to true
                    isLoading = true;
                    // update start counter
                    START_FROM += DefaultConstants.MAX_TO_LOAD;
                    // get posts
                    getPosts();
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
            if (lvPosts.getLastVisiblePosition() >= lvPosts.getCount() - 1) {
                // set loading flag to true
                isLoading = true;
                // update start counter
                START_FROM += DefaultConstants.MAX_TO_LOAD;
                // get posts
                getPosts();
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
            if (id == PostsAdapter.Row.ROW.ordinal()
                    || id == PostsAdapter.Row.ITEM_COMMENT.ordinal()) {
                // goto comments screen
                gotoCommentsScreen(position);
            } else if (id == PostsAdapter.Row.ITEM_MORE_OPTIONS.ordinal()) {
                // show more options dialog
                showMoreOptionsDialog(position, view);
            } else if (id == PostsAdapter.Row.ITEM_UP_VOTE.ordinal()) {
                // up vote post
                upVotePost(position);
            } else if (id == PostsAdapter.Row.ITEM_DOWN_VOTE.ordinal()) {
                // down vote post
                downVotePost(position);
            }
        } else {
            // show error
            String message = getResources().getString(R.string.strMsgErrorNoNetwork);
            ((UserWallsActivity)getActivity()).showStatus(HttpConstants.RESULT_ERROR, message);
        }
    }

    /**
     * goto comments screen
     * 
     * @param position
     */
    private void gotoCommentsScreen(int position) {
        // TODO Auto-generated method stub
        // get post data
        String postId = String.valueOf(listPosts.get(position).getPostId());
        String postBgColor = listPosts.get(position).getBgColor();
        String postBgImage = listPosts.get(position).getImage();
        // create bundle to pass data with fragment
        Bundle bundle = new Bundle();
        bundle.putString(DefaultConstants.EXTRA_POST_ID, postId);
        bundle.putString(DefaultConstants.EXTRA_POST_BG_COLOR, postBgColor);
        bundle.putString(DefaultConstants.EXTRA_POST_BG_IMAGE, postBgImage);
        // load screen
        if (listPosts.get(position).getTotalComments() != null) {
            Utils.loadFragmentInBackstack(getActivity(), DefaultConstants.FRAGMENT_CONTAINER_ID,
                    CommentsFragment.class, bundle, DefaultConstants.FRAGMENT_TAG_COMMENTS);
        } else {
            Utils.loadFragmentInBackstack(getActivity(), DefaultConstants.FRAGMENT_CONTAINER_ID,
                    AddCommentFragment.class, bundle, DefaultConstants.FRAGMENT_TAG_ADD_COMMENT);
        }
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
        Post post = listPosts.get(position);
        Integer status = post.getStatus() == null ? DefaultConstants.DEFAULT_INTEGER : post
                .getStatus();
        Integer report = post.getReport() == null ? DefaultConstants.DEFAULT_INTEGER : post
                .getReport();
        moreOptionsPopupDialog.setData(position, status, report);

        // show dialog
        moreOptionsPopupDialog.showDialogAtLocation(Utils.locateView(view));
    }

    /**
     * called on user action set status
     * 
     * @param position
     * @param statusCode
     */
    @Override
    public void onSetStatus(final int position, final int statusCode) {
        // TODO Auto-generated method stub
        // request api
        OfficewallService service = OfficewallServiceProvider.getService();
        service.setPostStatus(getPostStatusRequestJson(position, statusCode),
                new Callback<SetPostStatusRs>() {

                    @Override
                    public void success(SetPostStatusRs setPostStatusRs, Response response) {
                        // TODO Auto-generated method stub
                        // update item status on the screen
                        Post post = listPosts.get(position);
                        if (statusCode == DefaultConstants.SET_STATUS_SUBSCRIBE) {
                            post.setStatus(1);
                        } else {
                            post.setStatus(DefaultConstants.DEFAULT_INTEGER);
                        }
                        listPosts.set(position, post);
                    }

                    @Override
                    public void failure(RetrofitError error) {
                        // TODO Auto-generated method stub
                        // do nothing
                    }
                });
    }

    /**
     * creates http params json for subscribe post request.
     * 
     * @param position
     * @param statusCode
     * @return jsonObject
     */
    private JsonObject getPostStatusRequestJson(int position, int statusCode) {
        // TODO Auto-generated method stub
        // get data to pass with http request
        String request = HttpConstants.RQ_SET_POST_STATUS;
        String uid = OfficeWallApp.DefaultPref.getString(DefaultConstants.PREF_LOGIN_UID, "");
        String oAuthKey = OfficeWallApp.DefaultPref.getString(
                DefaultConstants.PREF_LOGIN_OAUTH_KEY, "");
        String postId = String.valueOf(listPosts.get(position).getPostId());
        String status = String.valueOf(statusCode);

        // create json from data
        JsonObject objJson = new JsonObject();
        objJson.addProperty(HttpConstants.HTTP_RQ_TYPE, request);
        objJson.addProperty(HttpConstants.PARAM_UID, uid);
        objJson.addProperty(HttpConstants.PARAM_OAUTH_KEY, oAuthKey);
        objJson.addProperty(HttpConstants.PARAM_SET_POST_STATUS_POST_ID, postId);
        objJson.addProperty(HttpConstants.PARAM_SET_POST_STATUS_STATUS, status);

        return objJson;
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
                        Post post = listPosts.get(position);
                        post.setReport(reason);
                        listPosts.set(position, post);
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
        String request = HttpConstants.RQ_REPORT_POST;
        String uid = OfficeWallApp.DefaultPref.getString(DefaultConstants.PREF_LOGIN_UID, "");
        String oAuthKey = OfficeWallApp.DefaultPref.getString(
                DefaultConstants.PREF_LOGIN_OAUTH_KEY, "");
        String postId = String.valueOf(listPosts.get(position).getPostId());
        String reason = String.valueOf(reasonCode);
        String text = reasonText;

        // create json from data
        JsonObject objJson = new JsonObject();
        objJson.addProperty(HttpConstants.HTTP_RQ_TYPE, request);
        objJson.addProperty(HttpConstants.PARAM_UID, uid);
        objJson.addProperty(HttpConstants.PARAM_OAUTH_KEY, oAuthKey);
        objJson.addProperty(HttpConstants.PARAM_REPORT_POST_POST_ID, postId);
        objJson.addProperty(HttpConstants.PARAM_REPORT_POST_REASON, reason);
        objJson.addProperty(HttpConstants.PARAM_REPORT_POST_TEXT, text);

        return objJson;
    }

    /**
     * up vote post
     * 
     * @param position
     */
    private void upVotePost(final int position) {
        // TODO Auto-generated method stub
        final Post post = listPosts.get(position);
        final int vote = (post.getVote() == null) ? DefaultConstants.DEFAULT_INTEGER : post
                .getVote();
        final int upVote = (post.getUpvotes() == null) ? DefaultConstants.DEFAULT_INTEGER : post
                .getUpvotes();
        final int downVote = (post.getDownvotes() == null) ? DefaultConstants.DEFAULT_INTEGER
                : post.getDownvotes();
        if (vote != DefaultConstants.VOTE_UP) {
            // update data
            if (vote == DefaultConstants.VOTE_DOWN) {
                post.setDownvotes(downVote - 1);
            }
            post.setUpvotes(upVote + 1);
            post.setVote(DefaultConstants.VOTE_UP);
            listPosts.set(position, post);
            adapterPosts.notifyDataSetChanged(listPosts);
            // request api
            OfficewallService service = OfficewallServiceProvider.getService();
            service.votePost(getVotePostRequestJson(position), new Callback<VotePostRs>() {

                @Override
                public void success(VotePostRs votePostRs, Response response) {
                    // TODO Auto-generated method stub
                    // do nothing
                }

                @Override
                public void failure(RetrofitError error) {
                    // TODO Auto-generated method stub
                    // reset data
                    if (vote == DefaultConstants.VOTE_DOWN) {
                        post.setDownvotes(downVote + 1);
                    }
                    post.setUpvotes(upVote - 1);
                    post.setVote(vote);
                    listPosts.set(position, post);
                    adapterPosts.notifyDataSetChanged(listPosts);
                }
            });
        }
    }

    /**
     * down vote post
     * 
     * @param position
     */
    private void downVotePost(final int position) {
        // TODO Auto-generated method stub
        final Post post = listPosts.get(position);
        final int vote = (post.getVote() == null) ? DefaultConstants.DEFAULT_INTEGER : post
                .getVote();
        final int upVote = (post.getUpvotes() == null) ? DefaultConstants.DEFAULT_INTEGER : post
                .getUpvotes();
        final int downVote = (post.getDownvotes() == null) ? DefaultConstants.DEFAULT_INTEGER
                : post.getDownvotes();
        if (vote != DefaultConstants.VOTE_DOWN) {
            // update data
            if (vote == DefaultConstants.VOTE_UP) {
                post.setUpvotes(upVote - 1);
            }
            post.setDownvotes(downVote + 1);
            post.setVote(DefaultConstants.VOTE_DOWN);
            listPosts.set(position, post);
            adapterPosts.notifyDataSetChanged(listPosts);
            // request api
            OfficewallService service = OfficewallServiceProvider.getService();
            service.votePost(getVotePostRequestJson(position), new Callback<VotePostRs>() {

                @Override
                public void success(VotePostRs votePostRs, Response response) {
                    // TODO Auto-generated method stub
                    // do nothing
                }

                @Override
                public void failure(RetrofitError error) {
                    // TODO Auto-generated method stub
                    // reset data
                    if (vote == DefaultConstants.VOTE_UP) {
                        post.setUpvotes(upVote + 1);
                    }
                    post.setDownvotes(downVote - 1);
                    post.setVote(vote);
                    listPosts.set(position, post);
                    adapterPosts.notifyDataSetChanged(listPosts);
                }
            });
        }
    }

    /**
     * creates http params json for vote post request.
     * 
     * @param position
     * @return jsonObject
     */
    private JsonObject getVotePostRequestJson(int position) {
        // TODO Auto-generated method stub
        // get data to pass with http request
        String request = HttpConstants.RQ_VOTE_POST;
        String uid = OfficeWallApp.DefaultPref.getString(DefaultConstants.PREF_LOGIN_UID, "");
        String oAuthKey = OfficeWallApp.DefaultPref.getString(
                DefaultConstants.PREF_LOGIN_OAUTH_KEY, "");
        String postId = String.valueOf(listPosts.get(position).getPostId());
        String vote = String.valueOf(listPosts.get(position).getVote());

        // create json from data
        JsonObject objJson = new JsonObject();
        objJson.addProperty(HttpConstants.HTTP_RQ_TYPE, request);
        objJson.addProperty(HttpConstants.PARAM_UID, uid);
        objJson.addProperty(HttpConstants.PARAM_OAUTH_KEY, oAuthKey);
        objJson.addProperty(HttpConstants.PARAM_VOTE_POST_POST_ID, postId);
        objJson.addProperty(HttpConstants.PARAM_VOTE_POST_VOTE, vote);

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
        mPostsCallback.cancel();
        mInviteFriendsCallback.cancel();
        /* hide progressbar */
        ((UserWallsActivity)getActivity()).hideProgressbar();
    }

}
