package org.kidinov.rijksmuseum.data.model.collection;

import com.google.gson.annotations.SerializedName;

import io.realm.RealmObject;

public class WebImage extends RealmObject {

    @SerializedName("guid")
    private String guid;
    @SerializedName("offsetPercentageX")
    private Integer offsetPercentageX;
    @SerializedName("offsetPercentageY")
    private Integer offsetPercentageY;
    @SerializedName("width")
    private Integer width;
    @SerializedName("height")
    private Integer height;
    @SerializedName("url")
    private String url;

    public String getGuid() {
        return guid;
    }

    public void setGuid(String guid) {
        this.guid = guid;
    }

    public Integer getOffsetPercentageX() {
        return offsetPercentageX;
    }

    public void setOffsetPercentageX(Integer offsetPercentageX) {
        this.offsetPercentageX = offsetPercentageX;
    }

    public Integer getOffsetPercentageY() {
        return offsetPercentageY;
    }

    public void setOffsetPercentageY(Integer offsetPercentageY) {
        this.offsetPercentageY = offsetPercentageY;
    }

    public Integer getWidth() {
        return width;
    }

    public void setWidth(Integer width) {
        this.width = width;
    }

    public Integer getHeight() {
        return height;
    }

    public void setHeight(Integer height) {
        this.height = height;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

}
