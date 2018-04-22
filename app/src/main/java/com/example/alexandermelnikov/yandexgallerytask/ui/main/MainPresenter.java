package com.example.alexandermelnikov.yandexgallerytask.ui.main;

import android.util.Log;

import com.arellomobile.mvp.InjectViewState;
import com.arellomobile.mvp.MvpPresenter;
import com.example.alexandermelnikov.yandexgallerytask.GalleryTaskApp;
import com.example.alexandermelnikov.yandexgallerytask.adapter.GalleryAdapter;
import com.example.alexandermelnikov.yandexgallerytask.api.ImagesResultHandler;
import com.example.alexandermelnikov.yandexgallerytask.data.UserDataRepository;
import com.example.alexandermelnikov.yandexgallerytask.model.api.Image;
import com.example.alexandermelnikov.yandexgallerytask.utils.SortMethods;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;


@InjectViewState
public class MainPresenter extends MvpPresenter<MainView> implements ImagesResultHandler, GalleryAdapter.OnGalleryItemClickListener {
    private static final String TAG = "MyTag";

    @Inject
    UserDataRepository mUserDataRep;

    private String mSelectedSearchSortMethod;
    private String mSearchInput;
    private String mCurrentHintObject;

    private ArrayList<Image> mCurrentImages;

    private boolean clearButtonBackModeOn;

    public MainPresenter() {
        GalleryTaskApp.getAppComponent().inject(this);
        mSearchInput = "";
        mCurrentImages = new ArrayList<>();
        clearButtonBackModeOn = false;
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
        if (clearButtonBackModeOn) {
            getViewState().animateBackButtonToClear();
            clearButtonBackModeOn = false;
        }
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
            if (mCurrentImages.isEmpty()) {
                getViewState().showImagesWithAnimation(images);
            } else {
                getViewState().showImagesNoAnimation(images);
            }
            getViewState().showHeader(mSearchInput);
            mCurrentImages.clear();
            mCurrentImages.addAll(images);
        } else {
            getViewState().showEmptySearchResultMessage();
        }
    }

    public void clearButtonPressed() {
        if (!clearButtonBackModeOn) {
            if (!mSearchInput.isEmpty()) {
                mSearchInput = "";
                getViewState().clearSearchInput();
                if (!mCurrentImages.isEmpty()) {
                    clearButtonBackModeOn = true;
                    getViewState().animateClearButtonToBack();
                } else {
                    getViewState().animateClearButton();
                }
            }
        } else {
            mCurrentImages.clear();
            getViewState().hideImagesWithAnimation();
            getViewState().hideHeader();
            getViewState().animateBackButtonToClear();
            clearButtonBackModeOn = false;
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

    @Override
    public void onGalleryItemClicked(int position) {
        getViewState().openGalleryItemPreviewDialog(mCurrentImages, position);
    }
}
