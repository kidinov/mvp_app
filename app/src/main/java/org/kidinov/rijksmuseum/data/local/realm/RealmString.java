package org.kidinov.rijksmuseum.data.local.realm;


import io.realm.RealmObject;

/**
 * Class that allow use gson and Realm together. Probably it's outdated already and realm play with gson well with it.
 * TODO check it
 */

public class RealmString extends RealmObject {
    private String val;

    public RealmString() {
    }

    public RealmString(String val) {
        this.val = val;
    }

    public String getVal() {
        return val;
    }

    public void setVal(String val) {
        this.val = val;
    }
}