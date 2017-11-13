package com.example.yousangji.howru.Model;

/**
 * Created by YouSangJi on 2017-10-24.
 */

public class obj_user {

    private String userid;
    private String emailaddr;
    private String username;
    private String fcmtoken;
    private String followee;
    private String nickname;
    private String profileurl;
    private String usermsg;

    public void setUserid(String userid) {
        this.userid = userid;
    }

    public void setEmailaddr(String emailaddr) {
        this.emailaddr = emailaddr;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setFcmtoken(String fcmtoken) {
        this.fcmtoken = fcmtoken;
    }

    public void setFollowee(String followee) {
        this.followee = followee;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public void setProfileurl(String profileurl) {
        this.profileurl = profileurl;
    }

    public void setUsermsg(String usermsg) {
        this.usermsg = usermsg;
    }

    public String getUserid() {
        return userid;
    }

    public String getEmailaddr() {
        return emailaddr;
    }

    public String getUsername() {
        return username;
    }

    public String getFcmtoken() {
        return fcmtoken;
    }

    public String getFollowee() {
        return followee;
    }

    public String getNickname() {
        return nickname;
    }

    public String getProfileurl() {
        return profileurl;
    }

    public String getUsermsg() {
        return usermsg;
    }
}
