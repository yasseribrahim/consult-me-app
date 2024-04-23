package com.consult.me.app.persenters.notification;

import com.consult.me.app.models.Notification;
import com.consult.me.app.persenters.BaseCallback;

import java.util.List;

public interface NotificationsCallback extends BaseCallback {
    default void onGetNotificationsComplete(List<Notification> notifications) {
    }

    default void onSaveNotificationComplete() {
    }

    default void onDeleteNotificationComplete(int position) {
    }
}
