package com.example.alexandermelnikov.yandexgallerytask.data;

import android.support.annotation.NonNull;

import com.example.alexandermelnikov.yandexgallerytask.model.realm.ImageSrc;

import java.util.ArrayList;

import io.realm.Realm;

/**
 * ImageSrcRepository.java – helper class for working with ImageSrc table in Realm
 * @author Alexander Melnikov
 */
public class ImageSrcRepository {

    /**
     * Insert new ImageSrc object to Realm database
     * @param source The ImageSrc object to be inserted
     */
    public void insertImageSrcToRealm(@NonNull ImageSrc source) {
        Realm realm = Realm.getDefaultInstance();
        realm.executeTransaction(r -> {
            int nextID;
            try {
                // Incrementing primary key manually
                nextID = realm.where(ImageSrc.class).max("id").intValue() + 1;
            } catch (NullPointerException e) {
                // If there is first item, being added to cache, give it id = 0
                nextID = 0;
            }
            source.setId(nextID);
            realm.insertOrUpdate(source);
        });
        realm.close();
    }

    /**
     * Get image sources objects from Realm by their phrase variable value
     * @param phrase The desirable phrase String value
     * @return List of ImageSrc objects
     */
    public ArrayList<ImageSrc> getImageSrcByRequestPhrase(String phrase) {
        ArrayList<ImageSrc> sources;
        Realm realm = Realm.getDefaultInstance();
        sources = new ArrayList<>(realm.where(ImageSrc.class)
                .equalTo("requestPhrase", phrase)
                .findAll());
        return sources;
    }

}
