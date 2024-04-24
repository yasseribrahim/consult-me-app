package com.consult.me.app.models;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.Objects;

public class Link implements Parcelable {
    private String id;
    private String consultantId;
    private String clientId;

    public Link() {
    }

    public Link(String id, String consultantId, String clientId) {
        this.id = id;
        this.consultantId = consultantId;
        this.clientId = clientId;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getConsultantId() {
        return consultantId;
    }

    public void setConsultantId(String consultantId) {
        this.consultantId = consultantId;
    }

    public String getClientId() {
        return clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    public boolean equals2(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Link link = (Link) o;
        return consultantId.equals(link.consultantId) && clientId.equals(link.clientId);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Link category = (Link) o;
        return id.equals(category.id) || equals2(o);
    }

    @Override
    public int hashCode() {
        return Objects.hash(consultantId, clientId);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.id);
        dest.writeString(this.consultantId);
        dest.writeString(this.clientId);
    }

    public void readFromParcel(Parcel source) {
        this.id = source.readString();
        this.consultantId = source.readString();
        this.clientId = source.readString();
    }

    protected Link(Parcel in) {
        this.id = in.readString();
        this.consultantId = in.readString();
        this.clientId = in.readString();
    }

    public static final Creator<Link> CREATOR = new Creator<Link>() {
        @Override
        public Link createFromParcel(Parcel source) {
            return new Link(source);
        }

        @Override
        public Link[] newArray(int size) {
            return new Link[size];
        }
    };
}
