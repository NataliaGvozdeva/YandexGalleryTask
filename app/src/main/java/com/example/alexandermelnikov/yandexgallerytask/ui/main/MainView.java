package com.example.alexandermelnikov.yandexgallerytask.ui.main;

import android.widget.ImageView;

import com.arellomobile.mvp.viewstate.strategy.OneExecutionStateStrategy;
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType;
import com.example.alexandermelnikov.yandexgallerytask.model.api.Photo;
import com.example.alexandermelnikov.yandexgallerytask.model.realm.ImageRequest;
import com.example.alexandermelnikov.yandexgallerytask.model.realm.ImageSrc;
import com.example.alexandermelnikov.yandexgallerytask.ui.BaseView;

import java.util.ArrayList;
import java.util.List;

/**
 * MainView.java – main application activity view class
 * @author Alexander Melnikov
 */

@StateStrategyType(OneExecutionStateStrategy.class)
public interface MainView extends BaseView {

    void setupEditTextHint(String hintObject);

    void showImagesWithAnimation(ArrayList<ImageSrc> sources);

    void showImagesNoAnimation(ArrayList<ImageSrc> sources);

    void showHistoryNoAnimation(ArrayList<ImageRequest> requests);

    void showHistoryWithAnimation(ArrayList<ImageRequest> requests);

    void hideHistory();

    void hideImagesWithAnimation();

    void showAppInfoDialog();

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

    void showEmptyHistroyMessage();

    void showSnackbarMessage(String message);

    void startApiWebsiteIntent();

    void openGalleryItemPreviewDialogFragment(ImageRequest imageRequest, int position);
}
