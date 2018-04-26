package com.example.alexandermelnikov.yandexgallerytask.model.realm;

import android.support.annotation.Nullable;

import java.io.Serializable;
import java.util.Date;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.RealmResults;
import io.realm.annotations.LinkingObjects;
import io.realm.annotations.PrimaryKey;

/**
 * ImageRequest.java – RealmObject child representing request for images
 * @author Alexander Melnikov
 */

public class ImageRequest extends RealmObject implements Serializable {

    @PrimaryKey
    private int id;

    private String phrase;

    @Nullable
    public RealmList<ImageSrc> sources;

    private Date requestDate;

    public ImageRequest() {
    }

    public ImageRequest(int id, String phrase) {
        this.id = id;
        this.phrase = phrase;
        requestDate = new Date();
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getPhrase() {
        return phrase;
    }

    public void setPhrase(String phrase) {
        this.phrase = phrase;
    }

    public Date getRequestDate() {
        return requestDate;
    }

    public void setRequestDate(Date requestDate) {
        this.requestDate = requestDate;
    }

    @Nullable
    public RealmList<ImageSrc> getSources() {
        return sources;
    }

    public void setSources(@Nullable RealmList<ImageSrc> sources) {
        this.sources = sources;
    }
}
