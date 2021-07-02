package com.reddigitalentertainment.sathijivanko;

import java.util.LinkedList;

public class PotentialMatchesInfo {

    private String userId;
    private String userName;
    private String bio;
    private String age;
    private String showAge;
    private String gender;
    private LinkedList<String> imageList;
    private String displayPicture;
    private int listOfImages;
    private String matchedDate;
    private String verified;

    public PotentialMatchesInfo(String userId, String userName, String bio, String age, String showAge,String gender,String verified) {
        this.userId = userId;
        this.userName = userName;
        this.bio = bio;
        this.age = age;
        this.showAge = showAge;
        this.gender = gender;
        this.verified = verified;
    }

    public String getVerified() {
        return verified;
    }

    public void setVerified(String verified) {
        this.verified = verified;
    }

    public String getMatchedDate() {
        return matchedDate;
    }

    public void setMatchedDate(String matchedDate) {
        this.matchedDate = matchedDate;
    }

    public int getListOfImages() {
        return listOfImages;
    }

    public void setListOfImages(int listOfImages) {
        this.listOfImages = listOfImages;
    }

    public String getDisplayPicture() {
        return displayPicture;
    }

    public void setDisplayPicture(String displayPicture) {
        this.displayPicture = displayPicture;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    public String getAge() {
        return age;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public void setAge(String age) {
        this.age = age;
    }

    public String getShowAge() {
        return showAge;
    }

    public void setShowAge(String showAge) {
        this.showAge = showAge;
    }

    public LinkedList<String> getImageList() {
        return imageList;
    }

    public void setImageList(LinkedList<String> imageList) {
        this.imageList = imageList;
    }
}
