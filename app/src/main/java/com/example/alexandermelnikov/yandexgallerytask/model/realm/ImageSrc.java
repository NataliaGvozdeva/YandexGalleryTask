package com.example.alexandermelnikov.yandexgallerytask.model.realm;

import java.io.Serializable;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * ImageSrc.java – RealmObject child representing list of image sources (url's for image downloads)
 * @author Alexander Melnikov
 */

public class ImageSrc extends RealmObject implements Serializable {

    @PrimaryKey
    private int id;

    private String requestPhrase;

    private String thumbnailUrl;

    private String largeUrl;

    private String photographer;

    private String pexelsUrl;

    public ImageSrc() {
    }

    public ImageSrc(int id, String requestPhrase, String thumbnailUrl, String largeUrl, String photographer, String pexelsUrl) {
        this.id = id;
        this.requestPhrase = requestPhrase;
        this.thumbnailUrl = thumbnailUrl;
        this.largeUrl = largeUrl;
        this.photographer = photographer;
        this.pexelsUrl = pexelsUrl;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getRequestPhrase() {
        return requestPhrase;
    }

    public void setRequestPhrase(String requestPhrase) {
        this.requestPhrase = requestPhrase;
    }

    public String getThumbnailUrl() {
        return thumbnailUrl;
    }

    public void setThumbnailUrl(String thumbnailUrl) {
        this.thumbnailUrl = thumbnailUrl;
    }

    public String getLargeUrl() {
        return largeUrl;
    }

    public void setLargeUrl(String largeUrl) {
        this.largeUrl = largeUrl;
    }

    public String getPhotographer() {
        return photographer;
    }

    public void setPhotographer(String photographer) {
        this.photographer = photographer;
    }

    public String getPexelsUrl() {
        return pexelsUrl;
    }

    public void setPexelsUrl(String pexelsUrl) {
        this.pexelsUrl = pexelsUrl;
    }

}
