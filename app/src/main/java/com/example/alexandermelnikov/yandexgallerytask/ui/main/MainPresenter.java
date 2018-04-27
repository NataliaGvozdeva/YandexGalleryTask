package com.example.alexandermelnikov.yandexgallerytask.ui.main;

import com.arellomobile.mvp.InjectViewState;
import com.arellomobile.mvp.MvpPresenter;
import com.example.alexandermelnikov.yandexgallerytask.GalleryTaskApp;
import com.example.alexandermelnikov.yandexgallerytask.adapter.GalleryAdapter;
import com.example.alexandermelnikov.yandexgallerytask.adapter.HistoryAdapter;
import com.example.alexandermelnikov.yandexgallerytask.api.ApiHelper;
import com.example.alexandermelnikov.yandexgallerytask.data.ImageRequestsRepository;
import com.example.alexandermelnikov.yandexgallerytask.data.ImageSrcRepository;
import com.example.alexandermelnikov.yandexgallerytask.model.realm.ImageRequest;
import com.example.alexandermelnikov.yandexgallerytask.model.realm.ImageSrc;

import java.util.ArrayList;

import javax.inject.Inject;

/**
 * MainPresenter.java – main application activity presenter class
 * @author Alexander Melnikov
 */
@InjectViewState
public class MainPresenter extends MvpPresenter<MainView> implements ApiHelper.ImagesResultHandler,
        GalleryAdapter.OnGalleryItemClickListener, HistoryAdapter.OnHistoryItemClickListener {

    @Inject
    ImageRequestsRepository imageRequestsRepository;
    @Inject
    ImageSrcRepository imageSrcRepository;

    private String mSearchInput;
    private String mCurrentSearchHintObject;
    private String mLastImagesRequestPhrase;

    private ImageRequest mLastLoadedImageRequest;
    private ArrayList<ImageSrc> mCurrentSources;

    //clearButtonBackModeOn is true when images are being showed and search edit text is empty
    private boolean clearButtonBackModeOn;
    //infoDialogIsShowing is true when dialog with information about the app is on screen
    private boolean infoDialogIsShowing;
    //historyIsShowing is true when history recycler view is visible on screen
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

        getViewState().setupEditTextHint(mCurrentSearchHintObject);
    }

    @Override
    public void detachView(MainView view) {
        getViewState().dettachInputListeners();
        super.detachView(view);
    }

    void setupSearchBarHint(String hintObject) {
        if (mCurrentSearchHintObject == null) {
            mCurrentSearchHintObject = hintObject;
        }
    }

    public void searchInputChanges(String input) {
        mSearchInput = input;
        if (!input.isEmpty() && clearButtonBackModeOn) {
            getViewState().animateBackButtonToClear();
            clearButtonBackModeOn = false;
        }
    }

    void loadImagesRequest(String phrase) {
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
            //Save previous sources emptiness state to decide whether showing images animation is needed or not
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
        /*
         * If clearButtonModeOn is true hide current showing images
         * Clear search edit text input if it's not empty and animate clear button
         */
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
        getViewState().openGalleryItemPreviewDialogFragment(mLastLoadedImageRequest, position);
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

    void showHistoryRequest(boolean withAnimation) {
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

    void hideHistory() {
        historyIsShowing = false;
        getViewState().hideHistory();
        getViewState().showBackground();
    }

    public void showApplicationInfo() {
        infoDialogIsShowing = true;
        getViewState().showAppInfoDialog();
    }

    void hideApplicationInfo() {
        infoDialogIsShowing = false;
    }

    public void apiLogoPressed() {
        getViewState().startApiWebsiteIntent();
    }

    boolean imagesOnScreen() {
        return !mCurrentSources.isEmpty();
    }

    boolean isHistoryIsShowing() {
        return historyIsShowing;
    }

    private void insertLastRequestAndSourcesToRealm() {
        /*
         * Check if imageRequest with the similar phrase as the last made one has been requested before and already available in realm
         * If not then insert new request to the realm
         */
        ImageRequest similarRequestFromDb = imageRequestsRepository.getImageRequestByRequestPhraseFromRealm(mLastLoadedImageRequest.getPhrase());
        if (similarRequestFromDb == null) {
            imageRequestsRepository.insertImageRequestToRealm(mLastLoadedImageRequest);
            for (ImageSrc src : mCurrentSources) {
                imageSrcRepository.insertImageSrcToRealm(src);
            }
            //Get list of managed images sources from realm and set the list to the ImageRequest object
            mCurrentSources = imageSrcRepository.getImageSrcByRequestPhrase(mLastLoadedImageRequest.getPhrase());
            imageRequestsRepository.setImageSrcListForImageRequest(mLastLoadedImageRequest, mCurrentSources);
        }
    }
}
