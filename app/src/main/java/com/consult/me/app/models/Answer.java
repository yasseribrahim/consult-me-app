package com.consult.me.app.models;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.Date;
import java.util.Objects;

public class Answer implements Parcelable {
    private String id;
    private String consultantId;
    private String answer;
    private Date date;
    private boolean done;

    public Answer() {
    }

    public Answer(String consultantId) {
        this.consultantId = consultantId;
    }

    public Answer(String id, String consultantId, String answer, Date date, boolean done) {
        this.id = id;
        this.consultantId = consultantId;
        this.answer = answer;
        this.date = date;
        this.done = done;
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

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public boolean isDone() {
        return done;
    }

    public void setDone(boolean done) {
        this.done = done;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Answer answer = (Answer) o;
        return consultantId.equals(answer.consultantId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(consultantId);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.id);
        dest.writeString(this.consultantId);
        dest.writeString(this.answer);
        dest.writeLong(this.date != null ? this.date.getTime() : -1);
        dest.writeByte(this.done ? (byte) 1 : (byte) 0);
    }

    public void readFromParcel(Parcel source) {
        this.id = source.readString();
        this.consultantId = source.readString();
        this.answer = source.readString();
        long tmpDate = source.readLong();
        this.date = tmpDate == -1 ? null : new Date(tmpDate);
        this.done = source.readByte() != 0;
    }

    protected Answer(Parcel in) {
        this.id = in.readString();
        this.consultantId = in.readString();
        this.answer = in.readString();
        long tmpDate = in.readLong();
        this.date = tmpDate == -1 ? null : new Date(tmpDate);
        this.done = in.readByte() != 0;
    }

    public static final Parcelable.Creator<Answer> CREATOR = new Parcelable.Creator<Answer>() {
        @Override
        public Answer createFromParcel(Parcel source) {
            return new Answer(source);
        }

        @Override
        public Answer[] newArray(int size) {
            return new Answer[size];
        }
    };
}
