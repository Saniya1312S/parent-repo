package com.example.parentwithsubscription.features.locationtracking.model;

import com.google.gson.annotations.SerializedName;

public class LocationData {

    @SerializedName("location")
    private Location location;

    @SerializedName("location_source")
    private String locationSource;

    @SerializedName("duration")
    private long duration;

    @SerializedName("from_time")
    private long fromTime;

    @SerializedName("to_time")
    private long toTime;

    @SerializedName("geofence")
    private String geofence;

    public LocationData(Location location, String locationSource, long duration, long fromTime, long toTime, String geofence) {
        this.location = location;
        this.locationSource = locationSource;
        this.duration = duration;
        this.fromTime = fromTime;
        this.toTime = toTime;
        this.geofence = geofence;
    }

    // Getter and Setter Methods
    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public String getLocationSource() {
        return locationSource;
    }

    public void setLocationSource(String locationSource) {
        this.locationSource = locationSource;
    }

    public long getDuration() {
        return duration;
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }

    public long getFromTime() {
        return fromTime;
    }

    public void setFromTime(long fromTime) {
        this.fromTime = fromTime;
    }

    public long getToTime() {
        return toTime;
    }

    public void setToTime(long toTime) {
        this.toTime = toTime;
    }

    public String getGeofence() {
        return geofence;
    }

    public void setGeofence(String geofence) {
        this.geofence = geofence;
    }

    // Location class to encapsulate location data
    public static class Location {

        @SerializedName("latitude")
        private double latitude;

        @SerializedName("longitude")
        private double longitude;

        @SerializedName("address")
        private String address;

        public Location(double latitude, double longitude, String address) {
            this.latitude = latitude;
            this.longitude = longitude;
            this.address = address;
        }

        // Getter and Setter Methods for Location
        public double getLatitude() {
            return latitude;
        }

        public void setLatitude(double latitude) {
            this.latitude = latitude;
        }

        public double getLongitude() {
            return longitude;
        }

        public void setLongitude(double longitude) {
            this.longitude = longitude;
        }

        public String getAddress() {
            return address;
        }

        public void setAddress(String address) {
            this.address = address;
        }
    }
}
