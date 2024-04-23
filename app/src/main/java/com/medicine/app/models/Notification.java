package com.medicine.app.models;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.Calendar;

public class Notification implements Parcelable {
    private String id;
    private String message;
    private long timestamp;
    private String prescriptionId;
    public Notification() {
        timestamp = Calendar.getInstance().getTimeInMillis();
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public String getPrescriptionId() {
        return prescriptionId;
    }

    public void setPrescriptionId(String prescriptionId) {
        this.prescriptionId = prescriptionId;
    }

    @Override
    public String toString() {
        return "Notification{" + "message='" + message + '\'' + ", timestamp=" + timestamp + ", location='" + prescriptionId + '\'' + '}';
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.id);
        dest.writeString(this.message);
        dest.writeLong(this.timestamp);
        dest.writeString(this.prescriptionId);
    }

    public void readFromParcel(Parcel source) {
        this.id = source.readString();
        this.message = source.readString();
        this.timestamp = source.readLong();
        this.prescriptionId = source.readString();
    }

    protected Notification(Parcel in) {
        this.id = in.readString();
        this.message = in.readString();
        this.timestamp = in.readLong();
        this.prescriptionId = in.readString();
    }

    public static final Creator<Notification> CREATOR = new Creator<Notification>() {
        @Override
        public Notification createFromParcel(Parcel source) {
            return new Notification(source);
        }

        @Override
        public Notification[] newArray(int size) {
            return new Notification[size];
        }
    };
}