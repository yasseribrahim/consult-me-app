package com.consult.me.app.models;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.Objects;

public class Medication implements Parcelable {
    private String id;
    private String medicationName;
    private String dosage;
    private int periodic;
    public Medication() {
    }
    public Medication(String id, String medicationName, String dosage, int periodic) {
        this.id = id;
        this.medicationName = medicationName;
        this.dosage = dosage;
        this.periodic = periodic;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getMedicationName() {
        return medicationName;
    }

    public void setMedicationName(String medicationName) {
        this.medicationName = medicationName;
    }

    public String getDosage() {
        return dosage;
    }

    public void setDosage(String dosage) {
        this.dosage = dosage;
    }

    public int getPeriodic() {
        return periodic;
    }

    public void setPeriodic(int periodic) {
        this.periodic = periodic;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Medication that = (Medication) o;
        return id != null && id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.id);
        dest.writeString(this.medicationName);
        dest.writeString(this.dosage);
        dest.writeInt(this.periodic);
    }

    public void readFromParcel(Parcel source) {
        this.id = source.readString();
        this.medicationName = source.readString();
        this.dosage = source.readString();
        this.periodic = source.readInt();
    }

    protected Medication(Parcel in) {
        this.id = in.readString();
        this.medicationName = in.readString();
        this.dosage = in.readString();
        this.periodic = in.readInt();
    }

    public static final Creator<Medication> CREATOR = new Creator<Medication>() {
        @Override
        public Medication createFromParcel(Parcel source) {
            return new Medication(source);
        }

        @Override
        public Medication[] newArray(int size) {
            return new Medication[size];
        }
    };
}
