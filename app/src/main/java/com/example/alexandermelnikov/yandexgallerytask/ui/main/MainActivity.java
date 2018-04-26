package com.example.alexandermelnikov.yandexgallerytask.ui.main;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.TypedArray;
import android.graphics.drawable.AnimatedVectorDrawable;
import android.net.Uri;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentTransaction;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.arellomobile.mvp.MvpAppCompatActivity;
import com.arellomobile.mvp.presenter.InjectPresenter;
import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.example.alexandermelnikov.yandexgallerytask.R;
import com.example.alexandermelnikov.yandexgallerytask.adapter.GalleryAdapter;
import com.example.alexandermelnikov.yandexgallerytask.adapter.HistoryAdapter;
import com.example.alexandermelnikov.yandexgallerytask.model.realm.ImageRequest;
import com.example.alexandermelnikov.yandexgallerytask.model.realm.ImageSrc;
import com.example.alexandermelnikov.yandexgallerytask.ui.image_fullscreen_dialog.SlideshowDialogFragment;
import com.example.alexandermelnikov.yandexgallerytask.utils.Constants;
import com.jakewharton.rxbinding2.view.RxView;
import com.jakewharton.rxbinding2.widget.RxTextView;

import java.io.File;
import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.realm.Realm;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class MainActivity extends MvpAppCompatActivity implements MainView{

    private static final String TAG = "MyTag";

    @InjectPresenter MainPresenter mMainActivityPresenter;

    private CompositeDisposable mDisposable = new CompositeDisposable();
    private GalleryAdapter mGalleryAdapter;
    private HistoryAdapter mHistoryAdapter;

    @BindView(R.id.btn_clear) ImageButton btnClear;
    @BindView(R.id.btn_search) ImageButton btnSearch;
    @BindView(R.id.rv_images) RecyclerView rvImages;
    @BindView(R.id.et_search) EditText etSearch;
    @BindView(R.id.tv_search_header) TextView tvHeaderText;
    @BindView(R.id.tv_results_counter) TextView tvResultsCounter;
    @BindView(R.id.progressBar) ProgressBar progressBar;
    @BindView(R.id.layout_header) RelativeLayout layoutHeader;
    @BindView(R.id.gallery_container) RelativeLayout layoutGalleryViewGroup;
    @BindView(R.id.sv_mainscroll) ScrollView scGalleryScroll;
    @BindView(R.id.main_background) ConstraintLayout layoutBackground;
    @BindView(R.id.iv_api_icon) ImageView ivApiLogo;
    @BindView(R.id.btn_history) Button btnHistory;
    @BindView(R.id.btn_info) Button btnInfo;
    @BindView(R.id.history_container) RelativeLayout historyViewGroup;
    @BindView(R.id.rv_history) RecyclerView rvHistory;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        //Get current orientation to understand which default number of columns to use
        int orientation = this.getResources().getConfiguration().orientation;
        int recyclerNumberOfColumns;
        if (orientation == Configuration.ORIENTATION_PORTRAIT) {
            recyclerNumberOfColumns = Constants.DEFAULT_PORTRAIT_NUM_OF_COLUMNS;
        } else {
            recyclerNumberOfColumns = Constants.DEFAULT_LANDSCAPE_NUM_OF_COLUMNS;
        }

        //Setup gallery RecyclerView
        mGalleryAdapter = new GalleryAdapter(this, new ArrayList<ImageSrc>(), mMainActivityPresenter);
        rvImages.setLayoutManager(new GridLayoutManager(getApplicationContext(), recyclerNumberOfColumns) {
            @Override
            public boolean canScrollVertically() {
                return false;
            }
        });
        rvImages.setAdapter(mGalleryAdapter);

        //Setup history RecyclerView
        mHistoryAdapter = new HistoryAdapter(this, new ArrayList<ImageRequest>(), mMainActivityPresenter);
        rvHistory.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        rvHistory.setAdapter(mHistoryAdapter);

        TypedArray hintObjects = getResources().obtainTypedArray(R.array.search_hint_objects);
        Random random = new Random();
        int index = random.nextInt(hintObjects.length());
        mMainActivityPresenter.setupSearchBarHint(hintObjects.getString(index));

        //Prevents the soft keyboard from pushing the view above it
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);

    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    @Override
    public void attachInputListeners() {

        Disposable searchButton = RxView.clicks(btnSearch)
                .subscribe(o -> mMainActivityPresenter.loadImagesRequest(etSearch.getText().toString()));

        Disposable clearButton = RxView.clicks(btnClear)
                .subscribe(o -> mMainActivityPresenter.clearButtonPressed());

        Disposable searchInputChanges = RxTextView.textChanges(etSearch)
                .debounce(150, TimeUnit.MILLISECONDS, AndroidSchedulers.mainThread())
                .map(charSequence -> charSequence.toString())
                .subscribe(text -> mMainActivityPresenter.searchInputChanges(text));

        //DEBUG
        btnSearch.setOnLongClickListener(v -> exportDatabase());

        Disposable historyButton = RxView.clicks(btnHistory)
                .subscribe(o -> mMainActivityPresenter.showHistoryRequest(true));

        Disposable logoClick = RxView.clicks(ivApiLogo)
                .subscribe(o -> mMainActivityPresenter.apiLogoPressed());

        etSearch.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                if (i == EditorInfo.IME_ACTION_SEARCH) {
                    mMainActivityPresenter.loadImagesRequest(etSearch.getText().toString());
                }
                return false;
            }
        });

        mDisposable.addAll(clearButton, searchButton, searchInputChanges, historyButton);
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
    public void showImagesWithAnimation(ArrayList<ImageSrc> sources) {
        scGalleryScroll.setVisibility(View.VISIBLE);
        mGalleryAdapter.replaceData(sources);
        YoYo.with(Techniques.SlideInUp)
                .duration(600)
                .playOn(layoutGalleryViewGroup);
    }

    @Override
    public void showImagesNoAnimation(ArrayList<ImageSrc> sources) {
        scGalleryScroll.setVisibility(View.VISIBLE);
        mGalleryAdapter.replaceData(sources);
    }

    @Override
    public void showHistoryNoAnimation(ArrayList<ImageRequest> requests) {
        btnHistory.setClickable(false);
        mHistoryAdapter.replaceData(requests);
        historyViewGroup.setVisibility(View.VISIBLE);
    }

    @Override
    public void showHistoryWithAnimation(ArrayList<ImageRequest> requests) {
        btnHistory.setClickable(false);
        mHistoryAdapter.replaceData(requests);
        historyViewGroup.setVisibility(View.VISIBLE);
        YoYo.with(Techniques.SlideInUp)
                .duration(600)
                .playOn(historyViewGroup);
    }

    @Override
    public void hideHistory() {
        btnHistory.setClickable(true);
        YoYo.with(Techniques.SlideOutDown)
                .withListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationCancel(Animator animation) {
                        super.onAnimationCancel(animation);
                        historyViewGroup.setVisibility(View.GONE);
                    }

                    @Override
                    public void onAnimationEnd(Animator animation) {
                        super.onAnimationEnd(animation);
                        historyViewGroup.setVisibility(View.GONE);
                    }
                })
                .duration(600)
                .playOn(historyViewGroup);
    }

    @Override
    public void hideImagesWithAnimation() {
        YoYo.with(Techniques.SlideOutDown)
                .duration(500)
                .withListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationCancel(Animator animation) {
                        super.onAnimationCancel(animation);
                        mGalleryAdapter.clearData();
                        scGalleryScroll.setVisibility(View.GONE);
                    }

                    @Override
                    public void onAnimationEnd(Animator animation) {
                        super.onAnimationEnd(animation);
                        mGalleryAdapter.clearData();
                        scGalleryScroll.setVisibility(View.GONE);
                    }
                })
                .playOn(layoutGalleryViewGroup);
    }

    @Override
    public void showProgressBar() {
        progressBar.setVisibility(View.VISIBLE);
        progressBar.setAlpha(1);
    }

    @Override
    public void hideProgressBar() {
        progressBar.setVisibility(View.INVISIBLE);
        progressBar.setAlpha(0);
    }

    @Override
    public void showBackground() {
        layoutBackground.animate()
                .alpha(1.0f)
                .setDuration(300);
    }

    @Override
    public void hideBackground() {
        layoutBackground.animate()
                .alpha(0.0f)
                .setDuration(300);
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
    public void clearSearchInput() {
        etSearch.setText("");
    }

    @Override
    public void hideKeyboard() {
        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
    }

    @Override
    public void showHeader(String lastSearchObject, int resultsCount) {
        if (layoutHeader.getVisibility() == View.INVISIBLE) {
            layoutHeader.setVisibility(View.VISIBLE);
            YoYo.with(Techniques.SlideInDown)
                .duration(500)
                .playOn(layoutHeader);
        }
        tvHeaderText.setText(new StringBuilder(getResources().getString(R.string.search_results)).append(" ").append(lastSearchObject));
        tvResultsCounter.setText(new StringBuilder(getResources().getString(R.string.results_count)).append(" ").append(resultsCount));
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
        Snackbar.make(findViewById(R.id.main_layout), getResources().getString(R.string.empty_search_result),
                Snackbar.LENGTH_SHORT).show();
    }

    @Override
    public void showSnackbarMessage(String message) {
        Snackbar.make(findViewById(R.id.main_layout), message,
                Snackbar.LENGTH_SHORT).show();
    }


    @Override
    public void startApiWebsiteIntent() {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse(Constants.PEXELS_API_SITE_URL));
        startActivity(intent);
    }

    @Override
    public void openGalleryItemPreviewDialog(ImageRequest imageRequest, int position) {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        SlideshowDialogFragment fragment = new SlideshowDialogFragment();
        Bundle args = new Bundle();
        args.putSerializable(fragment.REQUEST_PHRASE, imageRequest.getPhrase());
        args.putInt(fragment.POSITION, position);
        fragment.setArguments(args);
        fragment.show(ft, "slideshow");
    }

    @Override
    public void onBackPressed() {
        if (scGalleryScroll.getVisibility() == View.VISIBLE) {
            mMainActivityPresenter.hideImages();
        } else if (historyViewGroup.getVisibility() == View.VISIBLE) {
            mMainActivityPresenter.hideHistory();
        } else {
            super.onBackPressed();
        }
    }


    //REALM DEBUG
    public boolean exportDatabase() {

        Log.d(TAG, "exportDatabase");
        // init realm
        Realm realm = Realm.getDefaultInstance();

        File exportRealmFile = null;
        // get or create an "export.realm" file
        exportRealmFile = new File(this.getExternalCacheDir(), "export.realm");

        // if "export.realm" already exists, delete
        exportRealmFile.delete();

        // copy current realm to "export.realm"
        realm.writeCopyTo(exportRealmFile);


        realm.close();

        // init email intent and add export.realm as attachment
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("plain/text");
        intent.putExtra(Intent.EXTRA_EMAIL, "melnikov.ws@gmail.com");
        intent.putExtra(Intent.EXTRA_SUBJECT, "My Database");
        intent.putExtra(Intent.EXTRA_TEXT, "realm database file");
        Uri u = Uri.fromFile(exportRealmFile);
        intent.putExtra(Intent.EXTRA_STREAM, u);

        // start email intent
        startActivity(Intent.createChooser(intent, "title"));
        return true;
    }
}
