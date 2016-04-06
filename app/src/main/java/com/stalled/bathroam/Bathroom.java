package com.stalled.bathroam;

import com.google.android.gms.maps.model.LatLng;

public class Bathroom {
    private int id;
    private LatLng location;
    private float rating;

    public Bathroom(int _id, LatLng _location, float _rating) {
        id = _id;
        location = _location;
        rating = _rating;
    }

    public double getRating() {
        return rating;
    }

    public LatLng getLocation() {
        return location;
    }

    @Override
    public boolean equals(Object o) {

        // If the object is compared with itself then return true
        if (o == this) {
            return true;
        }

        /* Check if o is an instance of Complex or not
          "null instanceof [type]" also returns false */
        if (!(o instanceof Bathroom)) {
            return false;
        }

        // typecast o to Complex so that we can compare data members
        Bathroom c = (Bathroom) o;

        // Compare the data members and return accordingly
        return c.id == this.id;
    }

}
