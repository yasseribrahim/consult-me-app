package com.consult.me.app.ui.activities;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.recyclerview.widget.LinearLayoutManager;

import com.consult.me.app.Constants;
import com.consult.me.app.models.Notification;
import com.consult.me.app.models.Prescription;
import com.consult.me.app.models.User;
import com.consult.me.app.persenters.notification.NotificationsCallback;
import com.consult.me.app.persenters.notification.NotificationsPresenter;
import com.consult.me.app.persenters.prescription.PrescriptionsCallback;
import com.consult.me.app.persenters.prescription.PrescriptionsPresenter;
import com.consult.me.app.persenters.user.UsersCallback;
import com.consult.me.app.persenters.user.UsersPresenter;
import com.consult.me.app.ui.adptres.MedicationsAdapter;
import com.consult.me.app.ui.fragments.MedicationBottomSheetDialog;
import com.consult.me.app.utilities.DatesUtils;
import com.consult.me.app.utilities.helpers.LocaleHelper;
import com.consult.me.app.utilities.helpers.StorageHelper;
import com.consult.me.app.R;
import com.consult.me.app.databinding.ActivityPrescriptionBinding;
import com.consult.me.app.models.Medication;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class PrescriptionActivity extends BaseActivity implements PrescriptionsCallback, NotificationsCallback, MedicationBottomSheetDialog.OnMedicationCallback, UsersCallback, MedicationsAdapter.OnItemClickListener {
    private ActivityPrescriptionBinding binding;
    private PrescriptionsPresenter presenter;
    private NotificationsPresenter notificationsPresenter;
    private UsersPresenter usersPresenter;
    private Prescription prescription;
    private User currentUser, patient;
    private MedicationsAdapter adapter;
    private List<Medication> medications;
    private boolean canEdit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        LocaleHelper.setLocale(this, getCurrentLanguage().getLanguage());
        super.onCreate(savedInstanceState);
        binding = ActivityPrescriptionBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        presenter = new PrescriptionsPresenter(this);
        notificationsPresenter = new NotificationsPresenter(this);
        usersPresenter = new UsersPresenter(this);

        prescription = getIntent().getParcelableExtra(Constants.ARG_OBJECT);
        currentUser = StorageHelper.getCurrentUser();
        canEdit = currentUser.isConsultant();

        if (prescription.getMedications() == null) {
            prescription.setMedications(new ArrayList<>());
        }

        medications = new ArrayList<>();
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new MedicationsAdapter(medications, this);
        binding.recyclerView.setAdapter(adapter);

        bind();

        binding.btnAddMedication.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MedicationBottomSheetDialog dialog = MedicationBottomSheetDialog.newInstance(prescription, new Medication());
                dialog.show(getSupportFragmentManager(), "");
            }
        });

        binding.btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String title = binding.title.getText().toString().trim();
                String description = binding.description.getText().toString().trim();

                if (title.isEmpty()) {
                    binding.title.setError(getString(R.string.str_title_hint));
                    binding.title.requestFocus();
                    return;
                }
                if (description.isEmpty()) {
                    binding.description.setError(getString(R.string.str_description_hint));
                    binding.description.requestFocus();
                    return;
                }

                prescription.setTitle(title);
                prescription.setDescription(binding.description.getText().toString());
                if (prescription.getId() == null) {
                    prescription.setDoctorId(StorageHelper.getCurrentUser().getUsername());
                }

                presenter.save(prescription);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (prescription.getId() != null) {
            presenter.getPrescription(prescription.getId());
        } else {
            usersPresenter.getUserByUsername(prescription.getPatientId());
        }
    }

    @Override
    public void onGetPrescriptionComplete(Prescription prescription) {
        this.prescription = prescription;
        bind();
        usersPresenter.getUserByUsername(prescription.getPatientId());
    }

    @Override
    public void onGetUserComplete(User user) {
        this.patient = user;
    }

    @Override
    public void onSavePrescriptionComplete() {
        PrescriptionsCallback.super.onSavePrescriptionComplete();
        Toast.makeText(this, R.string.str_message_added_successfully, Toast.LENGTH_LONG).show();
        Notification notification = new Notification();
        notification.setMessage(getString(R.string.str_notification_message_prescription, prescription.getTitle(), DatesUtils.formatDate(prescription.getDate())));
        notification.setPrescriptionId(prescription.getId());
        if (patient != null) {
            notificationsPresenter.save(notification, Arrays.asList(patient));
        } else {
            finish();
        }
    }

    @Override
    public void onSaveNotificationComplete() {
        finish();
    }

    @Override
    public void onFailure(String message, View.OnClickListener listener) {
        Toast.makeText(this, getString(R.string.str_save_fail, message), Toast.LENGTH_LONG).show();
    }

    private void bind() {
        binding.title.setEnabled(canEdit);
        binding.description.setEnabled(canEdit);
        binding.btnSave.setVisibility(canEdit ? View.VISIBLE : View.GONE);
        binding.btnAddMedication.setVisibility(canEdit ? View.VISIBLE : View.GONE);

        binding.doctor.setText(prescription.getDoctorId());
        binding.patient.setText(prescription.getPatientId());
        binding.title.setText(prescription.getTitle());
        binding.description.setText(prescription.getDescription());
        binding.date.setText(DatesUtils.formatDate(prescription.getDate()));

        medications.clear();
        medications.addAll(prescription.getMedications());
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onViewListener(Medication medication) {
        if (currentUser.isConsultant()) {
            MedicationBottomSheetDialog dialog = MedicationBottomSheetDialog.newInstance(prescription, medication);
            dialog.show(getSupportFragmentManager(), "");
        }
    }

    @Override
    public void onDeleteListener(Medication medication) {
        prescription.getMedications().remove(medication);
        presenter.save(prescription);
    }

    @Override
    public void onMedicationChangedCallback() {
        presenter.getPrescription(prescription.getId());
        Notification notification = new Notification();
        notification.setMessage(getString(R.string.str_notification_message_prescription, prescription.getTitle(), DatesUtils.formatDate(prescription.getDate())));
        notification.setPrescriptionId(prescription.getId());
        if (patient != null) {
            notificationsPresenter.save(notification, Arrays.asList(patient));
        }
    }
}