package com.example.myroute_cs426_finalproject.Model;

import android.graphics.Bitmap;
import android.location.Location;
import android.os.Parcel;
import android.os.Parcelable;

import java.lang.reflect.Array;
import java.util.List;

public class RouteModel  implements Parcelable{
    private String name;
    private String info;
    private String[] latlngs;
    private List<Location> locationList;
    private Bitmap routeImage;

    public RouteModel(){};
    public RouteModel(List<Location> locationList){
        this.locationList = locationList;
    };

    protected RouteModel(Parcel in) {
        name = in.readString();
        info = in.readString();
        locationList = in.createTypedArrayList(Location.CREATOR);
        latlngs = in.createStringArray();
        routeImage = in.readParcelable(Bitmap.class.getClassLoader());
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeString(info);
        dest.writeTypedList(locationList);
        dest.writeStringArray(latlngs);
        dest.writeParcelable(routeImage, flags);
    }

    @Override
    public int describeContents() {
        return 0;
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



    public Bitmap getRouteImage() {
        return routeImage;
    }

    public void setRouteImage(Bitmap routeImage) {
        this.routeImage = routeImage;
    }


    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    public String[] getLatlngs() {
        return latlngs;
    }

    public void setLatlngs(String[] latlngs) {
        this.latlngs = latlngs;
    }
}
