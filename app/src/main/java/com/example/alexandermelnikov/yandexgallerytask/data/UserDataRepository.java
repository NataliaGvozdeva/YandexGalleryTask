package com.example.alexandermelnikov.yandexgallerytask.data;

import android.support.annotation.NonNull;

import com.orhanobut.hawk.Hawk;

/**
 * Created by AlexMelnikov on 20.04.18.
 */

public class UserDataRepository {

    public void putValue(String key, @NonNull Object value) {
        Hawk.put(key, value);
    }

    public Object getValue(String key, @NonNull Object defaultValue) {
        if (!Hawk.contains(key))
            putValue(key, defaultValue);
        return Hawk.get(key);
    }
}
