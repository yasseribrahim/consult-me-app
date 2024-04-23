package com.consult.me.app.persenters.about;

import com.consult.me.app.models.About;
import com.consult.me.app.persenters.BaseCallback;

public interface AboutCallback extends BaseCallback {
    default void onGetAboutComplete(About about) {
    }

    default void onSaveAboutComplete() {
    }
}
