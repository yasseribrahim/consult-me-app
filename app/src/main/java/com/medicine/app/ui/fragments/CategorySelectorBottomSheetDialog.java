package com.medicine.app.ui.fragments;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.medicine.app.models.Category;
import com.medicine.app.persenters.category.CategoriesPresenter;
import com.medicine.app.utilities.ToastUtils;
import com.medicine.app.Constants;
import com.medicine.app.R;
import com.medicine.app.databinding.FragmentBottomSheetCategorySelectorBinding;
import com.medicine.app.persenters.category.CategoriesCallback;
import com.medicine.app.ui.adptres.CategoriesSelectorAdapter;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import java.util.ArrayList;
import java.util.List;

public class CategorySelectorBottomSheetDialog extends BottomSheetDialogFragment implements CategoriesCallback, CategoriesSelectorAdapter.OnCategoriesClickListener {
    private FragmentBottomSheetCategorySelectorBinding binding;
    private CategoriesPresenter presenter;
    private CategoriesSelectorAdapter adapter;
    private List<Category> categories;
    private Category selectedCategory;
    private OnCategorySelectedCallback callback;

    public interface OnCategorySelectedCallback {
        void onCategorySelectedCallback(Category category);
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof OnCategorySelectedCallback) {
            callback = (OnCategorySelectedCallback) context;
        }
    }

    public CategorySelectorBottomSheetDialog() {
    }

    public static CategorySelectorBottomSheetDialog newInstance(Category selectedCategory) {
        CategorySelectorBottomSheetDialog fragment = new CategorySelectorBottomSheetDialog();
        Bundle args = new Bundle();
        args.putParcelable(Constants.ARG_OBJECT, selectedCategory);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onResume() {
        super.onResume();
        load();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentBottomSheetCategorySelectorBinding.inflate(inflater);

        presenter = new CategoriesPresenter(this);
        selectedCategory = getArguments().getParcelable(Constants.ARG_OBJECT);

        binding.refreshLayout.setColorSchemeResources(R.color.refreshColor1, R.color.refreshColor2, R.color.refreshColor3, R.color.refreshColor4);
        binding.refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                load();
            }
        });

        this.categories = new ArrayList<>();
        adapter = new CategoriesSelectorAdapter(categories, this, selectedCategory);
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.recyclerView.setAdapter(adapter);
        return binding.getRoot();
    }

    @Override
    public void onGetCategoriesComplete(List<Category> categories) {
        this.categories.clear();
        this.categories.addAll(categories);
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onCategorySelectedListener(Category category) {
        if (callback != null) {
            callback.onCategorySelectedCallback(category);
        }
        dismiss();
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        var dialog = super.onCreateDialog(savedInstanceState);
        dialog.getWindow().setDimAmount(0.4f); /** Set dim amount here (the dimming factor of the parent fragment) */
        return dialog;
    }

    @Override
    public void onFailure(String message, View.OnClickListener listener) {
        ToastUtils.longToast(message);
    }

    @Override
    public void onShowLoading() {
        binding.refreshLayout.setRefreshing(true);
    }

    @Override
    public void onHideLoading() {
        binding.refreshLayout.setRefreshing(false);
    }

    private void load() {
        presenter.getCategories();
    }
}