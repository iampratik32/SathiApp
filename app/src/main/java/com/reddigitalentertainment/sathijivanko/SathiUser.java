package com.reddigitalentertainment.sathijivanko;

import com.google.firebase.auth.AuthCredential;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class SathiUser {

    private String userId;
    private String name;
    private String gender;
    private int age;
    private String bio;
    private String location;
    private String email;
    private String preference;
    private String showAge;
    private LinkedList<String> userImages;
    private String displayPicture;
    private String userJob;
    private String userSchool;
    private String userCollege;
    private String verified;
    private String lookingFor;
    private String showme;
    private String lookingIn;
    private List<PotentialMatchesInfo> userLikes;
    private List<PotentialMatchesInfo> userDislikes;

    public List<PotentialMatchesInfo> getUserLikes() {
        return userLikes;
    }

    public void setUserLikes(List<PotentialMatchesInfo> userLikes) {
        this.userLikes = userLikes;
    }

    public List<PotentialMatchesInfo> getUserDislikes() {
        return userDislikes;
    }

    public void setUserDislikes(List<PotentialMatchesInfo> userDislikes) {
        this.userDislikes = userDislikes;
    }

    public String getVerified() {
        return verified;
    }

    public void setVerified(String verified) {
        this.verified = verified;
    }

    public String getLookingIn() {
        return lookingIn;
    }

    public void setLookingIn(String lookingIn) {
        this.lookingIn = lookingIn;
    }

    public String getShowme() {
        return showme;
    }

    public void setShowme(String showme) {
        this.showme = showme;
    }

    private String status;
    private ArrayList<Chat_PotentialChatUser> chatUsers;
    private AuthCredential authCredential;

    public SathiUser(String id, String n, String g, int a, String b, String d, String e, String p, String showA,String showme,String lookingIn,String verified){

        this.userId = id;
        this.name = n;
        this.gender = g;
        this.age = a;
        this.bio = b;
        this.location = d;
        this.email = e;
        this.preference = p;
        this.showAge = showA;
        this.showme = showme;
        this.lookingIn=lookingIn;
        this.verified = verified;
    }

    public AuthCredential getAuthCredential() {
        return authCredential;
    }

    public void setAuthCredential(AuthCredential authCredential) {
        this.authCredential = authCredential;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }


    public ArrayList<Chat_PotentialChatUser> getChatUsers() {
        return chatUsers;
    }

    public void setChatUsers(ArrayList<Chat_PotentialChatUser> chatUsers) {
        this.chatUsers = chatUsers;
    }

    public String getUserCollege() {
        return userCollege;
    }

    public void setUserCollege(String userCollege) {
        this.userCollege = userCollege;
    }

    public String getLookingFor() {
        return lookingFor;
    }

    public void setLookingFor(String lookingFor) {
        this.lookingFor = lookingFor;
    }

    public String getUserSchool() {
        return userSchool;
    }

    public void setUserSchool(String userSchool) {
        this.userSchool = userSchool;
    }

    public String getUserJob() {
        return userJob;
    }

    public void setUserJob(String userJob) {
        this.userJob = userJob;
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

    public String getShowAge() {
        return showAge;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPreference() {
        return preference;
    }

    public void setPreference(String preference) {
        this.preference = preference;
    }

    public void setShowAge(String showAge) {
        this.showAge = showAge;
    }

    public LinkedList<String> getUserImages() {
        return userImages;
    }

    public void setUserImages(LinkedList<String> userImages) {
        this.userImages = userImages;
    }
}
