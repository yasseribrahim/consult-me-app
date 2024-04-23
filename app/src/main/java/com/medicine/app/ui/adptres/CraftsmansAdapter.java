package com.medicine.app.ui.adptres;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.medicine.app.models.User;
import com.medicine.app.utilities.NumbersUtils;
import com.medicine.app.utilities.helpers.StorageHelper;
import com.medicine.app.R;
import com.medicine.app.databinding.ItemCraftsmanBinding;

import java.util.List;

public class CraftsmansAdapter extends RecyclerView.Adapter<CraftsmansAdapter.ViewHolder> {
    private List<User> craftsman;
    private OnCraftsmanClickListener listener;
    private User currentUser;

    public CraftsmansAdapter(List<User> craftsman, OnCraftsmanClickListener listener) {
        this.craftsman = craftsman;
        this.listener = listener;
        this.currentUser = StorageHelper.getCurrentUser();
    }

    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_craftsman, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        User user = craftsman.get(position);

        holder.binding.name.setText(user.getFullName());
        holder.binding.username.setText(user.getUsername());
        holder.binding.phone.setText(user.getPhone());
        holder.binding.description.setText(user.getDescription());
        holder.binding.review.setRating(user.getRating());
        holder.binding.reviewValue.setText(NumbersUtils.round(user.getRating(), 1) + "");
        holder.binding.description.setVisibility(user.getDescription() != null && !user.getDescription().isEmpty() ? View.VISIBLE : View.GONE);
        Glide.with(holder.itemView.getContext()).load(user.getImageProfile()).placeholder(R.drawable.ic_profile).into(holder.binding.image);
    }

    // total number of rows
    @Override
    public int getItemCount() {
        return craftsman.size();
    }

    // stores and recycles views as they are scrolled off screen
    public class ViewHolder extends RecyclerView.ViewHolder {
        ItemCraftsmanBinding binding;

        ViewHolder(View view) {
            super(view);
            binding = ItemCraftsmanBinding.bind(view);
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener != null) {
                        listener.onCraftsmanViewListener(craftsman.get(getAdapterPosition()));
                    }
                }
            });
            binding.containerContacts.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener != null) {
                        listener.onCraftsmanContactsListener(craftsman.get(getAdapterPosition()));
                    }
                }
            });
            binding.containerReview.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener != null) {
                        listener.onCraftsmanReviewListener(craftsman.get(getAdapterPosition()));
                    }
                }
            });
        }
    }

    public interface OnCraftsmanClickListener {
        void onCraftsmanViewListener(User user);

        void onCraftsmanContactsListener(User user);

        void onCraftsmanReviewListener(User user);
    }
}