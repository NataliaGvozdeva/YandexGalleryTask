package com.example.alexandermelnikov.yandexgallerytask.api;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.annotation.NonNull;
import android.util.Log;

import com.example.alexandermelnikov.yandexgallerytask.R;
import com.example.alexandermelnikov.yandexgallerytask.model.api.Photo;
import com.example.alexandermelnikov.yandexgallerytask.model.api.ResponseRoot;
import com.example.alexandermelnikov.yandexgallerytask.model.api.Src;
import com.example.alexandermelnikov.yandexgallerytask.model.realm.ImageRequest;
import com.example.alexandermelnikov.yandexgallerytask.model.realm.ImageSrc;
import com.example.alexandermelnikov.yandexgallerytask.utils.Constants;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * ApiHelper.java – class for calling api requests with retrofit
 * @author Alexander Melnikov
 */
public class ApiHelper {

    private static final String TAG = "ApiHelper";

    private Context mContext;

    private ApiService mService;

    public ApiHelper(Context context){
        Retrofit retrofit = new Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl(Constants.API_BASE_URL)
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build();
        mService = retrofit.create(ApiService.class);
        mContext = context;
    }

    /**
     * Perform API images request
     * @param phrase String search phrase
     * @param handler ImagesResultHandler object for handling response
     */
    public void getImages(String phrase, final ImagesResultHandler handler) {
        //Check if the internet connection is available and make the api request if yes
        if (internetConnectionAvailible()) {
            mService.getImages(Constants.API_KEY, phrase, Constants.DEFAULT_RESULTS_PER_PAGE).enqueue(new Callback<ResponseRoot>() {

                @Override
                public void onResponse(@NonNull Call<ResponseRoot> call, @NonNull Response<ResponseRoot> response) {
                    String error;
                    if (response.isSuccessful()) {
                        ImageRequest imageRequest = new ImageRequest(-1, phrase);
                        ArrayList<ImageSrc> imageSources = new ArrayList<>();
                        List<Photo> photos = response.body().getPhotos();
                        for (Photo photo : photos) {
                            Src photoSrc = photo.getSrc();
                            imageSources.add(new ImageSrc(-1, phrase, photoSrc.getMedium(), photoSrc.getLarge2x(), photo.getPhotographer(), photo.getUrl()));
                        }
                        handler.onImagesResultSuccessfulResponse(imageRequest, imageSources);
                    } else {
                        error = mContext.getString(R.string.error_request_failed);
                        if (response.code() == 401) {
                            error = mContext.getString(R.string.error_api_key_wrong);
                        } else if (response.code() == 402) {
                            error = mContext.getString(R.string.error_api_key_blocked);
                        }
                        handler.onImagesResultFailure(error);
                    }
                }

                @Override
                public void onFailure(@NonNull Call<ResponseRoot> call, @NonNull Throwable t) {
                    Log.e(TAG, t.toString());
                }
            });

        } else {
            handler.onImagesResultFailure(mContext.getString(R.string.error_no_connection));
        }
    }

    /**
     * Perform API curated images request
     * @param handler ImagesResultHandler object for handling response
     */
    public void getCuratedImages(final ImagesResultHandler handler) {
        if (internetConnectionAvailible()) {
            mService.getCuratedImages(Constants.API_KEY, Constants.DEFAULT_RESULTS_PER_PAGE).enqueue(new Callback<ResponseRoot>() {

                @Override
                public void onResponse(@NonNull Call<ResponseRoot> call, @NonNull Response<ResponseRoot> response) {
                    if (response.isSuccessful()) {
                        ImageRequest imageRequest = new ImageRequest(-1, Constants.CURATED_IMAGES_PHRASE);
                        ArrayList<ImageSrc> imageSources = new ArrayList<>();
                        List<Photo> photos = response.body().getPhotos();
                        for (Photo photo : photos) {
                            Src photoSrc = photo.getSrc();
                            imageSources.add(new ImageSrc(-1, Constants.CURATED_IMAGES_PHRASE, photoSrc.getMedium(), photoSrc.getLarge2x(), photo.getPhotographer(), photo.getUrl()));
                        }
                        handler.onCuratedImagesResultSuccessfulResponse(imageRequest, imageSources);
                    } else {
                        handler.onCuratedImagesResultFailure();
                    }
                }

                @Override
                public void onFailure(@NonNull Call<ResponseRoot> call, @NonNull Throwable t) {
                    Log.e(TAG, t.toString());
                }
            });

        } else {
            handler.onCuratedImagesResultFailure();
        }
    }


    public interface ImagesResultHandler {

        /**
         * Method is called on successful response from getImages api request
         * @param imageRequest An ImageRequest object from api request
         * @param imageSources A list of ImageSrc objects from api request
         */
        void onImagesResultSuccessfulResponse(ImageRequest imageRequest, ArrayList<ImageSrc> imageSources);

        /**
         * Method is called on unsuccessful response from getImages api request
         * @param message String message describing the error
         */
        void onImagesResultFailure(String message);

        /**
         * Method is called on successful response from getCuratedImages api request
         * @param imageRequest An ImageRequest object from api request
         * @param imageSources A list of ImageSrc objects from api request
         */
        void onCuratedImagesResultSuccessfulResponse(ImageRequest imageRequest, ArrayList<ImageSrc> imageSources);

        /**
         * Method is called on unsuccessful response from getCuratedImages api request
         */
        void onCuratedImagesResultFailure();

    }

    private boolean internetConnectionAvailible() {
        ConnectivityManager cm = (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo;
        try {
            networkInfo = cm.getActiveNetworkInfo();
        } catch (NullPointerException e) {
            Log.e(TAG, "getImages: ", e);
            networkInfo = null;
        }
        return (networkInfo != null && networkInfo.isConnected());
    }

}
