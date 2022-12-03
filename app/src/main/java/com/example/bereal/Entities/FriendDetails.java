package com.example.bereal.Entities;

public class FriendDetails {
    private String status;
    private String username;

    public FriendDetails(){
        this(null,null);
    }

    public FriendDetails(String status, String username) {
        this.status = status;
        this.username = username;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
