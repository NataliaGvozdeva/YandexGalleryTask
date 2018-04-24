package com.example.alexandermelnikov.yandexgallerytask.model.api;

import java.io.Serializable;
import java.util.List;

/**
 * Created by AlexMelnikov on 17.04.18.
 */

public class Photo implements Serializable {
    public int id;
    public int width;
    public int height;
    public String url;
    public String photographer;
    public Src src;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getPhotographer() {
        return photographer;
    }

    public void setPhotographer(String photographer) {
        this.photographer = photographer;
    }

    public Src getSrc() {
        return src;
    }

    public void setSrc(Src src) {
        this.src = src;
    }
}
