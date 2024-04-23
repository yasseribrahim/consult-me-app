package com.medicine.app.models;

import android.os.Parcel;
import android.os.Parcelable;

public class Contacts implements Parcelable {
    private String id;
    private String phone;
    private String email;
    private String whatsApp;
    private String facebook;

    public Contacts() {
    }

    public Contacts(String id, String phone, String email, String whatsApp, String facebook) {
        this.id = id;
        this.phone = phone;
        this.email = email;
        this.whatsApp = whatsApp;
        this.facebook = facebook;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getWhatsApp() {
        return whatsApp;
    }

    public void setWhatsApp(String whatsApp) {
        this.whatsApp = whatsApp;
    }

    public String getFacebook() {
        return facebook;
    }

    public void setFacebook(String facebook) {
        this.facebook = facebook;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.id);
        dest.writeString(this.phone);
        dest.writeString(this.email);
        dest.writeString(this.whatsApp);
        dest.writeString(this.facebook);
    }

    public void readFromParcel(Parcel source) {
        this.id = source.readString();
        this.phone = source.readString();
        this.email = source.readString();
        this.whatsApp = source.readString();
        this.facebook = source.readString();
    }

    protected Contacts(Parcel in) {
        this.id = in.readString();
        this.phone = in.readString();
        this.email = in.readString();
        this.whatsApp = in.readString();
        this.facebook = in.readString();
    }

    public static final Creator<Contacts> CREATOR = new Creator<Contacts>() {
        @Override
        public Contacts createFromParcel(Parcel source) {
            return new Contacts(source);
        }

        @Override
        public Contacts[] newArray(int size) {
            return new Contacts[size];
        }
    };
}
