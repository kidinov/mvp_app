package org.kidinov.rijksmuseum.data.model.collection;

import com.google.gson.annotations.SerializedName;

import org.kidinov.rijksmuseum.data.model.ObjectWithLoadingTime;

import java.util.Date;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class Collection extends RealmObject implements ObjectWithLoadingTime {

    @PrimaryKey
    private String maker;

    @SerializedName("count")
    private Integer count;
    @SerializedName("artObjects")
    private RealmList<ArtObject> artObjects = new RealmList<>();

    private Date loadingTime;

    public Integer getCount() {
        return count;
    }

    public void setCount(Integer count) {
        this.count = count;
    }

    public RealmList<ArtObject> getArtObjects() {
        return artObjects;
    }

    public void setArtObjects(RealmList<ArtObject> artObjects) {
        this.artObjects = artObjects;
    }

    @Override
    public Date getLoadingTime() {
        return loadingTime;
    }

    public void setLoadingTime(Date loadingTime) {
        this.loadingTime = loadingTime;
    }

    public String getMaker() {
        return maker;
    }

    public void setMaker(String maker) {
        this.maker = maker;
    }

    @Override public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || !(o instanceof Collection)) return false;

        Collection that = (Collection) o;

        return getMaker().equals(that.getMaker());

    }

    @Override public int hashCode() {
        return getMaker().hashCode();
    }
}
