package com.example.alexandermelnikov.yandexgallerytask.ui;

import com.arellomobile.mvp.MvpView;
import com.arellomobile.mvp.viewstate.strategy.SkipStrategy;
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType;

/**
 * Created by AlexMelnikov on 17.04.18.
 */

public interface BaseView extends MvpView {

    @StateStrategyType(SkipStrategy.class)
    void attachInputListeners();

    @StateStrategyType(SkipStrategy.class)
    void dettachInputListeners();

}
