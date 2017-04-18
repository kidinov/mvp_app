package org.kidinov.rijksmuseum.data.model.collection;

import com.google.gson.annotations.SerializedName;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class ArtObject extends RealmObject {

    @SerializedName("id")
    @PrimaryKey
    private String id;
    @SerializedName("title")
    private String title;
    @SerializedName("webImage")
    private WebImage webImage;

    //API doesn't provide any ordering param
    private Integer fetchOrderNumber;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }


    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public WebImage getWebImage() {
        return webImage;
    }

    public void setWebImage(WebImage webImage) {
        this.webImage = webImage;
    }

    public Integer getFetchOrderNumber() {
        return fetchOrderNumber;
    }

    public void setFetchOrderNumber(Integer fetchOrderNumber) {
        this.fetchOrderNumber = fetchOrderNumber;
    }

    @Override public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || !(o instanceof ArtObject)) return false;

        ArtObject artObject = (ArtObject) o;

        return getId() != null ? getId().equals(artObject.getId()) : artObject.getId() == null;

    }

    @Override public int hashCode() {
        return getId() != null ? getId().hashCode() : 0;
    }
}
