package com.consult.me.app.persenters.messaging;

import com.consult.me.app.models.Message;
import com.consult.me.app.persenters.BaseCallback;

public interface MessagingCallback extends BaseCallback {
    void onSendMessageSuccess();

    void onSendMessageFailure(String message);

    void onGetMessageSuccess(Message message);

    void onGetMessageFailure(String message);

    void onEmptyMessaging();
}
