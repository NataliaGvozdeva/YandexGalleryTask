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
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentTransaction;
import android.os.Bundle;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.method.LinkMovementMethod;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
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

/**
 * MainActivity.java – main application activity class
 * @author Alexander Melnikov
 */
public class MainActivity extends MvpAppCompatActivity implements MainView {

    private static final String TAG = "MainActivity";
    
    @InjectPresenter MainPresenter mMainActivityPresenter;

    private CompositeDisposable mDisposable = new CompositeDisposable();
    private GalleryAdapter mGalleryAdapter;
    private HistoryAdapter mHistoryAdapter;
    private LinearLayoutManager mHistoryLayoutManager;
    private MaterialDialog appInfoDialog;

    private float initButtonsViewGroupY;
    private float maxAnimationHeight;

    @BindView(R.id.progress_bar) ProgressBar progressBar;
    @BindView(R.id.iv_api_icon) ImageView ivApiLogo;
    @BindView(R.id.btn_clear) ImageButton btnClear;
    @BindView(R.id.btn_search) ImageButton btnSearch;
    @BindView(R.id.btn_history) Button btnHistory;
    @BindView(R.id.btn_info) Button btnInfo;
    @BindView(R.id.tv_search_header) TextView tvHeaderText;
    @BindView(R.id.tv_results_counter) TextView tvResultsCounter;
    @BindView(R.id.et_search) EditText etSearch;
    @BindView(R.id.history_container) RelativeLayout historyViewGroup;
    @BindView(R.id.layout_header) RelativeLayout layoutHeader;
    @BindView(R.id.gallery_container) RelativeLayout layoutGalleryViewGroup;
    @BindView(R.id.button_container) LinearLayout buttonsViewGroup;
    @BindView(R.id.lbl_no_connection) LinearLayout lblNoConnection;
    @BindView(R.id.sv_mainscroll) NestedScrollView svGalleryScroll;
    @BindView(R.id.rv_images) RecyclerView rvImages;
    @BindView(R.id.rv_history) RecyclerView rvHistory;
    @BindView(R.id.appbar) AppBarLayout myAppBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        //Get current orientation to decide which default number of columns to use
        int orientation = this.getResources().getConfiguration().orientation;
        int recyclerNumberOfColumns;
        if (orientation == Configuration.ORIENTATION_PORTRAIT) {
            recyclerNumberOfColumns = Constants.DEFAULT_PORTRAIT_NUM_OF_COLUMNS;
        } else {
            recyclerNumberOfColumns = Constants.DEFAULT_LANDSCAPE_NUM_OF_COLUMNS;
        }

        GridLayoutManager mGalleryLayoutManager = new GridLayoutManager(getApplicationContext(), recyclerNumberOfColumns) {
            @Override
            public boolean canScrollVertically() {
                return false;
            }
        };
        mHistoryLayoutManager = new LinearLayoutManager(getApplicationContext());

        //Setup gallery RecyclerView
        mGalleryAdapter = new GalleryAdapter(this, new ArrayList<>(), mMainActivityPresenter);
        rvImages.setLayoutManager(mGalleryLayoutManager);
        rvImages.setAdapter(mGalleryAdapter);

        //Setup history RecyclerView
        mHistoryAdapter = new HistoryAdapter(this, new ArrayList<>(), mMainActivityPresenter);
        rvHistory.setLayoutManager(mHistoryLayoutManager);
        rvHistory.setAdapter(mHistoryAdapter);

        //Choose random string search object for search edit text hint and save it to presenter
        TypedArray hintObjects = getResources().obtainTypedArray(R.array.search_hint_objects);
        Random random = new Random();
        int index = random.nextInt(hintObjects.length());
        mMainActivityPresenter.setupSearchBarHint(hintObjects.getString(index));
        hintObjects.recycle();

