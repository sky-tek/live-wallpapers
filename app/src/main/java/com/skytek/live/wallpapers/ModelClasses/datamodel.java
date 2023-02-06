package com.skytek.live.wallpapers.ModelClasses;

public class datamodel {
    int image;
    String language;
    Boolean check=false;
    String lcode;

    public datamodel() {
    }

    public datamodel(int image,String language, Boolean check, String lcode) {
        this.image=image;
        this.language = language;
        this.check = check;
        this.lcode=lcode;
    }

    public Integer getImage() {
        return image;
    }

    public void setImage(Integer image) {
        this.image = image;
    }

    public String getLanguage() {
        return language;
    }
    public String getLcode() {
        return lcode;
    }

    public void setLanguage(String language) {
        this.language = language;
    }
    public void setLcode(String lcode){this.lcode=lcode;}

    public Boolean getCheck() {
        return check;
    }

    public void setCheck(Boolean check) {
        this.check = check;
    }
}
