package com.example.alexandermelnikov.yandexgallerytask;

import android.app.Application;

import com.example.alexandermelnikov.yandexgallerytask.api.ApiHelper;
import com.example.alexandermelnikov.yandexgallerytask.dagger.AppComponent;
import com.example.alexandermelnikov.yandexgallerytask.dagger.DaggerAppComponent;

import io.realm.Realm;
import uk.co.chrisjenx.calligraphy.CalligraphyConfig;


public class GalleryTaskApp extends Application {

    public static final String BASE_FONT = "fonts/Roboto-Regular.ttf";

    private static ApiHelper apiHelper;
    private static AppComponent appComponent;

    @Override
    public void onCreate() {
        super.onCreate();

        apiHelper = new ApiHelper(this);

        Realm.init(this);

        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
                .setDefaultFontPath(BASE_FONT)
                .setFontAttrId(R.attr.fontPath)
                .build()
        );
    }

    public static ApiHelper getApiHelper() {
        return apiHelper;
    }

    public static AppComponent getAppComponent() {
        if (appComponent == null) {
            appComponent = DaggerAppComponent.builder().build();
        }
        return appComponent;
    }
}
