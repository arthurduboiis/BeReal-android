package com.example.bereal.Entities;

import java.util.ArrayList;
import java.util.HashMap;

public class UserRequests {

    private HashMap<String, ArrayList<FriendDetails>> ownRequests;
    private HashMap<String, ArrayList<FriendDetails>> friendsRequests;
    private HashMap<String, ArrayList<FriendDetails>> friends;

    public UserRequests() {
        this.ownRequests = new HashMap<>();
        this.friendsRequests = new HashMap<>();
        this.friends = new HashMap<>();
    }

    public UserRequests(HashMap<String, ArrayList<FriendDetails>> ownRequests, HashMap<String, ArrayList<FriendDetails>> friendsRequests, HashMap<String, ArrayList<FriendDetails>> friends) {
        this.ownRequests = ownRequests;
        this.friendsRequests = friendsRequests;
        this.friends = friends;
    }

    public HashMap<String, ArrayList<FriendDetails>> getOwnRequests() {
        return ownRequests;
    }

    public void setOwnRequests(HashMap<String, ArrayList<FriendDetails>> ownRequests) {
        this.ownRequests = ownRequests;
    }

    public HashMap<String, ArrayList<FriendDetails>> getFriendsRequests() {
        return friendsRequests;
    }

    public void setFriendsRequests(HashMap<String, ArrayList<FriendDetails>> friendsRequests) {
        this.friendsRequests = friendsRequests;
    }

    public HashMap<String, ArrayList<FriendDetails>> getFriends() {
        return friends;
    }

    public void setFriends(HashMap<String, ArrayList<FriendDetails>> friends) {
        this.friends = friends;
    }
}
