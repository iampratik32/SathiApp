package com.reddigitalentertainment.sathijivanko;

import android.content.Context;
import android.view.View;

import androidx.viewpager.widget.ViewPager;

import java.util.LinkedList;
import java.util.List;

public class SathiUserHolder {

    private static SathiUser sathiUser = null;
    private static boolean chatLifeCycle = false;
    private static int totalLoad = 0;
    private static List<PotentialMatchesInfo> potentialLists;
    private static boolean loadChat = false;
    private static String userAddress = null;
    private static String userCity = null;
    private static String userZone = null;
    private static int totalDislikes;
    private static int totalLikes;
    private static String userCountry = null;
    private static String facebookEmail = null;
    private static String currentDate = null;
    private static Chat_PotentialUserAdapter unMatchAdapter;
    private static LinkedList thatImages;
    private boolean updateChat =false;
    private static ViewPager profilePager;
    private static Context profileContext;
    private static View profileFragmentView;

    public static boolean isChatLifeCycle() {
        return chatLifeCycle;
    }

    public static void setChatLifeCycle(boolean chatLifeCycle) {
        SathiUserHolder.chatLifeCycle = chatLifeCycle;
    }

    public static View getProfileFragmentView() {
        return profileFragmentView;
    }

    public static void setProfileFragmentView(View profileFragmentView) {
        SathiUserHolder.profileFragmentView = profileFragmentView;
    }

    public static ViewPager getProfilePager() {
        return profilePager;
    }

    public static void setProfilePager(ViewPager profilePager) {
        SathiUserHolder.profilePager = profilePager;
    }

    public static Context getProfileContext() {
        return profileContext;
    }

    public static void setProfileContext(Context profileContext) {
        SathiUserHolder.profileContext = profileContext;
    }

    public boolean isUpdateChat() {
        return updateChat;
    }

    public void setUpdateChat(boolean updateChat) {
        this.updateChat = updateChat;
    }

    public static String getFacebookEmail() {
        return facebookEmail;
    }

    public static void setFacebookEmail(String facebookEmail) {
        SathiUserHolder.facebookEmail = facebookEmail;
    }

    public static String getUserZone() {
        return userZone;
    }

    public static void setUserZone(String userZone) {
        SathiUserHolder.userZone = userZone;
    }

    public static LinkedList getThatImages() {
        return thatImages;
    }

    public static void setThatImages(LinkedList thatImages) {
        SathiUserHolder.thatImages = thatImages;
    }


    public static Chat_PotentialUserAdapter getUnMatchAdapter() {
        return unMatchAdapter;
    }

    public static void setUnMatchAdapter(Chat_PotentialUserAdapter unMatchAdapter) {
        SathiUserHolder.unMatchAdapter = unMatchAdapter;
    }

    private static List<PotentialMatchesInfo> shownPeople;

    public static List<PotentialMatchesInfo> getShownPeople() {
        return shownPeople;
    }

    public static void setShownPeople(List<PotentialMatchesInfo> shownPeople) {
        SathiUserHolder.shownPeople = shownPeople;
    }

    public static int getTotalDislikes() {
        return totalDislikes;
    }

    public static void setTotalDislikes(int totalDislikes) {
        SathiUserHolder.totalDislikes = totalDislikes;
    }

    public static int getTotalLikes() {
        return totalLikes;
    }

    public static void setTotalLikes(int totalLikes) {
        SathiUserHolder.totalLikes = totalLikes;
    }

    public static String getCurrentDate() {
        return currentDate;
    }

    public static void setCurrentDate(String currentDate) {
        SathiUserHolder.currentDate = currentDate;
    }


    public static List<PotentialMatchesInfo> getDealtWithList() {
        return dealtWithList;
    }

    public static String getUserCity() {
        return userCity;
    }

    public static void setUserCity(String userCity) {
        SathiUserHolder.userCity = userCity;
    }

    public static String getUserCountry() {
        return userCountry;
    }

    public static void setUserCountry(String userCountry) {
        SathiUserHolder.userCountry = userCountry;
    }

    public static void setDealtWithList(List<PotentialMatchesInfo> dealtWithList) {
        SathiUserHolder.dealtWithList = dealtWithList;
    }


    public static String getUserAddress() {
        return userAddress;
    }

    public static void setUserAddress(String userAddress) {
        SathiUserHolder.userAddress = userAddress;
    }

    private static List<PotentialMatchesInfo> dealtWithList;

    public static boolean isLoadChat() {
        return loadChat;
    }

    public static void setLoadChat(boolean loadChat) {
        SathiUserHolder.loadChat = loadChat;
    }

    public static List<PotentialMatchesInfo> getShownUsers() {
        return shownUsers;
    }

    public static void setShownUsers(List<PotentialMatchesInfo> shownUsers) {
        SathiUserHolder.shownUsers = shownUsers;
    }

    private static List<PotentialMatchesInfo> shownUsers;

    public static void setSathiUser(SathiUser user){
        sathiUser = user;
    }
    public static SathiUser getSathiUser(){
        return sathiUser;
    }

    public static int getTotalLoad() {
        return totalLoad;
    }


    public static void setTotalLoad(int totalLoad) {
        SathiUserHolder.totalLoad = totalLoad;
    }

    public static List<PotentialMatchesInfo> getPotentialLists() {
        return potentialLists;
    }

    public static void setPotentialLists(List<PotentialMatchesInfo> potentialLists) {
        SathiUserHolder.potentialLists = potentialLists;
    }
}
