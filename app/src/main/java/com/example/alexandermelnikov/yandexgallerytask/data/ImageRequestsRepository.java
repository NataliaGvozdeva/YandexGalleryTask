package com.example.alexandermelnikov.yandexgallerytask.data;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.example.alexandermelnikov.yandexgallerytask.model.realm.ImageRequest;
import com.example.alexandermelnikov.yandexgallerytask.model.realm.ImageSrc;

import java.util.ArrayList;
import java.util.Date;

import io.realm.Realm;
import io.realm.RealmList;
import io.realm.Sort;

/**
 * ImageRequestsRepository.java – helper class for working with ImageRequest table in Realm
 * @author Alexander Melnikov
 */

public class ImageRequestsRepository {

    private static final String TAG = "MyTag";

    /**
     * Insert new ImageRequest object to realm database
     * @param request The ImageRequest realm object to be inserted
     */
    public void insertImageRequestToRealm(@NonNull ImageRequest request) {
        Realm realm = null;
        try {
            realm = Realm.getDefaultInstance();
            realm.executeTransaction(new Realm.Transaction() {
                @Override
                public void execute(Realm realm) {
                    Log.d(TAG, "Insert to realm (ImageRequest)");
                    int nextID;
                    try {
                        // Incrementing primary key manually
                        nextID = realm.where(ImageRequest.class).max("id").intValue() + 1;
                    } catch (NullPointerException e) {
                        // If there is first item, being added to cache, give it id = 0
                        nextID = 0;
                    }
                    request.setId(nextID);
                    realm.copyToRealmOrUpdate(request);
                }
            });
        } finally {
            if (realm != null) {
                realm.close();
            }
        }
    }

    /**
     * Get all ImageRequest objects from Realm sorted by date
     * @return An ArrayList of ImageRequest objects
     */
    public ArrayList<ImageRequest> getImageRequestsSortedByDateFromRealm() {
        ArrayList<ImageRequest> requests;
        Realm realm = Realm.getDefaultInstance();
        requests = new ArrayList<ImageRequest>(realm.where(ImageRequest.class)
                .sort("requestDate", Sort.DESCENDING)
                .findAll());
        return requests;
    }

    /**
     * Get ImageRequest by request variable value
     * @param phrase The String phrase which returned ImageRequest should contain
     * @return ImageRequest if succeed to find object in db with desired request value;
     *      returns null otherwise
     */
    @Nullable
    public ImageRequest getImageRequestByRequestPhraseFromRealm(@NonNull String phrase) {
        ImageRequest imageRequest;
        Realm realm = Realm.getDefaultInstance();
        imageRequest = realm.where(ImageRequest.class)
                .equalTo("phrase", phrase)
                .findFirst();
        return imageRequest;
    }

    /**
     * Set list or image sources for request
     * @param imageRequest The ImageRequest for which sources will be set
     * @param sources The ArrayList or sources to be set in the imageRequest sources variable value
     */
    public void setImageSrcListForImageRequest(ImageRequest imageRequest, ArrayList<ImageSrc> sources) {
        Realm realm = null;
        try {
            realm = Realm.getDefaultInstance();
            realm.executeTransaction(new Realm.Transaction() {
                @Override
                public void execute(Realm realm) {
                    RealmList<ImageSrc> _sources = new RealmList<>();
                    _sources.addAll(sources);
                    ImageRequest request = realm.where(ImageRequest.class)
                            .equalTo("id", imageRequest.getId())
                            .findFirst();
                    if (request != null) {
                        realm.copyToRealmOrUpdate(_sources);
                        request.setSources(_sources);
                    }
                }
            });
        } finally {
            if (realm != null) {
                realm.close();
            }
        }
    }

    /**
     * Update date of request object with phrase
     * @param phrase The String phrase which updating request must have as a phrase variable value
     */
    public void updateImageRequestDateByPhrase(String phrase) {
        Realm realm = Realm.getDefaultInstance();
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                ImageRequest request = realm.where(ImageRequest.class)
                        .equalTo("phrase", phrase)
                        .findFirst();
                if (request != null) {
                    request.setRequestDate(new Date());
                }
            }
        });
        realm.close();
    }

}
