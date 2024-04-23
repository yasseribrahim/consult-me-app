package com.consult.me.app.persenters.links;

import androidx.annotation.NonNull;

import com.consult.me.app.Constants;
import com.consult.me.app.models.Link;
import com.consult.me.app.persenters.BasePresenter;
import com.consult.me.app.utilities.helpers.StorageHelper;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class LinksPresenter implements BasePresenter {
    private DatabaseReference reference;
    private ValueEventListener listener;
    private LinksCallback callback;

    public LinksPresenter(LinksCallback callback) {
        reference = FirebaseDatabase.getInstance().getReference().child(Constants.NODE_NAME_LINKS).getRef();
        this.callback = callback;
    }

    public void save(Link link) {
        callback.onHideLoading();
        if (link.getId() == null) {
            link.setId("link-" + System.currentTimeMillis());
        }
        reference.child(link.getId()).setValue(link).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                if (callback != null) {
                    callback.onSaveLinkComplete();
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

    public void delete(Link link) {
        reference.child(link.getId()).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                if (callback != null) {
                    callback.onDeleteLinkComplete(link);
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

    public void getLinks() {
        callback.onShowLoading();
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<Link> links = new ArrayList<>();
                for (var child : snapshot.getChildren()) {
                    var link = child.getValue(Link.class);
                    var currentUser = StorageHelper.getCurrentUser();
                    var accepted = (currentUser.isPatient() && link.getPatientId().equalsIgnoreCase(currentUser.getUsername())) ||
                            (currentUser.isDoctor() && link.getDoctorId().equalsIgnoreCase(currentUser.getUsername()));
                    if (accepted) {
                        links.add(link);
                    }
                }

                if (callback != null) {
                    callback.onGetLinksComplete(links);
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
