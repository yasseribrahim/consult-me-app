package com.medicine.app.persenters.about;

import com.medicine.app.models.About;
import com.medicine.app.persenters.BaseCallback;

public interface AboutCallback extends BaseCallback {
    default void onGetAboutComplete(About about) {
    }

    default void onSaveAboutComplete() {
    }
}
