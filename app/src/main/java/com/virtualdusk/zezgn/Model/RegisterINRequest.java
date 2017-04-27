package com.virtualdusk.zezgn.Model;

/**
 * Created by Amit Sharma on 9/26/2016.
 */
public class RegisterINRequest {

    public String getFname() {
        return fname;
    }

    public void setFname(String fname) {
        this.fname = fname;
    }

    public String getLname() {
        return lname;
    }

    public void setLname(String lname) {
        this.lname = lname;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getContact_no() {
        return contact_no;
    }

    public void setContact_no(String contact_no) {
        this.contact_no = contact_no;
    }

    public String getCountry_id() {
        return country_id;
    }

    public void setCountry_id(String country_id) {
        this.country_id = country_id;
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

    private String fname,lname,email,
            contact_no,country_id,
            password,device_type,device_token,social_register;


//    fname” : “Kishore”,
//            “lname” : “Suthar”,
//            “email” : Kishore.suthar@endivesoftware.com,
//            “contact_no” : 9782135185,
//            “country_id” : 21,
//            “password”: “*******”
//            “device_type”: “iphone”
//            “device_token” : “sffsfdsfsfssd”
//            “social_register”: “true/false”,

}
