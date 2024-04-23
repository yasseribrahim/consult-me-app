package com.medicine.app.models;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class Prescription implements Parcelable {
    private String id;
    private String title;
    private String description;
    private String doctorId;
    private String patientId;
    private Date date;
    private List<Medication> medications;

    public Prescription(String id) {
        this.id = id;
        this.medications = new ArrayList<>();
        date = Calendar.getInstance().getTime();
    }

    public Prescription() {
        this.medications = new ArrayList<>();
        date = Calendar.getInstance().getTime();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public void setDoctorId(String doctorId) {
        this.doctorId = doctorId;
    }

    public String getDoctorId() {
        return doctorId;
    }

    public String getPatientId() {
        return patientId;
    }

    public void setPatientId(String patientId) {
        this.patientId = patientId;
    }

    public List<Medication> getMedications() {
        return medications;
    }

    public void setMedications(List<Medication> medications) {
        this.medications = medications;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Prescription question = (Prescription) o;
        return id.equals(question.id);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.id);
        dest.writeString(this.title);
        dest.writeString(this.description);
        dest.writeString(this.doctorId);
        dest.writeString(this.patientId);
        dest.writeLong(this.date != null ? this.date.getTime() : -1);
        dest.writeTypedList(this.medications);
    }

    public void readFromParcel(Parcel source) {
        this.id = source.readString();
        this.title = source.readString();
        this.description = source.readString();
        this.doctorId = source.readString();
        this.patientId = source.readString();
        long tmpDate = source.readLong();
        this.date = tmpDate == -1 ? null : new Date(tmpDate);
        this.medications = source.createTypedArrayList(Medication.CREATOR);
    }

    protected Prescription(Parcel in) {
        this.id = in.readString();
        this.title = in.readString();
        this.description = in.readString();
        this.doctorId = in.readString();
        this.patientId = in.readString();
        long tmpDate = in.readLong();
        this.date = tmpDate == -1 ? null : new Date(tmpDate);
        this.medications = in.createTypedArrayList(Medication.CREATOR);
    }

    public static final Creator<Prescription> CREATOR = new Creator<Prescription>() {
        @Override
        public Prescription createFromParcel(Parcel source) {
            return new Prescription(source);
        }

        @Override
        public Prescription[] newArray(int size) {
            return new Prescription[size];
        }
    };
}
