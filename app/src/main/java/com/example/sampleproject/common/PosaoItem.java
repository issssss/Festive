package com.example.sampleproject.common;

import android.os.Parcel;
import android.os.Parcelable;

public class PosaoItem implements Parcelable {

    private String pText;

    public PosaoItem(){
    }

    public PosaoItem(String text) {
        pText = text;
    }

    protected PosaoItem(Parcel in) {
        pText = in.readString();
    }

    public String getmText() {
        return pText;
    }

    public static final Parcelable.Creator<PosaoItem> CREATOR = new Parcelable.Creator<PosaoItem>() {
        @Override
        public PosaoItem createFromParcel(Parcel in) {
            return new PosaoItem(in);
        }

        @Override
        public PosaoItem[] newArray(int size) {
            return new PosaoItem[size];
        }
    };

    public String getText() {
        return pText;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(pText);
    }
}
