package com.medicine.app.persenters.category;

import androidx.annotation.NonNull;

import com.medicine.app.Constants;
import com.medicine.app.models.Category;
import com.medicine.app.persenters.BasePresenter;
import com.medicine.app.utilities.helpers.StorageHelper;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class CategoriesPresenter implements BasePresenter {
    private DatabaseReference reference;
    private ValueEventListener listener;
    private CategoriesCallback callback;

    public CategoriesPresenter(CategoriesCallback callback) {
        reference = FirebaseDatabase.getInstance().getReference().child(Constants.NODE_NAME_CATEGORIES).getRef();
        this.callback = callback;
    }

    public void save(Category category) {
        callback.onHideLoading();
        if (category.getId() == null) {
            category.setId("category-" + System.currentTimeMillis());
        }
        reference.child(category.getId()).setValue(category).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                if (callback != null) {
                    callback.onSaveCategoryComplete();
                    callback.onHideLoading();
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                callback.onFailure(e.getMessage(), null);
                callback.onHideLoading();
            }
        });
    }

    public void getCategory(String id) {
        callback.onShowLoading();
        listener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                Category category = null;
                if (snapshot.exists()) {
                    category = snapshot.getValue(Category.class);
                }

                if (callback != null) {
                    callback.onGetCategoryComplete(category);
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
        reference.child(id).addListenerForSingleValueEvent(listener);
    }

    public void delete(Category category) {
        reference.child(category.getId()).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                if (callback != null) {
                    callback.onDeleteCategoryComplete(category);
                    callback.onHideLoading();
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                callback.onFailure(e.getMessage(), null);
                callback.onHideLoading();
            }
        });
    }

    public void getCategories() {
        callback.onShowLoading();
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<Category> categories = new ArrayList<>();
                var isAdmin = StorageHelper.getCurrentUser().isAdmin();
                for (var child : snapshot.getChildren()) {
                    var event = child.getValue(Category.class);
                    categories.add(event);
                }

                if (callback != null) {
                    callback.onGetCategoriesComplete(categories);
                }
                callback.onHideLoading();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                callback.onHideLoading();
            }
        });
    }

    public void count() {
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                callback.onGetCategoriesCountComplete(snapshot.getChildrenCount());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    public void onDestroy() {
        if (reference != null && listener != null) {
            reference.removeEventListener(listener);
        }
    }
}
