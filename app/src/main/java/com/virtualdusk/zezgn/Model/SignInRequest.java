package com.virtualdusk.zezgn.Model;

/**
 * Created by Amit Sharma on 9/26/2016.
 */
public class SignInRequest {

    private String  email,password,device_type,device_token,social_register;

    private String contact_no;

    public String getContact_no() {
        return contact_no;
    }

    public void setContact_no(String contact_no) {
        this.contact_no = contact_no;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getDevice_type() {
        return device_type;
    }

    public void setDevice_type(String device_type) {
        this.device_type = device_type;
    }

    public String getDevice_token() {
        return device_token;
    }

    public void setDevice_token(String device_token) {
        this.device_token = device_token;
    }

    public String getSocial_register() {
        return social_register;
    }

    public void setSocial_register(String social_register) {
        this.social_register = social_register;
    }
}
