package com.example.alexandermelnikov.yandexgallerytask;

import android.app.Application;

import com.example.alexandermelnikov.yandexgallerytask.api.ApiHelper;

/**
 * Created by AlexMelnikov on 17.04.18.
 */

public class GalleryTaskApp extends Application {
    private static ApiHelper apiHelper;

    @Override
    public void onCreate() {
        super.onCreate();

        apiHelper = new ApiHelper();
    }

    public static ApiHelper getApiHelper() {
        return apiHelper;
    }
}
