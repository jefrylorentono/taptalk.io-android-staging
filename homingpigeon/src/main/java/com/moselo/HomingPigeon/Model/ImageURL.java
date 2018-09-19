package com.moselo.HomingPigeon.Model;

import android.os.Parcel;
import android.os.Parcelable;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ImageURL implements Parcelable {
    @JsonProperty("fullsize") private String fullsize;
    @JsonProperty("thumbnail") private String thumbnail;

    public ImageURL() { }

    public String getFullsize() {
        return fullsize;
    }

    public void setFullsize(String fullsize) {
        this.fullsize = fullsize;
    }

    public String getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(String thumbnail) {
        this.thumbnail = thumbnail;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.fullsize);
        dest.writeString(this.thumbnail);
    }

    protected ImageURL(Parcel in) {
        this.fullsize = in.readString();
        this.thumbnail = in.readString();
    }

    public static final Parcelable.Creator<ImageURL> CREATOR = new Parcelable.Creator<ImageURL>() {
        @Override
        public ImageURL createFromParcel(Parcel source) {
            return new ImageURL(source);
        }

        @Override
        public ImageURL[] newArray(int size) {
            return new ImageURL[size];
        }
    };
}
