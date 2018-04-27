package com.example.alexandermelnikov.yandexgallerytask.dagger;

import com.example.alexandermelnikov.yandexgallerytask.api.ApiHelper;
import com.example.alexandermelnikov.yandexgallerytask.dagger.module.RepositoryModule;
import com.example.alexandermelnikov.yandexgallerytask.ui.image_fullscreen_dialog.SlideshowDialogFragment;
import com.example.alexandermelnikov.yandexgallerytask.ui.main.MainPresenter;

import javax.inject.Singleton;

import dagger.Component;


@Singleton
@Component(modules = {RepositoryModule.class
})
public interface AppComponent {

    void inject(MainPresenter presenter);

    void inject(SlideshowDialogFragment dialogFragment);

}
