package com.medicine.app.ui.fragments;

import android.content.DialogInterface;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.medicine.app.models.Category;
import com.medicine.app.persenters.category.CategoriesPresenter;
import com.medicine.app.utilities.ToastUtils;
import com.medicine.app.utilities.helpers.StorageHelper;
import com.medicine.app.R;
import com.medicine.app.databinding.FragmentCategoriesBinding;
import com.medicine.app.persenters.category.CategoriesCallback;
import com.medicine.app.ui.adptres.CategoriesAdapter;

import java.util.ArrayList;
import java.util.List;

public class CategoriesFragment extends Fragment implements CategoriesCallback, CategoriesAdapter.OnCategoriesClickListener {
    private FragmentCategoriesBinding binding;
    private CategoriesPresenter presenter;
    private CategoriesAdapter categoriesAdapter;
    private List<Category> categories, searchedCategories;

    public static CategoriesFragment newInstance() {
        Bundle args = new Bundle();
        CategoriesFragment fragment = new CategoriesFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentCategoriesBinding.inflate(inflater);

        presenter = new CategoriesPresenter(this);

        binding.btnAdd.setVisibility(StorageHelper.getCurrentUser().isAdmin() ? View.VISIBLE : View.GONE);
        binding.refreshLayout.setColorSchemeResources(R.color.refreshColor1, R.color.refreshColor2, R.color.refreshColor3, R.color.refreshColor4);
        binding.refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                load();
            }
        });

        binding.btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                var category = new Category();
                openCategoryActivity(category);
            }
        });

        binding.textSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                search(binding.textSearch.getText().toString());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        categories = new ArrayList<>();
        searchedCategories = new ArrayList<>();
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        categoriesAdapter = new CategoriesAdapter(searchedCategories, this);
        binding.recyclerView.setAdapter(categoriesAdapter);

        return binding.getRoot();
    }

    private void load() {
        presenter.getCategories();
    }

    @Override
    public void onResume() {
        super.onResume();
        load();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (presenter != null) {
            presenter.onDestroy();
        }
    }

    @Override
    public void onGetCategoriesComplete(List<Category> categories) {
        this.categories.clear();
        this.categories.addAll(categories);
        search(binding.textSearch.getText().toString());
    }

    @Override
    public void onShowLoading() {
        binding.refreshLayout.setRefreshing(true);
    }

    @Override
    public void onHideLoading() {
        binding.refreshLayout.setRefreshing(false);
    }

    @Override
    public void onFailure(String message, View.OnClickListener listener) {
        Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
    }

    private void search(String searchedText) {
        searchedCategories.clear();
        if (!searchedText.isEmpty()) {
            for (Category category : categories) {
                if (isMatched(category, searchedText)) {
                    searchedCategories.add(category);
                }
            }
        } else {
            searchedCategories.addAll(categories);
        }

        refresh();
    }

    private boolean isMatched(Category category, String text) {
        String searchedText = text.toLowerCase();
        boolean result = category.getName().toLowerCase().contains(searchedText);
        return result;
    }

    private void refresh() {
        binding.message.setVisibility(View.GONE);
        if (searchedCategories.isEmpty()) {
            binding.message.setVisibility(View.VISIBLE);
        }

        categoriesAdapter.notifyDataSetChanged();
    }

    @Override
    public void onSaveCategoryComplete() {
        Toast.makeText(getContext(), R.string.str_message_added_successfully, Toast.LENGTH_LONG).show();
        load();
    }

    @Override
    public void onCategoryDeleteListener(Category category) {
        int index = categories.indexOf(category);
        if (index >= 0 && index < categories.size()) {
            categories.remove(index);
        }

        presenter.delete(category);
    }

    @Override
    public void onCategoryEditListener(Category category) {
        openCategoryActivity(category);
    }

    @Override
    public void onDeleteCategoryComplete(Category category) {
        int index = searchedCategories.indexOf(category);
        if (index != -1) {
            searchedCategories.remove(index);
            categoriesAdapter.notifyItemRemoved(index);
        }
        Toast.makeText(getContext(), R.string.str_message_delete_successfully, Toast.LENGTH_LONG).show();
    }

    private void openCategoryActivity(Category category) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle(R.string.str_add_new);

        final EditText input = new EditText(getContext());
        input.setText(category.getName());
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        builder.setView(input);

        builder.setPositiveButton(R.string.str_save, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String text = input.getText().toString();
                if (text.isEmpty()) {
                    ToastUtils.longToast(R.string.str_enter_value);
                    return;
                }
                var exist = categories.stream().anyMatch(grade -> {
                    return grade.getName().equalsIgnoreCase(text);
                });
                if (exist) {
                    ToastUtils.longToast("Sorry " + text + " already exist");
                } else {
                    category.setName(text);
                    presenter.save(category);
                }
            }
        });
        builder.setNegativeButton(R.string.str_cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        dialog = builder.show();
    }

    private AlertDialog dialog;
}