package com.example.alexandermelnikov.yandexgallerytask.dagger.module;

import com.example.alexandermelnikov.yandexgallerytask.data.ImageRequestsRepository;
import com.example.alexandermelnikov.yandexgallerytask.data.ImageSrcRepository;

import dagger.Module;
import dagger.Provides;


@Module
public class RepositoryModule {

    @Provides
    ImageRequestsRepository provideImageRequestsRepository() {
        return new ImageRequestsRepository();
    }

    @Provides
    ImageSrcRepository provideImageSrcRepository() {
        return new ImageSrcRepository();
    }

}
