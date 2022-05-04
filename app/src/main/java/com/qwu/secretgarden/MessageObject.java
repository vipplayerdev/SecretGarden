package com.qwu.secretgarden;

public class MessageObject {

    private String id;
    private String msg;
    private String thumb;
    private String user_id;
    private String family_code;
    private String time;
    private String user_name;

    public MessageObject(){

    }

    public MessageObject(String id, String msg, String thumb, String user_id, String family_code, String time, String user_name) {

        this.id = id;
        this.msg = msg;
        this.thumb = thumb;
        this.user_id = user_id;
        this.family_code = family_code;
        this.time = time;
        this.user_name = user_name;

    }

    public void setId(String v){
        this.id = v;
    }
    public String getId(){
        return this.id;
    }

    public void setMsg(String v){
        this.msg = v;
    }
    public String getMsg(){
        return this.msg;
    }

    public void setThumb(String v){
        this.thumb = v;
    }
    public String getThumb(){
        return this.thumb;
    }

    public void setUserId(String v){
        this.user_id = v;
    }
    public String getUserId(){
        return this.user_id;
    }

    public void setFamilyCode(String v){
        this.family_code = v;
    }
    public String getFamilyCode(){
        return this.family_code;
    }

    public void setUserName(String v){
        this.user_name = v;
    }
    public String getUserName(){
        return this.user_name;
    }

    public void setTime(String v){
        this.time = v;
    }
    public String getTime(){
        return this.time;
    }


}
