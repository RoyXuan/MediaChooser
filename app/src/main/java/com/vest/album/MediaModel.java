package com.vest.album;

public class MediaModel {

    public String url = null;
    private String miniUrl;
    public boolean status = false;
    public int type;
    public String name;

    public MediaModel(String url, boolean status, int type, String name) {
        this.url = url;
        this.status = status;
        this.type = type;
        this.name = name;
    }

    public String getMiniUrl() {
        return miniUrl;
    }

    public void setMiniUrl(String miniUrl) {
        this.miniUrl = miniUrl;
    }

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }
}
