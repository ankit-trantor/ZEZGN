package com.virtualdusk.zezgn.Model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by bhart.gupta on 21-Nov-16.
 */

public class CreationParcelable implements Parcelable {

    public String strDesignImage = "",
            strProductImage = "",
            strId = "",
            strTitle = "",
            strProductStyleId = "",
            strModifiedDate = "",
            strProductView = "",
            strProductId = "",
            strDesignId = "",
            strprodutcIdX = "",
            strUserId = "",
            strDesignCategoryId = "",
            strDesignTitle = "";


    public CreationParcelable( String strDesignImage,String strProductImage,String  strId,String strTitle,String strProductStyleId,
                               String  strModifiedDate,String strProductView,String strProductId,String  strDesignId,String strprodutcIdX,
                               String  strUserId,String strDesignCategoryId,String strDesignTitle ){

        this.strDesignImage = strDesignImage;
        this.strProductImage = strProductImage;
        this.strId = strId;
        this.strTitle = strTitle;
        this.strProductStyleId = strProductStyleId;
        this.strModifiedDate = strModifiedDate;
        this.strProductView = strProductView;
        this.strProductId = strProductId;
        this.strDesignId = strDesignId;
        this.strprodutcIdX = strprodutcIdX;
        this.strUserId = strUserId;
        this.strDesignCategoryId = strDesignCategoryId;
        this.strDesignTitle = strDesignTitle;


    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(strDesignImage);
        parcel.writeString(strProductImage);
        parcel.writeString(strId);
        parcel.writeString(strTitle);
        parcel.writeString(strProductStyleId);
        parcel.writeString(strModifiedDate);
        parcel.writeString(strProductView);
        parcel.writeString(strProductId);
        parcel.writeString(strDesignId);
        parcel.writeString(strprodutcIdX);
        parcel.writeString(strUserId);
        parcel.writeString(strDesignCategoryId);
        parcel.writeString(strDesignTitle);

    }

    // Creator
    public static final Parcelable.Creator<CreationParcelable> CREATOR
            = new Parcelable.Creator<CreationParcelable>() {
        public CreationParcelable createFromParcel(Parcel in) {
            return new CreationParcelable(in);
        }

        public CreationParcelable[] newArray(int size) {
            return new CreationParcelable[size];
        }
    };

    // "De-parcel object
    public CreationParcelable(Parcel in) {
        this.strDesignImage = in.readString();
        this.strProductImage = in.readString();
        this.strId = in.readString();
        this.strTitle = in.readString();
        this.strProductStyleId = in.readString();
        this.strModifiedDate = in.readString();
        this.strProductView = in.readString();
        this.strProductId = in.readString();
        this.strDesignId = in.readString();
        this.strprodutcIdX = in.readString();
        this.strUserId = in.readString();
        this.strDesignCategoryId = in.readString();
        this.strDesignTitle = in.readString();

    }


}
