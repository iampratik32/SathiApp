package com.reddigitalentertainment.sathijivanko.BasicDisplay;

import com.google.gson.annotations.SerializedName;

public class IGBDAccessTokenBaseResponseData extends IGBDBaseResponseData {
    @SerializedName("access_token")
    private String accessToken;

    public String getAccessToken() {
        return accessToken;
    }
}
