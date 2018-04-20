package com.example.alexandermelnikov.yandexgallerytask.dagger;

import com.example.alexandermelnikov.yandexgallerytask.dagger.module.RepositoryModule;
import com.example.alexandermelnikov.yandexgallerytask.ui.main.MainPresenter;

import javax.inject.Singleton;

import dagger.Component;

/**
 * Created by AlexMelnikov on 20.04.18.
 */

@Singleton
@Component(modules = {RepositoryModule.class
})
public interface AppComponent {

    void inject(MainPresenter presenter);

}
