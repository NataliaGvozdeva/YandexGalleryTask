package com.example.alexandermelnikov.yandexgallerytask.api;

import com.example.alexandermelnikov.yandexgallerytask.model.api.ResponseRoot;

import java.util.List;

import io.reactivex.Observable;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Query;
import retrofit2.http.Url;

/**
 * Created by AlexMelnikov on 17.04.18.
 */

public interface ApiService {

   @GET("images")
   Call<ResponseRoot> getImages(@Query("fields") String fields,
                                @Query("sort_order") String sort,
                                @Query("phrase") String phrase,
                                @Header("Api-Key") String apiKey);
}
