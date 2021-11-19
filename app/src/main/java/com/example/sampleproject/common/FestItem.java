package com.example.sampleproject.common;

import android.os.Parcel;
import android.os.Parcelable;

public class FestItem implements Parcelable {

    private String imeFest;
    private String dogIposao="";
    private String brojPos="";
    private String prosto="";
    private String brojOso="";
    public FestItem(){}

    public  FestItem(String text){
        imeFest=text;
    }
    public FestItem(String text, String text2, String prostorija, String brPos, String brOso){
        imeFest=text;
        dogIposao=text2;
        brojPos=brPos;
        prosto=prostorija;
        brojOso=brOso;
    }
    public FestItem(String text, String text2, String brPos, String brOso){
        imeFest=text;
        dogIposao=text2;
        brojPos=brPos;
        brojOso=brOso;
    }
    protected FestItem(Parcel in) {
        imeFest=in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<FestItem> CREATOR = new Creator<FestItem>() {
        @Override
        public FestItem createFromParcel(Parcel in) {
            return new FestItem(in);
        }

        @Override
        public FestItem[] newArray(int size) {
            return new FestItem[size];
        }
    };
    public String getTextFest() {
        return imeFest;
    }
    public String getTextPos(){return dogIposao;}
    public String getTextBrPos(){return brojPos;}
    public String getTextBrOso(){return brojOso;}
    public String getTextPro(){return prosto;}
}
