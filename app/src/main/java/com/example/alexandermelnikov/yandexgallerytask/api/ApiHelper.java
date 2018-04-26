package com.example.alexandermelnikov.yandexgallerytask.api;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
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

public class ApiHelper {

    private static final String TAG = "MyTag";

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

    public void getImages(String phrase, final ImagesResultHandler handler) {
        ConnectivityManager cm =
                (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = cm.getActiveNetworkInfo();

        if (networkInfo != null && networkInfo.isConnected()) {

            mService.getImages(Constants.API_KEY, phrase, Constants.DEFAULT_RESULTS_PER_PAGE).enqueue(new Callback<ResponseRoot>() {
                @Override
                public void onResponse(Call<ResponseRoot> call, Response<ResponseRoot> response) {
                    Log.d(TAG, response.toString());
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
                public void onFailure(Call<ResponseRoot> call, Throwable t) {
                    Log.e(TAG, t.toString());
                }
            });

        } else {
            handler.onImagesResultFailure(mContext.getString(R.string.error_no_connection));
        }
    }


    public interface ImagesResultHandler {
        void onImagesResultSuccessfulResponse(ImageRequest imageRequest, ArrayList<ImageSrc> imageSources);

        void onImagesResultFailure(String message);
    }

}
