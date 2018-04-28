package com.example.alexandermelnikov.yandexgallerytask;

import com.example.alexandermelnikov.yandexgallerytask.ui.main.MainPresenter;
import com.example.alexandermelnikov.yandexgallerytask.ui.main.MainView;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.mockito.Mockito.verify;


@RunWith(MockitoJUnitRunner.class)
public class MainPresenterTest {

    @Mock
    private MainView view;
    private MainPresenter presenter;

    @Before
    public void setUp() throws Exception {
        presenter = new MainPresenter();
        presenter.attachView(view);
    }

    @Test
    public void shouldDetachInputListeners() throws Exception {
        presenter.detachView(view);
        verify(view).dettachInputListeners();
    }

    @Test
    public void shouldShowEmptyHistoryMessage() throws Exception {
        presenter.onImagesResultFailure("message");
        verify(view).showSnackbarMessage("message");
    }

    @Test
    public void shouldHideProgressBarAndShowSnackbar() throws Exception {
        presenter.onImagesResultFailure("message");
        verify(view).hideProgressBar();
        verify(view).showSnackbarMessage("message");
    }

    @Test
    public void shouldClearSearchInput() throws Exception {
        presenter.searchInputChanges("input");
        presenter.clearButtonPressed();
        verify(view).clearSearchInput();
        verify(view).animateClearButton();
    }

    @Test
    public void shouldHideImagesHideHeaderAndShowBackground() throws Exception {
        presenter.hideSearchedImages();
        verify(view).hideImagesWithAnimation();
        verify(view).hideHeader();
        verify(view).showBackground();
    }

    @Test
    public void shouldShowInfoDialog() throws Exception {
        presenter.showApplicationInfo();
        verify(view).showAppInfoDialog();
    }

    @Test
    public void shouldStartApiWebsiteIntent() throws Exception {
        presenter.apiLogoPressed();
        verify(view).startApiWebsiteIntent();
    }

}
