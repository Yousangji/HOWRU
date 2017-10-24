package com.example.yousangji.howru.Model;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by YouSangJi on 2017-10-24.
 */

public class obj_chatmsg {

    String msg_nickname="";
    String msg_content="";
    String msg_state="";
    String msg_rmnum="";
    JSONObject jsonmsg;
    int flag=0;

    public obj_chatmsg(String msg_state,String msg_nickname,String msg_content,String msg_rmnum){
        this.msg_nickname=msg_nickname;
        this.msg_content=msg_content;
        this.msg_rmnum=msg_rmnum;
        this.msg_state=msg_state;
        this.jsonmsg=new JSONObject();

    }

    public obj_chatmsg(){

    }

    public void setFlag(int mymsg) {
        this.flag = mymsg;
    }

    public void setMsg_nickname(String msg_nickname) {
        this.msg_nickname = msg_nickname;
    }

    public void setMsg_content(String msg_content) {
        this.msg_content = msg_content;
    }

    public void setMsg_state(String msg_state) {
        this.msg_state = msg_state;
    }

    public void setMsg_rmnum(String msg_rmnum) {
        this.msg_rmnum = msg_rmnum;
    }

    public String getMsg_nickname() {
        return msg_nickname;
    }

    public String getMsg_content() {
        return msg_content;
    }

    public String getMsg_state() {
        return msg_state;
    }

    public String getMsg_rmnum() {
        return msg_rmnum;
    }

    public int getFlag() {
        return flag;
    }

    public String toJSONstr(){
        try {
            jsonmsg = new JSONObject();
            jsonmsg.put("msg", this.msg_content);
            jsonmsg.put("rmnum", this.msg_rmnum);
            jsonmsg.put("nickname", this.msg_nickname);
            jsonmsg.put("state", this.msg_state);
        }catch (JSONException e){
            e.printStackTrace();
        }

        return jsonmsg.toString();
    }
}
