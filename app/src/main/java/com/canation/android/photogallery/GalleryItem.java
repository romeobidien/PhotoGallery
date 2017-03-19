package com.canation.android.photogallery;

import com.google.gson.annotations.SerializedName;

/**
 * Created by CangNguyen on 3/19/2017.
 */

public class GalleryItem {

    @SerializedName("title")
    private String mCaption;

    @SerializedName("id")
    private String mId;

    @SerializedName("url_s")
    private String mUrl;

    @Override
    public String toString() {
        return mCaption;
    }

}
