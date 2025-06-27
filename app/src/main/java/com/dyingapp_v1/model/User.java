package com.dyingapp_v1.model;

import org.json.JSONException;
import org.json.JSONObject;

public class User {
    private String id;
    private String mbsType;
    private String userName;
    private String userEmail;
    private String userPhoneDefault;
    private String userDOB;
    private String userPassword;
    private String userAva;
    private String userAddressDefault;
    private String userAddressOther;

    public User() {}

    public User(String id, String mbsType, String userName, String userEmail, String userPhoneDefault,
                String userDOB, String userPassword, String userAva,
                String userAddressDefault, String userAddressOther) {
        this.id = id;
        this.mbsType = mbsType;
        this.userName = userName;
        this.userEmail = userEmail;
        this.userPhoneDefault = userPhoneDefault;
        this.userDOB = userDOB;
        this.userPassword = userPassword;
        this.userAva = userAva;
        this.userAddressDefault = userAddressDefault;
        this.userAddressOther = userAddressOther;
    }

    // Getters and setters (can be auto-generated)

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getMbsType() { return mbsType; }
    public void setMbsType(String mbsType) { this.mbsType = mbsType; }

    public String getUserName() { return userName; }
    public void setUserName(String userName) { this.userName = userName; }

    public String getUserEmail() { return userEmail; }
    public void setUserEmail(String userEmail) { this.userEmail = userEmail; }

    public String getUserPhoneDefault() { return userPhoneDefault; }
    public void setUserPhoneDefault(String userPhoneDefault) { this.userPhoneDefault = userPhoneDefault; }

    public String getUserDOB() { return userDOB; }
    public void setUserDOB(String userDOB) { this.userDOB = userDOB; }

    public String getUserPassword() { return userPassword; }
    public void setUserPassword(String userPassword) { this.userPassword = userPassword; }

    public String getUserAva() { return userAva; }
    public void setUserAva(String userAva) { this.userAva = userAva; }

    public String getUserAddressDefault() { return userAddressDefault; }
    public void setUserAddressDefault(String userAddressDefault) { this.userAddressDefault = userAddressDefault; }

    public String getUserAddressOther() { return userAddressOther; }
    public void setUserAddressOther(String userAddressOther) { this.userAddressOther = userAddressOther; }

    public JSONObject toJson() throws JSONException {
        JSONObject obj = new JSONObject();
        obj.put("UserName", userName);
        obj.put("UserEmail", userEmail);
        obj.put("UserPhoneDefault", userPhoneDefault);
        obj.put("UserPassword", userPassword);
        obj.put("UserDOB", userDOB);
        obj.put("UserAva", userAva);
        obj.put("MbsType", mbsType);
        obj.put("UserAddressDefault", userAddressDefault);
        obj.put("UserAddressOther", userAddressOther);
        return obj;
    }

}
