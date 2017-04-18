package org.kidinov.rijksmuseum.data.model.agenda;

import com.google.gson.annotations.SerializedName;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class Period extends RealmObject {

    @SerializedName("id")
    @PrimaryKey
    private String id;
    @SerializedName("text")
    private String text;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

}
