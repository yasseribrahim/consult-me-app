package com.consult.me.app.ui.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.consult.me.app.databinding.FragmentAboutBinding;
import com.consult.me.app.models.About;
import com.consult.me.app.persenters.about.AboutCallback;
import com.consult.me.app.persenters.about.AboutPresenter;
import com.consult.me.app.ui.activities.admin.AboutEditActivity;
import com.consult.me.app.utilities.helpers.StorageHelper;

public class AboutFragment extends Fragment implements AboutCallback {
    private FragmentAboutBinding binding;
    private AboutPresenter presenter;

    public static AboutFragment newInstance() {
        Bundle args = new Bundle();
        AboutFragment fragment = new AboutFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentAboutBinding.inflate(inflater);

        binding.btnEdit.setVisibility(StorageHelper.getCurrentUser().isAdmin() ? View.VISIBLE : View.GONE);
        binding.btnEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getContext(), AboutEditActivity.class));
            }
        });

        presenter = new AboutPresenter(this);
        return binding.getRoot();
    }

    @Override
    public void onResume() {
        super.onResume();
        presenter.getAbout();
    }

    @Override
    public void onShowLoading() {
        ProgressDialogFragment.show(getChildFragmentManager());
    }

    @Override
    public void onHideLoading() {
        ProgressDialogFragment.hide(getChildFragmentManager());
    }

    @Override
    public void onFailure(String message, View.OnClickListener listener) {
        Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onGetAboutComplete(About about) {
        binding.content.setText(about.getContent());
        binding.conditions.setText(about.getConditions());
        binding.objectives.setText(about.getObjectives());
    }
}