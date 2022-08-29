package com.example.myroute_cs426_finalproject.Model;

import android.graphics.Bitmap;
import android.location.Location;
import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;

public class RouteModel  implements Parcelable{
    private String name;
    private String imageUrl;
    private List<Location> locationList;
    private List<Location> bookmarkList;
    private Bitmap routeImage;

    protected RouteModel(Parcel in) {
        name = in.readString();
        imageUrl = in.readString();
        locationList = in.createTypedArrayList(Location.CREATOR);
        bookmarkList = in.createTypedArrayList(Location.CREATOR);
        routeImage = in.readParcelable(Bitmap.class.getClassLoader());
    }

    public static final Creator<RouteModel> CREATOR = new Creator<RouteModel>() {
        @Override
        public RouteModel createFromParcel(Parcel in) {
            return new RouteModel(in);
        }

        @Override
        public RouteModel[] newArray(int size) {
            return new RouteModel[size];
        }
    };

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Location> getLocationList() {
        return locationList;
    }

    public void setLocationList(List<Location> locationList) {
        this.locationList = locationList;
    }

    public List<Location> getBookmarkList() {
        return bookmarkList;
    }

    public void setBookmarkList(List<Location> bookmarkList) {
        this.bookmarkList = bookmarkList;
    }

    public Bitmap getRouteImage() {
        return routeImage;
    }

    public void setRouteImage(Bitmap routeImage) {
        this.routeImage = routeImage;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeString(imageUrl);
        dest.writeTypedList(locationList);
        dest.writeTypedList(bookmarkList);
        dest.writeParcelable(routeImage, flags);
    }


    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
}
