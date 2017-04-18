package org.kidinov.rijksmuseum.data.model.agenda;

import com.google.gson.annotations.SerializedName;

import java.util.Date;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class Option extends RealmObject {

    @SerializedName("id")
    @PrimaryKey
    private String id;
    @SerializedName("period")
    private Period period;
    @SerializedName("exposition")
    private Exposition exposition;
    @SerializedName("expositionType")
    private ExpositionType expositionType;
    @SerializedName("date")
    private Date date;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Period getPeriod() {
        return period;
    }

    public void setPeriod(Period period) {
        this.period = period;
    }

    public Exposition getExposition() {
        return exposition;
    }

    public void setExposition(Exposition exposition) {
        this.exposition = exposition;
    }

    public ExpositionType getExpositionType() {
        return expositionType;
    }

    public void setExpositionType(ExpositionType expositionType) {
        this.expositionType = expositionType;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    @Override public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || !(o instanceof Option)) return false;

        Option option = (Option) o;

        return getId().equals(option.getId());

    }

    @Override public int hashCode() {
        return getId().hashCode();
    }
}
