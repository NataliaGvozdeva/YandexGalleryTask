package com.example.alexandermelnikov.yandexgallerytask.model.api;

import java.util.List;

/**
 * Created by AlexMelnikov on 17.04.18.
 */

public class Image {
    public String id;
    public List<DisplaySize> display_sizes;
    public List<ReferralDestination> referral_destinations;
    public String title;

    public Image() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public List<DisplaySize> getDisplay_sizes() {
        return display_sizes;
    }

    public void setDisplay_sizes(List<DisplaySize> display_sizes) {
        this.display_sizes = display_sizes;
    }

    public List<ReferralDestination> getReferral_destinations() {
        return referral_destinations;
    }

    public void setReferral_destinations(List<ReferralDestination> referral_destinations) {
        this.referral_destinations = referral_destinations;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
