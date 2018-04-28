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

    /**
     * Set search edit text hint text velue
     * @param hintObject String value of an example object to search
     */
    void setupSearchEditTextHint(String hintObject);

    /**
     * Show images recycler view with animation
     * @param sources List of ImageSrc objects to display on recycler view
     */
    void showImagesWithAnimation(ArrayList<ImageSrc> sources);

    /**
     * Show images recycler view without animation
     * @param sources List of ImageSrc objects to display on recycler view
     */
    void showImagesNoAnimation(ArrayList<ImageSrc> sources);

    /**
     * Show history recycler view without animation
     * @param requests List of ImageRequest objects to display on recycler view
     */
    void showHistoryNoAnimation(ArrayList<ImageRequest> requests);

    /**
     * Show history recycler view with animation
     * @param requests List of ImageRequest objects to display on recycler view
     */
    void showHistoryWithAnimation(ArrayList<ImageRequest> requests);

    /**
     * Hide history recycler view
     */
    void hideHistory();

    /**
     * Hide images recycler view with animation
     */
    void hideImagesWithAnimation();

    /**
     * Show dialog with information about the app
     */
    void showAppInfoDialog();

    /**
     * Show loading images progress bar
     */
    void showProgressBar();

    /**
     * Hide loading images progress bar
     */
    void hideProgressBar();

    void showNoConnectionMessage();

    void hideNoConnectionMessage();

    void showOptionButtons();

    void hideOptionButtons();

    void animateSearchButton();

    void animateClearButtonToBack();

    void animateBackButtonToClear();

    void animateClearButton();

    void animateEmptySearchBar();

    void clearSearchInput();

    void hideKeyboard();

    /**
     * Show header ViewGroup
     * @param lastSearchObject The String of last searched object
     * @param resultsCount Int value of all found images count
     */
    void showHeader(String lastSearchObject, int resultsCount);

    void hideHeader();

    void showEmptySearchResultMessage();

    void showEmptyHistroyMessage();

    void showSnackbarMessage(String message);

    void startApiWebsiteIntent();

    /**
     * Show fullscreen image DialogFragment
     * @param imageRequest ImageRequest object from which images will be shown
     * @param position Position of the picked image to show firstly
     */
    void openGalleryItemPreviewDialogFragment(ImageRequest imageRequest, int position);
}
