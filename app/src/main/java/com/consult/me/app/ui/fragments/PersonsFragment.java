package com.consult.me.app.ui.fragments;

import android.content.Intent;
import android.net.Uri;
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

import com.consult.me.app.Constants;
import com.consult.me.app.R;
import com.consult.me.app.databinding.FragmentPersonsBinding;
import com.consult.me.app.models.Link;
import com.consult.me.app.models.User;
import com.consult.me.app.persenters.links.LinksCallback;
import com.consult.me.app.persenters.links.LinksPresenter;
import com.consult.me.app.persenters.user.UsersCallback;
import com.consult.me.app.persenters.user.UsersPresenter;
import com.consult.me.app.ui.activities.QuestionsRepliedActivity;
import com.consult.me.app.ui.adptres.PersonsAdapter;
import com.consult.me.app.utilities.helpers.StorageHelper;

import java.util.ArrayList;
import java.util.List;

public class PersonsFragment extends Fragment implements UsersCallback, LinksCallback, PersonsAdapter.OnItemClickListener {
    private FragmentPersonsBinding binding;
    private UsersPresenter presenter;
    private LinksPresenter linksPresenter;
    private PersonsAdapter adapter;
    private List<User> users, searchedUsers;
    private List<Link> links;

    public static PersonsFragment newInstance(int userType) {
        Bundle args = new Bundle();
        args.putInt(Constants.ARG_ID, userType);
        PersonsFragment fragment = new PersonsFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentPersonsBinding.inflate(inflater);

        presenter = new UsersPresenter(this);
        linksPresenter = new LinksPresenter(this);
        links = new ArrayList<>();

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

        users = new ArrayList<>();
        searchedUsers = new ArrayList<>();
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new PersonsAdapter(searchedUsers, this);
        binding.recyclerView.setAdapter(adapter);

        return binding.getRoot();
    }

    private void load() {
        linksPresenter.getLinks();
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
        if (linksPresenter != null) {
            linksPresenter.onDestroy();
        }
    }

    @Override
    public void onGetLinksComplete(List<Link> links) {
        this.links.clear();
        this.links.addAll(links);
        presenter.getUsers(StorageHelper.getCurrentUser().isConsultant() ? Constants.USER_TYPE_CLIENT : Constants.USER_TYPE_CONSULTANT);
    }

    @Override
    public void onGetUsersComplete(List<User> users) {
        this.users.clear();
        var isCurrentUserDoctor = StorageHelper.getCurrentUser().isConsultant();
        var ids = new ArrayList<String>();
        links.stream().forEach(e -> {
            if (isCurrentUserDoctor) {
                ids.add(e.getClientId());
            } else {
                ids.add(e.getConsultantId());
            }
        });

        for (var user : users) {
            if (ids.contains(user.getUsername())) {
                this.users.add(user);
            }
        }
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
        searchedUsers.clear();
        if (!searchedText.isEmpty()) {
            for (User user : users) {
                if (isMatched(user, searchedText)) {
                    searchedUsers.add(user);
                }
            }
        } else {
            searchedUsers.addAll(users);
        }

        refresh();
    }

    private boolean isMatched(User user, String text) {
        String searchedText = text.toLowerCase();
        boolean result = user.getFullName().toLowerCase().contains(searchedText) ||
                (user.getAddress() != null && user.getAddress().toLowerCase().contains(searchedText)) ||
                (user.getPhone() != null && user.getPhone().toLowerCase().contains(searchedText)) ||
                (user.getUsername() != null && user.getUsername().toLowerCase().contains(searchedText));
        return result;
    }

    private void refresh() {
        binding.message.setVisibility(View.GONE);
        if (searchedUsers.isEmpty()) {
            binding.message.setVisibility(View.VISIBLE);
        }

        adapter.notifyDataSetChanged();
    }

    @Override
    public void onViewListener(User user) {
        if (user.isClient()) {
            Intent intent = new Intent(getContext(), QuestionsRepliedActivity.class);
            intent.putExtra(Constants.ARG_OBJECT, user.getUsername());
            startActivity(intent);
        }
    }

    @Override
    public void onDeleteListener(User user) {
        String patientId, doctorId;
        if (StorageHelper.getCurrentUser().isClient()) {
            patientId = StorageHelper.getCurrentUser().getUsername();
            doctorId = user.getUsername();
        } else {
            doctorId = StorageHelper.getCurrentUser().getUsername();
            patientId = user.getUsername();
        }

        int position = links.indexOf(new Link("", doctorId, patientId));
        if (position >= 0) {
            Link link = links.get(position);
            linksPresenter.delete(link);
        }
    }

    @Override
    public void onCallListener(User user) {
        Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + user.getPhone()));
        startActivity(intent);
    }

    @Override
    public void onDeleteLinkComplete(Link link) {
        load();
        Toast.makeText(getContext(), R.string.str_message_delete_successfully, Toast.LENGTH_LONG).show();
    }
}