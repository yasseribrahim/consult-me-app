package com.medicine.app.ui.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.medicine.app.models.User;
import com.medicine.app.persenters.user.UsersCallback;
import com.medicine.app.persenters.user.UsersPresenter;
import com.medicine.app.Constants;
import com.medicine.app.R;
import com.medicine.app.databinding.FragmentCraftsmansBinding;
import com.medicine.app.ui.activities.ReviewsActivity;
import com.medicine.app.ui.adptres.CraftsmansAdapter;

import java.util.ArrayList;
import java.util.List;

public class CraftsmansFragment extends Fragment implements UsersCallback, CraftsmansAdapter.OnCraftsmanClickListener {
    private FragmentCraftsmansBinding binding;
    private UsersPresenter presenter;
    private CraftsmansAdapter adapter;
    private List<User> craftsman, searchedCraftsman;

    public static CraftsmansFragment newInstance() {
        Bundle args = new Bundle();
        CraftsmansFragment fragment = new CraftsmansFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentCraftsmansBinding.inflate(inflater);

        presenter = new UsersPresenter(this);

        binding.refreshLayout.setColorSchemeResources(R.color.refreshColor1, R.color.refreshColor2, R.color.refreshColor3, R.color.refreshColor4);
        binding.refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                load();
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

        craftsman = new ArrayList<>();
        searchedCraftsman = new ArrayList<>();
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new CraftsmansAdapter(searchedCraftsman, this);
        binding.recyclerView.setAdapter(adapter);

        return binding.getRoot();
    }

    private void load() {
        presenter.getUsers(Constants.USER_TYPE_DOCTOR);
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
    public void onGetUsersComplete(List<User> users) {
        this.craftsman.clear();
        this.craftsman.addAll(users);
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
        searchedCraftsman.clear();
        if (!searchedText.isEmpty()) {
            for (User user : craftsman) {
                if (isMatched(user, searchedText)) {
                    searchedCraftsman.add(user);
                }
            }
        } else {
            searchedCraftsman.addAll(craftsman);
        }

        refresh();
    }

    private boolean isMatched(User user, String text) {
        String searchedText = text.toLowerCase();
        boolean result = user.getFullName().toLowerCase().contains(searchedText) ||
                (user.getAddress() != null && user.getAddress().toLowerCase().contains(searchedText)) ||
                (user.getDescription() != null && user.getDescription().toLowerCase().contains(searchedText));
        return result;
    }

    private void refresh() {
        binding.message.setVisibility(View.GONE);
        if (searchedCraftsman.isEmpty()) {
            binding.message.setVisibility(View.VISIBLE);
        }

        adapter.notifyDataSetChanged();
    }

    @Override
    public void onCraftsmanViewListener(User user) {
        ContactsBottomSheetDialog dialog = ContactsBottomSheetDialog.newInstance(user, false);
        dialog.show(getParentFragmentManager(), "");
    }

    @Override
    public void onCraftsmanContactsListener(User user) {
        ContactsBottomSheetDialog dialog = ContactsBottomSheetDialog.newInstance(user, false);
        dialog.show(getParentFragmentManager(), "");
    }

    @Override
    public void onCraftsmanReviewListener(User user) {
        Intent intent = new Intent(getContext(), ReviewsActivity.class);
        intent.putExtra(Constants.ARG_OBJECT, user);
        startActivity(intent);
    }
}