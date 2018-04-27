package com.example.alexandermelnikov.yandexgallerytask;

import android.app.Application;

import com.example.alexandermelnikov.yandexgallerytask.api.ApiHelper;
import com.example.alexandermelnikov.yandexgallerytask.dagger.AppComponent;
import com.example.alexandermelnikov.yandexgallerytask.dagger.DaggerAppComponent;
import com.orhanobut.hawk.Hawk;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import uk.co.chrisjenx.calligraphy.CalligraphyConfig;

/**
 * Created by AlexMelnikov on 17.04.18.
 */

public class GalleryTaskApp extends Application {

    public static final String BASE_FONT = "fonts/Roboto-Regular.ttf";

    private static ApiHelper apiHelper;
    private static AppComponent appComponent;

    @Override
    public void onCreate() {
        super.onCreate();

        apiHelper = new ApiHelper(this);

        Hawk.init(this).build();
        Realm.init(this);

        appComponent = DaggerAppComponent.builder().build();

        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
                .setDefaultFontPath(BASE_FONT)
                .setFontAttrId(R.attr.fontPath)
                .build()
        );

        //REALM MIGRATION
        /*RealmConfiguration config2 = new RealmConfiguration.Builder()
                .deleteRealmIfMigrationNeeded()
                .build();
        Realm realm = Realm.getInstance(config2);*/
    }

    public static ApiHelper getApiHelper() {
        return apiHelper;
    }

    public static AppComponent getAppComponent() {
        return appComponent;
    }
}
