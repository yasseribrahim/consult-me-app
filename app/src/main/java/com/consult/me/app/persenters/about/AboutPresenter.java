package com.consult.me.app.persenters.about;

import androidx.annotation.NonNull;

import com.consult.me.app.Constants;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.consult.me.app.models.About;
import com.consult.me.app.persenters.BasePresenter;

public class AboutPresenter implements BasePresenter {
    private DatabaseReference reference;
    private ValueEventListener listener;
    private AboutCallback callback;

    public AboutPresenter(AboutCallback callback) {
        reference = FirebaseDatabase.getInstance().getReference().child(Constants.NODE_NAME_ABOUT).getRef();
        this.callback = callback;
    }

    public void save(About about) {
        callback.onShowLoading();
        reference.setValue(about).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                callback.onSaveAboutComplete();
                callback.onHideLoading();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                callback.onFailure(e.getMessage(), null);
                callback.onHideLoading();
            }
        });
    }

    public void getAbout() {
        callback.onShowLoading();
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                About about = new About();
                if (snapshot.exists()) {
                    about = snapshot.getValue(About.class);
                }
                if (callback != null) {
                    callback.onGetAboutComplete(about);
                }
                callback.onHideLoading();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

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
