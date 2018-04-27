package com.example.alexandermelnikov.yandexgallerytask.model.api;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

public class ResponseRoot implements Serializable {

    @SerializedName("total_results")
    private int totalResults;

    @SerializedName("per_page")
    private int perPage;

    private int page;
    private List<Photo> photos;

    public int getTotalResults() {
        return totalResults;
    }

    public void setTotalResults(int totalResults) {
        this.totalResults = totalResults;
    }

    public int getPerPage() {
        return perPage;
    }

    public void setPerPage(int perPage) {
        this.perPage = perPage;
    }

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public List<Photo> getPhotos() {
        return photos;
    }

    public void setPhotos(List<Photo> photos) {
        this.photos = photos;
    }
}
