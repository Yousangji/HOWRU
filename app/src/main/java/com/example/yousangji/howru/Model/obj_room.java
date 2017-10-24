package com.example.yousangji.howru.Model;

/**
 * Created by YouSangJi on 2017-10-24.
 */

public class obj_room {

    private int no;
    private String streamernick;
    private String rmtitle;
    private String rmthumburl;
    private int count;
    private String rmid;
    private String rmpath;
    private String streamerid;
    private int onair;

    public  obj_room(){

    }

    public void setCount(int count) {
        this.count = count;
    }

    public void setNickname(String nickname) {
        this.streamernick = nickname;
    }

    public void setThumnailurl(String thumnailurl) {
        this.rmthumburl = thumnailurl;
    }

    public void setTitle(String title) {
        this.rmtitle = title;
    }

    public void setRoomid(String roomid) {
        this.rmid = roomid;
    }

    public void setRmpath(String rmpath) {
        this.rmpath = rmpath;
    }

    public void setStreamerid(String streamerid) {
        this.streamerid = streamerid;
    }

    public void setOnair(int onair) {
        this.onair = onair;
    }


    public int getCount() {
        return count;
    }

    public String getNickname() {
        return streamernick;
    }

    public String getThumnailurl() {
        return rmthumburl;
    }

    public String getTitle() {
        return rmtitle;
    }

    public String getRoomid() {
        return rmid;
    }

    public String getRmpath() {
        return rmpath;
    }

    public String getStreamerid() {
        return streamerid;
    }

    public int getOnair() {
        return onair;
    }

}
