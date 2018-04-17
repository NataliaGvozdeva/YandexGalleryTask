package com.example.alexandermelnikov.yandexgallerytask.api;

import android.text.TextUtils;
import android.util.Log;

import com.example.alexandermelnikov.yandexgallerytask.model.api.Image;
import com.example.alexandermelnikov.yandexgallerytask.model.api.ResponseRoot;
import com.example.alexandermelnikov.yandexgallerytask.utils.Constants;

import java.util.List;
import java.util.Observable;

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

    public void getImages(String phrase, String sort) {
        mService.getImages(Constants.API_BASE_FIELDS, sort, phrase, Constants.API_KEY).enqueue(new Callback<ResponseRoot>() {

            @Override
            public void onResponse(Call<ResponseRoot> call, Response<ResponseRoot> response) {
                Log.d("API", response.toString());
                if (response.body() != null){
                    List<Image> images = response.body().getImages();
                    for (int i = 0; i < images.size(); i++)
                        Log.d(TAG, "" + i + ": " +images.get(i).getTitle());
                }
            }

            @Override
            public void onFailure(Call<ResponseRoot> call, Throwable t) {
                Log.d("API", t.toString());
            }
        });
    }

}
