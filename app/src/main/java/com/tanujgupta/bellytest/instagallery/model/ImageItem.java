//Model class for storing each images thumbnail and standard size image url

package com.tanujgupta.bellytest.instagallery.model;


public class ImageItem {

    private String urlThumbnail;
    private String urlStandard;

    public ImageItem(String urlThumbnail, String urlStandard) {

        this.urlThumbnail = urlThumbnail;
        this.urlStandard =  urlStandard;
    }

    public String getUrlThumbnail() {
        return urlThumbnail;
    }

    public void setUrlThumbnail(String urlThumbnail) {
        this.urlThumbnail = urlThumbnail;
    }

    public String getUrlStandard() {
        return urlStandard;
    }

    public void setUrlStandard(String urlStandard) {
        this.urlStandard = urlStandard;
    }
}