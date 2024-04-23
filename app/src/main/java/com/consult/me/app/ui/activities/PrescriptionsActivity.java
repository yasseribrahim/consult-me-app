package com.consult.me.app.ui.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.consult.me.app.Constants;
import com.consult.me.app.models.Prescription;
import com.consult.me.app.models.User;
import com.consult.me.app.persenters.prescription.PrescriptionsCallback;
import com.consult.me.app.persenters.prescription.PrescriptionsPresenter;
import com.consult.me.app.ui.adptres.PrescriptionsAdapter;
import com.consult.me.app.utilities.ToastUtils;
import com.consult.me.app.utilities.helpers.LocaleHelper;
import com.consult.me.app.utilities.helpers.StorageHelper;
import com.consult.me.app.R;
import com.consult.me.app.databinding.ActivityPrescriptionsBinding;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class PrescriptionsActivity extends BaseActivity implements PrescriptionsCallback, PrescriptionsAdapter.OnItemClickListener {
    private ActivityPrescriptionsBinding binding;
    private PrescriptionsPresenter presenter;
    private User user;
    private List<Prescription> prescriptions;
    private PrescriptionsAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        LocaleHelper.setLocale(this, getCurrentLanguage().getLanguage());
        super.onCreate(savedInstanceState);
        binding = ActivityPrescriptionsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        presenter = new PrescriptionsPresenter(this);

        user = getIntent().getParcelableExtra(Constants.ARG_OBJECT);
        binding.username.setText(user.getUsername());

        binding.btnAdd.setVisibility(StorageHelper.getCurrentUser().isDoctor() ? View.VISIBLE : View.GONE);
        binding.btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(PrescriptionsActivity.this, PrescriptionActivity.class);
                Prescription prescription = new Prescription();
                prescription.setPatientId(user.getUsername());
                prescription.setDoctorId(StorageHelper.getCurrentUser().getUsername());
                prescription.setDate(Calendar.getInstance().getTime());
                intent.putExtra(Constants.ARG_OBJECT, prescription);
                startActivity(intent);
            }
        });

        binding.refreshLayout.setColorSchemeResources(R.color.refreshColor1, R.color.refreshColor2, R.color.refreshColor3, R.color.refreshColor4);
        binding.refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                load();
            }
        });

        prescriptions = new ArrayList<>();
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new PrescriptionsAdapter(prescriptions, this);
        binding.recyclerView.setAdapter(adapter);
    }

    @Override
    protected void onResume() {
        super.onResume();
        load();
    }

    private void load() {
        presenter.getPrescriptions(user.getUsername());
    }

    @Override
    public void onGetPrescriptionsComplete(List<Prescription> prescriptions) {
        this.prescriptions.clear();
        this.prescriptions.addAll(prescriptions);
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onFailure(String message, View.OnClickListener listener) {
        Toast.makeText(this, getString(R.string.str_save_fail, message), Toast.LENGTH_LONG).show();
    }

    @Override
    public void onViewListener(Prescription prescription) {
        Intent intent = new Intent(this, PrescriptionActivity.class);
        intent.putExtra(Constants.ARG_OBJECT, prescription);
        startActivity(intent);
    }

    @Override
    public void onDeleteListener(Prescription prescription) {
        presenter.delete(prescription);
    }

    @Override
    public void onDeletePrescriptionComplete(Prescription prescription) {
        ToastUtils.longToast(R.string.str_message_delete_successfully);
        load();
    }
}