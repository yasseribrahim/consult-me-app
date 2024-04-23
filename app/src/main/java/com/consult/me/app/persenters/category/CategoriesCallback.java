package com.consult.me.app.persenters.category;

import com.consult.me.app.models.Category;
import com.consult.me.app.persenters.BaseCallback;

import java.util.List;

public interface CategoriesCallback extends BaseCallback {
    default void onGetCategoriesComplete(List<Category> categories) {
    }

    default void onGetCategoriesCountComplete(long count) {
    }

    default void onSaveCategoryComplete() {
    }

    default void onDeleteCategoryComplete(Category category) {
    }

    default void onGetCategoryComplete(Category category) {
    }
}
