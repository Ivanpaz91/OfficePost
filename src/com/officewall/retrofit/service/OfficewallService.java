
package com.officewall.retrofit.service;

import com.google.gson.JsonObject;
import com.officewall.pojo.wrappers.AddCommentRs;
import com.officewall.pojo.wrappers.AddPostRs;
import com.officewall.pojo.wrappers.GetCommentsRs;
import com.officewall.pojo.wrappers.GetUserWallsRs;
import com.officewall.pojo.wrappers.GetPostsRs;
import com.officewall.pojo.wrappers.InviteFriendsRs;
import com.officewall.pojo.wrappers.LoginRs;
import com.officewall.pojo.wrappers.ReportCommentRs;
import com.officewall.pojo.wrappers.ReportPostRs;
import com.officewall.pojo.wrappers.SignupRs;
import com.officewall.pojo.wrappers.SetPostStatusRs;
import com.officewall.pojo.wrappers.VoteCommentRs;
import com.officewall.pojo.wrappers.VotePostRs;

import retrofit.Callback;
import retrofit.http.Body;
import retrofit.http.POST;

public interface OfficewallService {

    /**
     * Signup request
     */
    /* Asyncronous call */
    @POST("/officewall/test/")
    public void signup(@Body JsonObject objJson, Callback<SignupRs> callback);

    /* Syncronous call */
    @POST("/officewall/test/")
    public SignupRs signup(@Body JsonObject objJson);

    /**
     * Login request
     */
    /* Asyncronous call */
    @POST("/officewall/test/")
    public void login(@Body JsonObject objJson, Callback<LoginRs> callback);

    /* Syncronous call */
    @POST("/officewall/test/")
    public LoginRs login(@Body JsonObject objJson);

    /**
     * Get user wall request
     */
    /* Asyncronous call */
    @POST("/officewall/test/")
    public void getUserWalls(@Body JsonObject objJson, Callback<GetUserWallsRs> callback);

    /* Syncronous call */
    @POST("/officewall/test/")
    public GetUserWallsRs getUserWalls(@Body JsonObject objJson);

    /**
     * Get post request
     */
    /* Asyncronous call */
    @POST("/officewall/test/")
    public void getPosts(@Body JsonObject objJson, Callback<GetPostsRs> callback);

    /* Syncronous call */
    @POST("/officewall/test/")
    public GetPostsRs getPosts(@Body JsonObject objJson);

    /**
     * Add post request
     */
    /* Asyncronous call */
    @POST("/officewall/test/")
    public void addPost(@Body JsonObject objJson, Callback<AddPostRs> callback);

    /* Syncronous call */
    @POST("/officewall/test/")
    public AddPostRs addPost(@Body JsonObject objJson);

    /**
     * Get comment request
     */
    /* Asyncronous call */
    @POST("/officewall/test/")
    public void getComments(@Body JsonObject objJson, Callback<GetCommentsRs> callback);

    /* Syncronous call */
    @POST("/officewall/test/")
    public GetCommentsRs getComments(@Body JsonObject objJson);

    /**
     * Add comment request
     */
    /* Asyncronous call */
    @POST("/officewall/test/")
    public void addComment(@Body JsonObject objJson, Callback<AddCommentRs> callback);

    /* Syncronous call */
    @POST("/officewall/test/")
    public AddCommentRs addComment(@Body JsonObject objJson);

    /**
     * Vote post request
     */
    /* Asyncronous call */
    @POST("/officewall/test/")
    public void votePost(@Body JsonObject objJson, Callback<VotePostRs> callback);

    /**
     * Vote comment request
     */
    /* Asyncronous call */
    @POST("/officewall/test/")
    public void voteComment(@Body JsonObject objJson, Callback<VoteCommentRs> callback);

    /**
     * Vote comment request
     */
    /* Asyncronous call */
    @POST("/officewall/test/")
    public void inviteFriends(@Body JsonObject objJson, Callback<InviteFriendsRs> callback);

    /**
     * Set post status request
     */
    /* Asyncronous call */
    @POST("/officewall/test/")
    public void setPostStatus(@Body JsonObject objJson, Callback<SetPostStatusRs> callback);

    /**
     * Report post request
     */
    /* Asyncronous call */
    @POST("/officewall/test/")
    public void reportPost(@Body JsonObject objJson, Callback<ReportPostRs> callback);

    /**
     * Report post request
     */
    /* Asyncronous call */
    @POST("/officewall/test/")
    public void reportComment(@Body JsonObject objJson, Callback<ReportCommentRs> callback);

}
