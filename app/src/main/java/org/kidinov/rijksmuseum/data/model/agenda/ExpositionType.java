package org.kidinov.rijksmuseum.data.model.agenda;

import com.google.gson.annotations.SerializedName;

import io.realm.RealmObject;

public class ExpositionType extends RealmObject {

    @SerializedName("friendlyName")
    private String friendlyName;

    public String getFriendlyName() {
        return friendlyName;
    }

    public void setFriendlyName(String friendlyName) {
        this.friendlyName = friendlyName;
    }

}
