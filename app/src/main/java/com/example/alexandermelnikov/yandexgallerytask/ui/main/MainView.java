package com.example.alexandermelnikov.yandexgallerytask.ui.main;

import android.widget.ImageView;

import com.arellomobile.mvp.viewstate.strategy.OneExecutionStateStrategy;
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType;
import com.example.alexandermelnikov.yandexgallerytask.model.api.Photo;
import com.example.alexandermelnikov.yandexgallerytask.ui.BaseView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by AlexMelnikov on 17.04.18.
 */

@StateStrategyType(OneExecutionStateStrategy.class)
public interface MainView extends BaseView {

    void setupEditTextHint(String hintObject);

    void showImagesWithAnimation(List<Photo> photos);

    void showImagesNoAnimation(List<Photo> photos);

    void hideImagesWithAnimation();

    void showProgressBar();

    void hideProgressBar();

    void showBackground();

    void hideBackground();

    void animateSearchButton();

    void animateClearButtonToBack();

    void animateBackButtonToClear();

    void animateClearButton();

    void animateEmptySearchBar();

    void clearSearchInput();

    void hideKeyboard();

    void showHeader(String lastSearchObject, int resultsCount);

    void hideHeader();

    void showEmptySearchResultMessage();

    void startApiWebsiteIntent();

    void openGalleryItemPreviewDialog(ArrayList<Photo> photos, int position, ImageView sharedImageView);
}
