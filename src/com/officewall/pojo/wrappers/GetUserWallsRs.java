
package com.officewall.pojo.wrappers;

import java.util.ArrayList;
import java.util.List;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.officewall.pojo.core.UserWall;

public class GetUserWallsRs {
    @SerializedName("@response_code")
    @Expose
    private Integer responseCode;
    @Expose
    private List<UserWall> walls = new ArrayList<UserWall>();
    @Expose
    private String description;
    @SerializedName("user_message")
    @Expose
    private String userMessage;
    @Expose
    private String destination;

    public Integer getResponseCode() {
        return responseCode;
    }

    public void setResponseCode(Integer responseCode) {
        this.responseCode = responseCode;
    }

    public List<UserWall> getWalls() {
        return walls;
    }

    public void setWalls(List<UserWall> walls) {
        this.walls = walls;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getUserMessage() {
        return userMessage;
    }

    public void setUserMessage(String userMessage) {
        this.userMessage = userMessage;
    }

    public String getDestination() {
        return destination;
    }

    public void setDestination(String destination) {
        this.destination = destination;
    }
}
