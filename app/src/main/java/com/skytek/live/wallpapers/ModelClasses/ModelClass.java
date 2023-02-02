package com.skytek.live.wallpapers.ModelClasses;

public class ModelClass {
    String thumb_path;
    String id;
    String likes;
    String downloads;


    public ModelClass() {

    }
    public ModelClass(String thumb_path,String id,String likes,String downloads) {

        this.thumb_path = thumb_path;
        this.id = id;
        this.likes = likes;
        this.downloads = downloads;

    }

    public String getThumb_path() {
        return thumb_path;
    }

    public void setThumb_path(String thumb_path) {
        this.thumb_path = thumb_path;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getLikes() {
        return likes;
    }

    public void setLikes(String likes) {
        this.likes = likes;
    }

    public String getDownloads() {
        return downloads;
    }

    public void setDownloads(String downloads) {
        this.downloads = downloads;
    }
}
