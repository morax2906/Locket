package com.tandev.locket.test;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "friend_table")
public class FriendEntity {

    @PrimaryKey
    @NonNull
    private String uid;  // Sử dụng uid làm khóa chính

    private String first_name;
    private String last_name;
    private String badge;
    private String profile_picture_url;
    private boolean temp;
    private String username;

    public FriendEntity(@NonNull String uid, String first_name, String last_name, String badge, String profile_picture_url, boolean temp, String username) {
        this.uid = uid;
        this.first_name = first_name;
        this.last_name = last_name;
        this.badge = badge;
        this.profile_picture_url = profile_picture_url;
        this.temp = temp;
        this.username = username;
    }

    // Getter và Setter cho các trường
    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getFirst_name() {
        return first_name;
    }

    public void setFirst_name(String first_name) {
        this.first_name = first_name;
    }

    public String getLast_name() {
        return last_name;
    }

    public void setLast_name(String last_name) {
        this.last_name = last_name;
    }

    public String getBadge() {
        return badge;
    }

    public void setBadge(String badge) {
        this.badge = badge;
    }

    public String getProfile_picture_url() {
        return profile_picture_url;
    }

    public void setProfile_picture_url(String profile_picture_url) {
        this.profile_picture_url = profile_picture_url;
    }

    public boolean isTemp() {
        return temp;
    }

    public void setTemp(boolean temp) {
        this.temp = temp;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
