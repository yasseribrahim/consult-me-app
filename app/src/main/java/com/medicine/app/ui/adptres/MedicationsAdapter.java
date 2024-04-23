package com.medicine.app.ui.adptres;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.recyclerview.widget.RecyclerView;

import com.medicine.app.R;
import com.medicine.app.databinding.ItemMedicationBinding;
import com.medicine.app.models.Medication;
import com.medicine.app.utilities.helpers.StorageHelper;

import java.util.List;

public class MedicationsAdapter extends RecyclerView.Adapter<MedicationsAdapter.ViewHolder> {
    private List<Medication> medications;
    private OnItemClickListener listener;

    public MedicationsAdapter(List<Medication> medications, OnItemClickListener listener) {
        this.medications = medications;
        this.listener = listener;
    }

    // inflates the row layout from xml when needed
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_medication, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Medication medication = medications.get(position);

        holder.binding.name.setText(medication.getMedicationName());
        holder.binding.dosage.setText(medication.getDosage());
        holder.binding.periodic.setText(holder.binding.periodic.getResources().getString(R.string.str_periodic_format, medication.getPeriodic() + ""));
        holder.binding.containerRemove.setVisibility(StorageHelper.getCurrentUser().isDoctor() ? View.VISIBLE : View.GONE);
    }

    private int getSize(String id) {
        return medications.size();
    }

    // total number of rows
    @Override
    public int getItemCount() {
        return medications.size();
    }

    // stores and recycles views as they are scrolled off screen
    public class ViewHolder extends RecyclerView.ViewHolder {
        ItemMedicationBinding binding;

        ViewHolder(View view) {
            super(view);
            binding = ItemMedicationBinding.bind(view);
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener != null)
                        listener.onViewListener(medications.get(getAdapterPosition()));
                }
            });
            binding.containerRemove.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener != null)
                        listener.onDeleteListener(medications.get(getAdapterPosition()));
                }
            });
        }
    }

    public interface OnItemClickListener {
        void onViewListener(Medication medication);

        void onDeleteListener(Medication medication);
    }
}