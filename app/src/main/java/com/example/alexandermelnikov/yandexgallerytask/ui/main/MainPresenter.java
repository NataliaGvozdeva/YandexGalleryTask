package com.example.alexandermelnikov.yandexgallerytask.ui.main;


import com.arellomobile.mvp.InjectViewState;
import com.arellomobile.mvp.MvpPresenter;
import com.example.alexandermelnikov.yandexgallerytask.GalleryTaskApp;
import com.example.alexandermelnikov.yandexgallerytask.ui.adapter.GalleryAdapter;
import com.example.alexandermelnikov.yandexgallerytask.ui.adapter.HistoryAdapter;
import com.example.alexandermelnikov.yandexgallerytask.api.ApiHelper;
import com.example.alexandermelnikov.yandexgallerytask.data.ImageRequestsRepository;
import com.example.alexandermelnikov.yandexgallerytask.data.ImageSrcRepository;
import com.example.alexandermelnikov.yandexgallerytask.model.realm.ImageRequest;
import com.example.alexandermelnikov.yandexgallerytask.model.realm.ImageSrc;
import com.example.alexandermelnikov.yandexgallerytask.utils.Constants;

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

    /*
     * Curated Images are the ones displayed on the screen when option buttons are visible
     * and no images have yet been requested by a search phrase
     */
    private ImageRequest mSearchImagesRequest;
    private ImageRequest mCuratedImagesRequest;
    private ArrayList<ImageSrc> mCurrentShowingImagesSources;
    private ArrayList<ImageSrc> mCuratedImagesSources;

    private boolean optionButtonsLayoutIsOnScreen;
    private boolean noConnectionLabelIsOnScreen;
    //clearButtonBackModeOn is true when images are being showed and search edit text is empty
    private boolean clearButtonBackModeOn;
    //infoDialogIsOnScreen is true when dialog with information about the app is on screen
    private boolean infoDialogIsOnScreen;
    //historyIsOnScreen is true when history recycler view is visible on screen
    private boolean historyIsOnScreen;

    MainPresenter() {
        GalleryTaskApp.getAppComponent().inject(this);
        mSearchInput = "";
        mCurrentShowingImagesSources = new ArrayList<>();
        mCuratedImagesSources = new ArrayList<>();
        clearButtonBackModeOn = false;
        historyIsOnScreen = false;
        infoDialogIsOnScreen = false;
        noConnectionLabelIsOnScreen = false;
        optionButtonsLayoutIsOnScreen = true;
    }


    @Override
    public void attachView(MainView view) {
        super.attachView(view);
        getViewState().attachInputListeners();

        //Check if history recycler has been showing on previous attach
        if (historyIsOnScreen) {
            showHistoryRequest(false);
        }
        //Check if application information dialog has been showing on previous attach
        if (infoDialogIsOnScreen) {
            showApplicationInfo();
        }
        /*
         * Check if option buttons are visible on screen
         * If yes: display no connection label if needed, or request curated images
         * If no: show images which were requested on previous view attachment
         */
        if (optionButtonsLayoutIsOnScreen) {
            if (noConnectionLabelIsOnScreen) {
                getViewState().showNoConnectionMessage();
            } else {
                showCuratedImagesRequest();
            }
        } else {
            showSearchedImages(false);
        }

        getViewState().setupSearchEditTextHint(mCurrentSearchHintObject);
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

    void searchInputChanges(String input) {
        mSearchInput = input;
        if (!input.isEmpty() && clearButtonBackModeOn) {
            getViewState().animateBackButtonToClear();
            clearButtonBackModeOn = false;
        }
    }

    void showImagesRequest(String phrase) {
        if (historyIsOnScreen) {
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
                getViewState().showProgressBar();
                GalleryTaskApp.getApiHelper().getImages(phrase, this);
            } else {
                mCurrentShowingImagesSources = imageSrcRepository.getImageSrcByRequestPhrase(phrase);
                showSearchedImages(noConnectionLabelIsOnScreen);
                mSearchImagesRequest = imageRequestsRepository.getImageRequestByRequestPhraseFromRealm(phrase);
                if (mSearchImagesRequest != null) {
                    imageRequestsRepository.updateImageRequestDateByPhrase(mSearchImagesRequest.getPhrase());
                }
            }
        } else {
            getViewState().animateEmptySearchBar();
        }
    }

    @Override
    public void onImagesResultSuccessfulResponse(ImageRequest imageRequest, ArrayList<ImageSrc> imageSources) {
        if (!imageSources.isEmpty()) {
            mCurrentShowingImagesSources.clear();
            mCurrentShowingImagesSources.addAll(imageSources);
            mSearchImagesRequest = imageRequest;
            showSearchedImages(noConnectionLabelIsOnScreen);
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

    //Get and images curated by Pexels to display them on the main screen
    private void showCuratedImagesRequest() {
        /*
         * Verify if request for curated images has not yet been made
         * If it has been made previously then get curated image sources from Realm
         * Request curated images from api otherwise
         */
        ImageRequest curatedImagesRequestFromDb = imageRequestsRepository.getImageRequestByRequestPhraseFromRealm(Constants.CURATED_IMAGES_PHRASE);
        if (curatedImagesRequestFromDb == null) {
            GalleryTaskApp.getApiHelper().getCuratedImages(this);
        } else {
            mCuratedImagesSources = imageSrcRepository.getImageSrcByRequestPhrase(Constants.CURATED_IMAGES_PHRASE);
            mCuratedImagesRequest = imageRequestsRepository.getImageRequestByRequestPhraseFromRealm(Constants.CURATED_IMAGES_PHRASE);
            showCuratedImages(false);
        }
    }

    @Override
    public void onCuratedImagesResultSuccessfulResponse(ImageRequest imageRequest, ArrayList<ImageSrc> imageSources) {
        if (!imageSources.isEmpty()) {
            mCuratedImagesSources.clear();
            mCuratedImagesSources.addAll(imageSources);
            //Save curated images request and images sources in realm
            imageRequestsRepository.insertImageRequestToRealm(imageRequest);
            for (ImageSrc src : imageSources) {
                imageSrcRepository.insertImageSrcToRealm(src);
            }
            //Get managed list of source objects from db and set it to imageRequest
            mCuratedImagesSources = imageSrcRepository.getImageSrcByRequestPhrase(imageRequest.getPhrase());
            imageRequestsRepository.setImageSrcListForImageRequest(imageRequest, mCuratedImagesSources);
            mCuratedImagesRequest = imageRequestsRepository.getImageRequestByRequestPhraseFromRealm(Constants.CURATED_IMAGES_PHRASE);

            showCuratedImages(false);
        }
    }

    @Override
    public void onCuratedImagesResultFailure() {
        noConnectionLabelIsOnScreen = true;
        getViewState().showNoConnectionMessage();
    }

    private void showSearchedImages(boolean withAnimation) {
        if (withAnimation) {
            getViewState().showImagesWithAnimation(mCurrentShowingImagesSources);
        } else {
            getViewState().showImagesNoAnimation(mCurrentShowingImagesSources);
        }
        getViewState().showHeader(mLastImagesRequestPhrase, mCurrentShowingImagesSources.size());
        optionButtonsLayoutIsOnScreen = false;
        getViewState().hideOptionButtons();
        if (noConnectionLabelIsOnScreen) {
            noConnectionLabelIsOnScreen = false;
            getViewState().hideNoConnectionMessage();
        }
    }

    void hideSearchedImages() {
        optionButtonsLayoutIsOnScreen = true;
        getViewState().hideHeader();
        if (clearButtonBackModeOn) {
            getViewState().animateBackButtonToClear();
            clearButtonBackModeOn = false;
        }
        showCuratedImages(false);
        getViewState().showOptionButtons();
    }

    private void showCuratedImages(boolean withAnimation) {
        if (!mCuratedImagesSources.isEmpty()) {
            mCurrentShowingImagesSources.clear();
            mCurrentShowingImagesSources.addAll(mCuratedImagesSources);
            if (withAnimation) {
                getViewState().showImagesWithAnimation(mCurrentShowingImagesSources);
            } else {
                getViewState().showImagesNoAnimation(mCurrentShowingImagesSources);
            }
        } else {
            showCuratedImagesRequest();
        }
        if (noConnectionLabelIsOnScreen) {
            noConnectionLabelIsOnScreen = false;
            getViewState().hideNoConnectionMessage();
        }
    }

    void clearButtonPressed() {
        /*
         * If clearButtonModeOn is true hide current showing images
         * Clear search edit text input if it's not, empty input and animate clear button
         */
        if (!clearButtonBackModeOn) {
            if (!mSearchInput.isEmpty()) {
                mSearchInput = "";
                getViewState().clearSearchInput();
                if (!optionButtonsLayoutIsOnScreen) {
                    clearButtonBackModeOn = true;
                    getViewState().animateClearButtonToBack();
                } else {
                    getViewState().animateClearButton();
                }
            }
        } else {
            hideSearchedImages();
        }
    }

    @Override
    public void onGalleryItemClicked(int position) {
        if (!historyIsOnScreen && !infoDialogIsOnScreen) {
            //If option buttons are on screen, it means we want to pass curated images to the dialog fragment (not search requested)
            if (optionButtonsLayoutIsOnScreen) {
                getViewState().openGalleryItemPreviewDialogFragment(mCuratedImagesRequest, position);
            } else {
                getViewState().openGalleryItemPreviewDialogFragment(mSearchImagesRequest, position);
            }
        }
    }

    @Override
    public void onHistoryItemClicked(String requestPhrase) {
        hideHistory();
        if (mSearchInput.isEmpty()) {
            clearButtonBackModeOn = true;
            getViewState().animateClearButtonToBack();
        }
        showCuratedImages(true);
        showImagesRequest(requestPhrase);
    }

    void showHistoryRequest(boolean withAnimation) {
        if (!infoDialogIsOnScreen) {
            ArrayList<ImageRequest> imageRequests = imageRequestsRepository.getImageRequestsSortedByDateFromRealm();
            if (!imageRequests.isEmpty()) {
                historyIsOnScreen = true;
                if (withAnimation) {
                    getViewState().showHistoryWithAnimation(imageRequests);
                } else {
                    getViewState().showHistoryNoAnimation(imageRequests);
                }
                optionButtonsLayoutIsOnScreen = false;
                getViewState().hideImagesWithAnimation();
                getViewState().hideOptionButtons();
            } else {
                getViewState().showEmptyHistroyMessage();
            }
        }
    }

    void hideHistory() {
        historyIsOnScreen = false;
        getViewState().hideHistory();
        optionButtonsLayoutIsOnScreen = true;
        getViewState().showOptionButtons();
        showCuratedImages(true);
    }

    void showApplicationInfo() {
        if (!historyIsOnScreen) {
            infoDialogIsOnScreen = true;
            getViewState().showAppInfoDialog();
        }
    }

    void hideApplicationInfo() {
        infoDialogIsOnScreen = false;
    }

    void apiLogoPressed() {
        getViewState().startApiWebsiteIntent();
    }

    boolean imagesOnScreen() {
        return !optionButtonsLayoutIsOnScreen;
    }

    boolean isHistoryIsOnScreen() {
        return historyIsOnScreen;
    }

    private void insertLastRequestAndSourcesToRealm() {
        /*
         * Check if imageRequest with the similar phrase as the last made one has been requested before and already available in realm
         * If not then insert new request to the realm
         */
        ImageRequest similarRequestFromDb = imageRequestsRepository.getImageRequestByRequestPhraseFromRealm(mSearchImagesRequest.getPhrase());
        if (similarRequestFromDb == null) {
            imageRequestsRepository.insertImageRequestToRealm(mSearchImagesRequest);
            for (ImageSrc src : mCurrentShowingImagesSources) {
                imageSrcRepository.insertImageSrcToRealm(src);
            }
            //Get list of managed images sources from realm and set the list to the ImageRequest object
            mCurrentShowingImagesSources = imageSrcRepository.getImageSrcByRequestPhrase(mSearchImagesRequest.getPhrase());
            imageRequestsRepository.setImageSrcListForImageRequest(mSearchImagesRequest, mCurrentShowingImagesSources);
        }
    }
}
