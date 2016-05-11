
package com.officewall.pojo.wrappers;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class LoginRs {
    @SerializedName("@response_code")
    @Expose
    private Integer responseCode;
    @SerializedName("user_id")
    @Expose
    private String userId;
    @SerializedName("oAuth_key")
    @Expose
    private String oAuthKey;
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

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getOAuthKey() {
        return oAuthKey;
    }

    public void setOAuthKey(String oAuthKey) {
        this.oAuthKey = oAuthKey;
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
