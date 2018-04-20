package com.example.alexandermelnikov.yandexgallerytask.ui.main;

import android.util.Log;

import com.arellomobile.mvp.InjectViewState;
import com.arellomobile.mvp.MvpPresenter;
import com.example.alexandermelnikov.yandexgallerytask.GalleryTaskApp;
import com.example.alexandermelnikov.yandexgallerytask.api.ImagesResultHandler;
import com.example.alexandermelnikov.yandexgallerytask.data.UserDataRepository;
import com.example.alexandermelnikov.yandexgallerytask.model.api.Image;
import com.example.alexandermelnikov.yandexgallerytask.utils.SortMethods;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

/**
 * Created by AlexMelnikov on 17.04.18.
 */

@InjectViewState
public class MainPresenter extends MvpPresenter<MainView> implements ImagesResultHandler {
    private static final String TAG = "MyTag";

    @Inject
    UserDataRepository mUserDataRep;

    private String mSelectedSearchSortMethod;
    private String mSearchInput;
    private String mCurrentHintObject;


    public MainPresenter() {
        GalleryTaskApp.getAppComponent().inject(this);
        mSearchInput = "";
    }

    @Override
    public void attachView(MainView view) {
        super.attachView(view);
        getViewState().attachInputListeners();

        mSelectedSearchSortMethod = (String) mUserDataRep.getValue(mUserDataRep.SEARCH_SORT_METHOD, null);
        if (mSelectedSearchSortMethod == null) {
            mUserDataRep.putValue(mUserDataRep.SEARCH_SORT_METHOD, SortMethods.best.toString());
            mSelectedSearchSortMethod = SortMethods.best.toString();
        }

        getViewState().setupEditTextHint(mCurrentHintObject);
    }

    @Override
    public void detachView(MainView view) {
        super.detachView(view);
    }

    public void setupSearchBarHint(String hintObject) {
        if (mCurrentHintObject == null) {
            mCurrentHintObject = hintObject;
        }
    }

    public void searchInputChanges(String input) {
        mSearchInput = input;
    }

    public void loadImagesRequest(String phrase) {
        if (!phrase.isEmpty()) {
            GalleryTaskApp.getApiHelper().getImages(phrase, mSelectedSearchSortMethod, this);
            getViewState().animateSearchButton();
        } else {
            getViewState().animateEmptySearchBar();
        }
        getViewState().hideKeyboard();
    }

    @Override
    public void onImagesResultPassed(List<Image> images) {
        if (!images.isEmpty()) {
            getViewState().replaceGalleryData(images);
            getViewState().showHeader(mSearchInput);
        } else {
            getViewState().showSnackbarMessage("The search has not given any results");
        }
    }

    public void clearSearchRequest() {
        if (!mSearchInput.isEmpty()) {
            mSearchInput = "";
            getViewState().clearSearchInput();
            getViewState().animateClearButton();
        }
    }

    public void sortButtonPressed() {
        getViewState().showSortMethodsDialog();
    }

    public void sortMethodPicked(int option) {
        switch (option) {
            case 0:
                mUserDataRep.putValue(mUserDataRep.SEARCH_SORT_METHOD, SortMethods.best.toString());
                mSelectedSearchSortMethod = SortMethods.best.toString();
                break;
            case 1:
                mUserDataRep.putValue(mUserDataRep.SEARCH_SORT_METHOD, SortMethods.most_popular.toString());
                mSelectedSearchSortMethod = SortMethods.most_popular.toString();
                break;
            case 2:
                mUserDataRep.putValue(mUserDataRep.SEARCH_SORT_METHOD, SortMethods.newest.toString());
                mSelectedSearchSortMethod = SortMethods.newest.toString();
                break;
        }
        Log.d(TAG, "sortMethodPicked: "  + mSelectedSearchSortMethod);
    }
}
