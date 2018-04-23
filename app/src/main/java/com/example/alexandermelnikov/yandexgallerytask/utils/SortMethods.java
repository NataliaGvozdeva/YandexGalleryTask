package com.example.alexandermelnikov.yandexgallerytask.utils;

/**
 * Created by AlexMelnikov on 20.04.18.
 */

public enum SortMethods {
    best(0),
    most_popular(1),
    newest(2);

    private int index;

    private SortMethods(int index) {
        this.index = index;
    }

    public int getIndex() {
        return index;
    }
}
