package com.consult.me.app.persenters.notification;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.consult.me.app.Constants;
import com.consult.me.app.models.Notification;
import com.consult.me.app.models.PushNotification;
import com.consult.me.app.models.User;
import com.consult.me.app.persenters.BasePresenter;
import com.consult.me.app.utilities.helpers.StorageHelper;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class NotificationsPresenter implements BasePresenter {
    private DatabaseReference reference;
    private ValueEventListener listener;
    private NotificationsCallback callback;

    public NotificationsPresenter(NotificationsCallback callback) {
        reference = FirebaseDatabase.getInstance().getReference().child(Constants.NODE_NAME_NOTIFICATIONS).getRef();
        this.callback = callback;
    }

    public void save(Notification notification, List<User> users) {
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                PushNotification pushNotification = new PushNotification(notification.getPrescriptionId(), notification.getMessage());
                for (User user : users) {
                    reference.child(user.getId()).push().setValue(pushNotification);
                }
                if (callback != null) {
                    callback.onSaveNotificationComplete();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void delete(Notification notification, int position) {
        String userId = StorageHelper.getCurrentUser().getId();
        reference.child(userId).child(notification.getId()).removeValue(new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(@Nullable DatabaseError error, @NonNull DatabaseReference ref) {
                if (callback != null) {
                    callback.onDeleteNotificationComplete(position);
                }
            }
        });
    }

    public void getNotifications() {
        callback.onShowLoading();
        String userId = StorageHelper.getCurrentUser().getId();
        listener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                List<Notification> notifications = new ArrayList<>();
                for (DataSnapshot child : snapshot.getChildren()) {
                    Notification notification = child.getValue(Notification.class);
                    notification.setId(child.getKey());
                    notifications.add(notification);
                }

                if (callback != null) {
                    callback.onGetNotificationsComplete(notifications);
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
        reference.child(userId).addListenerForSingleValueEvent(listener);
    }

    @Override
    public void onDestroy() {
        if (reference != null && listener != null) {
            reference.removeEventListener(listener);
        }
    }
}
