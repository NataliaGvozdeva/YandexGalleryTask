package com.example.alexandermelnikov.yandexgallerytask.ui.main;

import com.arellomobile.mvp.viewstate.strategy.OneExecutionStateStrategy;
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType;
import com.example.alexandermelnikov.yandexgallerytask.model.api.Image;
import com.example.alexandermelnikov.yandexgallerytask.ui.BaseView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by AlexMelnikov on 17.04.18.
 */

@StateStrategyType(OneExecutionStateStrategy.class)
public interface MainView extends BaseView {

    void setupEditTextHint(String hintObject);

    void showImagesWithAnimation(List<Image> images);

    void showImagesNoAnimation(List<Image> images);

    void hideImagesWithAnimation();

    void animateSearchButton();

    void animateClearButtonToBack();

    void animateBackButtonToClear();

    void animateClearButton();

    void showSortMethodsDialog();

    void animateEmptySearchBar();

    void clearSearchInput();

    void hideKeyboard();

    void showHeader(String lastSearchObject);

    void hideHeader();

    void showEmptySearchResultMessage();

    void openGalleryItemPreviewDialog(ArrayList<Image> images, int position);
}
