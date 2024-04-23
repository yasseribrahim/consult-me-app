package com.medicine.app.persenters.user;

import com.medicine.app.models.User;
import com.medicine.app.persenters.BaseCallback;

import java.util.List;

public interface UsersCallback extends BaseCallback {
    default void onGetUsersComplete(List<User> users) {
    }

    default void onSaveUserComplete() {
    }
    default void onGetUsersCountComplete(long adminCount, long craftsCount, long clientsCount) {
    }

    default void onDeleteUserComplete(User user) {
    }

    default void onSignupUserComplete() {
    }

    default void onSignupUserFail(String message) {
    }

    default void onGetUserComplete(User user) {
    }
}
