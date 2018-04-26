package com.example.alexandermelnikov.yandexgallerytask.ui.main;

import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.ImageView;

import com.arellomobile.mvp.InjectViewState;
import com.arellomobile.mvp.MvpPresenter;
import com.example.alexandermelnikov.yandexgallerytask.GalleryTaskApp;
import com.example.alexandermelnikov.yandexgallerytask.adapter.GalleryAdapter;
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
        GalleryAdapter.OnGalleryItemClickListener {
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

    public MainPresenter() {
        GalleryTaskApp.getAppComponent().inject(this);
        mSearchInput = "";
        mCurrentSources = new ArrayList<>();
        clearButtonBackModeOn = false;
    }

    @Override
    public void attachView(MainView view) {
        super.attachView(view);
        getViewState().attachInputListeners();

        //Check if mCurrentSources is not empty and show those previously loaded images if so
        if (!mCurrentSources.isEmpty()) {
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
        mLastImagesRequestPhrase = phrase;
        if (!phrase.isEmpty()) {
            /*
             * Verify if request by the given phrase has not yet been made
             * If it has been made previously then get image sources from Realm
             * Request images from api otherwise
             */
            ImageRequest similarRequestFromDb = imageRequestsRepository.getImageRequestByRequestPhraseFromRealm(phrase);
            if (similarRequestFromDb == null) {
                GalleryTaskApp.getApiHelper().getImages(phrase, this);
                getViewState().animateSearchButton();
                getViewState().showProgressBar();
                getViewState().hideKeyboard();
            } else {
                boolean sourcesEmpty = mCurrentSources.isEmpty();
                mCurrentSources = imageSrcRepository.getImageSrcByRequestPhrase(phrase);
                showImages(sourcesEmpty);
                mLastLoadedImageRequest = imageRequestsRepository.getImageRequestByRequestPhraseFromRealm(phrase);
            }
        } else {
            getViewState().animateEmptySearchBar();
        }
    }

    @Override
    public void onImagesResultSuccessfulResponse(ImageRequest imageRequest, ArrayList<ImageSrc> imageSources) {
        if (!imageSources.isEmpty()) {
/*            if (mCurrentSources.isEmpty()) {
                mCurrentSources.clear();
                mCurrentSources.addAll(imageSources);
                showImages(true);
            } else {
                mCurrentSources.clear();
                mCurrentSources.addAll(imageSources);
                showImages(false);
            }*/
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
    public void onGalleryItemClicked(int position, ImageView sharedImageView) {
        getViewState().openGalleryItemPreviewDialog(mLastLoadedImageRequest, position);
    }

    public void apiLogoPressed() {
        getViewState().startApiWebsiteIntent();
    }


    private void insertLastRequestAndSourcesToRealm() {
        /*
         * Check if imageRequest with the similar phrase as the last made one has been made before and already in realm
         * If not then insert new request to the realm, else update request date in the previously made request object
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
        } else {
            imageRequestsRepository.updateImageRequestDateByPhrase(mLastLoadedImageRequest.getPhrase());
        }
    }
}
