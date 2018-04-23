package com.example.alexandermelnikov.yandexgallerytask.model.api;

import java.io.Serializable;

/**
 * Created by AlexMelnikov on 17.04.18.
 */

public class DisplaySize implements Serializable {
    public boolean is_watermarked;
    public String name;
    public String uri;

    public DisplaySize() {
    }

    public boolean isIs_watermarked() {
        return is_watermarked;
    }

    public void setIs_watermarked(boolean is_watermarked) {
        this.is_watermarked = is_watermarked;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }
}
