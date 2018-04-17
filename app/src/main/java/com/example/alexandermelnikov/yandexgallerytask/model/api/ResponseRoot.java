package com.example.alexandermelnikov.yandexgallerytask.model.api;

import java.util.List;

/**
 * Created by AlexMelnikov on 17.04.18.
 */

public class ResponseRoot {
    public int result_count;
    public List<Image> images;

    public ResponseRoot() {
    }

    public int getResult_count() {
        return result_count;
    }

    public void setResult_count(int result_count) {
        this.result_count = result_count;
    }

    public List<Image> getImages() {
        return images;
    }

    public void setImages(List<Image> images) {
        this.images = images;
    }
}