        //Prevents the soft keyboard from pushing the view above it
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);

        //Setup appInfoDialog
        appInfoDialog = new MaterialDialog.Builder(this)
                .customView(R.layout.dialog_app_info, false)
                .positiveText(android.R.string.ok)
                .dismissListener(d -> mMainActivityPresenter.hideApplicationInfo())
                .build();
        TextView text = appInfoDialog.getView().findViewById(R.id.tv_content);
        text.setMovementMethod(LinkMovementMethod.getInstance());

        //Setup values for buttonsViewGroup animation
        buttonsViewGroup.post(() -> {
            initButtonsViewGroupY = buttonsViewGroup.getY();
            maxAnimationHeight = buttonsViewGroup.getHeight() * 1.6f;
        });
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    @Override
    public void attachInputListeners() {

        Disposable searchButton = RxView.clicks(btnSearch)
                .subscribe(o -> mMainActivityPresenter.showImagesRequest(etSearch.getText().toString()));

        Disposable clearButton = RxView.clicks(btnClear)
                .subscribe(o -> mMainActivityPresenter.clearButtonPressed());

        Disposable searchInputChanges = RxTextView.textChanges(etSearch)
                .debounce(150, TimeUnit.MILLISECONDS, AndroidSchedulers.mainThread())
                .map(charSequence -> charSequence.toString())
                .subscribe(text -> mMainActivityPresenter.searchInputChanges(text));

        Disposable historyButton = RxView.clicks(btnHistory)
                .subscribe(o -> mMainActivityPresenter.showHistoryRequest(true));

        Disposable infoButton = RxView.clicks(btnInfo)
                .subscribe(o -> mMainActivityPresenter.showApplicationInfo());

        Disposable logoClick = RxView.clicks(ivApiLogo)
                .subscribe(o -> mMainActivityPresenter.apiLogoPressed());

        etSearch.setOnEditorActionListener(((textView, i, keyEvent) -> {
            if (i == EditorInfo.IME_ACTION_SEARCH) {
                mMainActivityPresenter.showImagesRequest(etSearch.getText().toString());
            }
            return false;
        }));

        //Setup Y animation for buttonViewGroup on changed offset of the AppBarLayout
        myAppBar.addOnOffsetChangedListener((appBarLayout, verticalOffset) -> {
            float fraction = ((float) Math.abs(verticalOffset)) / appBarLayout.getTotalScrollRange();
            float newY = initButtonsViewGroupY + maxAnimationHeight * fraction;
            if (newY >= initButtonsViewGroupY && newY > appBarLayout.getTotalScrollRange()) {
                buttonsViewGroup.setY(newY);
            }
        });

        mDisposable.addAll(clearButton, searchButton, searchInputChanges, historyButton, infoButton, logoClick);
    }

    @Override
    public void dettachInputListeners() {
        mDisposable.clear();
    }

    @Override
    public void setupSearchEditTextHint(String hintObject) {
        etSearch.setHint(getString(R.string.search_hint, hintObject));
    }

    @Override
    public void animateEmptySearchBar() {
        YoYo.with(Techniques.Shake)
                .duration(600)
                .playOn(etSearch);
    }

    @Override
    public void showImagesWithAnimation(ArrayList<ImageSrc> sources) {
        svGalleryScroll.setVisibility(View.VISIBLE);
        mGalleryAdapter.replaceData(sources);
        YoYo.with(Techniques.SlideInUp)
                .duration(600)
                .playOn(layoutGalleryViewGroup);

    }

    @Override
    public void showImagesNoAnimation(ArrayList<ImageSrc> sources) {
        svGalleryScroll.setVisibility(View.VISIBLE);
        mGalleryAdapter.replaceData(sources);
        svGalleryScroll.fullScroll(View.FOCUS_UP);
    }

    @Override
    public void hideImagesWithAnimation() {
        YoYo.with(Techniques.SlideOutDown)
                .duration(500)
                .withListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationCancel(Animator animation) {
                        super.onAnimationCancel(animation);
                        svGalleryScroll.fullScroll(View.FOCUS_UP);
                        mGalleryAdapter.clearData();
                        svGalleryScroll.setVisibility(View.GONE);
                    }

                    @Override
                    public void onAnimationEnd(Animator animation) {
                        svGalleryScroll.fullScroll(View.FOCUS_UP);
                        super.onAnimationEnd(animation);
                        mGalleryAdapter.clearData();
                        svGalleryScroll.setVisibility(View.GONE);
                    }
                })
                .playOn(layoutGalleryViewGroup);
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
        YoYo.with(Techniques.SlideOutDown)
                .withListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationCancel(Animator animation) {
                        super.onAnimationCancel(animation);
                        historyViewGroup.setVisibility(View.GONE);
                        btnHistory.setClickable(true);
                        mHistoryLayoutManager.scrollToPosition(0);
                    }

                    @Override
                    public void onAnimationEnd(Animator animation) {
                        super.onAnimationEnd(animation);
                        historyViewGroup.setVisibility(View.GONE);
                        btnHistory.setClickable(true);
                        mHistoryLayoutManager.scrollToPosition(0);
                    }
                })
                .duration(600)
                .playOn(historyViewGroup);
    }

    @Override
    public void showAppInfoDialog() {
        if (!appInfoDialog.isShowing()) {
            appInfoDialog.show();
        }
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
    public void showNoConnectionMessage() {
        lblNoConnection.setVisibility(View.VISIBLE);
        lblNoConnection.animate()
                .alpha(1.0f)
                .setListener(new AnimatorListenerAdapter() {})
                .setDuration(300);
    }

    @Override
    public void hideNoConnectionMessage() {
        lblNoConnection.animate()
                .alpha(0.0f)
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationCancel(Animator animation) {
                        super.onAnimationCancel(animation);
                        lblNoConnection.setVisibility(View.GONE);
                    }

                    @Override
                    public void onAnimationEnd(Animator animation) {
                        super.onAnimationEnd(animation);
                        lblNoConnection.setVisibility(View.GONE);
                    }
                })
                .setDuration(300);
    }

    @Override
    public void showOptionButtons() {
        buttonsViewGroup.setVisibility(View.VISIBLE);
        buttonsViewGroup.animate()
                .alpha(1.0f)
                .setListener(new AnimatorListenerAdapter() {})
                .setDuration(300);
    }

    @Override
    public void hideOptionButtons() {
        buttonsViewGroup.animate()
                .alpha(0.0f)
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationCancel(Animator animation) {
                        super.onAnimationCancel(animation);
                        buttonsViewGroup.setVisibility(View.GONE);
                    }

                    @Override
                    public void onAnimationEnd(Animator animation) {
                        super.onAnimationEnd(animation);
                        buttonsViewGroup.setVisibility(View.GONE);
                    }
                })
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
        try {
            inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        } catch (NullPointerException e) {
            Log.e(TAG, "hideKeyboard: ", e);
        }
    }

    @Override
    public void showHeader(String lastSearchObject, int resultsCount) {
        layoutHeader.setVisibility(View.VISIBLE);
        tvHeaderText.setText(getString(R.string.search_results, lastSearchObject));
        tvResultsCounter.setText(getString(R.string.results_count, resultsCount));
    }

    @Override
    public void hideHeader() {
        layoutHeader.setVisibility(View.GONE);
    }

    @Override
    public void showEmptySearchResultMessage() {
        Snackbar.make(findViewById(R.id.main_layout), getResources().getString(R.string.empty_search_result),
                Snackbar.LENGTH_SHORT).show();
    }

    @Override
    public void showEmptyHistroyMessage() {
        Snackbar.make(findViewById(R.id.main_layout), getResources().getString(R.string.empty_history),
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
    public void openGalleryItemPreviewDialogFragment(ImageRequest imageRequest, int position) {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        SlideshowDialogFragment fragment = new SlideshowDialogFragment();
        Bundle args = new Bundle();
        args.putSerializable(fragment.REQUEST_PHRASE, imageRequest.getPhrase());
        args.putInt(fragment.POSITION, position);
        fragment.setArguments(args);
        fragment.show(ft, "slideshow");
    }

    //Overriding onBackPressed to get more predictable user navigation experience
    @Override
    public void onBackPressed() {
        if (mMainActivityPresenter.imagesOnScreen() && !mMainActivityPresenter.isHistoryIsOnScreen()) {
            mMainActivityPresenter.hideSearchedImages();
        } else if (mMainActivityPresenter.isHistoryIsOnScreen()) {
            mMainActivityPresenter.hideHistory();
            mMainActivityPresenter.showCuratedImages(true);
        } else {
            super.onBackPressed();
        }
    }

}
