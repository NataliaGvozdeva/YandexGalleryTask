package com.example.alexandermelnikov.yandexgallerytask.ui.main;

import android.util.Log;
import android.widget.ImageView;

import com.arellomobile.mvp.InjectViewState;
import com.arellomobile.mvp.MvpPresenter;
import com.example.alexandermelnikov.yandexgallerytask.GalleryTaskApp;
import com.example.alexandermelnikov.yandexgallerytask.adapter.GalleryAdapter;
import com.example.alexandermelnikov.yandexgallerytask.api.ImagesResultHandler;
import com.example.alexandermelnikov.yandexgallerytask.data.UserDataRepository;
import com.example.alexandermelnikov.yandexgallerytask.model.api.Photo;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;


@InjectViewState
public class MainPresenter extends MvpPresenter<MainView> implements ImagesResultHandler,
        GalleryAdapter.OnGalleryItemClickListener {
    private static final String TAG = "MyTag";

    @Inject
    UserDataRepository mUserDataRep;

    private String mSearchInput;
    private String mCurrentHintObject;

    private String lastImagesRequestPhrase;

    private ArrayList<Photo> mCurrentPhotos;

    private boolean clearButtonBackModeOn;

    public MainPresenter() {
        GalleryTaskApp.getAppComponent().inject(this);
        mSearchInput = "";
        mCurrentPhotos = new ArrayList<>();
        clearButtonBackModeOn = false;
    }

    @Override
    public void attachView(MainView view) {
        super.attachView(view);
        getViewState().attachInputListeners();

        //Check if mCurrentPhotos is not empty and show those previously loaded images if so
        if (!mCurrentPhotos.isEmpty()) {
            showImages(false);
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
        lastImagesRequestPhrase = phrase;
        if (!phrase.isEmpty()) {
            GalleryTaskApp.getApiHelper().getImages(phrase, this);
            getViewState().animateSearchButton();
            getViewState().showProgressBar();
            getViewState().hideKeyboard();
        } else {
            getViewState().animateEmptySearchBar();
        }
    }

    @Override
    public void onImagesResultPassed(List<Photo> photos) {
        if (!photos.isEmpty()) {
            if (mCurrentPhotos.isEmpty()) {
                mCurrentPhotos.clear();
                mCurrentPhotos.addAll(photos);
                showImages(true);
            } else {
                mCurrentPhotos.clear();
                mCurrentPhotos.addAll(photos);
                showImages(false);
            }

        } else {
            getViewState().showEmptySearchResultMessage();
        }
        getViewState().hideProgressBar();
    }

    public void clearButtonPressed() {
        if (!clearButtonBackModeOn) {
            if (!mSearchInput.isEmpty()) {
                mSearchInput = "";
                getViewState().clearSearchInput();
                if (!mCurrentPhotos.isEmpty()) {
                    clearButtonBackModeOn = true;
                    getViewState().animateClearButtonToBack();
                } else {
                    getViewState().animateClearButton();
                }
            }
        } else {
            hideImages();
        }
    }

    private void showImages(boolean withAnimation) {
        if (withAnimation) {
            getViewState().showImagesWithAnimation(mCurrentPhotos);
        } else {
            getViewState().showImagesNoAnimation(mCurrentPhotos);
        }
        getViewState().hideBackground();
        getViewState().showHeader(lastImagesRequestPhrase, mCurrentPhotos.size());
    }

    public void hideImages() {
        mCurrentPhotos.clear();
        getViewState().hideImagesWithAnimation();
        getViewState().hideHeader();
        getViewState().showBackground();
        if (clearButtonBackModeOn) {
            getViewState().animateBackButtonToClear();
            clearButtonBackModeOn = false;
        }
    }

    @Override
    public void onGalleryItemClicked(int position, ImageView sharedImageView) {
        getViewState().openGalleryItemPreviewDialog(mCurrentPhotos, position, sharedImageView);
    }

    public void apiLogoPressed() {
        getViewState().startApiWebsiteIntent();
    }
}
