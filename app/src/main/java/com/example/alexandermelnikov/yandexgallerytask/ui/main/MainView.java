package com.example.alexandermelnikov.yandexgallerytask.ui.main;

import com.arellomobile.mvp.viewstate.strategy.OneExecutionStateStrategy;
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType;
import com.example.alexandermelnikov.yandexgallerytask.model.api.Image;
import com.example.alexandermelnikov.yandexgallerytask.ui.BaseView;

import java.util.List;

/**
 * Created by AlexMelnikov on 17.04.18.
 */

@StateStrategyType(OneExecutionStateStrategy.class)
public interface MainView extends BaseView {

    void replaceGalleryData(List<Image> images);

}
