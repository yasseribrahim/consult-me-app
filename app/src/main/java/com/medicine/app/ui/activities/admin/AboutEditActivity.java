package com.medicine.app.ui.activities.admin;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Toast;

import com.medicine.app.R;
import com.medicine.app.databinding.ActivityAboutEditBinding;
import com.medicine.app.models.About;
import com.medicine.app.persenters.about.AboutCallback;
import com.medicine.app.persenters.about.AboutPresenter;
import com.medicine.app.ui.activities.BaseActivity;

public class AboutEditActivity extends BaseActivity implements AboutCallback {
    private ActivityAboutEditBinding binding;
    private AboutPresenter presenter;
    private About about;

    public static void start(Context context) {
        Intent intent = new Intent(context, AboutEditActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAboutEditBinding.inflate(getLayoutInflater());
        presenter = new AboutPresenter(this);
        about = new About();
        binding.content.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                about.setContent(s.toString().trim());
            }
        });
        binding.conditions.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                about.setConditions(s.toString().trim());
            }
        });
        binding.objectives.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                about.setObjectives(s.toString().trim());
            }
        });

        binding.btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                presenter.save(about);
            }
        });

        setContentView(binding.getRoot());
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
        presenter.getAbout();
    }

    @Override
    public void onSaveAboutComplete() {
        Toast.makeText(this, R.string.str_message_added_successfully, Toast.LENGTH_LONG).show();
    }

    @Override
    public void onShowLoading() {
        binding.progress.setVisibility(View.VISIBLE);
    }

    @Override
    public void onHideLoading() {
        binding.progress.setVisibility(View.GONE);
    }

    @Override
    public void onGetAboutComplete(About about) {
        this.about = about != null ? about : new About();
        binding.content.setText(about.getContent());
        binding.conditions.setText(about.getConditions());
        binding.objectives.setText(about.getObjectives());
    }
}