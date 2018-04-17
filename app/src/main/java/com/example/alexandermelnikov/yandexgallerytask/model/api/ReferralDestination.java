package com.example.alexandermelnikov.yandexgallerytask.model.api;

/**
 * Created by AlexMelnikov on 17.04.18.
 */

public class ReferralDestination {

    public String site_name;
    public String uri;

    public ReferralDestination() {
    }

    public String getSite_name() {
        return site_name;
    }

    public void setSite_name(String site_name) {
        this.site_name = site_name;
    }

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }
}
