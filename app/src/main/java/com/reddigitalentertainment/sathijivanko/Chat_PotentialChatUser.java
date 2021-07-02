package com.reddigitalentertainment.sathijivanko;

public class Chat_PotentialChatUser {
    private String userId;
    private String name;
    private String profileImage;
    private String age;
    private String userGender;
    private String verified;

    public Chat_PotentialChatUser(String userId, String name, String age, String userGender,String verified) {
        this.userId = userId;
        this.name = name;
        this.age = age;
        this.userGender = userGender;
        this.verified =verified;
    }

    public String getVerified() {
        return verified;
    }

    public void setVerified(String verified) {
        this.verified = verified;
    }

    public String getUserGender() {
        return userGender;
    }


    public void setUserGender(String userGender) {
        this.userGender = userGender;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getProfileImage() {
        return profileImage;
    }

    public void setProfileImage(String profileImage) {
        this.profileImage = profileImage;
    }

    public String getAge() {
        return age;
    }

    public void setAge(String age) {
        this.age = age;
    }
}
