package com.reddigitalentertainment.sathijivanko;

public class Feed_Class {
    private String userId;
    private long time;
    private String what;
    private String displayPicture;
    private String userName;
    private String gender;
    private String showAge;
    private String age;
    private String preference;
    private String bio;
    private String matched;
    private String verified;

    public String getVerified() {
        return verified;
    }

    public void setVerified(String verified) {
        this.verified = verified;
    }

    public String getMatched() {
        return matched;
    }

    public void setMatched(String matched) {
        this.matched = matched;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public String getWhat() {
        return what;
    }

    public void setWhat(String what) {
        this.what = what;
    }

    public String getDisplayPicture() {
        return displayPicture;
    }

    public void setDisplayPicture(String displayPicture) {
        this.displayPicture = displayPicture;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getShowAge() {
        return showAge;
    }

    public void setShowAge(String showAge) {
        this.showAge = showAge;
    }

    public String getAge() {
        return age;
    }

    public void setAge(String age) {
        this.age = age;
    }

    public String getPreference() {
        return preference;
    }

    public void setPreference(String preference) {
        this.preference = preference;
    }

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    public Feed_Class(String userId, long time, String what) {
        this.userId = userId;
        this.time = time;
        this.what = what;
    }
}
