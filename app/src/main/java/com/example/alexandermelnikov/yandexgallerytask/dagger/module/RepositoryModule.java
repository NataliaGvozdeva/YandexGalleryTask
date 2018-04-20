package com.example.alexandermelnikov.yandexgallerytask.dagger.module;

import com.example.alexandermelnikov.yandexgallerytask.data.UserDataRepository;

import dagger.Module;
import dagger.Provides;

/**
 * Created by AlexMelnikov on 20.04.18.
 */

@Module
public class RepositoryModule {

    @Provides
    UserDataRepository provideUserDataRepository() {
        return new UserDataRepository();
    }

}
