package com.example.alexandermelnikov.yandexgallerytask.api;

import android.util.Log;

import com.example.alexandermelnikov.yandexgallerytask.model.api.Photo;
import com.example.alexandermelnikov.yandexgallerytask.model.api.ResponseRoot;
import com.example.alexandermelnikov.yandexgallerytask.utils.Constants;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by AlexMelnikov on 17.04.18.
 */

public class ApiHelper {

    private static final String TAG = "MyTag";

    private ApiService mService;

    public ApiHelper(){
        Retrofit retrofit = new Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl(Constants.API_BASE_URL)
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build();
        mService = retrofit.create(ApiService.class);
    }

    public void getImages(String phrase, String sort, final ImagesResultHandler handler) {
        mService.getImages(Constants.API_KEY, phrase, Constants.DEFAULT_RESULTS_PER_PAGE).enqueue(new Callback<ResponseRoot>() {
            @Override
            public void onResponse(Call<ResponseRoot> call, Response<ResponseRoot> response) {
                Log.d(TAG, response.toString());
                if (response.body() != null){
                    Log.d(TAG, "onResponse: " + response.toString());
                    List<Photo> photos = response.body().getPhotos();
                    handler.onImagesResultPassed(photos);
                }
            }

            @Override
            public void onFailure(Call<ResponseRoot> call, Throwable t) {
                Log.d(TAG, t.toString());
            }
        });
    }

}
