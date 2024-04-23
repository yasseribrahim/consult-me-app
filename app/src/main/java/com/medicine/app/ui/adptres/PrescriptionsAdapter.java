package com.medicine.app.ui.adptres;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.recyclerview.widget.RecyclerView;

import com.medicine.app.R;
import com.medicine.app.databinding.ItemPrescriptionBinding;
import com.medicine.app.models.Prescription;
import com.medicine.app.utilities.DatesUtils;
import com.medicine.app.utilities.helpers.StorageHelper;

import java.util.List;

public class PrescriptionsAdapter extends RecyclerView.Adapter<PrescriptionsAdapter.ViewHolder> {
    private List<Prescription> prescriptions;
    private OnItemClickListener listener;

    public PrescriptionsAdapter(List<Prescription> prescriptions, OnItemClickListener listener) {
        this.prescriptions = prescriptions;
        this.listener = listener;
    }

    // inflates the row layout from xml when needed
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_prescription, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Prescription prescription = prescriptions.get(position);

        holder.binding.title.setText(prescription.getTitle());
        holder.binding.medicationCounter.setText(prescription.getMedications().size() + "");
        holder.binding.username.setText(StorageHelper.getCurrentUser().isPatient() ? prescription.getDoctorId() : prescription.getPatientId());
        holder.binding.description.setText(prescription.getDescription());
        holder.binding.date.setText(DatesUtils.formatDate(prescription.getDate()));
        holder.binding.containerRemove.setVisibility(StorageHelper.getCurrentUser().isDoctor() ? View.VISIBLE : View.GONE);
    }

    private int getSize(String id) {
        return prescriptions.size();
    }

    // total number of rows
    @Override
    public int getItemCount() {
        return prescriptions.size();
    }

    // stores and recycles views as they are scrolled off screen
    public class ViewHolder extends RecyclerView.ViewHolder {
        ItemPrescriptionBinding binding;

        ViewHolder(View view) {
            super(view);
            binding = ItemPrescriptionBinding.bind(view);
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener != null)
                        listener.onViewListener(prescriptions.get(getAdapterPosition()));
                }
            });
            binding.containerRemove.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener != null)
                        listener.onDeleteListener(prescriptions.get(getAdapterPosition()));
                }
            });
        }
    }

    public interface OnItemClickListener {
        void onViewListener(Prescription prescription);

        void onDeleteListener(Prescription prescription);
    }
}