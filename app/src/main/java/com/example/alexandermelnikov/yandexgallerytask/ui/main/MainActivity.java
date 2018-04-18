package com.example.alexandermelnikov.yandexgallerytask.ui.main;

import android.graphics.drawable.AnimatedVectorDrawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageButton;

import com.arellomobile.mvp.MvpAppCompatActivity;
import com.arellomobile.mvp.presenter.InjectPresenter;
import com.cjj.sva.JJSearchView;
import com.cjj.sva.anim.controller.JJAroundCircleBornTailController;
import com.example.alexandermelnikov.yandexgallerytask.GalleryTaskApp;
import com.example.alexandermelnikov.yandexgallerytask.R;
import com.example.alexandermelnikov.yandexgallerytask.adapter.GalleryAdapter;
import com.example.alexandermelnikov.yandexgallerytask.model.api.Image;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.disposables.CompositeDisposable;

public class MainActivity extends MvpAppCompatActivity implements MainView{

    private static final String TAG = "MyTag";

    @InjectPresenter MainPresenter mainPresenter;

    CompositeDisposable mDisposable = new CompositeDisposable();
    GalleryAdapter mGalleryAdapter;

    @BindView(R.id.btn_clear) ImageButton btnClear;
    @BindView(R.id.rv_images) RecyclerView rvImages;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        mGalleryAdapter = new GalleryAdapter(this, new ArrayList<Image>());
        rvImages.setLayoutManager(new GridLayoutManager(getApplicationContext(), 3));
        rvImages.setAdapter(mGalleryAdapter);
    }


    @Override
    public void attachInputListeners() {
        btnClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AnimatedVectorDrawable drawable = (AnimatedVectorDrawable) getResources()
                        .getDrawable(R.drawable.ic_clear_gray_anim_30dp);
                btnClear.setImageDrawable(drawable);
                drawable.start();
                mainPresenter.loadImagesRequest();
            }
        });
    }

    @Override
    public void dettachInputListeners() {
        mDisposable.clear();
    }

    @Override
    public void replaceGalleryData(List<Image> images) {
        mGalleryAdapter.replaceData(images);
    }
}
