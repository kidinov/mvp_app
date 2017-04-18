package org.kidinov.rijksmuseum.data.model.agenda;

import com.google.gson.annotations.SerializedName;

import org.kidinov.rijksmuseum.data.model.ObjectWithLoadingTime;

import java.util.Date;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class Agenda extends RealmObject implements ObjectWithLoadingTime {

    @PrimaryKey
    private String agendaDateString; //workaround for problem that it impossible to have PK as a Date type in Realm
    private Date agendaDate;
    @SerializedName("options")
    private RealmList<Option> options = new RealmList<>();
    private Date loadingTime;

    public RealmList<Option> getOptions() {
        return options;
    }

    public void setOptions(RealmList<Option> options) {
        this.options = options;
    }

    public Date getCreationTime() {
        return loadingTime;
    }

    public Date getAgendaDate() {
        return agendaDate;
    }

    public void setAgendaDate(Date agendaDate) {
        this.agendaDate = agendaDate;
    }

    public String getAgendaDateString() {
        return agendaDateString;
    }

    public void setAgendaDateString(String agendaDateString) {
        this.agendaDateString = agendaDateString;
    }

    @Override
    public Date getLoadingTime() {
        return loadingTime;
    }

    public void setLoadingTime(Date loadingTime) {
        this.loadingTime = loadingTime;
    }

    @Override public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || !(o instanceof Agenda)) return false;

        Agenda agenda = (Agenda) o;

        return getAgendaDateString().equals(agenda.getAgendaDateString());

    }

    @Override public int hashCode() {
        return getAgendaDateString().hashCode();
    }
}
