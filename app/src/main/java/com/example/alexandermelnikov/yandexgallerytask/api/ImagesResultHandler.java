package com.example.alexandermelnikov.yandexgallerytask.api;

import com.example.alexandermelnikov.yandexgallerytask.model.api.Image;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by AlexMelnikov on 18.04.18.
 */

public interface ImagesResultHandler {

    void onImagesResultPassed(List<Image> images);

}
