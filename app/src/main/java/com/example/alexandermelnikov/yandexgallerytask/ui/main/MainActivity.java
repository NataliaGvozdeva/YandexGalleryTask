package com.example.alexandermelnikov.yandexgallerytask.ui.main;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.AnimatedVectorDrawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;

import com.afollestad.materialdialogs.MaterialDialog;
import com.arellomobile.mvp.MvpAppCompatActivity;
import com.arellomobile.mvp.presenter.InjectPresenter;
import com.cjj.sva.JJSearchView;
import com.cjj.sva.anim.controller.JJAroundCircleBornTailController;
import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.example.alexandermelnikov.yandexgallerytask.GalleryTaskApp;
import com.example.alexandermelnikov.yandexgallerytask.R;
import com.example.alexandermelnikov.yandexgallerytask.adapter.GalleryAdapter;
import com.example.alexandermelnikov.yandexgallerytask.model.api.Image;
import com.jakewharton.rxbinding2.view.RxView;
import com.jakewharton.rxbinding2.widget.RxTextView;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;

public class MainActivity extends MvpAppCompatActivity implements MainView{

    private static final String TAG = "MyTag";

    @InjectPresenter MainPresenter mMainActivityPresenter;

    CompositeDisposable mDisposable = new CompositeDisposable();
    GalleryAdapter mGalleryAdapter;

    @BindView(R.id.btn_clear) ImageButton btnClear;
    @BindView(R.id.btn_search) ImageButton btnSearch;
    @BindView(R.id.btn_sort) ImageButton btnSort;
    @BindView(R.id.rv_images) RecyclerView rvImages;
    @BindView(R.id.et_search) EditText etSearch;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        mGalleryAdapter = new GalleryAdapter(this, new ArrayList<Image>());
        rvImages.setLayoutManager(new GridLayoutManager(getApplicationContext(), 3) {
            @Override
            public boolean canScrollVertically() {
                return false;
            }
        });
        rvImages.setAdapter(mGalleryAdapter);
    }


    @Override
    public void attachInputListeners() {

        Disposable searchButton = RxView.clicks(btnSearch)
                .subscribe(o -> mMainActivityPresenter.loadImagesRequest(etSearch.getText().toString()));

        Disposable clearButton = RxView.clicks(btnClear)
                .subscribe(o -> mMainActivityPresenter.clearSearchRequest());

        Disposable sortButton = RxView.clicks(btnSort)
                .subscribe(o -> mMainActivityPresenter.sortButtonPressed());

        Disposable searchInputChanges = RxTextView.textChanges(etSearch)
                .debounce(150, TimeUnit.MILLISECONDS, AndroidSchedulers.mainThread())
                .map(charSequence -> charSequence.toString())
                .filter(text -> !text.isEmpty())
                .subscribe(text -> {
                    mMainActivityPresenter.searchInputChanges(text);
                });

        mDisposable.addAll(clearButton, searchButton, sortButton, searchInputChanges);
    }

    @Override
    public void dettachInputListeners() {
        mDisposable.clear();
    }

    @Override
    public void setupEditTextHint() {
        TypedArray hintObjects = getResources().obtainTypedArray(R.array.search_hint_objects);
        Random random = new Random();
        int index = random.nextInt(hintObjects.length());
        Log.d(TAG, "setupEditTextHint: " + hintObjects.getString(index));
        etSearch.setHint(new StringBuilder("Search: ").append(hintObjects.getString(index)));
    }

    @Override
    public void animateEmptySearchBar() {
        YoYo.with(Techniques.Shake)
                .duration(600)
                .playOn(etSearch);
    }

    @Override
    public void replaceGalleryData(List<Image> images) {
        mGalleryAdapter.replaceData(images);
    }

    @Override
    public void animateSearchButton() {
        AnimatedVectorDrawable drawable = (AnimatedVectorDrawable) getResources()
                .getDrawable(R.drawable.ic_search_gray_anim_30dp);
        btnSearch.setImageDrawable(drawable);
        drawable.start();
    }

    @Override
    public void animateClearButton() {
        AnimatedVectorDrawable drawable = (AnimatedVectorDrawable) getResources()
                .getDrawable(R.drawable.ic_clear_gray_anim_30dp);
        btnClear.setImageDrawable(drawable);
        drawable.start();
    }

    @Override
    public void showSortMethodsDialog() {
        new MaterialDialog.Builder(this)
                .items(R.array.sort_methods)
                .itemsCallback((dialog, view, which, text) -> mMainActivityPresenter.sortMethodPicked(which))
                .show();
    }

    @Override
    public void clearSearchInput() {
        etSearch.setText("");
    }

    @Override
    public void updateSearchHeader(String searchObject) {

    }
}
