package com.qwu.secretgarden;

public class UserObject {

    private String id;
    private String account;
    private String avatar;
    private String name;


    public UserObject(String id, String account, String avatar, String name) {

        this.id = id;
        this.account = account;
        this.avatar = avatar;
        this.name = name;

    }

    public void setId(String v){
        this.id = v;
    }
    public String getId(){
        return this.id;
    }

    public void setAccount(String v){
        this.account = v;
    }
    public String getAccount(){
        return this.account;
    }

    public void setAvatar(String v){
        this.avatar = v;
    }
    public String getAvatar(){
        return this.avatar;
    }

    public void setName(String v){
        this.name = v;
    }
    public String getName(){
        return this.name;
    }


}
