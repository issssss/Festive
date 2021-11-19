package com.example.sampleproject.izvodac;

import android.os.Parcel;
import android.os.Parcelable;

public class SpecItem implements Parcelable{

    private String mText;

    public SpecItem(){

    }

    public SpecItem(String text) {
        mText = text;
    }

    protected SpecItem(Parcel in) {
        mText = in.readString();
    }

    public String getmText() {
        return mText;
    }

    public static final Parcelable.Creator<SpecItem> CREATOR = new Parcelable.Creator<SpecItem>() {
        @Override
        public SpecItem createFromParcel(Parcel in) {
            return new SpecItem(in);
        }

        @Override
        public SpecItem[] newArray(int size) {
            return new SpecItem[size];
        }
    };
    /**
     public void changeText1(String text){
     mText1 = text;
     }*/

    public String getText() {
        return mText;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(mText);
    }
}
