package com.example.alexandermelnikov.yandexgallerytask.ui.main;

import android.graphics.drawable.AnimatedVectorDrawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

import com.arellomobile.mvp.MvpAppCompatActivity;
import com.cjj.sva.JJSearchView;
import com.cjj.sva.anim.controller.JJAroundCircleBornTailController;
import com.example.alexandermelnikov.yandexgallerytask.GalleryTaskApp;
import com.example.alexandermelnikov.yandexgallerytask.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.disposables.CompositeDisposable;

public class MainActivity extends MvpAppCompatActivity implements MainView{

    private static final String TAG = "MyTag";

    @BindView(R.id.btn_clear)
    ImageButton btnClear;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);


        btnClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AnimatedVectorDrawable drawable = (AnimatedVectorDrawable) getResources()
                        .getDrawable(R.drawable.ic_clear_gray_anim_30dp);
                btnClear.setImageDrawable(drawable);
                drawable.start();
                getImages();
            }
        });
    }

    @Override
    public void attachInputListeners() {

    }

    public void getImages() {
        GalleryTaskApp.getApiHelper().getImages("dogs", "best");
    }
}
