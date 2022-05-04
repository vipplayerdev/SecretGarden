package com.qwu.secretgarden;


import java.util.Map;

public class UserInterface {

    public class UserStruct{
        public String Id;
        public String Account;
        public String Password;
        public String Avatar;
        public String FamilyCode;
    }

    public UserStruct user;
    public UserInterface(){
    }

    public boolean Add(String account,String password,String familyCode){

        return true;
    }

    public UserStruct Get(String account,String password){

        UserStruct u = new UserStruct();
        u.Id = "";
        this.user = u;
        return u;
    }

}
