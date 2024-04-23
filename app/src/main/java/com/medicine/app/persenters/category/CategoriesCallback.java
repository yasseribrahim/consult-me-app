package com.medicine.app.persenters.category;

import com.medicine.app.models.Category;
import com.medicine.app.persenters.BaseCallback;

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
