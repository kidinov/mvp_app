package org.kidinov.rijksmuseum.data.model.agenda;

import com.google.gson.annotations.SerializedName;

import io.realm.RealmObject;

public class Price extends RealmObject {

    @SerializedName("id")
    private String id;
    @SerializedName("amount")
    private Double amount;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

}
