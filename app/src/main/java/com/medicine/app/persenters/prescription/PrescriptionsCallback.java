package com.medicine.app.persenters.prescription;

import com.medicine.app.models.Prescription;
import com.medicine.app.persenters.BaseCallback;

import java.util.List;

public interface PrescriptionsCallback extends BaseCallback {
    default void onGetPrescriptionsComplete(List<Prescription> prescriptions) {
    }

    default void onGetPrescriptionsCountComplete(long count) {
    }

    default void onSavePrescriptionComplete() {
    }

    default void onDeletePrescriptionComplete(Prescription prescription) {
    }

    default void onGetPrescriptionComplete(Prescription prescription) {
    }
}
