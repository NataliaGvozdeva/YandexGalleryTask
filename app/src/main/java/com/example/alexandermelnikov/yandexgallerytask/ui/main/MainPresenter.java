package com.example.alexandermelnikov.yandexgallerytask.ui.main;

import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.ImageView;

import com.arellomobile.mvp.InjectViewState;
import com.arellomobile.mvp.MvpPresenter;
import com.example.alexandermelnikov.yandexgallerytask.GalleryTaskApp;
import com.example.alexandermelnikov.yandexgallerytask.adapter.GalleryAdapter;
import com.example.alexandermelnikov.yandexgallerytask.adapter.HistoryAdapter;
import com.example.alexandermelnikov.yandexgallerytask.api.ApiHelper;
import com.example.alexandermelnikov.yandexgallerytask.data.ImageRequestsRepository;
import com.example.alexandermelnikov.yandexgallerytask.data.ImageSrcRepository;
import com.example.alexandermelnikov.yandexgallerytask.data.UserDataRepository;
import com.example.alexandermelnikov.yandexgallerytask.model.realm.ImageRequest;
import com.example.alexandermelnikov.yandexgallerytask.model.realm.ImageSrc;

import java.util.ArrayList;

import javax.inject.Inject;


@InjectViewState
public class MainPresenter extends MvpPresenter<MainView> implements ApiHelper.ImagesResultHandler,
        GalleryAdapter.OnGalleryItemClickListener, HistoryAdapter.OnHistoryItemClickListener {
    private static final String TAG = "MyTag";

    @Inject
    UserDataRepository userDataRepository;

    @Inject
    ImageRequestsRepository imageRequestsRepository;

    @Inject
    ImageSrcRepository imageSrcRepository;


    private String mSearchInput;
    private String mCurrentHintObject;

    private String mLastImagesRequestPhrase;

    private ImageRequest mLastLoadedImageRequest;
    private ArrayList<ImageSrc> mCurrentSources;

    private boolean clearButtonBackModeOn;
    private boolean infoDialogIsShowing;
    private boolean historyIsShowing;

    public MainPresenter() {
        GalleryTaskApp.getAppComponent().inject(this);
        mSearchInput = "";
        mCurrentSources = new ArrayList<>();
        clearButtonBackModeOn = false;
        historyIsShowing = false;
        infoDialogIsShowing = false;
    }

    @Override
    public void attachView(MainView view) {
        super.attachView(view);
        getViewState().attachInputListeners();

        //Check if mCurrentSources is not empty and show those previously loaded images if so
        if (!mCurrentSources.isEmpty()) {
            showImages(false);
        }
        //Check if history recycler has been showing on previous attach
        if (historyIsShowing) {
            showHistoryRequest(false);
        }
        //Check if application information dialog has been showing on previous attach
        if (infoDialogIsShowing) {
            showApplicationInfo();
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
        if (!input.isEmpty() && clearButtonBackModeOn) {
            getViewState().animateBackButtonToClear();
            clearButtonBackModeOn = false;
        }
    }

    public void loadImagesRequest(String phrase) {
        if (historyIsShowing) {
            hideHistory();
        }

        mLastImagesRequestPhrase = phrase;
        if (!phrase.isEmpty()) {
            getViewState().animateSearchButton();
            getViewState().hideKeyboard();
            /*
             * Verify if request by the given phrase has not yet been made
             * If it has been made previously then get image sources from Realm
             * Request images from api otherwise
             */
            ImageRequest similarRequestFromDb = imageRequestsRepository.getImageRequestByRequestPhraseFromRealm(phrase);
            if (similarRequestFromDb == null) {
                GalleryTaskApp.getApiHelper().getImages(phrase, this);
                getViewState().showProgressBar();
            } else {
                boolean sourcesEmpty = mCurrentSources.isEmpty();
                mCurrentSources = imageSrcRepository.getImageSrcByRequestPhrase(phrase);
                showImages(sourcesEmpty);
                mLastLoadedImageRequest = imageRequestsRepository.getImageRequestByRequestPhraseFromRealm(phrase);
                imageRequestsRepository.updateImageRequestDateByPhrase(mLastLoadedImageRequest.getPhrase());
            }
        } else {
            getViewState().animateEmptySearchBar();
        }
    }

    @Override
    public void onImagesResultSuccessfulResponse(ImageRequest imageRequest, ArrayList<ImageSrc> imageSources) {
        if (!imageSources.isEmpty()) {
            boolean sourcesEmpty = mCurrentSources.isEmpty();
            mCurrentSources.clear();
            mCurrentSources.addAll(imageSources);
            showImages(sourcesEmpty);

            mLastLoadedImageRequest = imageRequest;
            insertLastRequestAndSourcesToRealm();
        } else {
            getViewState().showEmptySearchResultMessage();
        }
        getViewState().hideProgressBar();
    }

    @Override
    public void onImagesResultFailure(String message) {
        getViewState().hideProgressBar();
        getViewState().showSnackbarMessage(message);
    }

    public void clearButtonPressed() {
        if (!clearButtonBackModeOn) {
            if (!mSearchInput.isEmpty()) {
                mSearchInput = "";
                getViewState().clearSearchInput();
                if (!mCurrentSources.isEmpty()) {
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
            getViewState().showImagesWithAnimation(mCurrentSources);
        } else {
            getViewState().showImagesNoAnimation(mCurrentSources);
        }
        getViewState().hideBackground();
        getViewState().showHeader(mLastImagesRequestPhrase, mCurrentSources.size());
    }

    public void hideImages() {
        mCurrentSources.clear();
        getViewState().hideImagesWithAnimation();
        getViewState().hideHeader();
        getViewState().showBackground();
        if (clearButtonBackModeOn) {
            getViewState().animateBackButtonToClear();
            clearButtonBackModeOn = false;
        }
    }

    @Override
    public void onGalleryItemClicked(int position) {
        getViewState().openGalleryItemPreviewDialog(mLastLoadedImageRequest, position);
    }

    @Override
    public void onHistoryItemClicked(String requestPhrase) {
        hideHistory();
        if (mSearchInput.isEmpty()) {
            clearButtonBackModeOn = true;
            getViewState().animateClearButtonToBack();
        }
        loadImagesRequest(requestPhrase);
    }

    public void showHistoryRequest(boolean withAnimation) {
        ArrayList<ImageRequest> imageRequests = imageRequestsRepository.getImageRequestsSortedByDateFromRealm();
        if (!imageRequests.isEmpty()) {
            historyIsShowing = true;
            if (withAnimation) {
                getViewState().showHistoryWithAnimation(imageRequests);
            } else {
                getViewState().showHistoryNoAnimation(imageRequests);
            }
            getViewState().hideBackground();
        } else {
            getViewState().showEmptyHistroyMessage();
        }
    }

    public void hideHistory() {
        historyIsShowing = false;
        getViewState().hideHistory();
        getViewState().showBackground();
    }

    public void showApplicationInfo() {
        infoDialogIsShowing = true;
        getViewState().showAppInfoDialog();
    }

    public void hideApplicationInfo() {
        infoDialogIsShowing = false;
    }

    public void apiLogoPressed() {
        getViewState().startApiWebsiteIntent();
    }

    public boolean imagesOnScreen() {
        return !mCurrentSources.isEmpty();
    }

    public boolean isHistoryIsShowing() {
        return historyIsShowing;
    }

    private void insertLastRequestAndSourcesToRealm() {
        /*
         * Check if imageRequest with the similar phrase as the last made one has been made before and already in realm
         * If not then insert new request to the realm
         */
        ImageRequest similarRequestFromDb = imageRequestsRepository.getImageRequestByRequestPhraseFromRealm(mLastLoadedImageRequest.getPhrase());
        if (similarRequestFromDb == null) {
            imageRequestsRepository.insertImageRequestToRealm(mLastLoadedImageRequest);
            for (ImageSrc src : mCurrentSources) {
                imageSrcRepository.insertImageSrcToRealm(src);
            }
            //Get managed sources from realm and set them to the ImageRequest object
            mCurrentSources = imageSrcRepository.getImageSrcByRequestPhrase(mLastLoadedImageRequest.getPhrase());
            imageRequestsRepository.setImageSrcListForImageRequest(mLastLoadedImageRequest, mCurrentSources);
        }
    }
}
