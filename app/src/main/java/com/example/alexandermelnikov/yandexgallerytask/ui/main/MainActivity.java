package com.example.alexandermelnikov.yandexgallerytask.ui.main;

import android.animation.Animator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.AnimatedVectorDrawable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentTransaction;
import android.os.Bundle;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.arellomobile.mvp.MvpAppCompatActivity;
import com.arellomobile.mvp.presenter.InjectPresenter;
import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.example.alexandermelnikov.yandexgallerytask.R;
import com.example.alexandermelnikov.yandexgallerytask.adapter.GalleryAdapter;
import com.example.alexandermelnikov.yandexgallerytask.adapter.SortMethodsDialogAdapter;
import com.example.alexandermelnikov.yandexgallerytask.model.api.Photo;
import com.example.alexandermelnikov.yandexgallerytask.ui.image_fullscreen_dialog.SlideshowDialogFragment;
import com.example.alexandermelnikov.yandexgallerytask.utils.Constants;
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
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

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
    @BindView(R.id.tv_search_header) TextView tvHeaderText;
    @BindView(R.id.progressBar) ProgressBar progressBar;
    @BindView(R.id.layout_header) RelativeLayout layoutHeader;
    @BindView(R.id.gallery_container) RelativeLayout layoutGalleryContainer;
    @BindView(R.id.main_background) RelativeLayout layoutBackground;


    private MaterialDialog sortMethodsDialog;
    private RecyclerView rvSortMethods;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        mGalleryAdapter = new GalleryAdapter(this, new ArrayList<Photo>(), mMainActivityPresenter);
        rvImages.setLayoutManager(new GridLayoutManager(getApplicationContext(), Constants.DEFAULT_PORTRAIT_NUM_OF_COLUMNS) {
            @Override
            public boolean canScrollVertically() {
                return false;
            }
        });
        rvImages.setAdapter(mGalleryAdapter);

        TypedArray hintObjects = getResources().obtainTypedArray(R.array.search_hint_objects);
        Random random = new Random();
        int index = random.nextInt(hintObjects.length());
        mMainActivityPresenter.setupSearchBarHint(hintObjects.getString(index));

        //Prevents the soft keyboard from pushing the view above it
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);

    }

    @Override
    public void attachInputListeners() {

        Disposable searchButton = RxView.clicks(btnSearch)
                .subscribe(o -> mMainActivityPresenter.loadImagesRequest(etSearch.getText().toString()));

        Disposable clearButton = RxView.clicks(btnClear)
                .subscribe(o -> mMainActivityPresenter.clearButtonPressed());

        Disposable sortButton = RxView.clicks(btnSort)
                .subscribe(o -> mMainActivityPresenter.sortButtonPressed());

        Disposable searchInputChanges = RxTextView.textChanges(etSearch)
                .debounce(150, TimeUnit.MILLISECONDS, AndroidSchedulers.mainThread())
                .map(charSequence -> charSequence.toString())
                .filter(text -> !text.isEmpty())
                .subscribe(text -> {
                    mMainActivityPresenter.searchInputChanges(text);
                });

        etSearch.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                if (i == EditorInfo.IME_ACTION_SEARCH) {
                    mMainActivityPresenter.loadImagesRequest(etSearch.getText().toString());
                }
                return false;
            }
        });

        mDisposable.addAll(clearButton, searchButton, sortButton, searchInputChanges);
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    @Override
    public void dettachInputListeners() {
        mDisposable.clear();
    }

    @Override
    public void setupEditTextHint(String hintObject) {
        etSearch.setHint(new StringBuilder("Search: ").append(hintObject));
    }

    @Override
    public void animateEmptySearchBar() {
        YoYo.with(Techniques.Shake)
                .duration(600)
                .playOn(etSearch);
    }

    @Override
    public void showImagesWithAnimation(List<Photo> photos) {
        mGalleryAdapter.replaceData(photos);
        YoYo.with(Techniques.SlideInUp)
                .duration(500)
                .playOn(layoutGalleryContainer);
    }

    @Override
    public void showImagesNoAnimation(List<Photo> photos) {
        mGalleryAdapter.replaceData(photos);
    }

    @Override
    public void hideImagesWithAnimation() {
        YoYo.with(Techniques.SlideOutDown)
                .duration(500)
                .withListener(new Animator.AnimatorListener() {
                    @Override
                    public void onAnimationStart(Animator animator) {}

                    @Override
                    public void onAnimationEnd(Animator animator) {
                        mGalleryAdapter.clearData();
                    }

                    @Override
                    public void onAnimationCancel(Animator animator) {
                        mGalleryAdapter.clearData();
                    }

                    @Override
                    public void onAnimationRepeat(Animator animator) {}
                })
                .playOn(layoutGalleryContainer);
    }

    @Override
    public void showProgressBar() {
        progressBar.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideProgressBar() {
        progressBar.setVisibility(View.INVISIBLE);
    }

    @Override
    public void showBackground() {
        layoutBackground.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideBackground() {
        layoutBackground.setVisibility(View.INVISIBLE);
    }

    @Override
    public void animateSearchButton() {
        AnimatedVectorDrawable drawable = (AnimatedVectorDrawable) getResources()
                .getDrawable(R.drawable.ic_search_gray_anim_30dp);
        btnSearch.setImageDrawable(drawable);
        drawable.start();
    }

    @Override
    public void animateClearButtonToBack() {
        AnimatedVectorDrawable drawable = (AnimatedVectorDrawable) getResources()
                .getDrawable(R.drawable.ic_clear_gray_anim_to_back_28dp);
        btnClear.setImageDrawable(drawable);
        drawable.start();
    }

    @Override
    public void animateBackButtonToClear() {
        AnimatedVectorDrawable drawable = (AnimatedVectorDrawable) getResources()
                .getDrawable(R.drawable.ic_arrow_back_gray_anim_to_clear_28dp);
        btnClear.setImageDrawable(drawable);
        drawable.start();
    }

    @Override
    public void animateClearButton() {
        AnimatedVectorDrawable drawable = (AnimatedVectorDrawable) getResources()
                .getDrawable(R.drawable.ic_clear_gray_anim_28dp);
        btnClear.setImageDrawable(drawable);
        drawable.start();
    }

    @Override
    public void showSortMethodsDialog(String currentMethod, int currentMethodIndex) {
        sortMethodsDialog = new MaterialDialog.Builder(this)
                .title("Sorting")
                .customView(R.layout.dialog_sort_methods, false)
                //.onPositive(((dialog1, which) -> mCardBrowserPresenter.createNewDeckRequest()))
                .build();

        rvSortMethods = sortMethodsDialog.getView().findViewById(R.id.rv_methods);
        rvSortMethods.setLayoutManager(new LinearLayoutManager(this));
        rvSortMethods.setAdapter(new SortMethodsDialogAdapter(this,
                currentMethod, currentMethodIndex, mMainActivityPresenter));

        sortMethodsDialog.show();
    }

    @Override
    public void hideSortMethodsDialog() {
        try {
            sortMethodsDialog.hide();
        } catch (NullPointerException e) {
            Log.e(TAG, "hideDecksListDialog: " + e.getLocalizedMessage());
        }
    }

    @Override
    public void clearSearchInput() {
        etSearch.setText("");
    }

    @Override
    public void hideKeyboard() {
        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
    }

    @Override
    public void showHeader(String lastSearchObject) {
        if (layoutHeader.getVisibility() == View.INVISIBLE) {
            layoutHeader.setVisibility(View.VISIBLE);
            YoYo.with(Techniques.SlideInDown)
                .duration(500)
                .playOn(layoutHeader);
        }
        tvHeaderText.setText(new StringBuilder(getResources().getString(R.string.search_results)).append(" ").append(lastSearchObject));
    }

    @Override
    public void hideHeader() {
        YoYo.with(Techniques.SlideOutUp)
                .duration(500)
                .withListener(new Animator.AnimatorListener() {
                    @Override
                    public void onAnimationStart(Animator animator) {
                    }

                    @Override
                    public void onAnimationEnd(Animator animator) {
                        layoutHeader.setVisibility(View.INVISIBLE);
                    }

                    @Override
                    public void onAnimationCancel(Animator animator) {
                        layoutHeader.setVisibility(View.INVISIBLE);
                    }

                    @Override
                    public void onAnimationRepeat(Animator animator) {
                    }
                })
                .playOn(layoutHeader);
    }

    @Override
    public void showEmptySearchResultMessage() {
        Log.d(TAG, "showEmptySearchResultMessage: ");
        Snackbar.make(findViewById(R.id.main_layout), getResources().getString(R.string.empty_search_result),
                Snackbar.LENGTH_SHORT).show();
    }

    @Override
    public void openGalleryItemPreviewDialog(ArrayList<Photo> photos, int position, ImageView sharedImageView) {
        Bundle args = new Bundle();
        args.putSerializable("photos", photos);
        args.putInt("position", position);

        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        SlideshowDialogFragment fragment = SlideshowDialogFragment.newInstance();
        fragment.setArguments(args);
        ft.addSharedElement(sharedImageView, ViewCompat.getTransitionName(sharedImageView));
        fragment.show(ft, "slideshow");
    }

    @Override
    public void onBackPressed() {
        if (layoutBackground.getVisibility() == View.INVISIBLE) {
            mMainActivityPresenter.hideImages();
        } else {
            super.onBackPressed();
        }
    }
}
