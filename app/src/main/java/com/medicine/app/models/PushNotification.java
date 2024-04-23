package com.medicine.app.models;

import com.google.firebase.database.ServerValue;

import java.util.Map;

public class PushNotification {
    private String prescriptionId;
    private String message;
    private Map<String, String> timestamp;

    public PushNotification() {
        this.timestamp = ServerValue.TIMESTAMP;
    }

    public PushNotification(String prescriptionId, String message) {
        this.prescriptionId = prescriptionId;
        this.message = message;
        this.timestamp = ServerValue.TIMESTAMP;
    }

    public String getPrescriptionId() {
        return prescriptionId;
    }

    public void setPrescriptionId(String prescriptionId) {
        this.prescriptionId = prescriptionId;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Map<String, String> getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Map<String, String> timestamp) {
        this.timestamp = timestamp;
    }

    @Override
    public String toString() {
        return "PushNotification{" +
                "location='" + prescriptionId + '\'' +
                ", message='" + message + '\'' +
                ", timestamp=" + timestamp +
                '}';
    }
}
