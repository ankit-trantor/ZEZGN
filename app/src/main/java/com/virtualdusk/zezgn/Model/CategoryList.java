package com.virtualdusk.zezgn.Model;

/**
 * Created by Amit Sharma on 10/5/2016.
 */
public class CategoryList {

    private String id;
    private String name;
    private String icon;
    private String status;
    private String user_id;
    private String created;
    private String modified;
    private String designs;
    private String category_id;
    private String total;

    private String is_fav,design_id;

    public String getIs_fav() {
        return is_fav;
    }

    public void setIs_fav(String is_fav) {
        this.is_fav = is_fav;
    }

    public String getDesign_id() {
        return design_id;
    }

    public void setDesign_id(String design_id) {
        this.design_id = design_id;
    }
//    user_id” : 1,,
//            “design_id” : 4,
//            “is_fav” : 0/1,

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

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
