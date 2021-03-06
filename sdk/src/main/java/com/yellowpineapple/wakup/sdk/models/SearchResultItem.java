package com.yellowpineapple.wakup.sdk.models;

import android.location.Address;
import android.location.Location;
import android.text.TextUtils;

import com.yellowpineapple.wakup.sdk.utils.Strings;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by agutierrez on 17/12/15.
 */
public class SearchResultItem implements Serializable {

    public enum Type {
        COMPANY, LOCATION, NEAR_ME, HEADER;
    }

    static final int MAX_FIELDS = 3;

    boolean recent = false;
    Type type;
    String name = null;
    String description = null;
    double latitude = 0;
    double longitude = 0;
    Company company = null;

    public SearchResultItem(boolean recent, Address address) {
            this.recent = recent;
        this.type = Type.LOCATION;
        String[] fields = new String[] {
                address.getFeatureName(),
                address.getLocality(),
                address.getSubAdminArea(),
                address.getAdminArea()};
        List<String> info = new ArrayList<>();
        for (String field : fields) {
            if (Strings.notEmpty(field) &&
                    !info.contains(field) &&
                    // Limit displayed fields
                    info.size() < MAX_FIELDS) {
                info.add(field);
            }
        }
        this.description = TextUtils.join(", ", info);
        this.name = address.getFeatureName();
        this.latitude = address.getLatitude();
        this.longitude = address.getLongitude();
        this.company = null;
    }

    public Location getLocation() {
        Location mLocation = new Location(this.name);
        mLocation.setLatitude(getLatitude());
        mLocation.setLongitude(getLongitude());
        return mLocation;
    }

    public SearchResultItem(String name) {
        this.type = Type.HEADER;
        this.name = this.description = name;
    }

    public SearchResultItem(boolean recent, Company company) {
        this.recent = recent;
        this.type = Type.COMPANY;
        this.name = this.description = company.getName();
        this.company = company;
    }

    public SearchResultItem(String name, Location location) {
        this.recent = false;
        this.type = Type.NEAR_ME;
        this.name = this.description = name;
        this.latitude = location.getLatitude();
        this.longitude = location.getLongitude();
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof SearchResultItem) {
            SearchResultItem otherItem = (SearchResultItem) o;
            boolean sameType = otherItem.getType() == type;
            if (sameType) {
                switch (type) {
                    case COMPANY:
                        return otherItem.getCompany().getId() == company.getId();
                    case LOCATION:
                        return otherItem.getLatitude() == latitude &&
                                otherItem.getLongitude() == longitude &&
                                Strings.equals(otherItem.getName(), name);
                }
            }
        }
        return false;
    }

    public Company getCompany() {
        return company;
    }

    public boolean isRecent() {
        return recent;
    }

    public Type getType() {
        return type;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }
}
