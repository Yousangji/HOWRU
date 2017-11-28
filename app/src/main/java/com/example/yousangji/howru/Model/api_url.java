package com.example.yousangji.howru.Model;

/**
 * Created by YouSangJi on 2017-10-24.
 */

public class api_url {
    public static final String API_BASE_URL = "http://52.78.169.32/";
    public static final String SIGN_IN_URL = "sign/in.json";
    public static final String SIGN_UP_URL = "sign/up.json";
    public static final String GET_WORD_TYPE_LIST = "word/type/list.json";
    public static final String GET_STREAM_LIST="room/getroom.php";
    public static final String POST_STREAM="room/post.php";
    public static final String UPDATE_STREAMFLAG="room/updateflag.php";
    public static final String POST_USERS="users/post.php";
    public static final String GET_USERS="users/get.php";
    public static final String PUT_USERS="users/put.php";
    public static final String PUT_USERSWITHIMG="users/putwithimg.php";
    public static final String STREAM_LIVE=API_BASE_URL+"hls/";
    public static final String STREAM_VOD="res/rec/";
    public static final String STREAM_THUMBNAIL="res/rec/thum/";
    public static final String NOTIFY_FOLLOW="follow/put.php";
    public static final String NOTIFY_GET="follow/get.php";
    public static final String NOTIFY_SEND="follow/send.php";
    public static final String POST_VOD="room/postvod.php";

}
