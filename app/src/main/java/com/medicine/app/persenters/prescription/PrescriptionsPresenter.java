package com.medicine.app.persenters.prescription;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.medicine.app.Constants;
import com.medicine.app.models.Prescription;
import com.medicine.app.persenters.BasePresenter;
import com.medicine.app.utilities.helpers.StorageHelper;

import java.util.ArrayList;
import java.util.List;

public class PrescriptionsPresenter implements BasePresenter {
    private DatabaseReference reference;
    private ValueEventListener listener;
    private PrescriptionsCallback callback;

    public PrescriptionsPresenter(PrescriptionsCallback callback) {
        reference = FirebaseDatabase.getInstance().getReference().child(Constants.NODE_NAME_PRESCRIPTIONS).getRef();
        this.callback = callback;
    }

    public void save(Prescription prescription) {
        callback.onHideLoading();
        if (prescription.getId() == null) {
            prescription.setId("prescription-" + System.currentTimeMillis());
        }
        reference.child(prescription.getId()).setValue(prescription).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                if (callback != null) {
                    callback.onSavePrescriptionComplete();
                    callback.onHideLoading();
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                callback.onFailure(e.getMessage(), null);
                callback.onHideLoading();
            }
        });
    }

    public void getPrescription(String id) {
        callback.onShowLoading();
        listener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                Prescription prescription = null;
                if (snapshot.exists()) {
                    prescription = snapshot.getValue(Prescription.class);
                }

                if (callback != null) {
                    callback.onGetPrescriptionComplete(prescription);
                    callback.onHideLoading();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                if (callback != null) {
                    callback.onFailure("Unable to get message: " + databaseError.getMessage(), null);
                    callback.onHideLoading();
                }
            }
        };
        reference.child(id).addListenerForSingleValueEvent(listener);
    }

    public void delete(Prescription prescription) {
        reference.child(prescription.getId()).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                if (callback != null) {
                    callback.onDeletePrescriptionComplete(prescription);
                    callback.onHideLoading();
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                callback.onFailure(e.getMessage(), null);
                callback.onHideLoading();
            }
        });
    }

    public void getPrescriptions(String username) {
        callback.onShowLoading();
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<Prescription> prescriptions = new ArrayList<>();
                for (var child : snapshot.getChildren()) {
                    var prescription = child.getValue(Prescription.class);
                    var currentUser = StorageHelper.getCurrentUser();
                    var accepted = (currentUser.isPatient() && prescription.getPatientId().equalsIgnoreCase(currentUser.getUsername()) && prescription.getDoctorId().equalsIgnoreCase(username)) ||
                            (currentUser.isDoctor() && prescription.getDoctorId().equalsIgnoreCase(currentUser.getUsername()) && prescription.getPatientId().equalsIgnoreCase(username));
                    if (accepted) {
                        prescriptions.add(prescription);
                    }
                }

                if (callback != null) {
                    callback.onGetPrescriptionsComplete(prescriptions);
                }
                callback.onHideLoading();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                callback.onHideLoading();
            }
        });
    }

    @Override
    public void onDestroy() {
        if (reference != null && listener != null) {
            reference.removeEventListener(listener);
        }
    }
}
