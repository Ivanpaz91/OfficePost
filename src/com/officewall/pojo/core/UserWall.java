
package com.officewall.pojo.core;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class UserWall {
    @SerializedName("wall_id")
    @Expose
    private String wallId;
    @SerializedName("wall_name")
    @Expose
    private String wallName;
    @SerializedName("wall_domain")
    @Expose
    private String wallDomain;
    @SerializedName("user_email")
    @Expose
    private String userEmail;
    @SerializedName("new_items")
    @Expose
    private String newItems;

    public String getWallId() {
        return wallId;
    }

    public void setWallId(String wallId) {
        this.wallId = wallId;
    }

    public String getWallName() {
        return wallName;
    }

    public void setWallName(String wallName) {
        this.wallName = wallName;
    }

    public String getWallDomain() {
        return wallDomain;
    }

    public void setWallDomain(String wallDomain) {
        this.wallDomain = wallDomain;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public String getNewItems() {
        return newItems;
    }

    public void setNewItems(String newItems) {
        this.newItems = newItems;
    }
}
