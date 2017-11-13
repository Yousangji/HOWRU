package com.example.yousangji.howru.Model;

/**
 * Created by YouSangJi on 2017-11-02.
 */

public class obj_noti {
    String userid;
    String notimessage;
    String fromid;

    public void setUserid(String userid) {
        this.userid = userid;
    }

    public void setNotimessage(String notimessage) {
        this.notimessage = notimessage;
    }

    public void setFromid(String fromid) {
        this.fromid = fromid;
    }


    public String getUserid() {
        return userid;
    }

    public String getNotimessage() {
        return notimessage;
    }

    public String getFromid() {
        return fromid;
    }
}
