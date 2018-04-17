package com.example.alexandermelnikov.yandexgallerytask.ui.main;

import com.arellomobile.mvp.InjectViewState;
import com.arellomobile.mvp.MvpPresenter;

/**
 * Created by AlexMelnikov on 17.04.18.
 */

@InjectViewState
public class MainPresenter extends MvpPresenter<MainView> {
    private static final String TAG = "MyTag";

    public MainPresenter() {
    }

    @Override
    public void attachView(MainView view) {
        super.attachView(view);
    }

    @Override
    public void detachView(MainView view) {
        super.detachView(view);
    }
}
