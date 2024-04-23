package com.medicine.app.models;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.Objects;

public class Link implements Parcelable {
    private String id;
    private String doctorId;
    private String patientId;

    public Link() {
    }

    public Link(String id, String doctorId, String patientId) {
        this.id = id;
        this.doctorId = doctorId;
        this.patientId = patientId;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDoctorId() {
        return doctorId;
    }

    public void setDoctorId(String doctorId) {
        this.doctorId = doctorId;
    }

    public String getPatientId() {
        return patientId;
    }

    public void setPatientId(String patientId) {
        this.patientId = patientId;
    }

    public boolean equals2(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Link link = (Link) o;
        return doctorId.equals(link.doctorId) && patientId.equals(link.patientId);
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
        return Objects.hash(doctorId, patientId);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.id);
        dest.writeString(this.doctorId);
        dest.writeString(this.patientId);
    }

    public void readFromParcel(Parcel source) {
        this.id = source.readString();
        this.doctorId = source.readString();
        this.patientId = source.readString();
    }

    protected Link(Parcel in) {
        this.id = in.readString();
        this.doctorId = in.readString();
        this.patientId = in.readString();
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
