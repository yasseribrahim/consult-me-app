package com.consult.me.app.persenters.prescription;

import com.consult.me.app.models.Prescription;
import com.consult.me.app.persenters.BaseCallback;

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
