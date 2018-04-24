package com.example.alexandermelnikov.yandexgallerytask.model.api;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by AlexMelnikov on 17.04.18.
 */

public class ResponseRoot {
    @SerializedName("total_results")
    public int totalResults;

    @SerializedName("per_page")
    public int perPage;

    public int page;
    public List<Photo> photos;

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
