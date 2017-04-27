package com.virtualdusk.zezgn.Model;

/**
 * Created by Amit Sharma on 10/17/2016.
 */
public class Coupon {

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getDesign_id() {
        return design_id;
    }

    public void setDesign_id(String design_id) {
        this.design_id = design_id;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getOrder_id() {
        return order_id;
    }

    public void setOrder_id(String order_id) {
        this.order_id = order_id;
    }

    public String getIs_read() {
        return is_read;
    }

    public void setIs_read(String is_read) {
        this.is_read = is_read;
    }

    public String getCreated() {
        return created;
    }

    public void setCreated(String created) {
        this.created = created;
    }

    public String getUpdated() {
        return updated;
    }

    public void setUpdated(String updated) {
        this.updated = updated;
    }

    public String getCoupon_id() {
        return coupon_id;
    }

    public void setCoupon_id(String coupon_id) {
        this.coupon_id = coupon_id;
    }

    public String getCoupon_code() {
        return coupon_code;
    }

    public void setCoupon_code(String coupon_code) {
        this.coupon_code = coupon_code;
    }

    public String getCoupon_discount_type() {
        return coupon_discount_type;
    }

    public void setCoupon_discount_type(String coupon_discount_type) {
        this.coupon_discount_type = coupon_discount_type;
    }

    public String getCoupon_amount_or_percentage() {
        return coupon_amount_or_percentage;
    }

    public void setCoupon_amount_or_percentage(String coupon_amount_or_percentage) {
        this.coupon_amount_or_percentage = coupon_amount_or_percentage;
    }

    public String getCoupon_applied_on() {
        return coupon_applied_on;
    }

    public void setCoupon_applied_on(String coupon_applied_on) {
        this.coupon_applied_on = coupon_applied_on;
    }

    public String getCoupon_start_date() {
        return coupon_start_date;
    }

    public void setCoupon_start_date(String coupon_start_date) {
        this.coupon_start_date = coupon_start_date;
    }

    public String getCoupon_end_date() {
        return coupon_end_date;
    }

    public void setCoupon_end_date(String coupon_end_date) {
        this.coupon_end_date = coupon_end_date;
    }

    public String getCoupon_status() {
        return coupon_status;
    }

    public void setCoupon_status(String coupon_status) {
        this.coupon_status = coupon_status;
    }

    public String getCoupon_created() {
        return coupon_created;
    }

    public void setCoupon_created(String coupon_created) {
        this.coupon_created = coupon_created;
    }

    public String getCoupon_modified() {
        return coupon_modified;
    }

    public void setCoupon_modified(String coupon_modified) {
        this.coupon_modified = coupon_modified;
    }

    private String id,type,design_id,coupon,user_id,order_id,is_read,created,updated,
    coupon_id,coupon_code,coupon_discount_type,coupon_amount_or_percentage,coupon_applied_on,
            coupon_start_date,coupon_end_date,coupon_status,coupon_created,coupon_modified;

    public String getCoupon() {
        return coupon;
    }

    public void setCoupon(String coupon) {
        this.coupon = coupon;
    }
}
