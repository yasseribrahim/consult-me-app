package com.consult.me.app.persenters.firebase;

import com.consult.me.app.persenters.BaseCallback;

public interface FirebaseCallback extends BaseCallback {
    default void onSaveTokenComplete() {
    }

    default void onGetTokenComplete(String token) {
    }
}
