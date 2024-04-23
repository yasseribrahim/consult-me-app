package com.consult.me.app.ui.fragments;

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

import com.consult.me.app.models.Notification;
import com.consult.me.app.models.Prescription;
import com.consult.me.app.models.Question;
import com.consult.me.app.ui.activities.PrescriptionActivity;
import com.consult.me.app.ui.activities.QuestionActivity;
import com.consult.me.app.Constants;
import com.consult.me.app.persenters.notification.NotificationsCallback;
import com.consult.me.app.persenters.notification.NotificationsPresenter;
import com.consult.me.app.ui.adptres.NotificationsAdapter;
import com.consult.me.app.R;
import com.consult.me.app.databinding.FragmentNotificationsBinding;

import java.util.ArrayList;
import java.util.List;

public class NotificationsFragment extends Fragment implements NotificationsCallback, NotificationsAdapter.OnItemClickListener {
    private FragmentNotificationsBinding binding;
    private NotificationsPresenter presenter;
    private NotificationsAdapter adapter;
    private List<Notification> notifications, searchedNotifications;

    public static NotificationsFragment newInstance() {
        Bundle args = new Bundle();
        NotificationsFragment fragment = new NotificationsFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentNotificationsBinding.inflate(inflater);

        presenter = new NotificationsPresenter(this);

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

        notifications = new ArrayList<>();
        searchedNotifications = new ArrayList<>();
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new NotificationsAdapter(searchedNotifications, this);
        binding.recyclerView.setAdapter(adapter);

        return binding.getRoot();
    }

    private void load() {
        presenter.getNotifications();
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
    public void onGetNotificationsComplete(List<Notification> notifications) {
        this.notifications.clear();
        this.notifications.addAll(notifications);
        search(binding.textSearch.getText().toString().trim());
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

    @Override
    public void onItemViewListener(Notification notification) {
        Intent intent = null;
        if (notification.getPrescriptionId() != null && notification.getPrescriptionId().startsWith("question")) {
            intent = new Intent(getContext(), QuestionActivity.class);
            var question = new Question();
            question.setId(notification.getPrescriptionId());
            intent.putExtra(Constants.ARG_OBJECT, question);
        } else if (notification.getPrescriptionId() != null && notification.getPrescriptionId().startsWith("prescription")) {
            intent = new Intent(getContext(), PrescriptionActivity.class);
            var prescription = new Prescription();
            prescription.setId(notification.getPrescriptionId());
            intent.putExtra(Constants.ARG_OBJECT, prescription);
        }
        if (intent != null) {
            startActivity(intent);
        }
    }

    @Override
    public void onDeleteItemViewListener(Notification notification) {
        int position = searchedNotifications.indexOf(notification);
        notifications.remove(notification);
        searchedNotifications.remove(notification);
        presenter.delete(notification, position);
    }

    @Override
    public void onDeleteNotificationComplete(int position) {
        Toast.makeText(getContext(), R.string.str_message_delete_successfully, Toast.LENGTH_LONG).show();
        adapter.notifyItemRemoved(position);
    }

    private void search(String searchedText) {
        searchedNotifications.clear();
        if (!searchedText.isEmpty()) {
            for (Notification notification : notifications) {
                if (isMatched(notification, searchedText)) {
                    searchedNotifications.add(notification);
                }
            }
        } else {
            searchedNotifications.addAll(notifications);
        }

        refresh();
    }

    private boolean isMatched(Notification notification, String text) {
        String searchedText = text.toLowerCase();
        boolean result = notification.getMessage().toLowerCase().contains(searchedText);
        return result;
    }

    private void refresh() {
        binding.message.setVisibility(View.GONE);
        if (searchedNotifications.isEmpty()) {
            binding.message.setVisibility(View.VISIBLE);
        }

        adapter.notifyDataSetChanged();
    }
}