package com.medicine.app.persenters.notification;

import com.medicine.app.models.Notification;
import com.medicine.app.persenters.BaseCallback;

import java.util.List;

public interface NotificationsCallback extends BaseCallback {
    default void onGetNotificationsComplete(List<Notification> notifications) {
    }

    default void onSaveNotificationComplete() {
    }

    default void onDeleteNotificationComplete(int position) {
    }
}
