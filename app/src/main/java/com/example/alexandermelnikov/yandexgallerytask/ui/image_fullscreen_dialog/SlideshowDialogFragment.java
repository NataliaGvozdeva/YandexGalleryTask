package com.example.alexandermelnikov.yandexgallerytask.ui.image_fullscreen_dialog;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.util.Linkify;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.alexandermelnikov.yandexgallerytask.GalleryTaskApp;
import com.example.alexandermelnikov.yandexgallerytask.R;
import com.example.alexandermelnikov.yandexgallerytask.data.ImageSrcRepository;
import com.example.alexandermelnikov.yandexgallerytask.model.api.Photo;
import com.example.alexandermelnikov.yandexgallerytask.model.realm.ImageSrc;
import com.github.chrisbanes.photoview.PhotoView;

import java.util.ArrayList;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;


public class SlideshowDialogFragment extends DialogFragment {

    public final String REQUEST_PHRASE = "request_phrase";
    public final String POSITION = "position";

    @BindView(R.id.viewpager)
    ViewPager viewPager;
    @BindView(R.id.title)
    TextView tvTitle;
    @BindView(R.id.lbl_count)
    TextView lblCount;
    @BindView(R.id.url)
    TextView tvUrl;

    @Inject
    ImageSrcRepository imageSrcRepository;

    private ArrayList<ImageSrc> sources;
    private MyViewPagerAdapter myViewPagerAdapter;
    private int selectedPosition = 0;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        GalleryTaskApp.getAppComponent().inject(this);
        setStyle(DialogFragment.STYLE_NORMAL, android.R.style.Theme_Black_NoTitleBar_Fullscreen);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_image_slider, container, false);
        ButterKnife.bind(this, v);

       /// sources = (ArrayList<ImageSrc>) getArguments().getSerializable("sources");
        sources = imageSrcRepository.getImageSrcByRequestPhrase(getArguments().getString(REQUEST_PHRASE));
        selectedPosition = getArguments().getInt(POSITION);

        myViewPagerAdapter = new MyViewPagerAdapter();
        viewPager.setAdapter(myViewPagerAdapter);
        viewPager.addOnPageChangeListener(viewPagerPageChangeListener);

        setCurrentItem(selectedPosition);

        return v;
    }

    private void setCurrentItem(int position) {
        viewPager.setCurrentItem(position, false);
        displayMetaInfo(selectedPosition);
    }

    ViewPager.OnPageChangeListener viewPagerPageChangeListener = new ViewPager.OnPageChangeListener() {

        @Override
        public void onPageSelected(int position) {
            displayMetaInfo(position);
        }

        @Override
        public void onPageScrolled(int arg0, float arg1, int arg2) {

        }

        @Override
        public void onPageScrollStateChanged(int arg0) {

        }
    };

    private void displayMetaInfo(int position) {
        lblCount.setText(new StringBuilder("" + (position + 1)).append(" of ").append(sources.size()).toString());

        ImageSrc source = sources.get(position);
        tvTitle.setText(new StringBuilder("Photo by: ").append(source.getPhotographer()));
        tvUrl.setText(source.getPexelsUrl());
        Linkify.addLinks(tvUrl, Linkify.WEB_URLS);
    }


    public class MyViewPagerAdapter extends PagerAdapter {

        private LayoutInflater layoutInflater;

        public MyViewPagerAdapter() {
        }

        @Override
        public Object instantiateItem(@NonNull ViewGroup container, int position) {

            layoutInflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View view = layoutInflater.inflate(R.layout.image_fullscreen, container, false);
            PhotoView imageViewPreview = (PhotoView) view.findViewById(R.id.iv_preview);
            ImageSrc source = sources.get(position);

            Glide.with(getActivity()).load(source.getLargeUrl())
                    .thumbnail(0.5f)
                    .crossFade()
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(imageViewPreview);

            container.addView(view);
            return view;
        }

        @Override
        public int getCount() {
            return sources.size();
        }

        @Override
        public boolean isViewFromObject(@NonNull View view, @NonNull Object obj) {
            return view == ((View) obj);
        }


        @Override
        public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
            container.removeView((View) object);
        }
    }

}
