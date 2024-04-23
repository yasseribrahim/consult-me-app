package com.medicine.app.ui.fragments;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.medicine.app.Constants;
import com.medicine.app.R;
import com.medicine.app.databinding.FragmentBottomSheetDialogMedicationBinding;
import com.medicine.app.models.Medication;
import com.medicine.app.models.Prescription;
import com.medicine.app.persenters.prescription.PrescriptionsCallback;
import com.medicine.app.persenters.prescription.PrescriptionsPresenter;
import com.medicine.app.utilities.ToastUtils;

import java.util.ArrayList;

public class MedicationBottomSheetDialog extends BottomSheetDialogFragment implements PrescriptionsCallback {
    private FragmentBottomSheetDialogMedicationBinding binding;
    private PrescriptionsPresenter presenter;
    private Prescription prescription;
    private Medication medication;
    private OnMedicationCallback callback;

    public interface OnMedicationCallback {
        void onMedicationChangedCallback();
    }

    public MedicationBottomSheetDialog() {
    }

    public static MedicationBottomSheetDialog newInstance(Prescription prescription, Medication medication) {
        MedicationBottomSheetDialog fragment = new MedicationBottomSheetDialog();
        Bundle args = new Bundle();
        args.putParcelable(Constants.ARG_OBJECT_1, prescription);
        args.putParcelable(Constants.ARG_OBJECT_2, medication);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof OnMedicationCallback) {
            callback = (OnMedicationCallback) context;
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        presenter = new PrescriptionsPresenter(this);
        prescription = getArguments().getParcelable(Constants.ARG_OBJECT_1);
        medication = getArguments().getParcelable(Constants.ARG_OBJECT_2);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentBottomSheetDialogMedicationBinding.inflate(inflater);
        bind();
        return binding.getRoot();
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        var dialog = super.onCreateDialog(savedInstanceState);
        dialog.getWindow().setDimAmount(0.4f); /** Set dim amount here (the dimming factor of the parent fragment) */
        return dialog;
    }

    private void bind() {
        binding.btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (prescription.getMedications() == null) {
                    prescription.setMedications(new ArrayList<>());
                }

                var medicationName = binding.medicationName.getText().toString();
                var dosage = binding.dosage.getText().toString();
                var periodic = binding.periodic.getText().toString();

                if (medicationName.isEmpty()) {
                    binding.medicationName.setError(getString(R.string.str_medication_name_alert));
                    binding.medicationName.requestFocus();
                    return;
                }
                if (dosage.isEmpty()) {
                    binding.dosage.setError(getString(R.string.str_dosage_alert));
                    binding.dosage.requestFocus();
                    return;
                }
                if (periodic.isEmpty()) {
                    binding.periodic.setError(getString(R.string.str_periodic_alert));
                    binding.periodic.requestFocus();
                    return;
                }

                int index = prescription.getMedications().indexOf(medication);
                if (index != -1) {
                    medication = prescription.getMedications().get(index);
                } else {
                    prescription.getMedications().add(medication);
                }
                medication.setMedicationName(medicationName);
                medication.setDosage(dosage);
                medication.setPeriodic(Integer.parseInt(periodic));

                presenter.save(prescription);
            }
        });

        binding.medicationName.setText(medication.getMedicationName());
        binding.dosage.setText(medication.getDosage());
        binding.periodic.setText(medication.getPeriodic() + "");
    }

    @Override
    public void onSavePrescriptionComplete() {
        ToastUtils.longToast(R.string.str_message_updated_successfully);
        if (callback != null) {
            callback.onMedicationChangedCallback();
        }
        dismiss();
    }

    @Override
    public void onFailure(String message, View.OnClickListener listener) {
        ToastUtils.longToast(message);
    }

    @Override
    public void onShowLoading() {
        ProgressDialogFragment.show(getParentFragmentManager());
    }

    @Override
    public void onHideLoading() {
        ProgressDialogFragment.hide(getParentFragmentManager());
    }
}