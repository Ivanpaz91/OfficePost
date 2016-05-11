
package com.officewall.pojo.core;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Post {
    @SerializedName("post_id")
    @Expose
    private Integer postId;
    @Expose
    private String text;
    @SerializedName("bg_color")
    @Expose
    private String bgColor;
    @SerializedName("new")
    @Expose
    private Integer _new;
    @Expose
    private Integer upvotes;
    @Expose
    private Integer downvotes;
    @SerializedName("total_comments")
    @Expose
    private Integer totalComments;
    @SerializedName("new_comments")
    @Expose
    private Integer newComments;
    @Expose
    private Integer vote;
    @Expose
    private Integer report;
    @Expose
    private Integer status;
    @Expose
    private String image;

    public Integer getPostId() {
        return postId;
    }

    public void setPostId(Integer postId) {
        this.postId = postId;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getBgColor() {
        return bgColor;
    }

    public void setBgColor(String bgColor) {
        this.bgColor = bgColor;
    }

    public Integer getNew() {
        return _new;
    }

    public void setNew(Integer _new) {
        this._new = _new;
    }

    public Integer getUpvotes() {
        return upvotes;
    }

    public void setUpvotes(Integer upvotes) {
        this.upvotes = upvotes;
    }

    public Integer getDownvotes() {
        return downvotes;
    }

    public void setDownvotes(Integer downvotes) {
        this.downvotes = downvotes;
    }

    public Integer getTotalComments() {
        return totalComments;
    }

    public void setTotalComments(Integer totalComments) {
        this.totalComments = totalComments;
    }

    public Integer getNewComments() {
        return newComments;
    }

    public void setNewComments(Integer newComments) {
        this.newComments = newComments;
    }

    public Integer getVote() {
        return vote;
    }

    public void setVote(Integer vote) {
        this.vote = vote;
    }

    public Integer getReport() {
        return report;
    }

    public void setReport(Integer report) {
        this.report = report;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }
}
