package com.virtualdusk.zezgn.Model;

/**
 * Created by Amit Sharma on 10/5/2016.
 */
public class DesignsList {

    private String id;
    private String name;
    private String icon;
    private String status;

    private String created;
    private String modified;
    private String designs;
    private String category_id;
    private String total;

//    /
//            "id":1,
//            "title":"Adidas",
//            "design_img":"1474115673-1.jpg",
//            "author_name":"John",
//            "number_of_colors":2,
//            "design_text":"Test",
//            "created":"2016-09-17T18:04:33+0530",
//            "design_category":{
//        "name":"Fashion"
//    },
//            "is_favorite":1




    private String title;
    private String design_img;
    private String author_name;
    private String number_of_colors;
    private String design_text;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDesign_img() {
        return design_img;
    }

    public void setDesign_img(String design_img) {
        this.design_img = design_img;
    }

    public String getAuthor_name() {
        return author_name;
    }

    public void setAuthor_name(String author_name) {
        this.author_name = author_name;
    }

    public String getNumber_of_colors() {
        return number_of_colors;
    }

    public void setNumber_of_colors(String number_of_colors) {
        this.number_of_colors = number_of_colors;
    }

    public String getDesign_text() {
        return design_text;
    }

    public void setDesign_text(String design_text) {
        this.design_text = design_text;
    }

    public String getDesign_category() {
        return design_category;
    }

    public void setDesign_category(String design_category) {
        this.design_category = design_category;
    }

    public String getDesign_category_name() {
        return design_category_name;
    }

    public void setDesign_category_name(String design_category_name) {
        this.design_category_name = design_category_name;
    }

    public String getIs_favorite() {
        return is_favorite;
    }

    public void setIs_favorite(String is_favorite) {
        this.is_favorite = is_favorite;
    }

    private String design_category;
    private String design_category_name;
    private String is_favorite;


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
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

    public String getDesigns() {
        return designs;
    }

    public void setDesigns(String designs) {
        this.designs = designs;
    }

    public String getCategory_id() {
        return category_id;
    }

    public void setCategory_id(String category_id) {
        this.category_id = category_id;
    }

    public String getTotal() {
        return total;
    }

    public void setTotal(String total) {
        this.total = total;
    }







}
