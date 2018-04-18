package com.example.alexandermelnikov.yandexgallerytask.ui.main;

import com.arellomobile.mvp.InjectViewState;
import com.arellomobile.mvp.MvpPresenter;
import com.example.alexandermelnikov.yandexgallerytask.GalleryTaskApp;
import com.example.alexandermelnikov.yandexgallerytask.api.ImagesResultHandler;
import com.example.alexandermelnikov.yandexgallerytask.model.api.Image;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by AlexMelnikov on 17.04.18.
 */

@InjectViewState
public class MainPresenter extends MvpPresenter<MainView> implements ImagesResultHandler {
    private static final String TAG = "MyTag";

    public MainPresenter() {
    }

    @Override
    public void attachView(MainView view) {
        super.attachView(view);
        getViewState().attachInputListeners();
    }

    @Override
    public void detachView(MainView view) {
        super.detachView(view);
    }


    public void loadImagesRequest() {
        GalleryTaskApp.getApiHelper().getImages("dogs", "best", this);
    }

    @Override
    public void onImagesResultPassed(List<Image> images) {
        getViewState().replaceGalleryData(images);
    }
}
