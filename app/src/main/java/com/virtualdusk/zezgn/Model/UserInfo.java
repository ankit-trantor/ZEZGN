package com.virtualdusk.zezgn.Model;

import java.io.File;

/**
 * Created by Amit Sharma on 9/26/2016.
 */
public class UserInfo {

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getAge_group() {
        return age_group;
    }

    public void setAge_group(String age_group) {
        this.age_group = age_group;
    }

    public String getConfirmation_code() {
        return confirmation_code;
    }

    public void setConfirmation_code(String confirmation_code) {
        this.confirmation_code = confirmation_code;
    }

    public String getContact_no() {
        return contact_no;
    }

    public void setContact_no(String contact_no) {
        this.contact_no = contact_no;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getCountry_id() {
        return country_id;
    }

    public void setCountry_id(String country_id) {
        this.country_id = country_id;
    }

    public String getZipcode() {
        return zipcode;
    }

    public void setZipcode(String zipcode) {
        this.zipcode = zipcode;
    }

    public String getProfile_pic() {
        return profile_pic;
    }

    public void setProfile_pic(String profile_pic) {
        this.profile_pic = profile_pic;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getSocial_register() {
        return social_register;
    }

    public void setSocial_register(String social_register) {
        this.social_register = social_register;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getIs_block() {
        return is_block;
    }

    public void setIs_block(String is_block) {
        this.is_block = is_block;
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

    public String getNotification_on() {
        return notification_on;
    }

    public void setNotification_on(String notification_on) {
        this.notification_on = notification_on;
    }

    public String getCreated() {
        return created;
    }

    public void setCreated(String created) {
        this.created = created;
    }

    public String getModified() {
        return modified;
    }

    public void setModified(String modified) {
        this.modified = modified;
    }

    private String id;//40,
    private String email;//arif.khan@endivesoftware.com,
    private String password;//$2y$10$H9VQBjfOU14ZZUxABL581.xOqt3kdfp.Yoftt6bu4.oNL.n3zDoI2,
    private String fname;//arif,
    private String lname;//khan,
    private String gender;//Female,
    private String age_group;//25-34,
    private String contact_no;//8696251627,
    private String address;//railway colony,
    private String city;//ggc,
    private String state;//rajasthan,
    private String country_id;//101,

    private String zipcode;//322201,
    private String profile_pic;//1474105703-79.png,
    private String role;//2,
    private String social_register;//true,
    private String status;//true,
    private String is_block;//false,
    private String device_type;//android,
    private String device_token;//123565465,
    private String notification_on;//true,
    private String created;//2016-08-10T18:28:38+0530,
    private String modified;//2016-09-22T16:39:12+0530

    private String user_id,confirmation_code;

    private File image;

    public String getUser_id() {
        return user_id;
    }

    private String old_password,new_password;

    public String getOld_password() {
        return old_password;
    }

    public void setOld_password(String old_password) {
        this.old_password = old_password;
    }

    public String getNew_password() {
        return new_password;
    }

    public void setNew_password(String new_password) {
        this.new_password = new_password;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }
}
